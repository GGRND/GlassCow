package com.eaaa.glasscow.transfer_cows;

import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eaaa.glasscow.model.Cow;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Transfer_Cow extends Activity {

    private String date;

    private Menu menu;

    /**
     * Initialises the elements in the current view
     */
    public abstract void initElements();

    /**
     * Sets the elements in the current view
     */
    public abstract void setElements();

    /**
     * Gets the required information about the current cow
     */
    public abstract void getCowInfo();

    /**
     * Sets the textview for dates to the current date
     */
    public void setCurrentDate(TextView dateTextView, RelativeLayout dateView) {
        setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date()));
        String simpleDate = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
        dateTextView.setText(simpleDate);
        dateView.setVisibility(View.VISIBLE);
    }

    /**
     * Removes last two numbers from the herd-number
     */
    public String convertHerdNumber(String herdNumber) {
        String numbers = herdNumber.substring(0, herdNumber.length() - 2);
        return numbers;
    }

    /**
     * Removes the zero in front of the cow-number
     */
    public String removeZero(String cowNumber) {
        int number = Integer.valueOf(cowNumber);
        String newCowNumber = String.valueOf(number);
        return newCowNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }
}
