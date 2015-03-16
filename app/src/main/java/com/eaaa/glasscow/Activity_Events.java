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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.model.CowValue;
import com.eaaa.glasscow.service.CowService;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;

public class Activity_Events extends Activity implements
		GestureDetector.BaseListener {

	private static final int MENU_SHOW_MORE = 0;
	private static final int MENU_BACK = 1;

	private int id;
	private String title;
	private List<CowValue> events;
	private int currentPage, totalPages;

	private TextView txtFooter;
	private ImageView[] imageViews;
	private TextView[] txtLabelViews, txtValueViews;

	private GestureDetector gDetector;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.standard);
		unpackBundle();
		initElements();
		nextPage();

		gDetector = new GestureDetector(this).setBaseListener(this);
	}

	private void unpackBundle() {
		Log.d("GlassCow:Events", "unpackingBundle");
		Bundle bundle = getIntent().getExtras();
		this.id = bundle.getInt("Id");
		this.title = getString(bundle.getInt("Title"));

//			Cow cow = CowService.getInstance().getCow(id);
		Cow cow = Activity_Main.cow;
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

		this.currentPage = 0;
		this.totalPages = (events.size() + 2) / 3;
		Log.d("GlassCow:Events", "page: " + currentPage + "/" + totalPages);
		Log.d("GlassCow:Events", "#events: " + events.size());
	}

	private void initElements() {
		TextView temp = (TextView) findViewById(R.id.Title);
		temp.setText(title);

		temp = (TextView) findViewById(R.id.CowID);
		temp.setText("Cow: " + id);
		txtFooter = (TextView) findViewById(R.id.footer);

		txtLabelViews = new TextView[3];
		txtLabelViews[0] = (TextView) findViewById(R.id.c1txt1);
		txtLabelViews[1] = (TextView) findViewById(R.id.c2txt1);
		txtLabelViews[2] = (TextView) findViewById(R.id.c3txt1);

		txtValueViews = new TextView[3];
		txtValueViews[0] = (TextView) findViewById(R.id.c1txt2);
		txtValueViews[1] = (TextView) findViewById(R.id.c2txt2);
		txtValueViews[2] = (TextView) findViewById(R.id.c3txt2);

		imageViews = new ImageView[3];
		imageViews[0] = (ImageView) findViewById(R.id.c1img);
		imageViews[1] = (ImageView) findViewById(R.id.c2img);
		imageViews[2] = (ImageView) findViewById(R.id.c3img);
	}

	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
			Log.d("GlassCow:Main", "onCreatePanelMenu");
			menu.clear();
			menu.add(Menu.NONE, MENU_SHOW_MORE, Menu.NONE, "Show More");
			menu.add(Menu.NONE, MENU_BACK, Menu.NONE, "Back");
			return true;
		}
		return super.onCreatePanelMenu(featureId, menu);
	}

	@Override
	public boolean onPreparePanel(int featureId, View view, Menu menu) {
		Log.d("GlassCow:Main", "onPreparePanel");
		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
			return true;
		}
		return super.onPreparePanel(featureId, view, menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d("GlassCow:Main", "onMenuItemSelected");
		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
			switch (item.getItemId()) {
			case MENU_SHOW_MORE:
				nextPage();
				break;
			case MENU_BACK:
				finish();
				break;
			}
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void nextPage() {
		currentPage++;
		if (currentPage > totalPages) {
			currentPage = 1;
		}
		txtFooter.setText("p. " + currentPage + "/" + totalPages);

		int item = currentPage * 3 - 3;
		int i = 0;
		while (i < 3 && item < events.size()) {
			CowValue temp = events.get(item);
			imageViews[i].setImageResource(temp.getRingColor());
			txtLabelViews[i].setText(temp.getKey());
			txtValueViews[i].setText(temp.getValue());
			item++;
			i++;
		}
		while (i < 3) {
			imageViews[i].setImageResource(R.drawable.ring_black);
			txtLabelViews[i].setText("");
			txtValueViews[i].setText("");
			i++;
		}
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		return gDetector.onMotionEvent(event);
	}

	@Override
	public boolean onGesture(Gesture g) {
		Log.d("GlassCow:Events", "gesture: " + g.name());
		if (g == Gesture.TAP) {
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			am.playSoundEffect(Sounds.TAP);

			nextPage();
		} else {
			return false;
		}
		return true;
	}
}
