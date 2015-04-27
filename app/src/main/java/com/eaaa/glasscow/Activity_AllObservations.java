package com.eaaa.glasscow;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.eaaa.glasscow.Activity_Main;
import com.eaaa.glasscow.R;
import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.model.CowObservation;
import com.eaaa.glasscow.service.CowService;
import com.eaaa.glasscow.service.DatabaseFields;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.WindowUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class Activity_AllObservations extends Activity implements
        GestureDetector.BaseListener {

    private static final int MENU_SHOW_MORE = 0;
    private static final int MENU_BACK = 1;
    private static final int MENU_SELECT = 2;
    private static final int FIELD_QUESTION = 0;

    private ArrayList<CowObservation> observations;
    private int currentPage;
    private TextView txtFooter;
    private ImageView imageView;
    private TextView txtDateTimeView, txtTextView;

    private GestureDetector gDetector;
    private CowObservation newObservation=null;
    private String shortAnimalNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.observation);
        initializeDisplay();
        initElements();
        nextPage();

        gDetector = new GestureDetector(this).setBaseListener(this);
    }


    private void initializeDisplay() {
        this.observations = CowService.getInstance().getObservations();
        Collections.sort(observations, new Comparator<CowObservation>() {
            @Override
            public int compare(CowObservation obs1, CowObservation obs2) {
                return obs2.getObservationDate().compareTo(obs1.getObservationDate());
            }
        });

        this.currentPage = 0;
        //this.observations.size() = observations.size();
        Log.d("GlassCow:observations", "page: " + currentPage + "/" + observations.size());
        Log.d("GlassCow:observations", "#observations: " + observations.size());
    }

    private void initElements() {
        txtFooter = (TextView) findViewById(R.id.ObsFooter);
        txtDateTimeView = (TextView) findViewById(R.id.ObsDateTime);
        txtTextView = (TextView) findViewById(R.id.ObsText);
        imageView = (ImageView) findViewById(R.id.ObsImg);
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            Log.d("GlassCow:Main", "onCreatePanelMenu");
            menu.clear();
            menu.add(Menu.NONE, MENU_SHOW_MORE, Menu.NONE, "More");
            menu.add(Menu.NONE, MENU_SELECT, Menu.NONE, "Select cow");
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
                case MENU_SELECT:
                    Bundle conData = new Bundle();
                    conData.putString("COW_NUMBER", this.shortAnimalNumber);
                    Intent intent = new Intent();
                    intent.putExtras(conData);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
            }
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void nextPage() {
        currentPage++;
        if (currentPage > observations.size()) {
            currentPage = 1;
        }
        txtFooter.setText("p. " + currentPage + "/" + observations.size());

        if (currentPage<=observations.size()) {
            CowObservation temp = observations.get(currentPage-1);
            imageView.setImageResource(R.drawable.ring_white);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            String DisplayDate = "";
            try {
                Date obsDate = dateFormat.parse(temp.getObservationDate());
                Date today = new Date();
                long diff = today.getTime() - obsDate.getTime();
                long days = diff/1000/60/60/24;
                long hours = (diff-days*1000*60*60*24)/1000/60/60+24*days;
                long minutes = (diff-(days*1000*60*60*24+hours*1000*60*60))/1000/60+((24*days)+hours)*60;
                DisplayDate = (days>1?(String.valueOf(days)+" dage"):(hours>1?(String.valueOf(hours)+ " timer"):(String.valueOf(minutes)+ " minutter")))+" siden";
            } catch (ParseException e) {
                e.printStackTrace();
            }
            txtDateTimeView.setText(DisplayDate);
            txtTextView.setText(temp.getDisplayText());

            TextView tempTxtView = (TextView) findViewById(R.id.ObsTitle);
            tempTxtView.setText(DatabaseFields.obsTypeName.get(Integer.valueOf(temp.getTypeId()).intValue()));
            tempTxtView = (TextView) findViewById(R.id.ObsCowID);
            this.shortAnimalNumber = temp.getShortAnimalNumber();
            tempTxtView.setText("Cow: " + Integer.valueOf(this.shortAnimalNumber).intValue());
        } else {
            imageView.setImageResource(R.drawable.ring_black);
            txtDateTimeView.setText("");
            txtTextView.setText("");
        }
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent observation) {
        return gDetector.onMotionEvent(observation);
    }

    @Override
    public boolean onGesture(Gesture g) {
        Log.d("GlassCow:observations", "gesture: " + g.name());
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
