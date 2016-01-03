package com.davideserafini.parkometer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.Calendar;


public class CalculateFeeFragment extends Fragment {

	public static final String TAG = "CalculateFeeFragment";

	private Button mCalculateBtn;
	private EditText mHourlyRateField;
	private EditText mParkStartField;
	private EditText mParkEndField;


	public static CalculateFeeFragment newInstance() {
		return new CalculateFeeFragment();
	}

	public CalculateFeeFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		View fragmentContent = inflater.inflate(R.layout.fragment_calculate_fee, container, false);

		mCalculateBtn = (Button) fragmentContent.findViewById(R.id.calculate_btn);
		addClickListenerToCalculateButton();

		mHourlyRateField = (EditText) fragmentContent.findViewById(R.id.hourly_rate_field);
		mParkStartField = (EditText) fragmentContent.findViewById(R.id.start_park_field);
		mParkEndField = (EditText) fragmentContent.findViewById(R.id.end_park_field);

		return fragmentContent;
	}

	private void addClickListenerToCalculateButton() {
		mCalculateBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if (isFormValid()) {
					BigDecimal totalFee = calculateTotalFee();
					ShowFeeDialogFragment showFeeDialogFragment = new ShowFeeDialogFragment();
					showFeeDialogFragment.setFee(totalFee);
					showFeeDialogFragment.show(getFragmentManager(), ShowFeeDialogFragment.TAG);
				}
			}

		});
	}

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

		// Calculate rate by subtracting the millis, converting to seconds and multiply by the "rate per seconds"
		long startTimeInMillis = startTime.getTimeInMillis();
		long endTimeInMillis = endTime.getTimeInMillis();
		long parkingTimeInMillis = endTimeInMillis - startTimeInMillis;
		long parkingTimeInMinutes = parkingTimeInMillis / 1000 / 60;

		BigDecimal costPerMinute = hourlyRate.divide(new BigDecimal("60"), 20, BigDecimal.ROUND_HALF_EVEN);
		Log.d("FEE", "costPerMinute: " + costPerMinute);

		return costPerMinute.multiply(new BigDecimal(parkingTimeInMinutes)).setScale(2, BigDecimal.ROUND_HALF_EVEN);
	}

	public class ShowFeeDialogFragment extends DialogFragment {

		public static final String TAG = "ShowFeeDialogFragment";
		private BigDecimal mFee;


		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage("" + mFee)
					.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// FIRE ZE MISSILES!
						}
					});
			// Create the AlertDialog object and return it
			return builder.create();
		}

		public void setFee(BigDecimal fee) {
			mFee = fee;
		}

	}

}
