package com.martinlaizg.geofind.views.fragment.login;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.martinlaizg.geofind.R;
import com.martinlaizg.geofind.data.access.api.service.exceptions.APIException;
import com.martinlaizg.geofind.views.viewmodel.LoginViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RegistryFragment
		extends Fragment {

	public static final String ARG_EMAIL = "ARG_EMAIL";
	private static final String TAG = RegistryFragment.class.getSimpleName();

	@BindView(R.id.name_input)
	TextInputLayout name_input;
	@BindView(R.id.username_input)
	TextInputLayout username_input;
	@BindView(R.id.email_input)
	TextInputLayout email_input;
	@BindView(R.id.password_input)
	TextInputLayout password_input;
	@BindView(R.id.c_password_input)
	TextInputLayout c_password_input;
	@BindView(R.id.btn_registry)
	MaterialButton btn_registry;

	private LoginViewModel viewModel;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_registry, container, false);
		ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		viewModel = ViewModelProviders.of(requireActivity()).get(LoginViewModel.class);
		String email = viewModel.getEmail();
		if(email != null && !email.isEmpty()) {
			email_input.getEditText().setText(email);
		}
		btn_registry.setOnClickListener(v -> registry());
	}

	private void registry() {
		btn_registry.setEnabled(false);
		try {
			if(name_input.getEditText().getText().toString().trim().isEmpty()) {
				name_input.setError(getString(R.string.required_name));
				return;
			}
			if(username_input.getEditText().getText().toString().trim().isEmpty()) {
				username_input.setError(getString(R.string.required_username));
				return;
			}
			if(email_input.getEditText().getText().toString().trim().isEmpty()) {
				email_input.setError(getString(R.string.required_password));
				return;
			}
			String password = password_input.getEditText().getText().toString().trim();
			if(password.isEmpty()) {
				password_input.setError(getString(R.string.required_verify_password));
				return;
			}
			final String c_password = c_password_input.getEditText().getText().toString().trim();
			if(c_password.isEmpty()) {
				password_input.setError(getString(R.string.required_verify_password));
				return;
			}
			if(!c_password.equals(password)) {
				password_input.setError(getString(R.string.password_does_not_match));
				return;
			}
		} catch(NullPointerException ex) {
			Log.e(TAG, "registry: ", ex);
		}

		String name = name_input.getEditText().getText().toString().trim();
		String username = username_input.getEditText().getText().toString().trim();
		String email = email_input.getEditText().getText().toString().trim();
		String password = password_input.getEditText().getText().toString().trim();
		viewModel.setRegistry(name, username, email, password);
		viewModel.registry().observe(this, (user) -> {
			btn_registry.setEnabled(true);
			if(user == null) {
				APIException error = viewModel.getError();
				Toast.makeText(requireActivity(),
				               getResources().getString(error.getType().getMessage()),
				               Toast.LENGTH_SHORT).show();
				return;
			}
			Toast.makeText(requireActivity(), getResources().getString(R.string.registry),
			               Toast.LENGTH_LONG).show();
			Navigation.findNavController(requireActivity(), R.id.main_fragment_holder)
					.popBackStack();

		});
		// TODO: add loading image
	}
}