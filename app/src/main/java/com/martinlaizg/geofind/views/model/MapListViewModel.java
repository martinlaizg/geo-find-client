package com.martinlaizg.geofind.views.model;

import android.app.Application;

import com.martinlaizg.geofind.MapListFragment;
import com.martinlaizg.geofind.data.access.database.entity.Map;
import com.martinlaizg.geofind.data.access.database.repository.MapRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class MapListViewModel extends AndroidViewModel {

    MapListFragment fragment;

    private MapRepository repository;
    private LiveData<List<Map>> allMaps;


    public MapListViewModel(@NonNull Application application) {
        super(application);
        repository = new MapRepository(application);
        allMaps = repository.getAllMaps();
    }


    public LiveData<List<Map>> getAllMaps() {
        return allMaps;
    }

    public void refreshMaps() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                repository.refreshMaps();

            }
        }).start();
    }

}
