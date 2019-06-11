package com.martinlaizg.geofind.views.fragment.creator;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.martinlaizg.geofind.R;
import com.martinlaizg.geofind.data.access.api.service.exceptions.APIException;
import com.martinlaizg.geofind.data.access.database.entities.Tour;
import com.martinlaizg.geofind.views.adapter.CreatorPlacesAdapter;
import com.martinlaizg.geofind.views.fragment.single.TourFragment;
import com.martinlaizg.geofind.views.viewmodel.CreatorViewModel;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreatorFragment
		extends Fragment
		implements View.OnClickListener {

	public static final String TOUR_ID = "TOUR_ID";
	private static final String TAG = CreatorFragment.class.getSimpleName();

	@BindView(R.id.tour_name)
	TextView tour_name;
	@BindView(R.id.tour_description)
	TextView tour_description;

	@BindView(R.id.add_place_button)
	MaterialButton add_place_button;
	@BindView(R.id.create_tour_button)
	MaterialButton create_tour_button;
	@BindView(R.id.edit_button)
	MaterialButton edit_button;

	@BindView(R.id.places_list)
	RecyclerView places_list;

	private CreatorViewModel viewModel;
	private CreatorPlacesAdapter adapter;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "onCreateView: ");
		View view = inflater.inflate(R.layout.fragment_creator, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		Log.e(TAG, "onViewCreated: ");
		viewModel = ViewModelProviders.of(requireActivity()).get(CreatorViewModel.class);
		adapter = new CreatorPlacesAdapter();
		places_list.setLayoutManager(new LinearLayoutManager(requireActivity()));
		places_list.setAdapter(adapter);

		// Load tour
		Bundle b = getArguments();
		int tour_id = Objects.requireNonNull(b).getInt(TOUR_ID);
		viewModel.loadTour(tour_id).observe(this, this::setTour);

		// set buttons
		add_place_button.setOnClickListener(v -> {
			Bundle p = new Bundle();
			p.putInt(CreatePlaceFragment.PLACE_POSITION, viewModel.getPlaces().size());
			Navigation.findNavController(requireActivity(), R.id.main_fragment_holder)
					.navigate(R.id.toCreatePlace, p);
		});
		edit_button.setOnClickListener(
				v -> Navigation.findNavController(requireActivity(), R.id.main_fragment_holder)
						.navigate(R.id.toEditTourName));
		create_tour_button.setOnClickListener(this);
	}

	private void setTour(Tour tour) {
		if(tour != null) {
			adapter.setPlaces(tour.getPlaces());
			if(tour.getId() != 0) {
				create_tour_button.setText(R.string.update_tour);
			}
			if(tour.getName().isEmpty()) {
				Navigation.findNavController(requireActivity(), R.id.main_fragment_holder)
						.navigate(R.id.toCreateTour);
			} else {
				tour_name.setText(tour.getName());
			}
			if(tour.getDescription().isEmpty()) {
				tour_description.setText(getResources().getString(R.string.without_decription));
			} else {
				tour_description.setText(tour.getDescription());
			}
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// reset the view model
		viewModel.reset();
	}

	@Override
	public void onClick(View v) {
		create_tour_button.setEnabled(false);
		viewModel.createTour().observe(this, tour -> {
			create_tour_button.setEnabled(true);
			if(tour == null) {
				APIException err = viewModel.getError();
				Toast.makeText(requireActivity(), err.getType().toString(), Toast.LENGTH_LONG)
						.show();
				Navigation.findNavController(requireActivity(), R.id.main_fragment_holder)
						.popBackStack(R.id.navTourList, false);
				return;
			}
			Toast.makeText(requireActivity(), R.string.tour_created, Toast.LENGTH_SHORT).show();
			Bundle b = new Bundle();
			b.putInt(TourFragment.TOUR_ID, tour.getId());
			Navigation.findNavController(requireActivity(), R.id.main_fragment_holder)
					.navigate(R.id.toNewTour, b);
		});
	}
}
