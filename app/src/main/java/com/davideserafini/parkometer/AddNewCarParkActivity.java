package com.davideserafini.parkometer;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AddNewCarParkActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_car_park);

		FragmentManager fragmentManager = getSupportFragmentManager();
		AddNewCarParkFragment addNewCarParkFragment = (AddNewCarParkFragment) fragmentManager.findFragmentByTag(AddNewCarParkFragment.TAG);

		if (addNewCarParkFragment == null) {
			addNewCarParkFragment = AddNewCarParkFragment.newInstance();
			fragmentManager.beginTransaction()
					.add(R.id.activity_content, addNewCarParkFragment, AddNewCarParkFragment.TAG)
					.commit();
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, R.transition.slide_out_to_right);
	}
}
