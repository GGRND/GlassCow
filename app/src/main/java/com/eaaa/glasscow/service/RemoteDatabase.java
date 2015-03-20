package com.eaaa.glasscow.service;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.eaaa.glasscow.Activity_Main;

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

import static com.eaaa.glasscow.service.DatabaseFields.FIELD_ID;
import static com.eaaa.glasscow.service.DatabaseFields.FIELD_JSON;
import static com.eaaa.glasscow.service.DatabaseFields.TABLE_COW;

/**
 * Created by morten on 17/03/15.
 */
public class RemoteDatabase {

    private static RemoteDatabase instance = null;
    private static String token = null;

    private static abstract class postSecureRequestTask extends AsyncTask<ArrayList<String>, Void, String> {

        abstract void callBack(String result);

        @Override
        protected String doInBackground(ArrayList<String>... parameters) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return postSecureRequest(parameters[0]);
            } catch (IOException e) {
                return "Unable execute postSecureRequestTask: "+e.getMessage();
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            callBack(result);
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
                throw new IOException("Non-2xx status code: " + responseCode);
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
            Activity_Main.Configuration conf = context.getConfiguration();

            //Add URL to params
            params.add(conf.Endpoint);

            //Add Body to params
            String template = conf.rst_template;
            String rst = String.format(template, conf.Endpoint, conf.Username, conf.Password, conf.Audience);
            params.add(rst);

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
            String result = tokenResponseBody.substring(startPosSAML, endPosSAML);

            //return SAML token
            RemoteDatabase.token = result;

            callBack(result);
        }
    }


        private static Activity_Main context;


    public static RemoteDatabase getInstance(Activity_Main... context) {
        if (RemoteDatabase.instance==null) {
            RemoteDatabase.instance = new RemoteDatabase(context[0]);
        }
        return RemoteDatabase.instance;
    }

   private RemoteDatabase(Activity_Main context) {
       RemoteDatabase.context = context;

        //initialize logging
        //File MyFile = new File(System.getProperty("user.dir")+File.separator+"conf"+File.separator+"log4j.properties");
        //PropertyConfigurator.configure(MyFile.getAbsolutePath());

        //get saml token
        //updateCattleDatabase();
   }


    public void updateCattleDatabase(final SQLiteDatabase db) {

        if (token==null)
        {
            new retrieveTokenTask() {
                @Override
                void callBack(String result) {
                    doUpdateCattleDatabase(db);
                }
            }.execute();
        }
        else
        {
            doUpdateCattleDatabase(db);
        }
    }

    private void doUpdateCattleDatabase(final SQLiteDatabase db)
    {
        //get cattle info
        ArrayList<String> params = new ArrayList<String>();
        params.add("https://devtest-dcf-odata.vfltest.dk/DCFOData/CattleWebApi/GoogleGlassesOperations/GetCowIndexCardMobilesForAgriBusiness?AgriBusinessId=54581");
        params.add("{\"$id\":\"1\",\"AgriBusinessId\":\"54581\"}");
        params.add("Authorization");
        params.add("SAML " + token);
        params.add("Content-Type");
        params.add("application/json");
        params.add("Host");
        params.add("devtest-dcf-odata.vfltest.dk");
        params.add("Connection");
        params.add("Keep-Alive");
        new postSecureRequestTask() {
            @Override
            void callBack(String json) {
                //Log.v("DEBUG", result);

                try {
                    JSONObject main = new JSONObject(json);
                } catch (JSONException e) {
                    Log.d("DEBUG",e.getMessage());
                }
                //JSONArray array = main.getJSONArray("value");
                //main = array.getJSONObject(0);

                db.execSQL("DROP TABLE IF EXISTS " + TABLE_COW);


                //db.insert(TABLE_COW, null, values);
            }
        }.execute(params);
    }





}
