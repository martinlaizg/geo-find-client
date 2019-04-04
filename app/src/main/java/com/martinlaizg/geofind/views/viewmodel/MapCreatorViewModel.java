package com.martinlaizg.geofind.views.viewmodel;

import android.app.Application;

import com.martinlaizg.geofind.data.access.database.entity.Location;
import com.martinlaizg.geofind.data.access.database.entity.Map;
import com.martinlaizg.geofind.data.access.database.entity.enums.PlayLevel;
import com.martinlaizg.geofind.data.access.database.repository.LocationRepository;
import com.martinlaizg.geofind.data.access.database.repository.MapRepository;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MapCreatorViewModel
		extends AndroidViewModel {

	private final MapRepository mapRepo;
	private final LocationRepository locRepo;
	private Map createdMap;
	private List<Location> createdLocations;


	public MapCreatorViewModel(@NonNull Application application) {
		super(application);
		mapRepo = new MapRepository(application);
		locRepo = new LocationRepository(application);
	}


	public MutableLiveData<Map> createMap() {
		MutableLiveData<Map> m = new MutableLiveData<>();
		new Thread(() -> {
			if (createdMap.getId().isEmpty()) { // Create map
				createdMap = mapRepo.create(createdMap);
				// TODO implement create locations
				//				if (!createdLocations.isEmpty()) {
				//					for (int i = 0; i < createdLocations.size(); i++) {
				//						createdLocations.get(i).setMap_id(createdMap.getId());
				//					}
				//					locRepo.create(createdLocations);
				//				}
			} else { // Update map
				// TODO implement update locations
				createdMap = mapRepo.update(createdMap);
			}
			m.postValue(createdMap);
		}).start();
		return m;
	}

	public List<Location> getCreatedLocations() {
		if (createdLocations == null) createdLocations = new ArrayList<>();
		return createdLocations;
	}

	public Map getCreatedMap() {
		if (createdMap == null) createdMap = new Map();
		return createdMap;
	}

	public void addLocation(String name, String description, String lat, String lon, int position) {
		Location l = new Location();
		l.setName(name);
		l.setDescription(description);
		l.setLat(lat);
		l.setLon(lon);
		if (createdLocations == null) {
			createdLocations = new ArrayList<>();
		}
		l.setPosition(position);
		if (position >= createdLocations.size()) {
			createdLocations.add(l);
		} else {
			createdLocations.set(position, l);
		}
	}


	public void setMap(String name, String description, String creator_id, PlayLevel pl) {
		if (createdMap == null) {
			createdMap = new Map();
		}
		createdMap.setName(name);
		createdMap.setDescription(description);
		createdMap.setCreator_id(creator_id);
		createdMap.setMin_level(pl);
	}

	public MutableLiveData<Map> getMap(String map_id) {
		return mapRepo.getMap(map_id);
	}

	public void clear() {
		createdMap = null;
		createdLocations = null;
	}

	public void setMap(Map map) {
		createdMap = map;
	}
}
