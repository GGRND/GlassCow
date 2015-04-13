package com.eaaa.glasscow;

import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by morten on 13/04/15.
 */

public class Configuration {

    private static Configuration instance = null;

    private final static String PREF_NAME = "Configuration";
    private SharedPreferences prefs  = null;
    private Activity_Main context = null;

    private final static String rst_template_key = "rst_template";
    private String rst_template;

    private final static String Endpoint_key = "Endpoint";
    private String Endpoint;

    private final static String Username_key = "Username";
    private String Username;

    private final static String Password_key = "Password";
    private String Password;

    private final static String AgriBusinessId_key = "AgriBusinessId";
    private String AgriBusinessId;

    private final static String Host_key = "Host";
    private String Host;

    private final static String Audience_key = "Audience";
    private String Audience;

    public String get_rst_template() {
        return rst_template;
    };
    public String get_Endpoint() {
        return Endpoint;
    };
    public String get_Username() {
        return Username;
    };
    public String get_Password() {
        return Password;
    };
    public String get_Audience() {
        return Audience;
    };
    public String get_AgriBusinessId() {
        return AgriBusinessId;
    };
    public String get_Host() {
        return Host;
    };

    private void LoadDefaultPreferences() {
        this.rst_template = context.readRawTextFile(R.raw.rst_template);
        this.AgriBusinessId = "54581";

        //devtest system
        this.Username = "googleglas";
        this.Password = "63625";
        this.Endpoint = "https://si-idp.vfltest.dk/adfs/services/trust/13/usernamemixed";
        this.Host = "devtest-dcf-odata.vfltest.dk";
        this.Audience = "https://devtest-dcf-odata.vfltest.dk/DCFOData/";

        //production system
        this.Username = "XXXXX";
        this.Password = "YYYYY";
        this.Endpoint = "https://idp.dlbr.dk/adfs/services/trust/13/usernamemixed";
        this.Host = "prod-dcf-odata.dlbr.dk";
        this.Audience = "https://prod-dcf-odata.dlbr.dk/DCFOData/";
    }

    public void LoadSharedPreferences()
    {
        String rst = prefs.getString(rst_template_key,null);
        if (rst!=null)
            rst_template=rst;

        String end = prefs.getString(Endpoint_key,null);
        if (end!=null)
            Endpoint=end;

        String usr = prefs.getString(Username_key,null);
        if (usr!=null)
            Username=usr;

        String pwd = prefs.getString(Password_key,null);
        if (pwd!=null)
            Password=pwd;

        String aud = prefs.getString(Audience_key,null);
        if (aud!=null)
            Audience=aud;

        String agri = prefs.getString(AgriBusinessId_key,null);
        if (agri!=null)
            AgriBusinessId=agri;

        String host = prefs.getString(Host_key,null);
        if (host!=null)
            Host=host;
    }

    //Sample:
    //{Username:"googleglas",Password:"63625",Endpoint:"https://si-idp.vfltest.dk/adfs/services/trust/13/usernamemixed",Audience:"https://devtest-dcf-odata.vfltest.dk/DCFOData/",AgriBusinessId:"54581",Host:"devtest-dcf-odata.vfltest.dk"}
    public void SetConfiguration(String json)
    {
        SharedPreferences.Editor editor = prefs.edit();

        //Load field values retrieved from JSON
        if (json!=null && !json.isEmpty()) {
            try {
                JSONObject config = new JSONObject(json);

                if (config.has(rst_template_key)) {
                    String rst = config.getString(rst_template_key);
                    rst_template = rst;
                    editor.putString(rst_template_key, rst);
                }

                if (config.has(Endpoint_key)) {
                    String end = config.getString(Endpoint_key);
                    Endpoint = end;
                    editor.putString(Endpoint_key, end);
                }

                if (config.has(Username_key)) {
                    String usr = config.getString(Username_key);
                    Username = usr;
                    editor.putString(Username_key, usr);
                }

                if (config.has(Password_key)) {
                    String pwd = config.getString(Password_key);
                    Password = pwd;
                    editor.putString(Password_key, pwd);
                }

                if (config.has(Audience_key)) {
                    String aud = config.getString(Audience_key);
                    Audience = aud;
                    editor.putString(Audience_key, aud);
                }

                if (config.has(AgriBusinessId_key)) {
                    String agri = config.getString(AgriBusinessId_key);
                    AgriBusinessId = agri;
                    editor.putString(AgriBusinessId_key, agri);
                }

                if (config.has(Host_key)) {
                    String host = config.getString(Host_key);
                    Host = host;
                    editor.putString(Host_key, host);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        //Persist changes in shared preferences
        editor.commit();

        //Load configuration
        LoadSharedPreferences();
    }

    private Configuration(Activity_Main ctx) {
        this.context = ctx;
        this.prefs = this.context.getSharedPreferences(PREF_NAME, context.MODE_MULTI_PROCESS);

        LoadDefaultPreferences();
        LoadSharedPreferences();
    }

    static public Configuration get_Instance(Activity_Main ctx) {
        if (instance==null)
            instance =  new Configuration(ctx);
        return instance;
    }
}
