package com.eaaa.glasscow;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.service.RemoteDatabase;
import com.google.android.glass.view.WindowUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Activity_Kill_Cow extends Activity {

    private TextView cowIDView, dateTextView, firstDescription, secondDescription;
    private RelativeLayout destructionView, dateView;
    private Menu menu;

    private Activity_Main activity_main;
    private RemoteDatabase remoteDatabase;
    private Cow cow;
    private static Context context;


    private final long transferCodeId = 19;
    private String animalNumber;
    private String date, herdId, shortAnimalNumber;
    private int menuNumberCounter = 1;

    // Menu item IDs:
    public static final int MENU_CURRENT_DATE_YES = 11;
    public static final int MENU_CURRENT_DATE_NO = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);

        setContentView(R.layout.activity_kill);
        activity_main = new Activity_Main();
        remoteDatabase = RemoteDatabase.getInstance(activity_main);
        initElements();
        firstDescription.setVisibility(View.VISIBLE);
        getCowInfo();
        setElements();
        Activity_Kill_Cow.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return Activity_Kill_Cow.context;
    }

    /**
     * Gets the required information about the current cow
     */
    private void getCowInfo() {
        cow = com.eaaa.glasscow.Activity_Main.cow;
        herdId = cow.getHerdId();
        shortAnimalNumber = cow.getShortNumber();
        animalNumber = cow.getFullNumber();
    }

    private void initElements() {
        cowIDView = (TextView) findViewById(R.id.CowID_killCow);
        dateTextView = (TextView) findViewById(R.id.date_killCow);
        firstDescription = (TextView) findViewById(R.id.first_description_killCow);
        secondDescription = (TextView) findViewById(R.id.second_description_killCow);
        destructionView = (RelativeLayout) findViewById(R.id.destruktion_killCow);
        dateView = (RelativeLayout) findViewById(R.id.date_text_killCow);
    }

    private void setElements() {
        cowIDView.setText(removeZero(shortAnimalNumber));
    }

    private void setMenu(Menu menu) {
        this.menu = menu;
    }

    /**
     * Sets the current date and makes the view visible
     */
    private void setCurrentDate() {
        date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());

        dateTextView.setText(date);
        dateView.setVisibility(View.VISIBLE);
    }

    /**
     * Toggles visibility of certain views given the context of the field variable menuNumberCounter
     */
    public void setCertainViewVisible() {
        if (menuNumberCounter == 1) {
            dateView.setVisibility(View.VISIBLE);
            firstDescription.setVisibility(View.INVISIBLE);
            destructionView.setVisibility(View.VISIBLE);
            secondDescription.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        setMenu(item.getSubMenu());

        switch (item.getItemId()){
            case MENU_CURRENT_DATE_YES:
                if (menuNumberCounter == 1) {
                    setCurrentDate();
                    setCertainViewVisible();
                }
                if (menuNumberCounter == 2) {
                    remoteDatabase.sendDeath(Integer.valueOf(convertHerdNumber(herdId)),
                            Long.valueOf(animalNumber), transferCodeId, date, "killed");
                }
                menuNumberCounter++;
                break;

            case MENU_CURRENT_DATE_NO:
                if (menuNumberCounter == 1) {

                }
                if (menuNumberCounter == 2) {

                }
                //TODO manuelt indtale dato.
                break;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * Fjerner nul fra ko-nummer
     */
    public String convertHerdNumber(String herdNumber) {
        String numbers = herdNumber.substring(0, herdNumber.length() - 2);
        return numbers;
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {

        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            MenuHandler.yesNoMenuItems(menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    private String removeZero(String cowNumber) {
        int number = Integer.valueOf(cowNumber);
        String newCowNumber = String.valueOf(number);
        return newCowNumber;
    }
}
