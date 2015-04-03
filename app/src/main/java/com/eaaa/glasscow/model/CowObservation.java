package com.eaaa.glasscow.model;

import com.eaaa.glasscow.service.DatabaseFields;

import java.util.HashMap;

/**
 * Created by morten on 02/04/15.
 */
public class CowObservation {
    private String id, typeId, observationDate;
    private HashMap<String, Boolean> valueMap = new HashMap<String, Boolean>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getObservationDate() {
        return observationDate;
    }

    public void setObservationDate(String observationDate) {
        this.observationDate = observationDate;
    }

    public Boolean getValue(String key) {
        return valueMap.get(key);
    }

    public void setValue(String key, Boolean value) {
        valueMap.put(key,value);
    }

    /**
     * Return human readable compact description of the observation
     * @return
     */
    public String getDisplayText() {
        String result = new String();
        String[] fields = DatabaseFields.obsTypeFields.get(new Integer(typeId).intValue());
        for (int i=0 ; i<fields.length ; i++) {
            String FieldKey = fields[i];
            Boolean FieldVal = getValue(FieldKey);
            if (FieldVal!=Boolean.TRUE)
                continue;
            if (!result.isEmpty())
                result += ", ";
            result += DatabaseFields.getDisplayName(FieldKey);
        }
        return result;
    }
}
