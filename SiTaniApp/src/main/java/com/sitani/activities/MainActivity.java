package com.sitani.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sitani.R;
import com.sitani.models.TodoItem;
import com.sitani.models.User;
import com.sitani.models.WeatherResponse;
import com.sitani.notifications.WeatherNotificationService;
import com.sitani.utils.Constants;
import com.sitani.utils.FirebaseHelper;
import com.sitani.utils.WeatherApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private BottomNavigationView bottomNavigation;
    private TextView welcomeTextView, userNameTextView;
    private TextView temperatureTextView, descriptionTextView, locationTextView;
    private TextView humidityTextView, windSpeedTextView;
    private TextView taskCountTextView, taskSummaryTextView;
    private MaterialCardView weatherCard, todoSummaryCard;
    private ProgressBar weatherProgressBar;
    private ImageView weatherIconImageView;

    private FusedLocationProviderClient fusedLocationClient;
    private WeatherNotificationService notificationService;
    private User currentUser;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        setupBottomNavigation();
        setupLocationServices();
        setupNotificationService();
        loadUserData();
        loadWeatherData();
        loadTodoSummary();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        welcomeTextView = findViewById(R.id.welcomeTextView);
        userNameTextView = findViewById(R.id.userNameTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        locationTextView = findViewById(R.id.locationTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);
        taskCountTextView = findViewById(R.id.taskCountTextView);
        taskSummaryTextView = findViewById(R.id.taskSummaryTextView);
        weatherCard = findViewById(R.id.weatherCard);
        todoSummaryCard = findViewById(R.id.todoSummaryCard);
        weatherProgressBar = findViewById(R.id.weatherProgressBar);
    }

    private void setupToolbar() {
        // Toolbar setup if needed
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                // Already on home
                return true;
            } else if (itemId == R.id.navigation_todo) {
                startActivity(new Intent(MainActivity.this, TodoActivity.class));
                return true;
            } else if (itemId == R.id.navigation_profile) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            } else if (itemId == R.id.navigation_logout) {
                showLogoutConfirmation();
                return true;
            }

            return false;
        });

        // Set home as selected by default
        bottomNavigation.setSelectedItemId(R.id.navigation_home);
    }

    private void setupLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupNotificationService() {
        notificationService = new WeatherNotificationService(this);
    }

    private void loadUserData() {
        FirebaseUser firebaseUser = FirebaseHelper.getCurrentUser();
        if (firebaseUser != null) {
            FirebaseHelper.getUser(firebaseUser.getUid(), task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        currentUser = document.toObject(User.class);
                        if (currentUser != null) {
                            updateWelcomeMessage();
                        }
                    }
                }
            });
        }
    }

    private void updateWelcomeMessage() {
        if (currentUser != null) {
            String name = currentUser.getName();
            userNameTextView.setText(name != null && !name.isEmpty() ? name : "User");
        }
    }

    private void loadWeatherData() {
        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constants.LOCATION_PERMISSION_REQUEST);
        } else {
            getCurrentLocation();
        }
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                // Permission denied, load weather with default location
                loadWeatherForDefaultLocation();
            }
        }
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                            loadWeatherForLocation(location.getLatitude(), location.getLongitude());
                        } else {
                            loadWeatherForDefaultLocation();
                        }
                    }
                });
    }

    private void loadWeatherForDefaultLocation() {
        // Default to Jakarta coordinates
        loadWeatherForLocation(-6.2088, 106.8456);
    }

    private void loadWeatherForLocation(double lat, double lon) {
        weatherProgressBar.setVisibility(View.VISIBLE);

        String apiKey = Constants.OPENWEATHER_API_KEY;
        if (apiKey.equals("YOUR_API_KEY_HERE")) {
            // Show sample data if API key is not set
            showSampleWeatherData();
            return;
        }

        WeatherApiClient.getWeatherService()
                .getCurrentWeather(lat, lon, apiKey, "metric")
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        weatherProgressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            updateWeatherUI(response.body());
                        } else {
                            showSampleWeatherData();
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        weatherProgressBar.setVisibility(View.GONE);
                        showSampleWeatherData();
                    }
                });
    }

    private void showSampleWeatherData() {
        // Create sample weather data for demonstration
        WeatherResponse sampleWeather = new WeatherResponse();
        WeatherResponse.Main main = new WeatherResponse.Main();
        main.setTemp(28.5);
        main.setHumidity(65);

        WeatherResponse.Weather weather = new WeatherResponse.Weather();
        weather.setDescription("Partly cloudy");

        WeatherResponse.Wind wind = new WeatherResponse.Wind();
        wind.setSpeed(3.5);

        sampleWeather.setMain(main);
        sampleWeather.setWeather(new WeatherResponse.Weather[]{weather});
        sampleWeather.setWind(wind);
        sampleWeather.setName("Jakarta");

        updateWeatherUI(sampleWeather);
    }

    private void updateWeatherUI(WeatherResponse weatherResponse) {
        if (weatherResponse.getMain() != null) {
            double temp = weatherResponse.getMain().getTemp();
            temperatureTextView.setText(String.format("%.1f°C", temp));

            int humidity = weatherResponse.getMain().getHumidity();
            humidityTextView.setText(humidity + "%");
        }

        if (weatherResponse.getWind() != null) {
            double windSpeed = weatherResponse.getWind().getSpeed();
            windSpeedTextView.setText(String.format("%.1f m/s", windSpeed));
        }

        if (weatherResponse.getWeather() != null && weatherResponse.getWeather().length > 0) {
            String description = weatherResponse.getWeather()[0].getDescription();
            descriptionTextView.setText(description);
        }

        String location = weatherResponse.getName() != null ? weatherResponse.getName() : "Unknown Location";
        locationTextView.setText(location);

        weatherProgressBar.setVisibility(View.GONE);
    }

    private void loadTodoSummary() {
        FirebaseUser firebaseUser = FirebaseHelper.getCurrentUser();
        if (firebaseUser != null) {
            FirebaseHelper.getUserTodos(firebaseUser.getUid(), task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (querySnapshot != null) {
                        List<com.google.firebase.firestore.DocumentSnapshot> documents = querySnapshot.getDocuments();
                        int totalTasks = documents.size();
                        int pendingTasks = 0;

                        for (com.google.firebase.firestore.DocumentSnapshot doc : documents) {
                            TodoItem todoItem = doc.toObject(TodoItem.class);
                            if (todoItem != null && !todoItem.isCompleted()) {
                                pendingTasks++;
                            }
                        }

                        updateTodoSummary(totalTasks, pendingTasks);
                    }
                }
            });
        }
    }

    private void updateTodoSummary(int totalTasks, int pendingTasks) {
        taskCountTextView.setText(totalTasks + " tasks");

        if (totalTasks == 0) {
            taskSummaryTextView.setText("No tasks for today");
        } else if (pendingTasks == 0) {
            taskSummaryTextView.setText("All tasks completed!");
        } else {
            taskSummaryTextView.setText(pendingTasks + " pending task" + (pendingTasks > 1 ? "s" : ""));
        }

        todoSummaryCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TodoActivity.class);
            startActivity(intent);
        });
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    FirebaseHelper.logoutUser();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to main activity
        loadTodoSummary();
    }
}