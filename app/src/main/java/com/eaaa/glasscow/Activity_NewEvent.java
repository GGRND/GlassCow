package com.eaaa.glasscow;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.eaaa.glasscow.*;
import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.model.CowValue;
import com.eaaa.glasscow.service.CowService;
import com.eaaa.glasscow.tools.CowMath;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;

public class Activity_NewEvent extends Activity implements GestureDetector.BaseListener {

	private static final int MENU_BACK = 0;
	private static final int MENU_CALVING = 1;

	private int id;
	private String title;
	private List<CowValue> events;

	private GestureDetector gDetector; // TODO slet?

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.new_event);
		unpackBundle();
		initElements();
		
		gDetector = new GestureDetector(this).setBaseListener(this);
	}

	private void unpackBundle() {
		Log.d("GlassCow:NewEvent", "***unpackingBundle***");
		Bundle bundle = getIntent().getExtras();
		this.id = bundle.getInt("Id");
		this.title = getString(bundle.getInt("Title"));

//		Cow cow = CowService.getInstance().getCow(id);
		Cow cow = com.eaaa.glasscow.Activity_Main.cow;

		switch (bundle.getInt("Title")) {
		case R.string.information:
			// Shouldn't happen
			break;
		case R.string.health:
			this.events = cow.getHealthEvents();
			break;
		case R.string.reproduction:
			this.events = cow.getReproductionEvents();
			break;
		}
	}

	private void initElements() {
		TextView temp = (TextView) findViewById(R.id.Title);
		temp.setText(title);

		temp = (TextView) findViewById(R.id.CowID);
		temp.setText("Cow: " + id);
		
		temp = (TextView) findViewById(R.id.EventText);
		temp.setText("Choose new event!");
	}

	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
			Log.d("GlassCow:NewEvent", "***onCreatePanelMenu***");
			menu.clear(); // TODO opdel i helth/reproduction menuer
			menu.add(Menu.NONE, MENU_BACK, Menu.NONE, "Back");
			menu.add(Menu.NONE, MENU_CALVING, Menu.NONE, "Calving");
			return true;
		}
		return super.onCreatePanelMenu(featureId, menu);
	}

//	@Override
//	public boolean onPreparePanel(int featureId, View view, Menu menu) {
//		Log.d("GlassCow:Main", "onPreparePanel");
//		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
//			return true;
//		}
//		return super.onPreparePanel(featureId, view, menu);
//	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d("GlassCow:NewEvent", "onMenuItemSelected");
		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
			switch (item.getItemId()) {
			case MENU_CALVING:
				events.add(new CowValue("***Kælvning***", CowMath.getDate())); // TODO
				Log.d("GlassCow:NewEvent", "***Kælvning***");
				finish();
				break;
			case MENU_BACK:
				Log.d("GlassCow:NewEvent", "***BACK***");
				finish();
				break;
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		return gDetector.onMotionEvent(event);
	}

	@Override
	public boolean onGesture(Gesture g) {
		Log.d("GlassCow:NewEvent", "gesture: " + g.name());
		if (g == Gesture.TAP) {
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			am.playSoundEffect(Sounds.TAP);

//			nextPage();
		} else {
			return false;
		}
		return true;
	}
}
