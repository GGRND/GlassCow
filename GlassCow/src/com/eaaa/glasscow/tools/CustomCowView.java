package com.eaaa.glasscow.tools;

import android.app.Activity;
import android.view.View;

import com.eaaa.glasscow.R;


public class CustomCowView {

	private View mainView;
	
	public CustomCowView(Activity activity){
		mainView = activity.getLayoutInflater().inflate(R.layout.standard, null);
	}
}
