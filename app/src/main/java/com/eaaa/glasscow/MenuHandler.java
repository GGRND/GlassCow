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
    private static final int MENU_IDENTIFY_COW = 6;
    //private static final int MENU_IDENTIFY_OBSERVATION = 7;
    private static final int MENU_EXIT = 8;
    private static final int MENU_NEW_EVENT = 9;
    private static final int MENU_TESTER = 10;
    private static final int MENU_TURNOVER = 11;

    //Numbers for aflivet menu
    private static final int MENU_CURRENT_DATE_YES = 11;
    private static final int MENU_CURRENT_DATE_NO = 12;

    //Numbers for "omsætninger" menu items
    private static final int MENU_TURNOVER_ENTRY = 13;
    private static final int MENU_TURNOVER_DEPARTURE = 14;
    private static final int MENU_TURNOVER_SLAUGHTER = 15;
    private static final int MENU_TURNOVER_DEAD = 16;
    private static final int MENU_TURNOVER_KILLED = 17;

    public static void updateMenu(Activity_Main activity, Menu menu, Screen_CowData data) {
        if (menu == null)
            return;
        Log.d("GlassCow:MenuHandler", "updating Menu " + activity.scrollView.getSelectedItemPosition());
        menu.clear();

        menu.add(Menu.NONE, MENU_IDENTIFY_COW, Menu.NONE, R.string.menu_identify_cow);
        menu.add(Menu.NONE, MENU_MORE, Menu.NONE, R.string.menu_more);
        menu.add(Menu.NONE, MENU_TESTER, Menu.NONE, R.string.tester);

        menu.addSubMenu(Menu.NONE, MENU_TURNOVER, Menu.NONE, R.string.turnover);

        if (data.getObservationTypeId()!=DatabaseFields.TYPE_ID_Yver) {
            menu.add(Menu.NONE, MENU_INFORMATION, Menu.NONE, R.string.information);
        }
        if (data.getObservationTypeId()!=DatabaseFields.TYPE_ID_Lemmer) {
            menu.add(Menu.NONE, MENU_HEALTH, Menu.NONE, R.string.health);
        }
        if (data.getObservationTypeId()!=DatabaseFields.TYPE_ID_Brunst) {
            menu.add(Menu.NONE, MENU_REPRODUCTION, Menu.NONE, R.string.reproduction);
        }

        menu.add(Menu.NONE, MENU_OBSERVATION, Menu.NONE, R.string.menu_observation);


        if (data.hasEvents()) {
            menu.add(Menu.NONE, MENU_EVENTS, Menu.NONE, R.string.menu_events);
            //menu.add(Menu.NONE, MENU_NEW_EVENT, Menu.NONE, R.string.menu_new_event);
        }

        //menu.add(Menu.NONE, MENU_IDENTIFY_OBSERVATION, Menu.NONE, R.string.menu_identify_observation);
        //menu.add(Menu.NONE, MENU_EXIT, Menu.NONE, R.string.menu_exit);
    }

    public static int onMainMenuItemSelected(Activity_Main activity, Screen_CowData data, int id, Menu menu) {
        Log.d("GlassCow:MenuHandler", "menu_id: " + id);
        switch (id) {
            case MENU_MORE:
                if (data.hasMore()) {
                    data.nextPage();
                }
                break;
            case MENU_EVENTS:
                if(data.hasEvents()){
                    activity.startEventActivity(data.getTitle());
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
            case MENU_IDENTIFY_COW:
                activity.identifyCowWithVoice();
                break;

            case MENU_TESTER:
                activity.identifyCow();
                break;

            case MENU_TURNOVER:
                turnoverMenuItems(menu);
                break;

            case MENU_TURNOVER_ENTRY:

                break;

            case MENU_TURNOVER_DEPARTURE:

                break;

            case MENU_TURNOVER_SLAUGHTER:

                break;

            case MENU_TURNOVER_DEAD:
                activity.registerDeadCow();
                break;

            case MENU_TURNOVER_KILLED:

                break;

        /*case MENU_IDENTIFY_OBSERVATION:
                activity.identifyObservation();
                break;*/
            case MENU_EXIT:
                activity.finish();
                break;
            default:
                break;
        }
        return -1;
    }

    public static void yesNoMenuItems(Menu menu) {
        menu.addSubMenu(Menu.NONE, MENU_CURRENT_DATE_YES, Menu.NONE, "Yes");
        menu.addSubMenu(Menu.NONE, MENU_CURRENT_DATE_NO, Menu.NONE, "No");
    }

    public static void turnoverMenuItems(Menu menu) {
        menu.add(Menu.NONE, MENU_TURNOVER_ENTRY, Menu.NONE, R.string.entry);
        menu.add(Menu.NONE, MENU_TURNOVER_DEPARTURE, Menu.NONE, R.string.departure);
        menu.add(Menu.NONE, MENU_TURNOVER_SLAUGHTER, Menu.NONE, R.string.slaughter);
        menu.add(Menu.NONE, MENU_TURNOVER_DEAD, Menu.NONE, R.string.dead);
        menu.add(Menu.NONE, MENU_TURNOVER_KILLED, Menu.NONE, R.string.killed);
    }

}
