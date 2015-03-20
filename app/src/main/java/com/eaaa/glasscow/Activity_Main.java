package com.eaaa.glasscow;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.eaaa.glasscow.AsyncCowDataChange.AsyncCowResponse;
import com.eaaa.glasscow.Screen_CowData.DataType;
import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.service.CowService;
import com.eaaa.glasscow.tools.CowScrollViewAdapter;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollView;
import 	java.io.*;

public class Activity_Main extends Activity implements AsyncCowResponse,
		GestureDetector.BaseListener {

	private static final int SPEECH_REQUEST = 0;
	private static final int NEW_EVENT = 1;
	
	public static Cow cow;
	
	private boolean voiceEnabled = true;
	public CowScrollViewAdapter scrollAdapter;
	public CardScrollView scrollView;

	private GestureDetector gDetector;

	private int page = 0;


    public class Configuration {
        public String rst_template;
        public String Endpoint;
        public String Username;
        public String Password;
        public String Audience;
        public String TrustedThumbprint;
        public String RstTemplateFile;

        public Configuration(String rst_templ) {
            this.rst_template = readRawTextFile(R.raw.rst_template);
            this.Username = "googleglas";
            this.Password = "63625";
            this.Endpoint = "https://si-idp.vfltest.dk/adfs/services/trust/13/usernamemixed"; //devtest
            //conf.Endpoint = "https://si-idp.vfltest.dk/adfs/services/trust/13/usernamemixed"; //devtest
            //conf.Endpoint = "https://idp.dlbr.dk/adfs/services/trust/13/usernamemixed"; //prod
            this.Audience = "https://devtest-dcf-odata.vfltest.dk/DCFOData/"; //devtest
            //conf.Audience = "https://devtest-dcf-odata.vfltest.dk/DCFOData/";
            this.TrustedThumbprint = "38604472D35600695F4045AA62D15F4C229E1820";
            this.RstTemplateFile = "RST_Template.xml";
        }
    }

    private Configuration conf = null;

    public String readRawTextFile(int resId)
    {
        InputStream inputStream = this.getResources().openRawResource(resId);

        InputStreamReader inputreader = new InputStreamReader(inputStream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        } catch (IOException e) {
            return null;
        }
        return text.toString();
    }

    public Configuration getConfiguration() {
        if (conf==null) {
            String template = readRawTextFile(R.raw.rst_template);
            conf = new Configuration(template);
        }
        return conf;
    };


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

		gDetector = new GestureDetector(this).setBaseListener(this);

		Log.d("GlassCow:Main", "Activity_Start");
		Cow cow = CowService.getInstance().getLastUsedCow();
		if (cow != null) {
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

	public void startEventActivity(int title, int id) {
		Intent intent = new Intent(this, Activity_Events.class);
		Bundle bundle = new Bundle();
		bundle.putInt("Title", title);
		bundle.putInt("Id", id);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	public void startNewEventActivity(int title, int id) {
		Intent intent = new Intent(this, Activity_NewEvent.class);
		Bundle bundle = new Bundle();
		bundle.putInt("Title", title);
		bundle.putInt("Id", id);
		intent.putExtras(bundle);
		 startActivity(intent);
		Log.d("GlassCow:Main", "***start NewEventActivity***" + id + " " + title);
	}

	public void identifyCowWithVoice() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		startActivityForResult(intent, SPEECH_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK) {
			Log.d("GlassCow:Main", "Handling Voice Input");
			List<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);

			int input = validateString(spokenText);
			if (input != -1) {
				Log.d("GlassCow:Main", "Cow_update: NEW COW ID: " + input);
				new AsyncCowDataChange(Activity_Main.this, input).execute();
			} else {
				Log.d("GlassCow:Main", "Cow_update: Invalid Input");
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
			Log.d("GlassCow:Main",
					"onCreatePanelMenu" + scrollView.getSelectedItemPosition());
			// getMenuInflater().inflate(R.menu.main, menu);
			MenuHandler.updateMenu(this, menu,
					(Screen_CowData) scrollAdapter.getItem(page));
			return true;
		}
		return super.onCreatePanelMenu(featureId, menu);
	}

	// @Override
	// public boolean onPreparePanel(int featureId, View view, Menu menu) {
	// if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
	// Log.d("GlassCow:Main", "onPreparePanel_id: " +
	// scrollView.getSelectedItemPosition());
	// MenuHandler.updateMenu(this, menu, (Screen_CowData)
	// scrollAdapter.getItem(response));
	// return voiceEnabled;
	// }
	// return super.onPreparePanel(featureId, view, menu);
	// }

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d("GlassCow:Main", "onMenuItemSelected " + item.getItemId());
		if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
			int temp = MenuHandler.onMainMenuItemSelected(this,
					(Screen_CowData) scrollAdapter.getItem(scrollView
							.getSelectedItemPosition()), item.getItemId());
			if (temp != -1 && temp != page) {
				page = temp;
				getWindow().invalidatePanelMenu(
						WindowUtils.FEATURE_VOICE_COMMANDS);
			}

		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public void asyncCowResponse(Cow cow) {
		if (cow != null) {
			Activity_Main.cow = cow; // remember selected cow
			for (int i = 0; i < scrollAdapter.getCount(); i++) {
				((Screen_CowData) scrollAdapter.getItem(i)).updateCow(cow);
			}
			Log.d("GlassCow:Main", "Cow_Update: Success");
			scrollAdapter.notifyDataSetChanged();
			getWindow().invalidatePanelMenu(WindowUtils.FEATURE_VOICE_COMMANDS);
		} else {
			Log.d("GlassCow:Main", "Cow_Update: Failed");
			// TODO ?
		}
	}

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		return gDetector.onMotionEvent(event);
	}

	@Override
	public boolean onGesture(Gesture g) {
		Log.d("GlassCow:Main", "gesture: " + g.name());
		if (g == Gesture.TAP) {
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			am.playSoundEffect(Sounds.TAP);

			Screen_CowData data = (Screen_CowData) scrollAdapter
					.getItem(scrollView.getSelectedItemPosition());
			if (data.hasMore()) {
				data.nextPage();
			}
		} else if (g == Gesture.TWO_TAP || g == Gesture.TWO_LONG_PRESS) {
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			am.playSoundEffect(Sounds.TAP);
			Screen_CowData data = (Screen_CowData) scrollAdapter
					.getItem(scrollView.getSelectedItemPosition());
			if (data.hasEvents()) {
				startEventActivity(data.getTitle(), data.getCowID());
			}
		} else if (g == Gesture.LONG_PRESS) {
			AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			am.playSoundEffect(Sounds.SUCCESS);
			identifyCowWithVoice();
		} else {
			return false;
		}
		return true;
	}
}
