package com.eaaa.glasscow.model;

import com.eaaa.glasscow.R;

public class CowValue {

	public enum RingColor {
		WHITE(R.drawable.ring_white), GREEN(R.drawable.ring_green), YELLOW(R.drawable.ring_yellow), RED(R.drawable.ring_red), BLUE(R.drawable.ring_blue);

		private int color;

		RingColor(int color) {
			this.color = color;
		}
 
		int getColor() {
			return color;
		}
	}

	private String key;
	private String value;
	private RingColor color;

	
	public CowValue(String key, String value) {
		this(key, value, RingColor.WHITE);
	}
	
	public CowValue(String key, String value, RingColor color) {
		this.setKey(key);
		this.setValue(value);
		this.color = color;
	}
	
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public void setRingColor(RingColor color){
		this.color = color;
	}
	
	public int getRingColor(){
		return color.getColor();
	}

}
