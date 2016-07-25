package com.eaaa.glasscow;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.service.RemoteDatabase;
import com.google.android.glass.view.WindowUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ThinkNick on 22-07-2016.
 */
public class Activity_Transfer_Cow extends Activity {

    private TextView cowIDView, dateTextView, firstDescription, secondDescription;
    private RelativeLayout destructionView, dateView;
    private Menu menu;

    private Activity_Main ctx;
    private RemoteDatabase remoteDatabase;
    private Cow cow;

    private final long transferCodeId = 19;
    private String animalId;
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

        setContentView(R.layout.activity_transfer);
        ctx = new Activity_Main();
        remoteDatabase = RemoteDatabase.getInstance(ctx);
        initElements();
        firstDescription.setVisibility(View.VISIBLE);
        getCowInfo();
        setElements();

    }

    /**
     * Gets the required information about the current cow
     */
    private void getCowInfo() {
        cow = com.eaaa.glasscow.Activity_Main.cow;
        herdId = cow.getHerdId();
        shortAnimalNumber = cow.getShortNumber();
        animalId = cow.getAnimalId();
    }


    private void initElements() {
        cowIDView = (TextView) findViewById(R.id.CowID_transfer);
        dateTextView = (TextView) findViewById(R.id.date_transfer);
        firstDescription = (TextView) findViewById(R.id.first_description_transfer);
        secondDescription = (TextView) findViewById(R.id.second_description_transfer);
        destructionView = (RelativeLayout) findViewById(R.id.destruktion_transfer);
        dateView = (RelativeLayout) findViewById(R.id.date_text_transfer);
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
        date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

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
        if (menuNumberCounter == 2) {

        }
    }


    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        setMenu(item.getSubMenu());

        switch (item.getItemId()) {
            case MENU_CURRENT_DATE_YES:
                if (menuNumberCounter == 1) {
                    setCurrentDate();
                    setCertainViewVisible();
                }
                if (menuNumberCounter == 2) {
                    remoteDatabase.sendObservations();
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

    private String removeZero(String cowNumber) {
        int number = Integer.valueOf(cowNumber);
        String newCowNumber = String.valueOf(number);
        return newCowNumber;
    }
}
