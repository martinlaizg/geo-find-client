package com.martinlaizg.geofind.data.repository;

import android.app.Application;

import com.martinlaizg.geofind.data.access.database.AppDatabase;
import com.martinlaizg.geofind.data.access.database.dao.PlaceDAO;
import com.martinlaizg.geofind.data.access.database.entities.Place;

import java.util.List;

public class PlaceRepository {

	private static final String TAG = PlaceRepository.class.getSimpleName();
	private static PlaceRepository instance;

	private final PlaceDAO placeDAO;

	private PlaceRepository(Application application) {
		AppDatabase database = AppDatabase.getInstance(application);
		placeDAO = database.placeDAO();
	}

	public static PlaceRepository getInstance(Application application) {
		if(instance == null) instance = new PlaceRepository(application);
		return instance;
	}

	/**
	 * Insert a List of Place into the database
	 *
	 * @param places
	 * 		places to be inserted
	 */
	public void insert(List<Place> places) {
		for(Place p : places) insert(p);
	}

	/**
	 * Insert a single Place into the database
	 *
	 * @param place
	 * 		place to be inserted
	 */
	private void insert(Place place) {
		Place p = placeDAO.getPlace(place.getId());
		if(p == null) {
			placeDAO.insert(place);
		} else {
			placeDAO.update(place);
		}
	}

	/**
	 * Get the list of places, by tour id, from the database
	 *
	 * @param tour_id
	 * 		tour id of the places
	 * @return the list of places
	 */
	List<Place> getTourPlaces(Integer tour_id) {
		return placeDAO.getTourPlaces(tour_id);

	}

}
