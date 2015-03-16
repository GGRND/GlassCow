package com.eaaa.glasscow.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CowPreference {

	private long timer = -1;
	private int cow = -1;

	private SharedPreferences prefs;

	private static final String PREF_NAME = "GLASSCOW_PREF";

	private static final String TIMER = "keyTimer";
	private static final String COW = "keyCow";

	public CowPreference(Context context, long delta) {
		prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_MULTI_PROCESS);
		open(delta);
	}

	private void open(long delta) {
		if (prefs.contains(TIMER)) {
			this.timer = prefs.getLong(TIMER, -1);
			long currentTime = System.currentTimeMillis();
			if ((currentTime - timer) < delta && prefs.contains(COW)) {
				this.cow = prefs.getInt(COW, -1);
			}
		}
	}

	private void save() {
		if (cow != -1) {
			Editor editor = prefs.edit();
			editor.putInt(COW, cow);
			editor.putLong(TIMER, System.currentTimeMillis());
			editor.commit();
		}
	}

	public int getCowID() {
		return cow;
	}

	public void updateCow(int id) {
		this.cow = id;
		save();
	}
}
