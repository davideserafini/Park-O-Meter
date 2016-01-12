package com.davideserafini.parkometer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.davideserafini.parkometer.model.CarPark;
import com.davideserafini.parkometer.model.City;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;


public class AddNewCarParkFragment extends Fragment {

	/** Tag to be used with FragmentManager */
	public static final String TAG = "AddNewCarParkFragment";

	// UI elements
	/** Save car park button */
	private Button mSaveBtn;
	/** Car park name field */
	private EditText mCarParkNameField;
	/** City field */
	private EditText mCityField;
	/** Hourly rate field */
	private EditText mHourlyRateField;


	public static AddNewCarParkFragment newInstance() {
		return new AddNewCarParkFragment();
	}

	public AddNewCarParkFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View fragmentContent = inflater.inflate(R.layout.fragment_add_new_car_park, container, false);

		mCarParkNameField = (EditText) fragmentContent.findViewById(R.id.car_park_name_field);
		mHourlyRateField = (EditText) fragmentContent.findViewById(R.id.hourly_rate_field);
		mCityField = (EditText) fragmentContent.findViewById(R.id.city_field);
		mSaveBtn = (Button) fragmentContent.findViewById(R.id.save_car_park_btn);
		addClickListenerToSaveButton();

		return fragmentContent;
	}

	/**
	 * Set click listener to the save button
	 *
	 * This method validates the form and if everything is correct it saves the new car park
	 */
	private void addClickListenerToSaveButton() {
		mSaveBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isFormValid()) {
					City city;
					String currentCity = mCityField.getText().toString().trim();
					String carParkName = mCarParkNameField.getText().toString();

					//Realm.deleteRealm(new RealmConfiguration.Builder(getContext()).build());
					Realm realm = Realm.getInstance(getContext());

					RealmResults<City> citiesResult = realm.where(City.class)
															 .equalTo("name", currentCity)
															 .findAll();

					Log.d("REALM", "citiesResult.size(): " + citiesResult.size());
					RealmResults<CarPark> carParksResults = realm.where(CarPark.class)
																	.equalTo("name", carParkName)
																	.equalTo("city.name", currentCity)
																	.findAll();

					Log.d("REALM", "carParksResults.size(): " + carParksResults.size());

					if (carParksResults.size() == 0) {
						realm.beginTransaction();
						CarPark carPark = realm.createObject(CarPark.class);
						carPark.setName(carParkName);
						if (citiesResult.size() == 0) {
							city = realm.createObject(City.class);
							city.setName(currentCity);
						} else {
							city = citiesResult.get(0);
						}
						carPark.setCity(city);
						String hourlyRate = mHourlyRateField.getText().toString();
						hourlyRate = hourlyRate.replace(".", "").replace(",", ""); // TODO: convert to regex
						carPark.setHourlyRate(Long.valueOf(hourlyRate));
						realm.commitTransaction();
					}
					realm.close();
				}
			}
		});
	}

	/**
	 * Validate the data inserted
	 *
	 * At the moment it only checks if the fields are not empty
	 *
	 * @return true if the form is valid, false otherwise
	 */
	private boolean isFormValid() {
		boolean valid = true;
		View firstFieldWithError = null;

		if (!isCarParkNameValid()) {
			valid = false;
			firstFieldWithError = mCarParkNameField;
		}

		if (!isRateFieldValid()) {
			valid = false;
			if (firstFieldWithError == null) {
				firstFieldWithError = mHourlyRateField;
			}
		}

		if (!isCityValid()) {
			valid = false;
			if (firstFieldWithError == null) {
				firstFieldWithError = mCityField;
			}
		}

		return valid;
	}

	/**
	 * Validate the car park name field
	 *
	 * @return true if field is valid, false otherwise
	 */
	private boolean isCarParkNameValid() {
		boolean valid = true;

		if (mCarParkNameField.getText().toString().length() == 0) {
			valid = false;
			mCarParkNameField.setError(getString(R.string.car_park_required_error));
		}

		return valid;
	}

	/**
	 * Validate the rate field
	 *
	 * @return true if field is valid, false otherwise
	 */
	private boolean isRateFieldValid() {
		boolean valid = true;

		if (mHourlyRateField.getText().toString().length() == 0) {
			valid = false;
			mHourlyRateField.setError(getString(R.string.hourly_rate_required_error));
		}

		return valid;
	}

	/**
	 * Validate the car park name field
	 *
	 * @return true if field is valid, false otherwise
	 */
	private boolean isCityValid() {
		boolean valid = true;

		if (mCityField.getText().toString().length() == 0) {
			valid = false;
			mCityField.setError(getString(R.string.city_required_error));
		}

		return valid;
	}

}
