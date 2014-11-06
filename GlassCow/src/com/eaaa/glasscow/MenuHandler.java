package com.eaaa.glasscow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.google.android.glass.widget.CardScrollView;

public class MenuHandler {

	private static final int MENU_SHOW_MORE = 0;
	private static final int MENU_SHOW_EVENTS = 1;
	private static final int MENU_INFORMATION = 2;
	private static final int MENU_HEALTH = 3;
	private static final int MENU_REPRODUCTION = 4;
	private static final int MENU_IDENTIFY = 5;
	private static final int MENU_EXIT = 6;

	public static void updateMenu(Activity_Main activity, Menu menu, Screen_CowData data) {
		Log.d("GlassCow:MenuHandler", "updating Menu" + activity.scrollView.getSelectedItemPosition());
		menu.clear();

		menu.add(Menu.NONE, MENU_SHOW_MORE, Menu.NONE, R.string.menu_show_more);

		if (data.hasEvents()) {
			menu.add(Menu.NONE, MENU_SHOW_EVENTS, Menu.NONE, R.string.menu_show_events);
		}
		menu.add(Menu.NONE, MENU_INFORMATION, Menu.NONE, R.string.information);
		menu.add(Menu.NONE, MENU_HEALTH, Menu.NONE, R.string.health);
		menu.add(Menu.NONE, MENU_REPRODUCTION, Menu.NONE, R.string.reproduction);
		menu.add(Menu.NONE, MENU_IDENTIFY, Menu.NONE, R.string.menu_identify);
		menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, R.string.menu_exit);
	}

	public static void onMainMenuItemSelected(Activity_Main activity, Screen_CowData data, int id) {
		Log.d("GlassCow:MenuHandler", "menu_id: " + id);
		// TODO
		switch (id) {
		case MENU_SHOW_MORE:
			if (data.hasMore()) {
				data.nextPage();
			}
			break;
		case MENU_SHOW_EVENTS:
			if(data.hasEvents()){
				startEventActivity(activity,data.getTitle(),data.getCowID());
			}
			break;
		case MENU_INFORMATION:
			if (activity.scrollView.getSelectedItemPosition() != 0) {
				activity.scrollView.animate(0, CardScrollView.Animation.NAVIGATION);
			}
			break;
		case MENU_HEALTH:
			if (activity.scrollView.getSelectedItemPosition() != 1) {
				activity.scrollView.animate(1, CardScrollView.Animation.NAVIGATION);
			}
			break;
		case MENU_REPRODUCTION:
			if (activity.scrollView.getSelectedItemPosition() != 2) {
				activity.scrollView.animate(2, CardScrollView.Animation.NAVIGATION);
			}
			break;
		case MENU_IDENTIFY:
			activity.identifyCowWithVoice();
			break;
		case MENU_EXIT:
			activity.finish();
			break;
		default:
			break;
		}
	}

	private static void startEventActivity(Activity_Main m, int title, int id) {
		Intent intent = new Intent(m, Activity_Events.class);
		Bundle bundle = new Bundle();
		bundle.putInt("Title", title);
		bundle.putInt("Id", id);
		intent.putExtras(bundle);
		m.startActivity(intent);
	}

	// TODO
	public static void onEventMenuItemSelected() {
	}
}
