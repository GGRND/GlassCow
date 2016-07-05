package com.eaaa.glasscow.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.eaaa.glasscow.Activity_Main;
import com.eaaa.glasscow.Configuration;
import com.eaaa.glasscow.model.CowObservation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

import static com.eaaa.glasscow.service.DatabaseFields.*;

import      java.util.concurrent.Executor;
/**
 * Created by morten on 17/03/15.
 */
public class RemoteDatabase {

    private static RemoteDatabase instance = null;
    private static String token = null, Username = null, Password = null;
    private static Activity_Main context;
    private static String Endpoint;
    private static String Audience;
    private static String rst;

    class PriorityExecutor implements Executor {
        private final int priority;

        public PriorityExecutor(int priority) {
            this.priority = priority;
        }

        public void execute(Runnable r) {
            Thread t = new Thread(r);
            t.setPriority(this.priority);
            t.start();
        }

    }

    private static abstract class postSecureRequestTask extends AsyncTask<ArrayList<String>, Void, String> {


        abstract void callBack(String result) throws JSONException;

        @Override
        protected String doInBackground(ArrayList<String>... parameters) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return postSecureRequest(parameters[0]);
            } catch (IOException e) {
                return null;
                //return "Unable execute postSecureRequestTask: "+e.getMessage();
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
                try {
                    callBack(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        }

        //Parameters: String url, String body, HeaderName1, HeaderValue1, HeaderName2, HeaderValue2 ...
        protected String postSecureRequest(ArrayList<String> params) throws IOException {
            StringBuffer response = new StringBuffer();

            //Test if connection is possible
            ConnectivityManager connMgr = (ConnectivityManager)
                    RemoteDatabase.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo==null || !networkInfo.isConnected())
                throw new IOException("No network available");

            //Setup and execute request
            URL obj = new URL(params.get(0));
            HttpsURLConnection con = null;
            try {
                con = (HttpsURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                for (int i=2; i<params.size(); i=i+2) {
                    con.setRequestProperty(params.get(i), params.get(i+1));
                }
                con.setDoOutput(true);
                OutputStream outstream = null;
                outstream = con.getOutputStream();
                DataOutputStream wr = new DataOutputStream(outstream);
                wr.writeBytes(params.get(1));
                wr.flush();
                wr.close();
            }
            catch (Exception E) {
                Log.v("ERROR", E.getMessage());
                return "";
            }

            //Test http response code
            int responseCode = con.getResponseCode();
            if (responseCode / 100 != 2)
            {
                throw new IOException("Non-2xx status code: " + responseCode + " " + con.getResponseMessage());
            }

            //Read response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine = new String();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            return response.toString();
        }
    }

    private static abstract class retrieveTokenTask extends postSecureRequestTask {

        @Override
        protected String doInBackground(ArrayList<String>... parameters) {

            ArrayList<String> params = new ArrayList<String>();
            Configuration conf = context.getConfiguration();

            //Load configuration
            RemoteDatabase.Endpoint = conf.get_Endpoint();
            RemoteDatabase.Username = conf.get_Username();
            RemoteDatabase.Password = conf.get_Password();
            RemoteDatabase.Audience = conf.get_Audience();
            String template = conf.get_rst_template();
            RemoteDatabase.rst = String.format(template, RemoteDatabase.Endpoint, RemoteDatabase.Username, RemoteDatabase.Password, RemoteDatabase.Audience);

            //Add url and body params
            params.add(RemoteDatabase.Endpoint);
            params.add(RemoteDatabase.rst);

            //Add header to params
            params.add("Content-Type");
            params.add("application/soap+xml; charset=utf-8");

            try {
                return postSecureRequest(params);
            } catch (IOException e) {
                return "Unable execute postSecureRequestTask: " + e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(String tokenResponseBody) {
            //Pull SAML from returned body
            int startPosSAML = tokenResponseBody.indexOf("<saml:Assertion");
            int endPosSAML = tokenResponseBody.indexOf("</saml:Assertion>") + 17;

            String result;
            if (startPosSAML>=0 && endPosSAML>=0)
                result = tokenResponseBody.substring(startPosSAML, endPosSAML);
            else
                result = null;

            //return SAML token
            RemoteDatabase.token = result;

            try {
                callBack(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static RemoteDatabase getInstance(Activity_Main context) {
        if (RemoteDatabase.instance==null) {
            RemoteDatabase.instance = new RemoteDatabase(context);
        }
        return RemoteDatabase.instance;
    }

   private RemoteDatabase(Activity_Main context) {
       RemoteDatabase.context = context;
   }

    private boolean isTokenRequestNeeded() {
        Configuration conf = context.getConfiguration();

        return (token == null) ||
                !(RemoteDatabase.Endpoint.equals(conf.get_Endpoint())) ||
                !(RemoteDatabase.Username.equals(conf.get_Username())) ||
                !(RemoteDatabase.Password.equals(conf.get_Password())) ||
                !(RemoteDatabase.Audience.equals(conf.get_Audience()));
    }

    public void updateCattleDatabase(final SQLiteDatabase db) {

        if (isTokenRequestNeeded())
        {
            new retrieveTokenTask() {
                @Override
                void callBack(String result) {
                    if (!isTokenRequestNeeded())
                        doUpdateCattleDatabase(db);
                }
            }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY));
        }
        else
        {
            doUpdateCattleDatabase(db);
        }
    }

    private void doUpdateCattleDatabase(final SQLiteDatabase db)
    {
        Configuration conf = context.getConfiguration();

        //get cattle info
        ArrayList<String> params = new ArrayList<String>();
        params.add(conf.get_Audience()+"CattleWebApi/GoogleGlassesOperations/GetCowIndexCardMobilesForAgriBusiness?AgriBusinessId="+conf.get_AgriBusinessId());
        params.add("{\"$id\":\"1\",\"AgriBusinessId\":\""+conf.get_AgriBusinessId()+"\"}");
        params.add("Authorization");
        params.add("SAML " + token);
        params.add("Content-Type");
        params.add("application/json");
        params.add("Host");
        params.add(conf.get_Host());
        params.add("Connection");
        params.add("Keep-Alive");
        new postSecureRequestTask() {
            @Override
            void callBack(String json) throws JSONException {
                JSONObject main = new JSONObject(json);
                JSONArray cows = main.getJSONArray("value");

                db.delete(TABLE_COW,null,new String[]{});
                db.delete(TABLE_OBSERVATION,null,new String[]{});

                for (int i=0; i<cows.length(); i++) {
                    JSONObject cow = cows.getJSONObject(i);
                    String AnimalNumber = cow.getString("AnimalNumber");
                    Integer animalShortNumber = Integer.parseInt(AnimalNumber.substring(AnimalNumber.length()-5));
                    String cowJSON = cow.toString();
                    ContentValues values = new ContentValues();
                    values.put(FIELD_AnimalShortNumber, animalShortNumber);
                    values.put(FIELD_JSON, cowJSON);
                    long result = db.insert(TABLE_COW, null, values);
                    if (result==-1)
                        Log.d("ERROR","Insert "+animalShortNumber+" failed!");
                    Log.d("GlassCow:Cow", "Id: "+animalShortNumber);
                }
            }
        }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY), params);
    }

    /**
     *
     */
    public void sendObservations(final SQLiteDatabase db, final ArrayList<CowObservation> observations) {
        if (isTokenRequestNeeded())
        {
            new retrieveTokenTask() {
                @Override
                void callBack(String result) {
                    if (!isTokenRequestNeeded())
                        doSendObservations(db, observations);
                }
            }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY));
        }
        else
        {
            doSendObservations(db, observations);
        }
    }

    private void doSendObservations(final SQLiteDatabase db, final ArrayList<CowObservation> observations) {
        Configuration conf = context.getConfiguration();

        //Initialize first observation to sent
        int i;
        for (i = 0; i<observations.size() && observations.get(i).getValue(FIELD_Sent)==Boolean.TRUE; i++);
        if (i >= observations.size())
            return;
        CowObservation obs = observations.get(i);

        //Mark observation as sent to backend database
        ContentValues Values = new ContentValues();
        Values.put(FIELD_Sent, true);
        db.update(TABLE_OBSERVATION, Values, "obs_id=?", new String[]{obs.getObservationId()});
        obs.setValue(FIELD_Sent, true);

        //send observation to backend database
        ArrayList<String> params = new ArrayList<String>();
        params.add(conf.get_Audience() + "CattleWebApi/GoogleGlassesOperations/CreateAnimalObservation?AgriBusinessId=" + conf.get_AgriBusinessId());
        params.add("{\"$id\":\"1\"," +
                "\"AnimalId\":\"" + obs.getAnimalId() + "\"," +
                "\"HerdId\":\"" + obs.getHerdId() + "\"," +
                "\"ObservationTypeId\":\"" + (Integer.valueOf(obs.getTypeId()).intValue()+1) + "\"," +
                "\"ObservationDate\":\"0001-01-01T00:00:00\"," + // "0001-01-01T00:00:00"
                "\"LeftFront\":" + String.valueOf(obs.getValue(FIELD_LeftFront)) + "," +
                "\"RightFront\":" + String.valueOf(obs.getValue(FIELD_RightFront)) + "," +
                "\"LeftBack\":" + String.valueOf(obs.getValue(FIELD_LeftBack)) + "," +
                "\"RightBack\":" + String.valueOf(obs.getValue(FIELD_RightBack)) + "," +
                "\"Clots\":" + String.valueOf(obs.getValue(FIELD_Clots)) + "," +
                "\"VisibleAbnormalities\":" + String.valueOf(obs.getValue(FIELD_VisibleAbnormalities)) + "," +
                "\"Sore\":" + String.valueOf(obs.getValue(FIELD_Sore)) + "," +
                "\"Swollen\":" + String.valueOf(obs.getValue(FIELD_Swollen)) + "," +
                "\"Limp\":" + String.valueOf(obs.getValue(FIELD_Limp)) + "," +
                "\"Mucus\":" + String.valueOf(obs.getValue(FIELD_Mucus)) + "," +
                "\"StandingHeat\":" + String.valueOf(obs.getValue(FIELD_StandingHeat)) + "," +
                "\"BleedOff\":" + String.valueOf(obs.getValue(FIELD_BleedOff)) + "," +
                "\"Mount\":" + String.valueOf(obs.getValue(FIELD_Mount)) + "}");

        params.add("Authorization");
        params.add("SAML " + token);

        params.add("Accept-Encoding");
        params.add("gzip, deflate");

        params.add("Content-Type");
        params.add("application/json; charset=utf-8");

        params.add("Host");
        params.add(conf.get_Host());

        new postSecureRequestTask() {
            @Override
            void callBack(String result) throws JSONException {
                Log.d("Sent obs response", result);
                doSendObservations(db, observations);
            }
        }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY), params);
    }


    //Parser om et dyr er aflivet, slagtning, whatever.
        public void sendDeath(final int herdId, final int cowNumber, final int transferCodeId, final int transferToId, final String date) {
            if (isTokenRequestNeeded())
            {
                new retrieveTokenTask() {
                    @Override
                    void callBack(String result) {
                        if (!isTokenRequestNeeded())
                            sendDeath(herdId, cowNumber, transferCodeId, transferToId, date);
                    }
                }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY));
            }
            else
            {
                sendDeath(herdId, cowNumber, transferCodeId, transferToId, date);
            }
        }
    private void doSendDeath(final int herdId, final int cowNumber, final int transferCodeId, final int transferToId, final String date) {
        Configuration conf = context.getConfiguration();

        //send observation to backend database
        ArrayList<String> params = new ArrayList<String>();
        params.add(conf.get_Audience() + "CattleWebApi/GoogleGlassesOperations/CreateAnimalObservation?AgriBusinessId=" + conf.get_AgriBusinessId());
        params.add("{\"$id\":\"1\"," +
                "\"AnimalId\":\"" + cowNumber + "\"," +
                "\"FromHerdNumber\":\"" + herdId + "\"," +
                "\"TransferDate\":\"" + date + "\"," +
                "\"TransferCodeId\":\"" + transferCodeId + "\"," +
                "\"TransferCause1Id\":\"" + null + "\"," +
                "\"ToHerdNumber\":\"" + 71930 + "}");

        params.add("Authorization");
        params.add("SAML " + token);

        params.add("Accept-Encoding");
        params.add("gzip, deflate");

        params.add("Content-Type");
        params.add("application/json; charset=utf-8");

        params.add("Host");
        params.add(conf.get_Host());

        new postSecureRequestTask() {
            @Override
            void callBack(String result) throws JSONException {
                Log.d("Sent obs response", result);
                doSendDeath(herdId, cowNumber, transferCodeId, transferToId, date);
            }
        }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY), params);
    }
}
