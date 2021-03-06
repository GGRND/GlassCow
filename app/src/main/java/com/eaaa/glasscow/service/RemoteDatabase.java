package com.eaaa.glasscow.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.eaaa.glasscow.Activity_Main;
import com.eaaa.glasscow.Configuration;
import com.google.android.glass.media.Sounds;

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


    private static int responseCode;
    private static boolean tokenOrResponse = false;
    private static boolean isACowBeingTransferred = false;
    private static Context currentAppContext;

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

            if (isACowBeingTransferred) {
                if (tokenOrResponse) {
                    AudioManager audio = (AudioManager) currentAppContext.getSystemService(Context.AUDIO_SERVICE);

                    //TODO FEEDBACK FOR DE FLESTE TYPER RESPONSES
                    if (responseCode != 200) {
                        audio.playSoundEffect(Sounds.ERROR);
                        Toast.makeText(currentAppContext, "Something went wrong, response_code: " + responseCode, Toast.LENGTH_LONG).show();
                    }
                    else {
                        audio.playSoundEffect(Sounds.SUCCESS);
                        Toast.makeText(currentAppContext, "The cow was successfully removed.", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }

        //Parameters: String url, String body, HeaderName1, HeaderValue1, HeaderName2, HeaderValue2 ...
        protected String postSecureRequest(ArrayList<String> params) throws IOException {
            StringBuffer response = new StringBuffer();

            //Manages whether it's a token or something else that's being handled
            if (isACowBeingTransferred) {
                if (tokenOrResponse == false) {
                    tokenOrResponse = true;
                }
            }

            //Test if connection is possible
            ConnectivityManager connMgr = (ConnectivityManager)
                    RemoteDatabase.context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo==null || !networkInfo.isConnected())
                throw new IOException("No network available");

            //Setup and execute request
            URL obj = new URL(params.get(0));
            HttpsURLConnection con;
            try {
                con = (HttpsURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                for (int i=2; i<params.size(); i=i+2) {
                    con.setRequestProperty(params.get(i), params.get(i+1));
                }
                con.setDoOutput(true);
                OutputStream outstream;
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

            //Should make it possible to debug this method
            //android.os.Debug.waitForDebugger();

            //Test http response code
            responseCode = con.getResponseCode();
            Log.d("ResponseCode: ", String.valueOf(responseCode));


            if (responseCode / 100 != 2)
            {
                throw new IOException("Non-2xx status code: " + responseCode + " " + con.getResponseMessage());
            }

            //Read response
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "UTF-8"));
            String inputLine;
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
    public void sendObservations() {
        if (isTokenRequestNeeded())
        {
            new retrieveTokenTask() {
                @Override
                void callBack(String result) {
                    if (!isTokenRequestNeeded())
                        doSendObservations();
                }
            }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY));
        }
        else
        {
            doSendObservations();
        }
    }

    private void doSendObservations() {
        Configuration conf = context.getConfiguration();
/*
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
*/
        //send observation to backend database
        ArrayList<String> params = new ArrayList<String>();
        params.add(conf.get_Audience() + "CattleWebApi/GoogleGlassesOperations/CreateAnimalObservation?AgriBusinessId=" + conf.get_AgriBusinessId());
        params.add("{\"$id\":\"1\"," +
                "\"AnimalId\":\"" + 1009742025 + "\"," +
                "\"HerdId\":\"" + 6362512 + "\"," +
                "\"ObservationTypeId\":\"" + 1 + "\"," +
                "\"ObservationDate\":\"0001-01-01T00:00:00\"," + // "0001-01-01T00:00:00"
                "\"LeftFront\":" + true + "," +
                "\"RightFront\":" + true + "," +
                "\"LeftBack\":" + false + "," +
                "\"RightBack\":" + false + "," +
                "\"Clots\":" + null + "," +
                "\"VisibleAbnormalities\":" + null + "," +
                "\"Sore\":" + null + "," +
                "\"Swollen\":" + null + "," +
                "\"Limp\":" + null + "," +
                "\"Mucus\":" + null + "," +
                "\"StandingHeat\":" + null + "," +
                "\"BleedOff\":" + null + "," +
                "\"Mount\":" + null + "}");

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
                doSendObservations();
            }
        }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY), params);
    }


    /**
     * Handles the database things for cows if they are: dead, killed or slaughtered.
     */
    public void transferCow(final Integer fromHerdId, final Integer toHerdId, final Long animalNumber, final long transferCodeId,
                            final String date, final Context appContext) {
        this.isACowBeingTransferred = true;
        currentAppContext = appContext;

        if (isTokenRequestNeeded())
        {
            new retrieveTokenTask() {
                @Override
                void callBack(String result) {
                    if (!isTokenRequestNeeded())
                        doTransferCow(fromHerdId, toHerdId, animalNumber, transferCodeId, date);
                }
            }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY));
        }
        else
        {
            doTransferCow(fromHerdId, toHerdId, animalNumber, transferCodeId, date);
        }
    }
    private void doTransferCow(final Integer fromHerdId, final Integer toHerdId, final Long animalNumber, final long transferCodeId, final String date) {
        //Should make it possible to debug this method
        //android.os.Debug.waitForDebugger();

        Configuration conf = context.getConfiguration();

        //send observation to backend database
        ArrayList<String> params = new ArrayList<String>();
        params.add(conf.get_Audience() + "CattleWebApi/AnimalTransferPublicOperations/UpdateAnimalTransfersPublic?AgriBusinessId=" + conf.get_AgriBusinessId());
        params.add("{\"$id\":\"1\"," +
                "\"AnimalId\":\"" + animalNumber + "\"," +
                "\"FromHerdNumber\":\"" + fromHerdId + "\"," +
                "\"TransferDate\":\"" + date + "\"," +
                "\"TransferCodeId\":\"" + transferCodeId + "\"," +
                "\"TransferCause1Id\":\"" + null + "\"," +
                "\"ToHerdNumber\":\"" + toHerdId + "}");

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
                //Log.d("Sent obs response", result);

            }
        }.executeOnExecutor(new PriorityExecutor(Thread.NORM_PRIORITY), params);
    }
}
