package com.martinlaizg.geofind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.martinlaizg.geofind.activity.LoginActivity;
import com.martinlaizg.geofind.activity.personal.MyAccountActivity;
import com.martinlaizg.geofind.config.Preferences;
import com.martinlaizg.geofind.entity.User;
import com.martinlaizg.geofind.fragment.LocationFragment;
import com.martinlaizg.geofind.fragment.MainFragment;
import com.martinlaizg.geofind.fragment.MapsFragment;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();


    @BindView(R.id.drawer)
    DrawerLayout drawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    // SharedPreferences
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        ButterKnife.bind(MainActivity.this);
        sp = Preferences.getInstance(getApplicationContext());
        checkLogin();
        initView();
    }

    private void initView() {
        // Initial fragment
        MainFragment mainFragment = new MainFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mainFragment);
        fragmentTransaction.commit();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getHeaderView(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Ir a perfil del usuario", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MyAccountActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        String stringUser = sp.getString(Preferences.USER, "");
        if (!Objects.equals(stringUser, "")) {
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
            User user = gson.fromJson(stringUser, User.class);

        }
    }

    private void checkLogin() {
        if (!sp.getBoolean(Preferences.LOGGED, false)) {
            Log.i(TAG, "User not logged");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean fragmentTransaction = false;
        Fragment fragment = null;
        menuItem.setChecked(true);

        switch (menuItem.getItemId()) {
            case R.id.menu_users:
                Toast.makeText(this, "Cargar usuarios", Toast.LENGTH_SHORT).show();
                fragment = new MapsFragment();
                fragmentTransaction = true;
                break;
            case R.id.menu_maps:
                Toast.makeText(this, "Cargar mapas", Toast.LENGTH_SHORT).show();
                fragment = new MapsFragment();
                fragmentTransaction = true;
                break;
            case R.id.menu_locations:
                Toast.makeText(this, "Cargar localizaciones", Toast.LENGTH_SHORT).show();
                fragment = new LocationFragment();
                fragmentTransaction = true;
                break;
            case R.id.menu_settings:
                Toast.makeText(this, "Ir a ajustes", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MyAccountActivity.class);
                startActivity(intent);
                menuItem.setChecked(false);
                break;
            case R.id.menu_log_out:
                Toast.makeText(this, "Cerrar sesión", Toast.LENGTH_SHORT).show();
                menuItem.setChecked(false);
                break;
        }
        if (fragmentTransaction) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
        drawer.closeDrawers();
        return true;
    }
}
