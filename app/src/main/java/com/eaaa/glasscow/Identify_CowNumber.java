package com.eaaa.glasscow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.WindowManager;
import android.widget.TextView;

import com.eaaa.glasscow.service.CowService;
import com.google.android.glass.view.WindowUtils;

import java.util.List;


/**
 * Created by Winther on 07/03/16.
 */
public class Identify_CowNumber extends Activity {

    private String cowNumber = "";
    private TextView newCowIDView;
    private Menu menu;

    // Classes
    private Activity_Main mainActivity;
    private CowService cowService;

    private int currentMinimum = 0;
    private int currentMaximum = 9999;
    private int currentCipher = 3;

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
        // Sørger for at skærmen er tændt hele tiden
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.identify_cownumber);

        initElements();
        mainActivity = new Activity_Main();
        cowService = CowService.getInstance(mainActivity);
        cowService.open();
    }


    public void initElements(){
        newCowIDView = (TextView) findViewById(R.id.newCowID);
    }

    /**
     * Menuen shown by saying "OK Glass"
     */
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {

        if (featureId == WindowUtils.FEATURE_VOICE_COMMANDS) {
            if (cowNumber.length() <= 3) {
                createNumbersInSubMenu(menu, cowService.returnNumbers(currentMinimum, currentMaximum, currentCipher));
            }
            return true;
        }
        return super.onCreatePanelMenu(featureId, menu);
    }

    /**
     * Adds submenues with numbers from 0-9
     */
    public void createNumbersInSubMenu(Menu menu, List<Integer> cowNumbers) {
        for (Integer cowNumber : cowNumbers) {
            if (cowNumber == 0) {
                SubMenu subMenu0 = menu.addSubMenu(Menu.NONE, MENU0, Menu.NONE, "0");
            }
            if (cowNumber == 1) {
                SubMenu subMenu1 = menu.addSubMenu(Menu.NONE, MENU1, Menu.NONE, "1");
            }
            if (cowNumber == 2) {
                SubMenu subMenu2 = menu.addSubMenu(Menu.NONE, MENU2, Menu.NONE, "2");
            }
            if (cowNumber == 3) {
                SubMenu subMenu3 = menu.addSubMenu(Menu.NONE, MENU3, Menu.NONE, "3");
            }
            if (cowNumber == 4) {
                SubMenu subMenu4 = menu.addSubMenu(Menu.NONE, MENU4, Menu.NONE, "4");
            }
            if (cowNumber == 5) {
                SubMenu subMenu5 = menu.addSubMenu(Menu.NONE, MENU5, Menu.NONE, "5");
            }
            if (cowNumber == 6) {
                SubMenu subMenu6 = menu.addSubMenu(Menu.NONE, MENU6, Menu.NONE, "6");
            }
            if (cowNumber == 7) {
                SubMenu subMenu7 = menu.addSubMenu(Menu.NONE, MENU7, Menu.NONE, "7");
            }
            if (cowNumber == 8) {
                SubMenu subMenu8 = menu.addSubMenu(Menu.NONE, MENU8, Menu.NONE, "8");
            }
            if (cowNumber == 9) {
                SubMenu subMenu9 = menu.addSubMenu(Menu.NONE, MENU9, Menu.NONE, "9");
            }
        }
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    /**
     * Different cases depending on which thing is chosen
     */
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        setMenu(item.getSubMenu());

        switch (item.getItemId()) {

            case MENU0:
                cowNumber += 0;
                callMethods(0, "minus");
                break;

            case MENU1:
                cowNumber += 1;
                callMethods(1, "minus");
                break;

            case MENU2:
                cowNumber += 2;
                callMethods(2, "minus");
                break;

            case MENU3:
                cowNumber += 3;
                callMethods(3, "minus");
                break;

            case MENU4:
                cowNumber += 4;
                callMethods(4, "minus");
                break;

            case MENU5:
                cowNumber += 5;
                callMethods(5, "minus");
                break;

            case MENU6:
                cowNumber += 6;
                callMethods(6, "minus");
                break;

            case MENU7:
                cowNumber += 7;
                callMethods(7, "minus");
                break;

            case MENU8:
                cowNumber += 8;
                callMethods(8, "minus");
                break;

            case MENU9:
                cowNumber += 9;
                callMethods(9, "minus");
                break;

            case R.id.number_ten:

                updateNewCowIDView();
                break;

            case R.id.number_eleven:
                cowNumber = "";
                updateNewCowIDView();
                break;
        }
        return true;
    }

    /**
     * Updates TextView in upper right corner
     */
    public void updateNewCowIDView() {
        if (cowNumber != null) {
            newCowIDView.setText(cowNumber);
            Log.d("FREDE", "opdaterer..");
        }
        returnToMain();
    }

    public void returnToMain() {
        if (cowNumber.length() >= 4) {
            Intent intent = new Intent();
            intent.putExtra("newCowID", cowNumber);
            setResult(Activity_Main.RESULT_OK, intent);
            Log.d("FREDE", "Return to main: " + cowNumber);
            cowNumber = "";
            finish();
        }
    }

    /**
     * Removes last cipher from received string
     */
    public String redo(String str) {
        if (str != null && str.length() > 0) {
            str = str.substring(0, str.length()-1);
        }
        return str;
    }

    /**
     * Accepts the Strings: "plus" or "minus"
     */
    public void setCurrentCipher(String plusOrMinus) {
        if (plusOrMinus.equals("plus")) {
            if (currentCipher <= 3) {
                currentCipher++;
            }
        }
        if (plusOrMinus.equals("minus")) {
            if (currentCipher >= 1) {
                currentCipher--;
            }
        }
    }

    /**
     * Sets the current value of the maximum and minimum cowID the database should return
     */
    public void setCurrentMaxMin(int spokenNumber) {
        currentMinimum += (int) (spokenNumber * Math.pow(10, currentCipher));
        currentMaximum -= (int) ((9 - spokenNumber) * Math.pow(10, currentCipher));
    }

    private void callMethods(int spokenNumber, String plusOrMinus) {
        updateNewCowIDView();
        setCurrentMaxMin(spokenNumber);
        setCurrentCipher(plusOrMinus);
        createNumbersInSubMenu(menu, cowService.returnNumbers(currentMinimum, currentMaximum, currentCipher));
    }

}


