package com.eaaa.glasscow.tools;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.eaaa.glasscow.Screen_CowData;
import com.google.android.glass.widget.CardScrollAdapter;

public class CowScrollViewAdapter extends CardScrollAdapter{

	private List<Screen_CowData> mViews;
	
	public CowScrollViewAdapter(List<Screen_CowData> views){
        this.mViews = views;
    }
	
    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public Object getItem(int i) {
        return mViews.get(i);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return mViews.get(i).getContentView();
    }

    @Override
    public int getPosition(Object o){
        for(int i = 0; i<mViews.size();i++){
            if(o.equals(mViews.get(i))){
                return i;
            }
        }
        return AdapterView.INVALID_POSITION;
    }
	
}
