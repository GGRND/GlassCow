package com.eaaa.glasscow.service;

import static com.eaaa.glasscow.service.DatabaseFields.FIELD_AnimalId;
import static com.eaaa.glasscow.service.DatabaseFields.FIELD_AnimalShortNumber;
import static com.eaaa.glasscow.service.DatabaseFields.FIELD_JSON;
import static com.eaaa.glasscow.service.DatabaseFields.TABLE_COW;
import static com.eaaa.glasscow.service.DatabaseFields.TABLE_OBSERVATION;

import com.eaaa.glasscow.Activity_Main;
import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.model.CowObservation;
import com.eaaa.glasscow.model.JSONCowParser;

import android.content.ContentValues;
import android.database.Cursor;
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
            cDB.loadRemoteCows();
    }

    public void reloadCows() {
        cDB.loadRemoteCows();
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

    public ArrayList<CowObservation> getObservations() {
        ArrayList<CowObservation> result = new ArrayList<CowObservation>();
        Cursor cursorContent = cDB.getDb().rawQuery("SELECT * FROM "+TABLE_OBSERVATION,new String[]{});
        cursorContent.moveToFirst();
        while(!cursorContent.isAfterLast()) {
            CowObservation obs = new CowObservation();
            result.add(obs);
            for (int i=0 ; i<cursorContent.getColumnCount(); i++) {
                String columnName = cursorContent.getColumnName(i);
                String value = cursorContent.getString(i);
                if (columnName.equals(DatabaseFields.FIELD_AnimalId))
                    obs.setAnimalId(value);
                else if (columnName.equals(DatabaseFields.FIELD_ShortAnimalNumber))
                    obs.setShortAnimalNumber(value);
                else if (columnName.equals(DatabaseFields.FIELD_HerdId))
                    obs.setHerdId(value);
                else if (columnName.equals(DatabaseFields.FIELD_ObservationDate))
                    obs.setObservationDate(value);
                else if (columnName.equals(DatabaseFields.FIELD_OBS_ID))
                    obs.setObservationId(value);
                else if (columnName.equals(DatabaseFields.FIELD_ObservationTypeId))
                    obs.setTypeId(value);
                else if (value!=null && (value.equalsIgnoreCase("yes") || value.equals("1") || value.equalsIgnoreCase("true")))
                    obs.setValue(columnName,true);
            }
            cursorContent.moveToNext();
            String[] fields = DatabaseFields.obsTypeFields.get(new Integer(obs.getTypeId()).intValue());
            for (int i = 0; i < fields.length; i++) {
                if (obs.getValue(fields[i])==null)
                    obs.setValue(fields[i],false);
            }
        }
        return result;
    }

    private void loadObservations(Cow cow) {
        String cow_id = cow.getAnimalId();
        Cursor cursorContent = cDB.getDb().rawQuery("SELECT * FROM "+TABLE_OBSERVATION+" WHERE "+ FIELD_AnimalId +"=?",new String[]{cow_id});
        cursorContent.moveToFirst();
        cow.setObservations(new ArrayList<CowObservation>());
        while(!cursorContent.isAfterLast()) {
            CowObservation obs = new CowObservation();
            cow.addObservation(obs);
            obs.setAnimalId(cow.getAnimalId());
            obs.setHerdId(cow.getHerdId());
            for (int i=0 ; i<cursorContent.getColumnCount(); i++) {
                String columnName = cursorContent.getColumnName(i);
                String value = cursorContent.getString(i);
                if (columnName.equals(DatabaseFields.FIELD_AnimalId))
                    obs.setAnimalId(value);
                else if (columnName.equals(DatabaseFields.FIELD_ShortAnimalNumber))
                    obs.setShortAnimalNumber(value);
                else if (columnName.equals(DatabaseFields.FIELD_ObservationDate))
                    obs.setObservationDate(value);
                else if (columnName.equals(DatabaseFields.FIELD_OBS_ID))
                    obs.setObservationId(value);
                else if (columnName.equals(DatabaseFields.FIELD_ObservationTypeId))
                    obs.setTypeId(value);
                else
                    if (value!=null && (value.equalsIgnoreCase("yes") || value.equals("1") || value.equalsIgnoreCase("true")))
                        obs.setValue(columnName,true);
            }
            cursorContent.moveToNext();
            String[] fields = DatabaseFields.obsTypeFields.get(new Integer(obs.getTypeId()).intValue());
            for (int i = 0; i < fields.length; i++) {
                if (obs.getValue(fields[i])==null)
                    obs.setValue(fields[i],false);
            }
        }
    }

    public String fetchCowJSON(int id) {
		Log.d("GlassCow:CowService", "fetchCowData");
        String Id = String.valueOf(id);
        while (Id.length()<5)
            Id = "0"+Id;
        Cursor cursorContent = cDB.getDb().query(TABLE_COW, new String[]{FIELD_AnimalShortNumber,FIELD_JSON}, FIELD_AnimalShortNumber +"=?", new String[]{Id}, null, null, null, null);
		Log.d("GlassCow:CowService", "fetchCowData2");
		if (cursorContent.moveToFirst()) {
			Log.d("GlassCow:CowService", "fetchCowData3");
			return cursorContent.getString(cursorContent.getColumnIndex(FIELD_JSON));
		} else {
			Log.d("GlassCow:CowService", "fetchCowData4");
			return null;
		}
	}

    public long insertObservation(CowObservation newObs) {
        String[] fields = DatabaseFields.obsTypeFields.get(new Integer(newObs.getTypeId()).intValue());
        ContentValues values = new ContentValues();
        values.put(DatabaseFields.FIELD_AnimalId, newObs.getAnimalId());
        values.put(DatabaseFields.FIELD_ShortAnimalNumber, newObs.getShortAnimalNumber());
        values.put(DatabaseFields.FIELD_HerdId, newObs.getHerdId());
        values.put(DatabaseFields.FIELD_ObservationTypeId, newObs.getTypeId());
        for (int f = 0; f < fields.length; f++) {
            String field = fields[f];
            Boolean val = newObs.getValue(fields[f]);
            values.put(field, val);
        }
        long result = cDB.getDb().insert(TABLE_OBSERVATION, null, values);
        loadObservations(Activity_Main.cow);
        cDB.sentObservationsToRemote(Activity_Main.cow.getObservations());
        return result;
    }
}
