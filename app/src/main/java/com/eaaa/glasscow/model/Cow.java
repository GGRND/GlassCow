package com.eaaa.glasscow.model;

import java.util.ArrayList;
import java.util.List;

public class Cow {

	private static final String TOSTRING = "Cow: ";

	private String fullID;
	private int id;
	private List<CowValue> information, health, reproduction;
	private List<CowValue> healthEvents, reproductionEvents;

	public Cow() {
		this.information = new ArrayList<CowValue>();
		this.health = new ArrayList<CowValue>();
		this.reproduction = new ArrayList<CowValue>();
		this.healthEvents = new ArrayList<CowValue>();
		this.reproductionEvents = new ArrayList<CowValue>();
	}

	@Override
	public String toString() {
		return TOSTRING + id;
	}
	
	public String getFullID(){
		return fullID;
	}
	
	public void setFullID(String fullID){
		this.fullID = fullID;
	}

	public int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
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

}
