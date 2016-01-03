package com.davideserafini.parkometer;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CalculateFeeActivity extends AppCompatActivity {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculate_fee);

		FragmentManager fragmentManager = getSupportFragmentManager();
		CalculateFeeFragment calculateFeeFragment = (CalculateFeeFragment) fragmentManager.findFragmentByTag(CalculateFeeFragment.TAG);

		if (calculateFeeFragment == null) {
			calculateFeeFragment = CalculateFeeFragment.newInstance();
			fragmentManager.beginTransaction()
					.add(R.id.activity_content, calculateFeeFragment, CalculateFeeFragment.TAG)
					.commit();
		}
	}
}
