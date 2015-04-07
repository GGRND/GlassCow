package com.eaaa.glasscow.model;

import java.util.ArrayList;
import java.util.List;

public class Cow {

	private static final String TOSTRING = "Cow: ";

	private String herdId, animalId, fullNumber, shortNumber;
	private List<CowValue> information, health, reproduction;
	private List<CowValue> healthEvents, reproductionEvents;
    private ArrayList<CowObservation> observations; // Map: TypeId -> CowObservation container

	public Cow() {
		this.information = new ArrayList<CowValue>();
		this.health = new ArrayList<CowValue>();
		this.reproduction = new ArrayList<CowValue>();
		this.healthEvents = new ArrayList<CowValue>();
		this.reproductionEvents = new ArrayList<CowValue>();
        this.observations = new ArrayList<CowObservation>();
	}

    public ArrayList<CowObservation> getObservations(Integer typeId) {
        ArrayList<CowObservation> result = new ArrayList<CowObservation>();
        for (int i=0 ; i<observations.size() ; i++) {
            if (observations.get(i).getTypeId().equals(typeId))
                result.add(observations.get(i));
        }
        return result;
    }

    @Override
	public String toString() {
		return TOSTRING + shortNumber;
	}
	
	public String getFullNumber(){
		return fullNumber;
	}
	
	public void setFullNumber(String fullID){
		this.fullNumber = fullID;
	}

	public String getShortNumber() {
		return shortNumber;
	}

	protected void setShortNumber(String id) {
		this.shortNumber = id;
	}

	protected void addInformation(CowValue element) {
		information.add(element);
	}

	public List<CowValue> getInformation() {
		return information;
	}

	protected void addHealth(CowValue element) {
		health.add(element);
	}

	public List<CowValue> getHealth() {
		return health;
	}

	protected void addReproduction(CowValue element) {
		reproduction.add(element);
	}

	public List<CowValue> getReproduction() {
		return reproduction;
	}

	protected void addHealthEvent(CowValue element) {
		healthEvents.add(element);
	}

	public List<CowValue> getHealthEvents() {
		return healthEvents;
	}

	protected void addReproductionEvent(CowValue element) {
		reproductionEvents.add(element);
	}

	public List<CowValue> getReproductionEvents() {
		return reproductionEvents;
	}

    public ArrayList<CowObservation> getObservations(int typeId) {
        ArrayList<CowObservation> result = new ArrayList<CowObservation>();
        for (int i=0; i<observations.size(); i++){
            CowObservation obs = observations.get(i);
            if (new Integer(obs.getTypeId()).intValue()==(typeId))
                result.add(obs);
        }
        return result;
    }

    public void addObservation(CowObservation obs) {
        this.observations.add(obs);
    }

    public ArrayList<CowObservation> getObservations() {
        return observations;
    }

    public void setObservations(ArrayList<CowObservation> obsList) {
        observations = obsList;
    }

    public void setHerdId(String herdId) {
        this.herdId = herdId;
    }

    public String getHerdId() {
        return herdId;
    }

    public void setAnimalId(String animalId) {
        this.animalId = animalId;
    }

    public String getAnimalId() {
        return animalId;
    }
}
