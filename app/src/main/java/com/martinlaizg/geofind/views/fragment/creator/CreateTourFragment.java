package com.martinlaizg.geofind.views.fragment.creator;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.martinlaizg.geofind.R;
import com.martinlaizg.geofind.config.Preferences;
import com.martinlaizg.geofind.data.access.api.error.ErrorType;
import com.martinlaizg.geofind.data.access.database.entities.Tour;
import com.martinlaizg.geofind.data.access.database.entities.User;
import com.martinlaizg.geofind.data.enums.PlayLevel;
import com.martinlaizg.geofind.utils.KeyboardUtils;
import com.martinlaizg.geofind.views.viewmodel.CreatorViewModel;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateTourFragment
		extends Fragment
		implements View.OnClickListener {

	public static final String TOUR_ID = "TOUR_ID";
	private static final String TAG = CreateTourFragment.class.getSimpleName();

	@BindView(R.id.tour_name_layout)
	TextInputLayout tour_name_layout;
	@BindView(R.id.tour_description_layout)
	TextInputLayout tour_description_layout;
	@BindView(R.id.tour_image_view)
	ImageView tour_image_view;

	@BindView(R.id.done_button)
	MaterialButton done_button;
	@BindView(R.id.add_image_button)
	MaterialButton add_image_button;
	@BindView(R.id.difficulty_spinner)
	Spinner difficulty_spinner;

	private CreatorViewModel viewModel;
	private String image_url = "";

	@Override
	public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.fragment_create_tour, container, false);
		ButterKnife.bind(this, view);
		OnBackPressedCallback callback = new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				showExitDialog();
			}
		};
		requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
		return view;
	}

	private void showExitDialog() {
		new MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.are_you_sure)
				.setMessage(getString(R.string.exit_lose_data_alert))
				.setPositiveButton(getString(R.string.ok), (dialog, which) -> Navigation
						.findNavController(requireActivity(), R.id.main_fragment_holder)
						.popBackStack()).show();
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		Bundle b = getArguments();
		int tour_id = 0;
		if(b != null) {
			tour_id = b.getInt(TOUR_ID);
		}
		Log.i(TAG, "onViewCreated: Get tour with id = " + tour_id);
		viewModel = ViewModelProviders.of(requireActivity()).get(CreatorViewModel.class);
		viewModel.getTour(tour_id).observe(this, this::setTour);

		done_button.setOnClickListener(this);
		add_image_button.setOnClickListener(v -> {
			AlertDialog alertDialog = buildDialog();
			alertDialog.show();
		});
	}

	private void setTour(Tour tour) {
		if(tour == null) {
			ErrorType error = viewModel.getError();
			Log.e(TAG, "setTour: Error getting the tour" + error.toString());
			if(error == ErrorType.EXIST) {
				Navigation.findNavController(requireActivity(), R.id.main_fragment_holder)
						.popBackStack();
			}
			return;
		}
		Objects.requireNonNull(tour_name_layout.getEditText()).setText(tour.getName());

		Objects.requireNonNull(tour_description_layout.getEditText())
				.setText(tour.getDescription());

		difficulty_spinner.setSelection(tour.getMin_level().ordinal());

		tour_image_view.setVisibility(View.GONE);
		if(tour.getImage() != null) image_url = tour.getImage();
		if(!image_url.isEmpty()) {
			Picasso.with(requireContext()).load(image_url).into(tour_image_view);
			tour_image_view.setVisibility(View.VISIBLE);
		}
		if(tour.getId() > 0) {
			done_button.setText(getString(R.string.update));
		}
	}

	private AlertDialog buildDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = requireActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.add_image_layout, new LinearLayout(requireContext()));
		TextInputLayout image_url_layout = view.findViewById(R.id.image_url_layout);
		Objects.requireNonNull(image_url_layout.getEditText()).setText(image_url);
		return builder.setView(view).setPositiveButton(R.string.ok, (dialog, id) -> {
			image_url = Objects.requireNonNull(image_url_layout.getEditText()).getText().toString();
			tour_image_view.setVisibility(View.GONE);
			if(!image_url.isEmpty()) {
				Picasso.with(requireContext()).load(image_url).into(tour_image_view);
				tour_image_view.setVisibility(View.VISIBLE);
			}

			KeyboardUtils.hideKeyboard(requireActivity());
		}).create();
	}

	@Override
	public void onClick(View v) {
		KeyboardUtils.hideKeyboard(requireActivity());

		tour_name_layout.setError("");
		String name = Objects.requireNonNull(tour_name_layout.getEditText()).getText().toString()
				.trim();
		if(name.isEmpty()) {
			tour_name_layout.setError(getString(R.string.required_name));
			return;
		}
		tour_name_layout.setError("");
		if(name.length() > getResources().getInteger(R.integer.max_name_length)) {
			tour_name_layout.setError(getString(R.string.text_too_long));
			return;
		}
		tour_description_layout.setError("");
		String description = Objects.requireNonNull(tour_description_layout.getEditText()).getText()
				.toString().trim();
		if(description.isEmpty()) {
			tour_description_layout.setError(getString(R.string.required_description));
			return;
		}
		if(description.length() > getResources().getInteger(R.integer.max_description_length)) {
			tour_description_layout.setError(getString(R.string.text_too_long));
			return;
		}

		PlayLevel pl = PlayLevel.getPlayLevel(difficulty_spinner.getSelectedItemPosition());
		User user = Preferences
				.getLoggedUser(PreferenceManager.getDefaultSharedPreferences(requireContext()));
		viewModel.updateTour(name, description, user.getId(), pl, image_url);
		int id = viewModel.getStoredTour().getId();
		Bundle args = new Bundle();
		args.putInt(CreatorFragment.TOUR_ID, id);
		Navigation.findNavController(requireActivity(), R.id.main_fragment_holder)
				.navigate(R.id.toCreator, args);
	}
}
