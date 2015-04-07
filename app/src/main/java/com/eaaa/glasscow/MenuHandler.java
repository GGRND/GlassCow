package com.eaaa.glasscow;

import android.util.Log;
import android.view.Menu;

import com.eaaa.glasscow.service.DatabaseFields;
import com.google.android.glass.widget.CardScrollView;

public class MenuHandler {

	private static final int MENU_MORE = 0;
    private static final int MENU_EVENTS = 1;
    private static final int MENU_OBSERVATION = 2;
	private static final int MENU_INFORMATION = 3;
	private static final int MENU_HEALTH = 4;
	private static final int MENU_REPRODUCTION = 5;
	private static final int MENU_IDENTIFY = 6;
	private static final int MENU_EXIT = 7;
	private static final int MENU_NEW_EVENT = 8;

	public static void updateMenu(Activity_Main activity, Menu menu, Screen_CowData data) {
		Log.d("GlassCow:MenuHandler", "updating Menu " + activity.scrollView.getSelectedItemPosition());
		menu.clear();

		menu.add(Menu.NONE, MENU_MORE, Menu.NONE, R.string.menu_more);

		if (data.hasEvents()) {
			menu.add(Menu.NONE, MENU_EVENTS, Menu.NONE, R.string.menu_events);
			//menu.add(Menu.NONE, MENU_NEW_EVENT, Menu.NONE, R.string.menu_new_event);
		}
        if (data.getObservationTypeId()!=DatabaseFields.TYPE_ID_Yver) {
            menu.add(Menu.NONE, MENU_INFORMATION, Menu.NONE, R.string.information);
        }
        if (data.getObservationTypeId()!=DatabaseFields.TYPE_ID_Lemmer) {
            menu.add(Menu.NONE, MENU_HEALTH, Menu.NONE, R.string.health);
        }
        if (data.getObservationTypeId()!=DatabaseFields.TYPE_ID_Brunst) {
            menu.add(Menu.NONE, MENU_REPRODUCTION, Menu.NONE, R.string.reproduction);
        }

        menu.add(Menu.NONE, MENU_IDENTIFY, Menu.NONE, R.string.menu_identify);
        menu.add(Menu.NONE, MENU_OBSERVATION, Menu.NONE, R.string.menu_observation);
		//menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, R.string.menu_exit);
	}

	public static int onMainMenuItemSelected(Activity_Main activity, Screen_CowData data, int id) {
		Log.d("GlassCow:MenuHandler", "menu_id: " + id);
		switch (id) {
		case MENU_EVENTS:
			if (data.hasMore()) {
				data.nextPage();
			}
			break;
        case MENU_OBSERVATION:
            activity.startObservationActivity(data.getObservationTypeId());
            break;
		case MENU_INFORMATION:
			if (activity.scrollView.getSelectedItemPosition() != 0) {
				activity.scrollView.animate(0, CardScrollView.Animation.NAVIGATION);
			}
			return 0;
		case MENU_HEALTH:
			if (activity.scrollView.getSelectedItemPosition() != 1) {
				activity.scrollView.animate(1, CardScrollView.Animation.NAVIGATION);

			}
			return 1;
		case MENU_REPRODUCTION:
			if (activity.scrollView.getSelectedItemPosition() != 2) {
				activity.scrollView.animate(2, CardScrollView.Animation.NAVIGATION);
			}
			return 2;
		case MENU_IDENTIFY:
			activity.identifyCowWithVoice();
			break;
		case MENU_EXIT:
			activity.finish();
			break;
		default:
			break;
		}
		return -1;
	}

}
