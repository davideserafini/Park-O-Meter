package com.davideserafini.parkometer.model;

import io.realm.RealmObject;


/**
 * City model that uses Realm to be persisted in database
 *
 * It includes:
 * - name
 */
public class City extends RealmObject {

	private String name;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
