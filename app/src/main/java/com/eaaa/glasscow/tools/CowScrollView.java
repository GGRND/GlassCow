package com.eaaa.glasscow.tools;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.eaaa.glasscow.Activity_Main;
import com.google.android.glass.view.WindowUtils;
import com.google.android.glass.widget.CardScrollView;

/**
 * Created by morten on 08/04/15.
 */
public class CowScrollView extends CardScrollView implements AdapterView.OnItemSelectedListener {
    private Activity_Main activity;

    public CowScrollView(Activity_Main context) {
        super(context);
        this.activity = context;

        super.setOnItemSelectedListener(this);
    }

    //@Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        activity.setPage(pos);
        activity.getWindow().invalidatePanelMenu(WindowUtils.FEATURE_VOICE_COMMANDS);

        Log.d("Google glass", "selected "+pos);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d("Google glass", "nothing selected ");
    }
}
