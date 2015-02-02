package com.eaaa.glasscow.model;

import java.text.ParseException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.eaaa.glasscow.model.CowValue.RingColor;
import com.eaaa.glasscow.tools.CowMath;

public class JSONCowParser {

	public static Cow parseJSONToCow(String json) {
		Log.d("GlassCow:JSONCowParser", "parseJSONToCow_start");
		Cow cow = new Cow();
		try {
			JSONObject main = new JSONObject(json);
			JSONArray array = main.getJSONArray("value");
			main = array.getJSONObject(0);

			cow.setFullID(main.getString("Id"));
			cow.setId(Integer.parseInt(cow.getFullID().substring(cow.getFullID().length() - 5)));

			addInformation(main, cow);
			addHealth(main, cow);
			addReproduction(main, cow);
			Log.d("GlassCow:JSONCowParser", "parseJSONToCow_done");
		} catch (JSONException e) {
			Log.d("GlassCow:JSONCowParser", "parseJSONToCow_json_exception");
			cow = null;
			e.printStackTrace();
		} catch (ParseException e) {
			Log.d("GlassCow:JSONCowParser", "parseJSONToCow_parse_exception");
			cow = null;
			e.printStackTrace();
		}

		return cow;
	}

	private static void addInformation(JSONObject main, Cow cow) throws JSONException, ParseException {
		// 1: Date of Birth
		cow.addInformation(new CowValue("Date of birth", "" + CowMath.daysSince(main.getString("DateOfBirth")) + " days old")); 

		// 2: Status
		JSONObject animalStatus = main.getJSONObject("AnimalStatus");
		cow.addInformation(new CowValue("Status", animalStatus.get("AnimalStatusText").toString()));

		// 3: Culling
		cow.addInformation(new CowValue("Culling", main.get("CullingText").toString()));

		// 4: KgECMDay
		cow.addInformation(new CowValue("KgECMDay", main.get("KgECMLastTestDay").toString()));

		// 5: ECM12M
		cow.addInformation(new CowValue("KgECM12Months", main.get("KgECMLast12Months").toString()));
	}

	private static void addHealth(JSONObject main, Cow cow) throws JSONException, ParseException {
		JSONArray tempHealthEvents = main.getJSONArray("CowIndexCardMobileHealths");
		for (int i = 0; i < tempHealthEvents.length(); i++) {
			JSONObject temp = tempHealthEvents.getJSONObject(i);
			cow.addHealthEvent(new CowValue(temp.getString("Text"), "" + CowMath.daysSince(temp.get("IllnessDate").toString()) + " days ago"));
		}

		// 1: ParaTB
		cow.addHealth(new CowValue("ParaTB", main.get("ParaTBLatestAntibody").toString()));

		// 2: CellCount
		cow.addHealth(new CowValue("Cell Count", main.get("SCCLastTestDay").toString()));

		// 3: Mastitis
		List<CowValue> healthEvents = cow.getHealthEvents();
		CowValue temp = findEvent("Yverbetændelse", healthEvents);
		cow.addHealth(new CowValue("Mastitis", (temp != null ? temp.getValue() : "NaN")));

		// 4: Hoof trimming
		temp = findEvent("Klovbeskæring", healthEvents);
		cow.addHealth(new CowValue("Hoof trimming", (temp != null ? temp.getValue() : "NaN")));
	}

	private static void addReproduction(JSONObject main, Cow cow) throws JSONException, ParseException {
		JSONArray tempReproductionEvents = main.getJSONArray("CowIndexCardMobileReproductions");
		for (int i = 0; i < tempReproductionEvents.length(); i++) {
			JSONObject temp = tempReproductionEvents.getJSONObject(i);
			cow.addReproductionEvent(new CowValue(temp.getString("Text"), "" + CowMath.daysSince(temp.get("EventDate").toString()) + " days ago"));
		}

		// 1 : pregnant
		  cow.addReproduction(new CowValue("Pregnant", (main.getBoolean("PregnancyStatus") ? "Yes" : "No")));

		// 2: Insemination
		List<CowValue> reproductionEvents = cow.getReproductionEvents();
		CowValue temp = findEvent("Inseminering", reproductionEvents);
		cow.addReproduction(new CowValue("Insemination", (temp != null ? temp.getValue() : "NaN")));

		// 3: Dry off
		int calveNumber = main.getInt("LastCalvingNumber");
		int tempInt = CowMath.calculateDryOff(main.get("ExpectedCalvingDate").toString(), calveNumber);
		cow.addReproduction(new CowValue("Dry Off", "in " + tempInt + " days", (tempInt < 0 ? RingColor.RED : RingColor.GREEN)));

		// 4: Calving
		cow.addReproduction(new CowValue("Calving", "in " + CowMath.daysSinceAbsolute(main.get("ExpectedCalvingDate").toString()) + " days"));

		// 5: Latest Calving
		cow.addReproduction(new CowValue("Last Calving", CowMath.daysSince(main.get("LastCalvingDate").toString()) + " days ago"));

		// 6: calve number
		cow.addReproduction(new CowValue("Calve Number", "" + calveNumber));

		// 7: Lactation
		// cow.addReproduction(new CowValue("Lactation", "" + main.get("LactationValue")));
	}

	private static CowValue findEvent(String target, List<CowValue> events) {
		int i = 0;
		while (i < events.size()) {
			CowValue temp = events.get(i);
			if (temp.getKey().equals(target)) {
				return temp;
			}
			i++;
		}
		return null;
	}
}
