package com.eaaa.glasscow.service;

import static com.eaaa.glasscow.service.DatabaseFields.FIELD_ID;
import static com.eaaa.glasscow.service.DatabaseFields.FIELD_JSON;
import static com.eaaa.glasscow.service.DatabaseFields.TABLE_COW;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONObject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.eaaa.glasscow.Activity_Main;

public class CowDatabase extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	public static final String DATABASE_NAME = "Cows";
    private final Activity_Main ctx;
    private SQLiteDatabase db = null;
    private RemoteDatabase remoteDatabase;

    public CowDatabase(Activity_Main context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.ctx = context;
	}

	@Override
	public void onCreate(SQLiteDatabase SQLdatabase) {
        db = SQLdatabase;

		db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT );", TABLE_COW,	FIELD_ID, FIELD_JSON));
		// db.execSQL(String.format("CREATE UNIQUE INDEX IF NOT EXISTS %s.%s ON %s (%s)", DATABASE_NAME, "CowId", TABLE_COW, FIELD_ID));
		Log.d("GlassCow:CowDatabase", "Step1");

        loadRemoteCows();
	}

    public void loadRemoteCows() {
        getWritableDatabase();
        RemoteDatabase.getInstance().updateCattleDatabase(db);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

	public void createSampleCows() {
        getWritableDatabase();
		Log.d("GlassCow:CowDatabase", "Step2");
		CreateCows cc = new CreateCows(db);
		cc.createCow1();
		Log.d("GlassCow:CowDatabase", "Step3");
		cc.createCow2();
		cc.createCow3();
		cc.createCow4();
		cc.createCow5();
		cc.createCow6();
		cc.createCow7();
		cc.createCow8();
		cc.createCow9();
 	}

	public boolean getCreated() {
        return db!=null;
	}

    @Override
    public SQLiteDatabase getWritableDatabase() {
        db = super.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COW);
        db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT );", TABLE_COW,	FIELD_ID, FIELD_JSON));
        this.remoteDatabase = RemoteDatabase.getInstance(this.ctx);

        return db;
    }

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COW);
	}

	@SuppressWarnings("resource")
	public String[] JsonParser(File file) throws Exception {
		InputStream is = new FileInputStream(file);
		String jsonTxt = is.toString();
		JSONObject json = new JSONObject(jsonTxt);
		return new String[] { json.get("id").toString(), json.toString() };
	}
}
