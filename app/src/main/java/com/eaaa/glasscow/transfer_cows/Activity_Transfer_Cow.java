package com.eaaa.glasscow.transfer_cows;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eaaa.glasscow.Activity_Main;
import com.eaaa.glasscow.MenuHandler;
import com.eaaa.glasscow.R;
import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.service.RemoteDatabase;
import com.eaaa.glasscow.transfer_cows.Transfer_Cow;
import com.google.android.glass.view.WindowUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by ThinkNick on 22-07-2016.
 */
public class Activity_Transfer_Cow extends Transfer_Cow {
    private String newHerdID = "";

    private TextView cowIDView, herdIDView, nherdIDView, dateTextView, firstDescription, secondDescription;
    private RelativeLayout transferView, dateView;
    private Menu menu;

    private Activity_Main ctx;
    private RemoteDatabase remoteDatabase;
    private Cow cow;

    private final long transferCodeId = 19;
    private String animalNumber;
    private String date, herdId, shortAnimalNumber;
    private int menuNumberCounter = 1;

    // Menu item ids:
    public static final int MENU_CURRENT_DATE_YES = 11;
    public static final int MENU_CURRENT_DATE_NO = 12;

    // Menu numbers
    private static final int MENU0 = 0;
    private static final int MENU1 = 1;
    private static final int MENU2 = 2;
    private static final int MENU3 = 3;
    private static final int MENU4 = 4;
    private static final int MENU5 = 5;
    private static final int MENU6 = 6;
    private static final int MENU7 = 7;
    private static final int MENU8 = 8;
    private static final int MENU9 = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(WindowUtils.FEATURE_VOICE_COMMANDS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_transfer);
        ctx = new Activity_Main();
        remoteDatabase = RemoteDatabase.getInstance(ctx);
        initElements();
        //firstDescription.setVisibility(View.VISIBLE);
        getCowInfo();
        setElements();
        setNewherdID();
    }

    /**
     * Gets the required information about the current cow
     */
    public void getCowInfo() {
        cow = com.eaaa.glasscow.Activity_Main.cow;
        herdId = cow.getHerdId();
        shortAnimalNumber = cow.getShortNumber();
        animalNumber = cow.getAnimalId();
    }


    public void initElements() {
        cowIDView = (TextView) findViewById(R.id.CowID_transfer);
        herdIDView = (TextView) findViewById(R.id.HerdID_transfer);
        nherdIDView = (TextView) findViewById(R.id.NHerdID_transfer);

        dateTextView = (TextView) findViewById(R.id.date_transfer);
        //firstDescription = (TextView) findViewById(R.id.first_description_transfer);
        //secondDescription = (TextView) findViewById(R.id.second_description_transfer);
        transferView = (RelativeLayout) findViewById(R.id.herd_transfer);
        dateView = (RelativeLayout) findViewById(R.id.date_text_transfer);
    }

    public void setElements() {
        cowIDView.setText(removeZero(shortAnimalNumber));
        herdIDView.setText(removeZero(herdId));

    }


    private void setNewherdID(){
        nherdIDView.setText(newHerdID);
    }

    /**
     * Toggles visibility of certain views given the context of the field variable menuNumberCounter
     */
    public void setCertainViewVisible() {
        if (menuNumberCounter == 1) {
            //firstDescription.setVisibility(View.INVISIBLE);
            transferView.setVisibility(View.VISIBLE);
            //secondDescription.setVisibility(View.VISIBLE);
        }
        if (menuNumberCounter == 2) {

        }
    }
    public void createNumbersInSubMenu(Menu menu) {
                SubMenu subMenu0 = menu.addSubMenu(Menu.NONE, MENU0, Menu.NONE, "0");

                SubMenu subMenu1 = menu.addSubMenu(Menu.NONE, MENU1, Menu.NONE, "1");


                SubMenu subMenu2 = menu.addSubMenu(Menu.NONE, MENU2, Menu.NONE, "2");


                SubMenu subMenu3 = menu.addSubMenu(Menu.NONE, MENU3, Menu.NONE, "3");


                SubMenu subMenu4 = menu.addSubMenu(Menu.NONE, MENU4, Menu.NONE, "4");


                SubMenu subMenu5 = menu.addSubMenu(Menu.NONE, MENU5, Menu.NONE, "5");

                SubMenu subMenu6 = menu.addSubMenu(Menu.NONE, MENU6, Menu.NONE, "6");

                SubMenu subMenu7 = menu.addSubMenu(Menu.NONE, MENU7, Menu.NONE, "7");

                SubMenu subMenu8 = menu.addSubMenu(Menu.NONE, MENU8, Menu.NONE, "8");

                SubMenu subMenu9 = menu.addSubMenu(Menu.NONE, MENU9, Menu.NONE, "9");
            }



    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        setMenu(item.getSubMenu());

        switch (item.getItemId()) {
            case MENU_CURRENT_DATE_YES:
                if (menuNumberCounter == 1) {
                    dateView.setVisibility(View.VISIBLE);
                    setCurrentDate(dateTextView, dateView);
                    createNumbersInSubMenu(menu);

                }
                if (menuNumberCounter == 2) {
                    //remoteDatabase.send2Herd(herdId, newHerdID, animalNumber, transferCodeId, date);
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
        if (newHerdID.length() < 7) {

            switch (item.getItemId()) {

                case MENU0:
                    newHerdID += 0;
                    createNumbersInSubMenu(menu);
                    setNewherdID();
                    break;

                case MENU1:
                    newHerdID += 1;
                    createNumbersInSubMenu(menu);
                    setNewherdID();
                    break;

                case MENU2:
                    newHerdID += 2;
                    createNumbersInSubMenu(menu);
                    setNewherdID();
                    break;

                case MENU3:
                    newHerdID += 3;
                    createNumbersInSubMenu(menu);
                    setNewherdID();

                    break;

                case MENU4:
                    newHerdID += 4;
                    createNumbersInSubMenu(menu);
                    setNewherdID();

                    break;

                case MENU5:
                    newHerdID += 5;
                    createNumbersInSubMenu(menu);
                    setNewherdID();

                    break;

                case MENU6:
                    newHerdID += 6;
                    createNumbersInSubMenu(menu);
                    setNewherdID();

                    break;

                case MENU7:
                    newHerdID += 7;
                    createNumbersInSubMenu(menu);
                    setNewherdID();

                    break;

                case MENU8:
                    newHerdID += 8;
                    createNumbersInSubMenu(menu);
                    setNewherdID();

                    break;

                case MENU9:
                    newHerdID += 9;
                    createNumbersInSubMenu(menu);
                    setNewherdID();

                    break;

                // }
            }
Log.d("Mnunumber", String.valueOf(menuNumberCounter));

            if (newHerdID.length() >= 6) {
                setCertainViewVisible();
                menuNumberCounter++;


            }
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
