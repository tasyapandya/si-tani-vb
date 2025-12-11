package com.sitani.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;
import com.sitani.R;
import com.sitani.models.User;
import com.sitani.utils.FirebaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nameInputLayout, emailInputLayout, passwordInputLayout, confirmPasswordInputLayout,
            phoneInputLayout, addressInputLayout;
    private TextInputEditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText,
            phoneEditText, addressEditText;
    private MaterialButton registerButton;
    private TextView loginTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        nameInputLayout = findViewById(R.id.nameInputLayout);
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        confirmPasswordInputLayout = findViewById(R.id.confirmPasswordInputLayout);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        addressInputLayout = findViewById(R.id.addressInputLayout);

        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        addressEditText = findViewById(R.id.addressEditText);

        registerButton = findViewById(R.id.registerButton);
        loginTextView = findViewById(R.id.loginTextView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> attemptRegister());

        loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptRegister() {
        // Reset errors
        nameInputLayout.setError(null);
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);
        confirmPasswordInputLayout.setError(null);
        phoneInputLayout.setError(null);
        addressInputLayout.setError(null);

        // Get values
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for empty name
        if (TextUtils.isEmpty(name)) {
            nameInputLayout.setError(getString(R.string.error_fields));
            focusView = nameInputLayout;
            cancel = true;
        }

        // Check for valid password
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInputLayout.setError(getString(R.string.error_password));
            focusView = passwordInputLayout;
            cancel = true;
        }

        // Check if passwords match
        if (!TextUtils.isEmpty(password) && !password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match");
            focusView = confirmPasswordInputLayout;
            cancel = true;
        }

        // Check for valid email
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            emailInputLayout.setError(getString(R.string.error_email));
            focusView = emailInputLayout;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            registerUser(name, email, password, phone, address);
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void registerUser(String name, String email, String password, String phone, String address) {
        FirebaseHelper.registerUser(email, password, task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = task.getResult().getUser();
                if (firebaseUser != null) {
                    // Create user object
                    User user = new User(
                        firebaseUser.getUid(),
                        name,
                        email,
                        phone,
                        address
                    );

                    // Save user data to Firestore
                    FirebaseHelper.saveUser(user, saveTask -> {
                        if (saveTask.isSuccessful()) {
                            // Registration successful, navigate to main activity
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            showProgress(false);
                            showError("Failed to save user data: " + saveTask.getException().getMessage());
                        }
                    });
                }
            } else {
                showProgress(false);
                String errorMessage = task.getException() != null ?
                    task.getException().getMessage() : getString(R.string.error_register);
                showError(errorMessage);
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        registerButton.setEnabled(!show);
        setFieldsEnabled(!show);
    }

    private void setFieldsEnabled(boolean enabled) {
        nameEditText.setEnabled(enabled);
        emailEditText.setEnabled(enabled);
        passwordEditText.setEnabled(enabled);
        confirmPasswordEditText.setEnabled(enabled);
        phoneEditText.setEnabled(enabled);
        addressEditText.setEnabled(enabled);
    }

    private void showError(String message) {
        if (message.toLowerCase().contains("email")) {
            emailInputLayout.setError(message);
            emailEditText.requestFocus();
        } else if (message.toLowerCase().contains("password")) {
            passwordInputLayout.setError(message);
            passwordEditText.requestFocus();
        } else {
            emailInputLayout.setError(message);
            emailEditText.requestFocus();
        }
    }
}