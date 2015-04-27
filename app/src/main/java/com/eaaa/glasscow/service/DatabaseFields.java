package com.eaaa.glasscow.service;

import java.util.HashMap;
import java.util.Map;

public class DatabaseFields
{
    /**
     * Cow card table
     */
	public static final String TABLE_COW = "cow";
    public static final String FIELD_AnimalShortNumber = "AnimalShortNumber";
	public static final String FIELD_JSON = "json";

    /**
     * TypeId's
     */
    public static final int TYPE_ID_Yver = 0;
    public static final int TYPE_ID_Lemmer = 1;
    public static final int TYPE_ID_Brunst = 2;

    /**
     * Observation field names
     */
    public static final String FIELD_OBS_ID = "obs_id";
    public static final String TABLE_OBSERVATION = "observation";
    public static final String FIELD_AnimalId = "AnimalId";
    public static final String FIELD_ShortAnimalNumber = "ShortAnimalNumber";
    public static final String FIELD_HerdId = "HerdId";
    public static final String FIELD_Sent = "Sent";
    public static final String FIELD_ObservationTypeId = "ObservationTypeId"; //0:Yver(Information), 1:Klove og Lemmer(Health), 2:Brunst(Reproduction)
    public static final String FIELD_ObservationDate = "ObservationDate"; //
    public static final String FIELD_LeftFront = "LeftFront"; // (1)
    public static final String FIELD_RightFront = "RightFront"; // (1)
    public static final String FIELD_LeftBack = "LeftBack"; // (1)
    public static final String FIELD_RightBack = "RightBack"; // (1)
    public static final String FIELD_Clots = "Clots"; // (Mælkeklumper) (0)
    public static final String FIELD_VisibleAbnormalities = "VisibleAbnormalities"; // (Mælkeforandringer) (0)
    public static final String FIELD_Sore = "Sore"; // (Øm) (0+1)
    public static final String FIELD_Swollen = "Swollen"; // (Hævet) (0+1)
    public static final String FIELD_Limp = "Limp"; // (Halt) (1)
    public static final String FIELD_Mucus = "Mucus"; // (Slim) (2)
    public static final String FIELD_StandingHeat = "StandingHeat"; // (Spring) (2)
    public static final String FIELD_BleedOff = "BleedOff"; // (Blødning) (2)
    public static final String FIELD_Mount = "Mount"; // (Står) (2)

    /**
     * Display field names
     */
    public static String getDisplayName(String FIELD_NAME) {
        if (FIELD_NAME.equals(FIELD_LeftFront)) {
                return "V.For";
        } else if (FIELD_NAME.equals(FIELD_RightFront)) {
                return "H.For";
        } else if (FIELD_NAME.equals(FIELD_LeftBack)) {
                return "V.Bag";
        } else if (FIELD_NAME.equals(FIELD_RightBack)) {
                return "H.Bag";
        } else if (FIELD_NAME.equals(FIELD_AnimalId)) {
                return "Dyr nr";
        } else if (FIELD_NAME.equals(FIELD_Clots)) {
                return "Klumper";
        } else if (FIELD_NAME.equals(FIELD_VisibleAbnormalities)) {
                return "Unormal";
        } else if (FIELD_NAME.equals(FIELD_Sore)) {
                return "Øm";
        } else if (FIELD_NAME.equals(FIELD_Swollen)) {
                return "Hævet";
        } else if (FIELD_NAME.equals(FIELD_Limp)) {
                return "Halt";
        } else if (FIELD_NAME.equals(FIELD_Mucus)) {
                return "Slim";
        } else if (FIELD_NAME.equals(FIELD_StandingHeat)) {
                return "Spring";
        } else if (FIELD_NAME.equals(FIELD_BleedOff)) {
                return "Blødning";
        } else if (FIELD_NAME.equals(FIELD_Mount)) {
                return "Står";
        }
        return FIELD_NAME;
    }

    /**
     * Observation types field mapping
     */
    public static final HashMap<Integer, String> obsTypeName = new HashMap<Integer, String>();
    public static final HashMap<Integer, String[]> obsTypeFields = new HashMap<Integer, String[]>();
    static {
        obsTypeName.put(0, "Yver");
        obsTypeFields.put(0,new String[]{
                FIELD_LeftFront, FIELD_RightFront, FIELD_LeftBack, FIELD_RightBack, FIELD_Clots, FIELD_Sore, FIELD_VisibleAbnormalities, FIELD_Swollen
        });

        obsTypeName.put(1, "Lemmer");
        obsTypeFields.put(1,new String[]{
                FIELD_LeftFront, FIELD_RightFront, FIELD_LeftBack, FIELD_RightBack, FIELD_Sore, FIELD_Swollen, FIELD_Limp
        });

        obsTypeName.put(2, "Brunst");
        obsTypeFields.put(2,new String[]{
                FIELD_Mucus, FIELD_StandingHeat, FIELD_BleedOff, FIELD_Mount
        });
    }

}
