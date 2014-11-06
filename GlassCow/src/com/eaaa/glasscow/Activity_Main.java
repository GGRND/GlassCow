package com.eaaa.glasscow;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.eaaa.glasscow.AsyncCowDataChange.AsyncCowResponse;
import com.eaaa.glasscow.Screen_CowData.DataType;
import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.service.CowService;
import com.eaaa.glasscow.tools.CowScrollViewAdapter;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollView;

public class Activity_Main extends Activity implements AsyncCowResponse {

	private static final int SPEECH_REQUEST = 0;

	private boolean voiceEnabled = true;
	public CowScrollViewAdapter scrollAdapter;
	public CardScrollView scrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		CowService.getInstance(this).open();

		scrollAdapter = new CowScrollViewAdapter(createViews());
		scrollView = new CardScrollView(this);
		scrollView.setAdapter(scrollAdapter);
		setContentView(scrollView);
		// TODO Touch commands
		
		Log.d("GlassCow:Main", "Activity_Start");
		Cow cow = CowService.getInstance().getLastUsedCow();
		if(cow != null){
			asyncCowResponse(cow);
		} else {
			identifyCowWithVoice();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		scrollView.activate();
	}

	@Override
	protected void onPause() {
		super.onPause();
		scrollView.deactivate();
	}

	private List<Screen_CowData> createViews() {
		List<Screen_CowData> views = new ArrayList<Screen_CowData>(3);

		views.add(new Screen_CowData(this, DataType.INFORMATION));
		views.add(new Screen_CowData(this, DataType.HEALTH));
		views.add(new Screen_CowData(this, DataType.REPRODUCTION));
		
		return views;
	}

	public void identifyCowWithVoice(){
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		startActivityForResult(intent, SPEECH_REQUEST);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			Log.d("GlassCow:Main", "Handling Voice Input");
			List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);
			
			int input = validateString(spokenText);
			if (input != -1) {
				Log.d("GlassCow:Main", "Cow_update: NEW COW ID: " + input);
				new AsyncCowDataChange(Activity_Main.this, input).execute();
			} else {
				Log.d("GlassCow:Main", "Cow_Updatge: Invalid Input");
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private int validateString(String text) {
		text.replace("\\s", "");
		if (text.matches("[0-9]{4,5}")) {
			return Integer.parseInt(text);
		}
		return -1;
	}

	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
			Log.d("GlassCow:Main", "onCreatePanelMenu" + menu);
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		return super.onCreatePanelMenu(featureId, menu);
	}

	@Override
	public boolean onPreparePanel(int featureId, View view, Menu menu) {
		Log.d("GlassCow:Main", "onPreparePanel");
		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
			MenuHandler.updateMenu(this, menu, (Screen_CowData)scrollAdapter.getItem(scrollView.getSelectedItemPosition()));
			return voiceEnabled;
		}
		return super.onPreparePanel(featureId, view, menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d("GlassCow:Main", "onMenuItemSelected");
		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
			MenuHandler.onMainMenuItemSelected(this, (Screen_CowData)scrollAdapter.getItem(scrollView.getSelectedItemPosition()), item.getItemId());
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void asyncCowResponse(Cow cow) {
		if (cow != null) {
			for (int i = 0; i < scrollAdapter.getCount(); i++) {
				((Screen_CowData) scrollAdapter.getItem(i)).updateCow(cow);
			}
			Log.d("GlassCow:Main", "Cow_Update: Success");
			scrollAdapter.notifyDataSetChanged();
		} else {
			Log.d("GlassCow:Main", "Cow_Update: Failed");
			//TODO ? 
			identifyCowWithVoice();
		}
	}
}
