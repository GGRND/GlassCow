package com.eaaa.glasscow;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eaaa.glasscow.model.Cow;
import com.eaaa.glasscow.model.CowValue;

public class Screen_CowData {

	public enum DataType {
		INFORMATION(R.string.information), HEALTH(R.string.health), REPRODUCTION(R.string.reproduction);

		int title;

		DataType(int title) {
			this.title = title;
		}

		int getTitle() {
			return title;
		}
	}

	private int cowID;
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
		temp.setText(type.getTitle());

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
		switch (type.getTitle()) {
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

	public void updateCow(int id, List<CowValue> values) {
		this.updateCow(id, values, null);
	}

	public void updateCow(int id, List<CowValue> values, List<CowValue> events) {
		this.cowID = id;
		txtID.setText("Cow: " + id);
		this.values = values;
		this.hasEvents = (events != null);
		this.currentPage = 0;
		this.totalPages = (values.size() + 2) / 3;

		nextPage();
	}

	public boolean nextPage() {
		if (1 < totalPages) {
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

			return true;
		}
		return false;
	}

	public boolean hasMore() {
		return (totalPages > 1);
	}

	public boolean hasEvents() {
		return hasEvents;
	}

	public int getCowID() {
		return cowID;
	}

	public int getTitle() {
		return type.getTitle();
	}

	public View getContentView() {
		return contentView;
	}
}
