package com.eaaa.glasscow;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.service.RemoteDatabase;
import com.eaaa.glasscow.transfer_cows.Transfer_Cow;
import com.google.android.glass.view.WindowUtils;

public class Activity_Dead_Cow extends Transfer_Cow {

    private TextView cowIDView, dateTextView, firstDescription, secondDescription;
    private RelativeLayout destructionView, dateView;

    private RemoteDatabase remoteDatabase;
    private Cow cow;

    private final long transferCodeId = 9;
    private String animalNumber;
    private String date, herdId, shortAnimalNumber;
    private int menuNumberCounter = 1;

    // Menu item ids:
    public static final int MENU_CURRENT_DATE_YES = 11;
    public static final int MENU_CURRENT_DATE_NO = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_dead);
        Activity_Main ctx = new Activity_Main();
        remoteDatabase = RemoteDatabase.getInstance(ctx);
        initElements();
        firstDescription.setVisibility(View.VISIBLE);
        getCowInfo();
        setElements();
    }

    /**
     * Gets the required information about the current cow
     */
    public void getCowInfo() {
        cow = com.eaaa.glasscow.Activity_Main.cow;
        herdId = cow.getHerdId();
        shortAnimalNumber = cow.getShortNumber();
        animalNumber = cow.getFullNumber();
    }

    public void initElements() {
        cowIDView = (TextView) findViewById(R.id.CowID_deadCow);
        dateTextView = (TextView) findViewById(R.id.date_deadCow);
        firstDescription = (TextView) findViewById(R.id.first_description_deadCow);
        secondDescription = (TextView) findViewById(R.id.second_description_deadCow);
        destructionView = (RelativeLayout) findViewById(R.id.destruktion_deadCow);
        dateView = (RelativeLayout) findViewById(R.id.date_text_deadCow);
    }

    public void setElements() {
        cowIDView.setText(removeZero(shortAnimalNumber));
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

        switch (item.getItemId()) {
            case MENU_CURRENT_DATE_YES:
                if (menuNumberCounter == 1) {
                    setCurrentDate(dateTextView, dateView);
                    setCertainViewVisible();
                }
                if (menuNumberCounter == 2) {
                    remoteDatabase.sendDeath(Integer.valueOf(convertHerdNumber(herdId)),
                            Long.valueOf(animalNumber), transferCodeId, super.getDate(), "dead", this.getApplicationContext());
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

    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {

        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            MenuHandler.yesNoMenuItems(menu);
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }
}
