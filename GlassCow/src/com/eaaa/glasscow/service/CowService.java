package com.eaaa.glasscow.service;

import static com.eaaa.glasscow.service.DatabaseFields.FIELD_ID;
import static com.eaaa.glasscow.service.DatabaseFields.FIELD_JSON;
import static com.eaaa.glasscow.service.DatabaseFields.TABLE_COW;

import org.json.JSONObject;

import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.model.JSONCowParser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CowService {

	private static volatile CowService service;

	private CowPreference prefs;

	private SQLiteDatabase db;
	private CowDatabase cDB;

	private CowService(Context context) {
		Log.d("GlassCow:CowService", "Service_Initialized");
		cDB = new CowDatabase(context);
		prefs = new CowPreference(context, System.currentTimeMillis());
	}

	public static CowService getInstance(Context... context) {
		if (service == null) {
			synchronized (CowService.class) {
				if (service == null) {
					service = new CowService(context[0]);
				}
			}
		}

		return service;
	}

	public void open() {
		db = cDB.getWritableDatabase();
		if (cDB.getCreated()) {
			cDB.createCows(db);
		}
	}

	public void close() {
		db.close();
	}

	// Database calls

	public Cow getLastUsedCow() {
		Log.d("GlassCow:CowService", "GetLastUsedCow");
		int id = prefs.getCowID();
		if (id != -1) {
			return getCow(id);
		}
		return null;
	}

	public Cow getCow(int id) {
		Log.d("GlassCow:CowService", "getCow: " + id);
		String result = fetchCowData(id);
		if (result != null) {
			prefs.updateCow(id);
			return JSONCowParser.parseJSONToCow(result);
		} else {
			return null;
		}
	}

	public String fetchCowData(int id) {
		Log.d("GlassCow:CowService", "fetchCowData");
		Cursor cursorContent = db.rawQuery(String.format("SELECT %s FROM %s WHERE %s = " + id, FIELD_JSON, TABLE_COW, FIELD_ID), null);
		Log.d("GlassCow:CowService", "fetchCowData2");
		if (cursorContent.moveToFirst()) {
			Log.d("GlassCow:CowService", "fetchCowData3");
			return cursorContent.getString(cursorContent.getColumnIndex(FIELD_JSON));
		} else {
			Log.d("GlassCow:CowService", "fetchCowData4");
			return null;
		}
	}

	public long insertCow(String cowId, JSONObject cow) {
		ContentValues values = new ContentValues();
		values.put(FIELD_ID, cowId);
		values.put(FIELD_JSON, cow.toString());
		return db.insert(TABLE_COW, null, values);
	}

}
