package com.eaaa.glasscow;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.model.CowValue;
import com.eaaa.glasscow.service.DatabaseFields;

public class Screen_CowData {

    public int getObservationTypeId() {
        if (type==DataType.INFORMATION)
            return DatabaseFields.TYPE_ID_Yver;
        else if (type==DataType.HEALTH)
            return DatabaseFields.TYPE_ID_Lemmer;
        else if (type==DataType.REPRODUCTION)
            return DatabaseFields.TYPE_ID_Brunst;
        else
            new Exception("Screen_CowData: Unknown type").printStackTrace();
        return -1; //never reached.
    }

    public enum DataType {
		INFORMATION(R.string.information), HEALTH(R.string.health), REPRODUCTION(R.string.reproduction);

		int title;

		DataType(int title) {
			this.title = title;
		}

		int getTitleID() {
			return title;
		}
	}

	private String cowID;
	private DataType type;
	private int currentPage, totalPages;
	private List<CowValue> values;
	private boolean hasEvents;

	private TextView txtID, txtFooter;
	private ImageView[] imageViews;
	private TextView[] txtLabelViews, txtValueViews;

	private View contentView;

	@SuppressLint("InflateParams")
	public Screen_CowData(Activity activity, DataType type) {
		this.type = type;
		contentView = activity.getLayoutInflater().inflate(R.layout.standard, null);
		init();
	}

	private void init() {
		TextView temp = (TextView) contentView.findViewById(R.id.Title);
		temp.setText(type.getTitleID());

		txtID = (TextView) contentView.findViewById(R.id.CowID);
		txtFooter = (TextView) contentView.findViewById(R.id.footer);

		txtLabelViews = new TextView[3];
		txtLabelViews[0] = (TextView) contentView.findViewById(R.id.c1txt1);
		txtLabelViews[1] = (TextView) contentView.findViewById(R.id.c2txt1);
		txtLabelViews[2] = (TextView) contentView.findViewById(R.id.c3txt1);

		txtValueViews = new TextView[3];
		txtValueViews[0] = (TextView) contentView.findViewById(R.id.c1txt2);
		txtValueViews[1] = (TextView) contentView.findViewById(R.id.c2txt2);
		txtValueViews[2] = (TextView) contentView.findViewById(R.id.c3txt2);

		imageViews = new ImageView[3];
		imageViews[0] = (ImageView) contentView.findViewById(R.id.c1img);
		imageViews[1] = (ImageView) contentView.findViewById(R.id.c2img);
		imageViews[2] = (ImageView) contentView.findViewById(R.id.c3img);
	}

	public void updateCow(Cow cow) {
		switch (type.getTitleID()) {
		case R.string.information:
			this.updateCow(cow.getId(), cow.getInformation());
			break;
		case R.string.health:
			this.updateCow(cow.getId(), cow.getHealth(), cow.getHealthEvents());
			break;
		case R.string.reproduction:
			this.updateCow(cow.getId(), cow.getReproduction(), cow.getReproductionEvents());
			break;
		}
	}

	public void updateCow(String id, List<CowValue> values) {
		this.updateCow(id, values, null);
	}

	public void updateCow(String id, List<CowValue> values, List<CowValue> events) {
		this.cowID = id;
		txtID.setText("Cow: " + id);
		this.values = values;
		this.hasEvents = (events != null);
		this.currentPage = 0;
		this.totalPages = (values.size() + 2) / 3;

		nextPage();
	}

	public void nextPage() {
		currentPage++;
		if (currentPage > totalPages) {
			currentPage = 1;
		}
		txtFooter.setText("p. " + currentPage + "/" + totalPages);

		int item = currentPage * 3 - 3;
		int i = 0;
		while (i < 3 && item < values.size()) {
			CowValue temp = values.get(item);
			imageViews[i].setImageResource(temp.getRingColor());
			txtLabelViews[i].setText(temp.getKey());
			txtValueViews[i].setText(temp.getValue());
			item++;
			i++;
		}
		while (i < 3) {
			imageViews[i].setImageResource(R.drawable.ring_black);
			txtLabelViews[i].setText("");
			txtValueViews[i].setText("");
			i++;
		}

	}

	public boolean hasMore() {
		return (totalPages > 1);
	}

	public boolean hasEvents() {
		return hasEvents;
	}

	public String getCowID() {
		return cowID;
	}

	public int getTitle() {
		return type.getTitleID();
	}

	public View getContentView() {
		return contentView;
	}
}
