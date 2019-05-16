package com.martinlaizg.geofind.views.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.martinlaizg.geofind.data.Crypto;
import com.martinlaizg.geofind.data.access.api.service.exceptions.APIException;
import com.martinlaizg.geofind.data.access.database.entities.User;
import com.martinlaizg.geofind.data.repository.RepositoryFactory;
import com.martinlaizg.geofind.data.repository.UserRepository;

public class LoginViewModel
		extends AndroidViewModel {

	private final UserRepository userRepo;

	private User user;
	private APIException error;

	public LoginViewModel(Application application) {
		super(application);
		userRepo = RepositoryFactory.getUserRepository(application);
		user = new User();
	}

	public MutableLiveData<User> login() {
		MutableLiveData<User> u = new MutableLiveData<>();
		new Thread(() -> {
			try {
				user = userRepo.login(user);
				u.postValue(user);
			} catch(APIException e) {
				setError(e);
				u.postValue(null);
			}
		}).start();
		return u;
	}

	public MutableLiveData<User> registry() {

		MutableLiveData<User> u = new MutableLiveData<>();
		new Thread(() -> {
			try {
				user = userRepo.registry(user);
				u.postValue(user);
			} catch(APIException e) {
				setError(e);
				u.postValue(null);
			}
		}).start();
		return u;
	}

	public void setLogin(String email, String password) {
		user.setEmail(email);
		user.setPassword(Crypto.hash(password));
	}

	public void setRegistry(String name, String username, String email, String password) {
		user.setName(name);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(Crypto.hash(password));
	}

	public String getEmail() {
		return user.getEmail();
	}

	public void setEmail(String email) {
		user.setEmail(email);
	}

	public APIException getError() {
		return error;
	}

	public void setError(APIException error) {
		this.error = error;
	}
}