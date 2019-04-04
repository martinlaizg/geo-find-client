package com.martinlaizg.geofind.data.access.retrofit.service;

import android.util.Log;

import com.martinlaizg.geofind.data.access.database.entity.Map;
import com.martinlaizg.geofind.data.access.retrofit.RestClient;
import com.martinlaizg.geofind.data.access.retrofit.RetrofitService;
import com.martinlaizg.geofind.data.access.retrofit.error.APIError;
import com.martinlaizg.geofind.data.access.retrofit.error.ErrorUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Response;

public class MapService {

	private static MapService mapService;
	private static RestClient restClient;
	private final String TAG = MapService.class.getSimpleName();

	public static MapService getInstance() {
		if (restClient == null) {
			restClient = RetrofitService.getRestClient();
		}
		if (mapService == null) {
			mapService = new MapService();
		}
		return mapService;
	}

	public List<Map> getAllMaps() {
		try {
			Response<List<Map>> response = restClient.getMaps(new HashMap<>()).execute();
			return response.body();
		} catch (IOException e) {
			Log.e(TAG, "getAllMaps: ", e);
		}
		return new ArrayList<>();
	}

	public Map create(Map map) {
		APIError apiError;
		Response<Map> response;
		try {
			response = restClient.createMap(map).execute();
			if (response.isSuccessful()) {
				return response.body();
			} else {
				apiError = ErrorUtils.parseError(response);
				Map m = new Map();
				m.setError(apiError);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Map getMap(String id) {
		Response<Map> response;
		try {
			response = restClient.getMap(id).execute();
			if (response.isSuccessful()) {
				return response.body();
			}
			APIError apiError = ErrorUtils.parseError(response);
			Map m = new Map();
			m.setError(apiError);
		} catch (IOException e) {
			Log.e(TAG, "createMap: ", e);
		}
		return null;
	}

	public Map update(Map map) {
		APIError apiError;
		Response<Map> response;
		try {
			response = restClient.update(map.getId(), map).execute();
			if (response.isSuccessful()) {
				return response.body();
			} else {
				apiError = ErrorUtils.parseError(response);
				Map m = new Map();
				m.setError(apiError);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
