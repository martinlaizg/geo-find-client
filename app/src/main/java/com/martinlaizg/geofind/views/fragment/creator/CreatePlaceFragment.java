package com.martinlaizg.geofind.views.fragment.creator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.martinlaizg.geofind.R;
import com.martinlaizg.geofind.data.access.database.entities.Place;
import com.martinlaizg.geofind.views.viewmodel.CreatorViewModel;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LOCATION_SERVICE;

public class CreatePlaceFragment
		extends Fragment
		implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

	public static final String PLACE_POSITION = "PLACE_POSITION";

	private static final String TAG = CreatePlaceFragment.class.getSimpleName();
	private static final int CAMERA_UPDATE_ZOOM = 15;
	private static final int PERMISSION_ACCESS_COARSE_AND_FINE_LOCATION = 1;

	@BindView(R.id.new_place_name_layout)
	TextInputLayout new_place_name;
	@BindView(R.id.new_place_description_layout)
	TextInputLayout new_place_description;
	@BindView(R.id.alert_no_place_text)
	TextView alert_no_place_text;
	@BindView(R.id.create_place)
	Button create_button;
	@BindView(R.id.new_place_map_view)
	MapView new_place_map_view;

	private CreatorViewModel viewModel;
	private MarkerOptions marker;
	private GoogleMap gMap;
	private Place place;

	@Override
	public void onClick(View v) {
		alert_no_place_text.setVisibility(View.GONE);
		try {
			if(Objects.requireNonNull(new_place_name.getEditText()).getText().toString().trim()
					.isEmpty()) {
				new_place_name.setError(getString(R.string.required_name));
				return;
			}
			if(new_place_name.getEditText().getText().toString().length() >
					getResources().getInteger(R.integer.max_name_length)) {
				new_place_name.setError(getString(R.string.text_too_long));
				return;
			}
			new_place_name.setError("");
			if(Objects.requireNonNull(new_place_description.getEditText()).getText().toString()
					.trim().isEmpty()) {
				new_place_description.setError(getString(R.string.required_description));
				return;
			}
			if(new_place_description.getEditText().getText().toString().length() >
					getResources().getInteger(R.integer.max_description_length)) {
				new_place_description.setError(getString(R.string.text_too_long));
				return;
			}
			new_place_description.setError("");
			if(marker == null) {
				alert_no_place_text.setVisibility(View.VISIBLE);
				return;
			}
		} catch(NullPointerException ex) {
			Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		}

		place.setName(new_place_name.getEditText().getText().toString().trim());
		place.setDescription(new_place_description.getEditText().getText().toString().trim());
		place.setPosition(marker.getPosition());

		viewModel.setPlace(place);
		Navigation.findNavController(requireActivity(), R.id.main_fragment_holder).popBackStack();
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		gMap = googleMap;
		if(requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED &&
				requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
						PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
					                   android.Manifest.permission.ACCESS_FINE_LOCATION},
			                   PERMISSION_ACCESS_COARSE_AND_FINE_LOCATION);
			Toast.makeText(requireActivity(), getString(R.string.rejected_location_access),
			               Toast.LENGTH_SHORT).show();
			return;
		}
		setPlace();
	}

	@SuppressLint("MissingPermission")
	private void setPlace() {
		Location usrLocation = getLastKnownLocation();
		if(gMap != null) {
			gMap.setMyLocationEnabled(true);
			gMap.setOnMapLongClickListener(this);
			gMap.getUiSettings().setMyLocationButtonEnabled(true);
			gMap.getUiSettings().setMapToolbarEnabled(false);
			gMap.getUiSettings().setTiltGesturesEnabled(false);

			LatLng usrLatLng = new LatLng(usrLocation.getLatitude(), usrLocation.getLongitude());
			gMap.clear();
			if(marker != null) {
				gMap.moveCamera(CameraUpdateFactory
						                .newLatLngZoom(marker.getPosition(), CAMERA_UPDATE_ZOOM));
				gMap.addMarker(marker);
			} else {
				gMap.animateCamera(
						CameraUpdateFactory.newLatLngZoom(usrLatLng, CAMERA_UPDATE_ZOOM));
			}
		}
	}

	@SuppressLint("MissingPermission")
	private Location getLastKnownLocation() {
		LocationManager locationManager = (LocationManager) requireActivity()
				.getSystemService(LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders(true);
		Location bestLocation = null;
		for(String provider : providers) {
			Location l = locationManager.getLastKnownLocation(provider);
			if(l == null) {
				continue;
			}
			if(bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
				// Found best last known location: %s", l);
				bestLocation = l;
			}
		}
		return bestLocation;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		if(requestCode == PERMISSION_ACCESS_COARSE_AND_FINE_LOCATION) {
			if(permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED &&
					permissions[1].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
					grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				setPlace();
			}
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_create_place, container, false);
		ButterKnife.bind(this, view);

		new_place_map_view.onCreate(savedInstanceState);
		new_place_map_view.onResume();
		new_place_map_view.getMapAsync(this);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		viewModel = ViewModelProviders.of(requireActivity()).get(CreatorViewModel.class);
		viewModel.setLoad(false);
		Bundle b = getArguments();
		if(b != null) {
			int position = b.getInt(PLACE_POSITION, viewModel.getPlaces().size());
			place = viewModel.getPlace(position);
			if(place != null) {
				if(place.getName() != null) {
					Objects.requireNonNull(new_place_name.getEditText()).setText(place.getName());
				}
				if(place.getDescription() != null) {
					Objects.requireNonNull(new_place_description.getEditText())
							.setText(place.getDescription());
				}
				if(place.getPosition() != null) {
					onMapLongClick(place.getPosition());
				}
			} else {
				Toast.makeText(requireContext(), getResources().getString(R.string.invalid_place),
				               Toast.LENGTH_SHORT).show();
			}
		}
		create_button.setOnClickListener(this);
		if(marker != null) create_button.setText(R.string.update_place);
	}

	@Override
	public void onMapLongClick(LatLng latLng) {
		alert_no_place_text.setVisibility(View.GONE);
		MarkerOptions m = new MarkerOptions().position(latLng);
		if(gMap != null) {
			gMap.clear();
			gMap.addMarker(m);
		}
		marker = m;

	}
}