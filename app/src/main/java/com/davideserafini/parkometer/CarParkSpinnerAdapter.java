package com.davideserafini.parkometer;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.davideserafini.parkometer.model.CarPark;

import java.util.List;


public class CarParkSpinnerAdapter extends ArrayAdapter<CarPark> {

	private LayoutInflater mInflater;
	private int mResource;
	private int mDropDownResource;

	/**
	 * {@inheritDoc}
	 */
	public CarParkSpinnerAdapter(Context context, int resource, List<CarPark> objects) {
		super(context, resource, objects);
		mInflater = LayoutInflater.from(context);
		mResource = resource;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDropDownViewResource(int resource) {
		super.setDropDownViewResource(resource);
		this.mDropDownResource = resource;
	}

	private View createViewFromResource(View convertView, ViewGroup parent, int resource) {
		View view;
		if (convertView == null) {
			view = mInflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}
		return view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView) createViewFromResource(convertView, parent, mResource);
		String textToSet = position == 0 ? "Scegli un parcheggio" : getItem(position).getName();
		text.setText(textToSet);
		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		TextView text = (TextView) createViewFromResource(convertView, parent, mDropDownResource);
		String textToSet = position == 0 ? "Scegli un parcheggio" : getItem(position).getName();
		text.setText(textToSet);
		return text;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getCount() {
		return super.getCount() + 1;
	}

	/**
	 * {@inheritDoc}
	 */
	public CarPark getItem(int position) {
		return super.getItem(position - 1);
	}

}
