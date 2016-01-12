package com.davideserafini.parkometer.model;

import io.realm.RealmObject;


/**
 * CarPark model that uses Realm to be persisted in database
 *
 * It includes:
 * - name
 * - hourly rate
 * - city
 */
public class CarPark extends RealmObject {

	private City city;
	// Realm doesn't support BigDecimal as of now, solution proposed is to
	// multiply the currency value by 100 and store the cents,
	// then divide back by 100 and convert to BigDecimal
	// ref http://stackoverflow.com/questions/26227402/using-realm-io-to-store-money-values
	private long hourlyRate;
	private String name;


	/* Realm getters and setter */
	public long getHourlyRate() {
		return hourlyRate;
	}

	public void setHourlyRate(long hourlyRate) {
		this.hourlyRate = hourlyRate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

}
