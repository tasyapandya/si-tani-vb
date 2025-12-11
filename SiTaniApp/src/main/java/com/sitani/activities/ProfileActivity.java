package com.sitani.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.sitani.R;
import com.sitani.models.User;
import com.sitani.utils.FirebaseHelper;

public class ProfileActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextInputLayout nameInputLayout, emailInputLayout, phoneInputLayout, addressInputLayout;
    private TextInputEditText nameEditText, emailEditText, phoneEditText, addressEditText;
    private MaterialButton saveButton, cancelButton, logoutButton;
    private ProgressBar progressBar;

    private User currentUser;
    private boolean hasChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupToolbar();
        setupClickListeners();
        loadUserData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        addressInputLayout = findViewById(R.id.addressInputLayout);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
        logoutButton = findViewById(R.id.logoutButton);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> {
            if (hasChanges) {
                showDiscardChangesDialog();
            } else {
                finish();
            }
        });
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveProfile());

        cancelButton.setOnClickListener(v -> {
            if (hasChanges) {
                showDiscardChangesDialog();
            } else {
                finish();
            }
        });

        logoutButton.setOnClickListener(v -> showLogoutConfirmation());

        // Track changes
        nameEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                hasChanges = true;
            }
        });

        phoneEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                hasChanges = true;
            }
        });

        addressEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                hasChanges = true;
            }
        });
    }

    private void loadUserData() {
        showProgress(true);

        FirebaseUser firebaseUser = FirebaseHelper.getCurrentUser();
        if (firebaseUser != null) {
            FirebaseHelper.getUser(firebaseUser.getUid(), task -> {
                showProgress(false);
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        currentUser = document.toObject(User.class);
                        if (currentUser != null) {
                            populateFields();
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void populateFields() {
        if (currentUser != null) {
            nameEditText.setText(currentUser.getName());
            emailEditText.setText(currentUser.getEmail());
            phoneEditText.setText(currentUser.getPhone());
            addressEditText.setText(currentUser.getAddress());
            hasChanges = false;
        }
    }

    private void saveProfile() {
        // Reset errors
        nameInputLayout.setError(null);
        phoneInputLayout.setError(null);
        addressInputLayout.setError(null);

        // Get values
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        boolean cancel = false;

        // Check for empty name
        if (name.isEmpty()) {
            nameInputLayout.setError(getString(R.string.error_fields));
            cancel = true;
        }

        if (cancel) {
            return;
        }

        // Update user object
        if (currentUser != null) {
            currentUser.setName(name);
            currentUser.setPhone(phone);
            currentUser.setAddress(address);

            showProgress(true);
            FirebaseHelper.saveUser(currentUser, task -> {
                showProgress(false);
                if (task.isSuccessful()) {
                    hasChanges = false;
                    Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout", (dialog, which) -> {
                    FirebaseHelper.logoutUser();
                    Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDiscardChangesDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Discard Changes")
                .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                .setPositiveButton("Discard", (dialog, which) -> {
                    hasChanges = false;
                    finish();
                })
                .setNegativeButton("Keep Editing", null)
                .show();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        saveButton.setEnabled(!show);
        cancelButton.setEnabled(!show);
        logoutButton.setEnabled(!show);
        nameEditText.setEnabled(!show);
        phoneEditText.setEnabled(!show);
        addressEditText.setEnabled(!show);
    }

    @Override
    public void onBackPressed() {
        if (hasChanges) {
            showDiscardChangesDialog();
        } else {
            super.onBackPressed();
        }
    }
}