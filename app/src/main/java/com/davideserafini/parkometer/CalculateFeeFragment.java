package com.davideserafini.parkometer;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.davideserafini.parkometer.model.CarPark;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class CalculateFeeFragment extends Fragment {

	/** Tag to be used with FragmentManager */
	public static final String TAG = "CalculateFeeFragment";

	// UI elements
	/** Calculate button */
	private Button mCalculateBtn;
	/** Add new parking button */
	private Button mCarParkAddNtBtn;
	/** Hourly rate field */
	private EditText mHourlyRateField;
	/** Park start time field */
	private EditText mParkStartField;
	/** Park end time field */
	private EditText mParkEndField;
	/** Park presets spinner */
	private Spinner mCarParkSelectionField;

	private boolean loaded = false;


	public static CalculateFeeFragment newInstance() {
		return new CalculateFeeFragment();
	}

	public CalculateFeeFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View fragmentContent = inflater.inflate(R.layout.fragment_calculate_fee, container, false);

		// Get UI elements and setup actions for calculate button and park start/end fields
		mCalculateBtn = (Button) fragmentContent.findViewById(R.id.calculate_btn);
		addClickListenerToCalculateButton();

		mCarParkAddNtBtn = (Button) fragmentContent.findViewById(R.id.car_park_add_new_btn);
		addClickListenerToAddNewParkButton();

		mHourlyRateField = (EditText) fragmentContent.findViewById(R.id.hourly_rate_field);

		mParkStartField = (EditText) fragmentContent.findViewById(R.id.start_park_field);
		addClickListenerToStartField();
		addDefaultValueToStartField();

		mParkEndField = (EditText) fragmentContent.findViewById(R.id.end_park_field);
		addClickListenerToEndField();

		mCarParkSelectionField = (Spinner) fragmentContent.findViewById(R.id.car_park_selection_field);
		setUpCarParkSelection();

		return fragmentContent;
	}

	/**
	 * Set the current time as default for start field
	 */
	private void addDefaultValueToStartField() {
		final Calendar calendar = Calendar.getInstance();
		mParkStartField.setText(String.format("%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
	}

	/**
	 * Set click listener to the park start time field
	 *
	 * This method displays the time picker to allow for quicker fill and easier error handling.
	 * It defaults to the time in this EditText when present, use current time otherwise
	 */
	private void addClickListenerToStartField() {
		mParkStartField.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				TimePickerFragment timePickerFragment = new TimePickerFragment();
				String[] startTime = mParkStartField.getText().toString().split(":");
				if (startTime.length == 2) {
					// Use the time in field if present
					timePickerFragment.setTime(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));
				} else {
					// Use the current time as the default values for the picker
					final Calendar calendar = Calendar.getInstance();
					timePickerFragment.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
				}
				// Set this field as destination field for the TimePicker OnTimeSet callback
				timePickerFragment.setDestinationField(mParkStartField);
				timePickerFragment.show(getFragmentManager(), TimePickerFragment.TAG);
			}
		});
	}

	/**
	 * Set click listener to the park end time field
	 *
	 * This method displays the time picker to allow for quicker fill and easier error handling
	 * It defaults in this order: to the time in this EditText, to the time in the start time EditText when present, current time otherwise
	 */
	private void addClickListenerToEndField() {
		mParkEndField.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				TimePickerFragment timePickerFragment = new TimePickerFragment();

				String[] startTime = mParkStartField.getText().toString().split(":");
				String[] endtTime = mParkEndField.getText().toString().split(":");
				if (endtTime.length == 2) {
					// Use the time in field if present
					timePickerFragment.setTime(Integer.parseInt(endtTime[0]), Integer.parseInt(endtTime[1]));
				} else if (startTime.length == 2) {
					// Use the time from start field if present
					timePickerFragment.setTime(Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));
				} else {
					// Use the current time as the default values for the picker
					final Calendar calendar = Calendar.getInstance();
					timePickerFragment.setTime(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
				}
				// Set this field as destination field for the TimePicker OnTimeSet callback
				timePickerFragment.setDestinationField(mParkEndField);
				timePickerFragment.show(getFragmentManager(), TimePickerFragment.TAG);
			}
		});
	}

	/**
	 * Set up park selection
	 */
	private void setUpCarParkSelection() {
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
				R.array.no_parks, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCarParkSelectionField.setAdapter(adapter);

		Realm realm = Realm.getInstance(getContext());
		final RealmResults<CarPark> carParks = realm.where(CarPark.class).findAllAsync();
		RealmChangeListener carParkQueryCallback = new RealmChangeListener() {
			@Override
			public void onChange() {
				carParks.removeChangeListeners();
				List<CarPark> carParkObjs = new ArrayList<>();
				for (CarPark carPark : carParks) {
					carParkObjs.add(carPark);
				}
				final CarParkSpinnerAdapter updatedAdapter = new CarParkSpinnerAdapter(getActivity(),
						android.R.layout.simple_spinner_item, carParkObjs);
				updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				mCarParkSelectionField.setAdapter(updatedAdapter);
				mCarParkSelectionField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
						if (i > 0) {
							CarPark carPark = (CarPark) adapterView.getItemAtPosition(i);
							BigDecimal rate = new BigDecimal((double) carPark.getHourlyRate() / 100).setScale(2, RoundingMode.HALF_EVEN);
							mHourlyRateField.setText(rate.toString());
						} else {
							mHourlyRateField.setText(null);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent){}
				});
			}
		};
		carParks.addChangeListener(carParkQueryCallback);
	}

	private void addClickListenerToAddNewParkButton() {
		mCarParkAddNtBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent subActivity = new Intent(getActivity().getApplicationContext(), AddNewCarParkActivity.class);
				startActivity(subActivity);
				getActivity().overridePendingTransition(R.transition.slide_in_from_right, android.R.anim.fade_out);
			}
		});
	}

	/**
	 * Set click listener to the calculate button
	 *
	 * This method validates the form and if everything is correct it calculates and displays the fee
	 */
	private void addClickListenerToCalculateButton() {
		mCalculateBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Validate form
				if (isFormValid()) {
					// Calculate fee
					BigDecimal totalFee = calculateTotalFee();
					// Create the dialog to display the fee
					ShowFeeDialogFragment showFeeDialogFragment = new ShowFeeDialogFragment();
					showFeeDialogFragment.setFee(totalFee);
					showFeeDialogFragment.show(getFragmentManager(), ShowFeeDialogFragment.TAG);
				}
			}

		});
	}

	/**
	 * Validate the data inserted
	 *
	 * At the moment it only checks if the fields are not empty
	 * TODO: verify that the end time is after the start time and notify the user if not
	 *
	 * @return true if the form is valid, false otherwise
	 */
	private boolean isFormValid() {
		boolean valid = true;
		View firstFieldWithError = null;

		if (!isRateFieldValid()) {
			valid = false;
			firstFieldWithError = mHourlyRateField;
		}

		if (!isStartFieldValid()) {
			valid = false;
			if (firstFieldWithError == null) {
				firstFieldWithError = mParkStartField;
			}
		}

		if (!isEndFieldValid()) {
			valid = false;
			if (firstFieldWithError == null) {
				firstFieldWithError = mParkEndField;
			}
		}

		if (!valid) {
			firstFieldWithError.requestFocus();
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
	 * Validate the start hour field
	 *
	 * @return true if field is valid, false otherwise
	 */
	private boolean isStartFieldValid() {
		boolean valid = true;

		if (mParkStartField.getText().toString().length() == 0) {
			valid = false;
			mParkStartField.setError(getString(R.string.park_start_required_error));
		}

		return valid;
	}

	/**
	 * Validate the end hour field
	 *
	 * @return true if field is valid, false otherwise
	 */
	private boolean isEndFieldValid() {
		boolean valid = true;

		if (mParkEndField.getText().toString().length() == 0) {
			valid = false;
			mParkEndField.setError(getString(R.string.park_end_required_error));
		}

		return valid;
	}

	/**
	 * Calculate total fee
	 *
	 * Get the rate per minute and multiply it for the minutes difference between start and end time
	 *
	 * @return BigDecimal representing the total fee
	 */
	private BigDecimal calculateTotalFee() {

		// Get values from fields
		BigDecimal hourlyRate = new BigDecimal(mHourlyRateField.getText().toString());
		String[] parkStartTimeComponents = mParkStartField.getText().toString().split(":");
		String[] parkEndTimeComponents = mParkEndField.getText().toString().split(":");

		// Create calendar object with the given start and end times
		Calendar startTime = Calendar.getInstance();
		startTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parkStartTimeComponents[0]));
		startTime.set(Calendar.MINUTE, Integer.parseInt(parkStartTimeComponents[1]));
		Calendar endTime = Calendar.getInstance();
		endTime.set(Calendar.HOUR_OF_DAY, Integer.parseInt(parkEndTimeComponents[0]));
		endTime.set(Calendar.MINUTE, Integer.parseInt(parkEndTimeComponents[1]));

		// Calculate rate by subtracting the millis, converting to seconds and multiply by the "rate per minutes"
		long startTimeInMillis = startTime.getTimeInMillis();
		long endTimeInMillis = endTime.getTimeInMillis();
		long parkingTimeInMillis = endTimeInMillis - startTimeInMillis;
		long parkingTimeInMinutes = parkingTimeInMillis / 1000 / 60;

		BigDecimal costPerMinute = hourlyRate.divide(new BigDecimal("60"), 20, BigDecimal.ROUND_HALF_EVEN);

		return costPerMinute.multiply(new BigDecimal(parkingTimeInMinutes)).setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}

	public void closeFeeDialog(){
		// Create the dialog to display the fee
		SetAlarmDialogFragment setAlarmDialogFragment = new SetAlarmDialogFragment();
		String[] parkEndTimeComponents = mParkEndField.getText().toString().split(":");
		setAlarmDialogFragment.setTime(Integer.parseInt(parkEndTimeComponents[0]), Integer.parseInt(parkEndTimeComponents[1]));
		setAlarmDialogFragment.show(getFragmentManager(), SetAlarmDialogFragment.TAG);
	}


	/**
	 * TimePicker to choose start and end times
	 */
	public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

		/** Tag to be used with FragmentManager */
		public static final String TAG = "TimePickerFragment";

		// UI Elements
		/** Destination field to be used in onTimeSet callback */
		private EditText mDestinationField;

		// Hour and minute to set when displaying the dialog
		private int mHour;
		private int mMinute;

		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, mHour, mMinute, DateFormat.is24HourFormat(getActivity()));
		}

		/**
		 * Set time to be displayed
		 *
		 * @param hour hour to be displayed
		 * @param minute minute to be displayed
		 */
		public void setTime(int hour, int minute) {
			mHour = hour;
			mMinute = minute;
		}

		/**
		 * Set destination field to be used in onTimeSet
		 *
		 * @param view desination field for onTimeSet callback
		 */
		public void setDestinationField(EditText view) {
			mDestinationField = view;
		}

		/**
		 * Set the chosen time to the destination field
		 *
		 * @param view
		 * @param hourOfDay chosen hour
		 * @param minute chosen minute
		 */
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			mDestinationField.setError(null);
			mDestinationField.setText(String.format("%02d:%02d", hourOfDay, minute));
		}
	}

	/**
	 * Dialog to display the final fee
	 */
	public static class ShowFeeDialogFragment extends DialogFragment {

		/** Tag to be used with FragmentManager */
		public static final String TAG = "ShowFeeDialogFragment";

		/** Fee to be displayed */
		private BigDecimal mFee;


		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("" + mFee)
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							((CalculateFeeFragment) getFragmentManager().findFragmentByTag(CalculateFeeFragment.TAG)).closeFeeDialog();
						}
					});
			// Create the AlertDialog object and return it
			return builder.create();
		}

		/**
		 * Set the fee to be displayed
		 *
		 * @param fee fee to be displayed
		 */
		public void setFee(BigDecimal fee) {
			mFee = fee;
		}
	}

	/**
	 * Dialog to display the final fee
	 */
	public static class SetAlarmDialogFragment extends DialogFragment {

		/** Tag to be used with FragmentManager */
		public static final String TAG = "SetAlarmDialogFragment";

		private int mHour;
		private int mMinute;

		@Override
		@NonNull
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(getString(R.string.set_alarm_question))
					.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
							Calendar calendar = Calendar.getInstance();
							calendar.set(Calendar.HOUR_OF_DAY, mHour);
							calendar.set(Calendar.MINUTE, mMinute);
							calendar.add(Calendar.MINUTE, -10);

							i.putExtra(AlarmClock.EXTRA_MESSAGE, "Park O' Meter");
							i.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY));
							i.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE));
							startActivity(i);
						}
					})
					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {}
					});
			// Create the AlertDialog object and return it
			return builder.create();
		}

		public void setTime(int hour, int minute) {
			mHour = hour;
			mMinute = minute;
		}
	}

}
