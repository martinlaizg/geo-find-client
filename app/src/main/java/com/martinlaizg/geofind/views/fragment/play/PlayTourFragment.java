package com.martinlaizg.geofind.views.fragment.play;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.martinlaizg.geofind.R;
import com.martinlaizg.geofind.config.Preferences;
import com.martinlaizg.geofind.data.access.api.error.ErrorType;
import com.martinlaizg.geofind.data.access.database.entities.Place;
import com.martinlaizg.geofind.data.access.database.entities.User;
import com.martinlaizg.geofind.views.viewmodel.PlayTourViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

abstract class PlayTourFragment
		extends Fragment
		implements LocationListener {

	public static final String TOUR_ID = "TOUR_ID";

	static final int PERMISSION_ACCESS_COARSE_AND_FINE_LOCATION = 1;

	private static final float DISTANCE_TO_COMPLETE = 15;
	private static final long LOC_TIME_REQ = 200;
	private static final float LOC_DIST_REQ = 2;

	@BindView(R.id.place_name)
	TextView place_name;
	@BindView(R.id.place_description)
	TextView place_description;
	@BindView(R.id.place_complete)
	TextView place_complete;
	@BindView(R.id.place_distance)
	TextView place_distance;

	Place place;
	Location usrLocation;
	Location placeLocation;
	float distance = Float.MAX_VALUE;
	private PlayTourViewModel viewModel;
	private LocationManager locationManager;
	private AlertDialog questionDialog;

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
			@NonNull int[] grantResults) {
		if(requestCode == PERMISSION_ACCESS_COARSE_AND_FINE_LOCATION && permissions.length >= 2) {
			if(permissions[0].equals(Manifest.permission.ACCESS_COARSE_LOCATION) &&
					grantResults[0] == PackageManager.PERMISSION_GRANTED &&
					permissions[1].equals(Manifest.permission.ACCESS_FINE_LOCATION) &&
					grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				Log.d(TAG(), "onRequestPermissionsResult: success");
			}
			Log.d(TAG(), "onRequestPermissionsResult: deny");
		}
	}

	protected abstract String TAG();

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		viewModel = ViewModelProviders.of(requireActivity()).get(PlayTourViewModel.class);
		Bundle b = getArguments();
		int tour_id = 0;
		if(b != null) {
			tour_id = b.getInt(TOUR_ID);
		}
		User u = Preferences
				.getLoggedUser(PreferenceManager.getDefaultSharedPreferences(requireContext()));
		viewModel.loadPlay(u.getId(), tour_id).observe(this, place -> {
			if(place == null) {
				Log.e(TAG(), "onViewCreated: Something went wrong");
				Navigation.findNavController(requireActivity(), R.id.main_fragment_holder)
						.popBackStack(R.id.navTour, false);
				return;
			}
			setPlace(place);
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG(), "onResume: check location permissions");
		locationManager = (LocationManager) requireActivity()
				.getSystemService(Context.LOCATION_SERVICE);
		if(requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED &&
				requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
						PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
					                   Manifest.permission.ACCESS_FINE_LOCATION},
			                   PERMISSION_ACCESS_COARSE_AND_FINE_LOCATION);
			return;
		}
		usrLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Log.i(TAG(), "onResume: start request location updates");
		locationManager
				.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_TIME_REQ, LOC_DIST_REQ,
				                        this);
		requireActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		updateView();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG(), "onPause: delete location updates");
		locationManager.removeUpdates(this);
		requireActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void setPlace(Place nextPlace) {
		place = nextPlace;
		placeLocation = place.getLocation();
		place_name.setText(place.getName());
		place_description.setText(place.getDescription());
		int numCompletedPlaces = viewModel.getPlay().getPlaces().size() + 1;
		int numPlaces = viewModel.getPlay().getTour().getPlaces().size();
		place_complete.setText(getResources().getQuantityString(R.plurals.place_number_number,
		                                                        numCompletedPlaces,
		                                                        numCompletedPlaces, numPlaces));
		updateView();
	}

	abstract void updateView();

	@Override
	public void onLocationChanged(@NonNull Location location) {
		Log.d(TAG(), "onLocationChanged: ");
		usrLocation = location;

		// Set distance
		if(placeLocation != null) {
			distance = (int) usrLocation.distanceTo(placeLocation);
			if(distance > 1000f) {
				float newDistance = distance / 1000;
				newDistance = Math.round(newDistance * 100f) / 100f;
				place_distance
						.setText(getResources().getString(R.string.place_distance_km, newDistance));
				Log.d(TAG(), "updateView: distance=" + newDistance + "km");
			} else {
				place_distance.setText(
						getResources().getString(R.string.place_distance_m, (int) distance));
				Log.d(TAG(), "updateView: distance=" + distance + "m");
			}
			if(distance < DISTANCE_TO_COMPLETE) {
				locationManager.removeUpdates(this);
				Log.d(TAG(), "updateView: user arrive to the place");

				// If has question display
				if(place != null && place.getQuestion() != null && !place.getQuestion().isEmpty()) {
					showQuestionDialog(place);
					questionDialog.show();
				} else {
					new MaterialAlertDialogBuilder(requireContext())
							.setTitle(R.string.place_completed)//
							.setPositiveButton(R.string.next,
							                   (dialogInterface, i) -> dialogInterface.dismiss())
							.setOnDismissListener(dialogInterface -> completePlace()).show();
				}
				return;
			}
		}
		updateView();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.i(TAG(), "onStatusChanged: ");
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.i(TAG(), "onProviderEnabled: ");
	}

	@Override
	public void onProviderDisabled(String provider) {
		Log.i(TAG(), "onProviderDisabled: ");
	}

	private void completePlace() {
		// hide question dialog if exist and is showing
		if(questionDialog != null && questionDialog.isShowing()) questionDialog.dismiss();
		locationManager.removeUpdates(this);
		viewModel.completePlace(place.getId()).observe(this, place -> {
			if(place == null) {
				if(viewModel.tourIsCompleted()) {
					new MaterialAlertDialogBuilder(requireContext())
							.setTitle(R.string.tour_completed) //
							.setPositiveButton(R.string.ok, (dialog, which) -> Navigation
									.findNavController(requireActivity(), R.id.main_fragment_holder)
									.popBackStack(R.id.navTour, false)).show();
					return;
				}
				ErrorType error = viewModel.getError();
				Log.e(TAG(), "completePlace: " + error.toString());
				Toast.makeText(requireContext(), viewModel.getError().toString(),
				               Toast.LENGTH_SHORT).show();

			} else {
				Log.d(TAG(), "updateView: Place done");
				setPlace(place);
				if(requireActivity()
						.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
						PackageManager.PERMISSION_GRANTED && requireActivity()
						.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
						PackageManager.PERMISSION_GRANTED) {
					Toast.makeText(requireContext(), R.string.rejected_location_access,
					               Toast.LENGTH_SHORT).show();
					return;
				}
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_TIME_REQ,
				                                       LOC_DIST_REQ, this);
			}
		});
	}

	private void showQuestionDialog(Place place) {
		AlertDialog.Builder dialogBuilder = new MaterialAlertDialogBuilder(requireContext());

		View dialogView = getLayoutInflater()
				.inflate(R.layout.question_layout, new ConstraintLayout(requireContext()), false);
		TextView question = dialogView.findViewById(R.id.question);
		question.setText(place.getQuestion());
		List<MaterialButton> texts = Arrays.asList(dialogView.findViewById(R.id.answer1),
		                                           dialogView.findViewById(R.id.answer2),
		                                           dialogView.findViewById(R.id.answer3));
		// Get random position to start
		int i = new Random().nextInt(texts.size());

		// Set correct answer
		texts.get(i).setText(place.getAnswer());
		texts.get(i).setOnClickListener(v -> completePlace());
		i++;
		i %= texts.size();
		// set second answer
		texts.get(i).setText(place.getAnswer2());
		texts.get(i).setOnClickListener(v -> showWrongAnswerToast());
		i++;
		i %= texts.size();
		texts.get(i).setText(place.getAnswer3());
		texts.get(i).setOnClickListener(v -> showWrongAnswerToast());

		dialogBuilder.setView(dialogView);
		questionDialog = dialogBuilder.create();
		if(requireActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED &&
				requireActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
						PackageManager.PERMISSION_GRANTED) {
			return;
		}
		questionDialog.setOnDismissListener(dialog -> locationManager
				.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOC_TIME_REQ, LOC_DIST_REQ,
				                        this));
	}

	private void showWrongAnswerToast() {
		if(questionDialog != null && questionDialog.isShowing()) questionDialog.dismiss();
		Toast.makeText(requireContext(), getString(R.string.wrong_answer), Toast.LENGTH_SHORT)
				.show();
	}
}
