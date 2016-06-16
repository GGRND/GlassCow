package com.eaaa.glasscow.model;

import java.text.ParseException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.eaaa.glasscow.model.*;
import com.eaaa.glasscow.model.CowValue;
import com.eaaa.glasscow.model.CowValue.RingColor;
import com.eaaa.glasscow.tools.CowMath;

public class JSONCowParser {

	public static com.eaaa.glasscow.model.Cow parseJSONToCow(String json) {
		Log.d("GlassCow:JSONCowParser", "parseJSONToCow_start");
		com.eaaa.glasscow.model.Cow cow = new com.eaaa.glasscow.model.Cow();
		try {
			JSONObject main = new JSONObject(json);

            /* Animal number */
            String fullNumber = main.getString("AnimalNumber");
			cow.setFullNumber(fullNumber);
            String shortNumber = fullNumber.substring(fullNumber.length() - 5);
			cow.setShortNumber(shortNumber);

            /* HerdId */
            String herdId = main.getString("HerdId");
            cow.setHerdId(herdId);

            /* AnimalId */
            String animalId = main.getString("AnimalId");
            cow.setAnimalId(animalId);

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

	private static void addInformation(JSONObject main, com.eaaa.glasscow.model.Cow cow) throws JSONException, ParseException {
		// 1: Date of Birth
		cow.addInformation(new com.eaaa.glasscow.model.CowValue("Date of birth", "" + CowMath.daysSince(main.getString("DateOfBirth")) + " days old"));

		// 2: Status
		JSONObject animalStatus = main.getJSONObject("AnimalStatus");
		cow.addInformation(new com.eaaa.glasscow.model.CowValue("Status", animalStatus.get("AnimalStatusText").toString()));

		// 3: Culling
		cow.addInformation(new com.eaaa.glasscow.model.CowValue("Culling", main.get("CullingText").toString()));

		// 4: KgECMDay
		cow.addInformation(new com.eaaa.glasscow.model.CowValue("KgECMDay", main.get("KgECMLastTestDay").toString()));

		// 5: ECM12M
		cow.addInformation(new com.eaaa.glasscow.model.CowValue("KgECM12Months", main.get("KgECMLast12Months").toString()));
	}

	private static void addHealth(JSONObject main, com.eaaa.glasscow.model.Cow cow) throws JSONException, ParseException {
		JSONArray tempHealthEvents = main.getJSONArray("CowIndexCardMobileHealths");
		for (int i = 0; i < tempHealthEvents.length(); i++) {
			JSONObject temp = tempHealthEvents.getJSONObject(i);
			cow.addHealthEvent(new com.eaaa.glasscow.model.CowValue(temp.getString("Text"), "" + CowMath.daysSince(temp.get("IllnessDate").toString()) + " days ago"));
		}

		// 1: ParaTB
		cow.addHealth(new com.eaaa.glasscow.model.CowValue("ParaTB", main.get("ParaTBLatestAntibody").toString()));

		// 2: CellCount
		cow.addHealth(new com.eaaa.glasscow.model.CowValue("Cell Count", main.get("SCCLastTestDay").toString()));

		// 3: Mastitis
		List<com.eaaa.glasscow.model.CowValue> healthEvents = cow.getHealthEvents();
		com.eaaa.glasscow.model.CowValue temp = findEvent("Yverbetændelse", healthEvents);
		cow.addHealth(new com.eaaa.glasscow.model.CowValue("Mastitis", (temp != null ? temp.getValue() : "NaN")));

		// 4: Hoof trimming
		temp = findEvent("Klovbeskæring", healthEvents);
		cow.addHealth(new com.eaaa.glasscow.model.CowValue("Hoof trimming", (temp != null ? temp.getValue() : "NaN")));
	}

	private static void addReproduction(JSONObject main, com.eaaa.glasscow.model.Cow cow) throws JSONException, ParseException {
		JSONArray tempReproductionEvents = main.getJSONArray("CowIndexCardMobileReproductions");
		for (int i = 0; i < tempReproductionEvents.length(); i++) {
			JSONObject temp = tempReproductionEvents.getJSONObject(i);
			cow.addReproductionEvent(new com.eaaa.glasscow.model.CowValue(temp.getString("Text"), "" + CowMath.daysSince(temp.get("EventDate").toString()) + " days ago"));
		}


		// 1 : pregnant
        cow.addReproduction(new com.eaaa.glasscow.model.CowValue("Pregnant", (main.getString("PregnancyStatus").equalsIgnoreCase("true") ? "Yes" : "No")));

		// 2: Insemination
		List<com.eaaa.glasscow.model.CowValue> reproductionEvents = cow.getReproductionEvents();
		com.eaaa.glasscow.model.CowValue temp = findEvent("Inseminering", reproductionEvents);
		cow.addReproduction(new com.eaaa.glasscow.model.CowValue("Insemination", (temp != null ? temp.getValue() : "NaN")));

		// 3: Dry off
        String calvingNumberStr = main.getString("LastCalvingNumber");
		int calveNumber = parseInt(calvingNumberStr);
        int tempInt = CowMath.calculateDryOff(main.get("ExpectedCalvingDate").toString(), calveNumber);
		cow.addReproduction(new com.eaaa.glasscow.model.CowValue("Dry Off", "in " + tempInt + " days", (tempInt < 0 ? RingColor.RED : RingColor.GREEN)));

		// 4: Calving
		cow.addReproduction(new com.eaaa.glasscow.model.CowValue("Calving", "in " + CowMath.daysSinceAbsolute(main.get("ExpectedCalvingDate").toString()) + " days"));

		// 5: Latest Calving
		cow.addReproduction(new com.eaaa.glasscow.model.CowValue("Last Calving", CowMath.daysSince(main.get("LastCalvingDate").toString()) + " days ago"));

		// 6: calve number
		cow.addReproduction(new com.eaaa.glasscow.model.CowValue("Calve Number", "" + calveNumber));

		// 7: Lactation
		// cow.addReproduction(new CowValue("Lactation", "" + main.get("LactationValue")));
	}

    private static int parseInt(String input) {
        int result=0;
        try {
            result = Integer.parseInt(input);
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

	private static com.eaaa.glasscow.model.CowValue findEvent(String target, List<com.eaaa.glasscow.model.CowValue> events) {
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
