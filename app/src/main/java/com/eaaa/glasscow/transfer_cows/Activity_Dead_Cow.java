package com.eaaa.glasscow.transfer_cows;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eaaa.glasscow.Activity_Main;
import com.eaaa.glasscow.MenuHandler;
import com.eaaa.glasscow.R;
import com.eaaa.glasscow.service.RemoteDatabase;
import com.google.android.glass.view.WindowUtils;

public class Activity_Dead_Cow extends Transfer_Interface {

    private TextView cowIDView, dateTextView, firstDescription, secondDescription;
    private RelativeLayout destructionView, dateView;

    private RemoteDatabase remoteDatabase;

    private final long transferCodeId = 9;
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

    public void initElements() {
        cowIDView = (TextView) findViewById(R.id.CowID_deadCow);
        dateTextView = (TextView) findViewById(R.id.date_deadCow);
        firstDescription = (TextView) findViewById(R.id.first_description_deadCow);
        secondDescription = (TextView) findViewById(R.id.second_description_deadCow);
        destructionView = (RelativeLayout) findViewById(R.id.destruktion_deadCow);
        dateView = (RelativeLayout) findViewById(R.id.date_text_deadCow);
    }

    public void setElements() {
        cowIDView.setText(removeZero(getShortAnimalNumber()));
        setToHerdId("71930");
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
                    remoteDatabase.transferCow(Integer.valueOf(convertHerdNumber(getHerdId())), Integer.valueOf(getToHerdId()),
                            Long.valueOf(getAnimalNumber()), transferCodeId, getDate(), this.getApplicationContext());
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
