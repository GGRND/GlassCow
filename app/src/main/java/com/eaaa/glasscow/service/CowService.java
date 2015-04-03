package com.eaaa.glasscow.service;

import static com.eaaa.glasscow.service.DatabaseFields.FIELD_ID;
import static com.eaaa.glasscow.service.DatabaseFields.FIELD_JSON;
import static com.eaaa.glasscow.service.DatabaseFields.FIELD_ObservationDate;
import static com.eaaa.glasscow.service.DatabaseFields.TABLE_COW;
import static com.eaaa.glasscow.service.DatabaseFields.TABLE_OBSERVATION;

import org.json.JSONObject;

import com.eaaa.glasscow.Activity_Main;
import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.model.CowObservation;
import com.eaaa.glasscow.model.JSONCowParser;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class CowService {

	private static volatile CowService service;
	private CowPreference prefs;
	private CowDatabase cDB;

	private CowService(Activity_Main context) {
		Log.d("GlassCow:CowService", "Service_Initialized");
		cDB = new CowDatabase(context);
		prefs = new CowPreference(context, System.currentTimeMillis());
	}

	public static CowService getInstance(Activity_Main... context) {
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
        cDB.getWritableDatabase();
        if (cDB.isCowReloadNeeded())
            cDB.loadRemoteCows(Thread.MAX_PRIORITY);
    }

    public void reloadCows(int priority) {
        cDB.loadRemoteCows(priority);
    }

	public void close() {
		cDB.getDb().close();
	}

	// Database calls

	public Cow getLastUsedCow() {
    	Log.d("GlassCow:CowService", "GetLastUsedCow");
		int id = prefs.getCowID();
		if (id>=0) {
			return getCow(id);
		}

		return null;
    }

    public Cow getCow(String id) {
        return getCow(Integer.parseInt(id));
    }

	public Cow getCow(int id) {
		Log.d("GlassCow:CowService", "getCow: " + id);
		String jsonResult = fetchCowJSON(id);
		if (jsonResult != null) {
			prefs.updateCow(id);
			Cow resultCow = JSONCowParser.parseJSONToCow(jsonResult);
            loadObservations(resultCow);
            return resultCow;
		} else {
			return null;
		}
	}

    private void loadObservations(Cow cow) {
        String cow_id = cow.getId();
        Cursor cursorContent = cDB.getDb().rawQuery("SELECT * FROM "+TABLE_OBSERVATION+" WHERE "+FIELD_ID+"=?",new String[]{cow_id});
        cursorContent.moveToFirst();
        cow.setObservations(new ArrayList<CowObservation>());
        while(!cursorContent.isAfterLast()) {
            CowObservation obs = new CowObservation();
            cow.addObservation(obs);
            for (int i=0 ; i<cursorContent.getColumnCount(); i++) {
                String columnName = cursorContent.getColumnName(i);
                String value = cursorContent.getString(i);
                if (columnName.equals(DatabaseFields.FIELD_ID))
                    obs.setId(value);
                else if (columnName.equals(DatabaseFields.FIELD_ObservationDate))
                    obs.setObservationDate(value);
                else if (columnName.equals(DatabaseFields.FIELD_ObservationTypeId))
                    obs.setTypeId(value);
                else
                    if (value!=null && (value.equalsIgnoreCase("yes") || value.equals("1") || value.equalsIgnoreCase("true")))
                        obs.setValue(columnName,true);
            }
            cursorContent.moveToNext();
        }
    }

    public String fetchCowJSON(int id) {
		Log.d("GlassCow:CowService", "fetchCowData");
        String Id = String.valueOf(id);
        while (Id.length()<5)
            Id = "0"+Id;
        Cursor cursorContent = cDB.getDb().query(TABLE_COW, new String[]{FIELD_ID,FIELD_JSON}, FIELD_ID+"=?", new String[]{Id}, null, null, null, null);
		//Cursor cursorContent = cDB.getDb().rawQuery(String.format("SELECT %s FROM %s WHERE %s = " + id, FIELD_JSON, TABLE_COW, FIELD_ID), null);
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
		return cDB.getDb().insert(TABLE_COW, null, values);
	}

    public long insertObservation(CowObservation newObs) {
        String[] fields = DatabaseFields.obsTypeFields.get(new Integer(newObs.getTypeId()).intValue());
        ContentValues values = new ContentValues();
        values.put(DatabaseFields.FIELD_ID, newObs.getId());
        values.put(DatabaseFields.FIELD_ObservationTypeId, newObs.getTypeId());
        for (int f = 0; f < fields.length; f++) {
            values.put(fields[f], newObs.getValue(fields[f]));
        }
        long result = cDB.getDb().insert(TABLE_OBSERVATION, null, values);
        loadObservations(Activity_Main.cow);
        return result;
    }
}
