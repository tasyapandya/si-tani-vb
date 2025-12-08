package com.farmerapp.activities;

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
import com.farmerapp.R;
import com.farmerapp.utils.Constants;
import com.farmerapp.utils.FirebaseHelper;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailInputLayout, passwordInputLayout;
    private TextInputEditText emailEditText, passwordEditText;
    private MaterialButton loginButton;
    private TextView registerTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupClickListeners();
        checkCurrentUser();
    }

    private void initViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout);
        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerTextView = findViewById(R.id.registerTextView);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());

        registerTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void checkCurrentUser() {
        FirebaseUser currentUser = FirebaseHelper.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void attemptLogin() {
        // Reset errors
        emailInputLayout.setError(null);
        passwordInputLayout.setError(null);

        // Get values
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            passwordInputLayout.setError(getString(R.string.error_password));
            focusView = passwordInputLayout;
            cancel = true;
        }

        // Check for a valid email
        if (TextUtils.isEmpty(email) || !isEmailValid(email)) {
            emailInputLayout.setError(getString(R.string.error_email));
            focusView = emailInputLayout;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            loginUser(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void loginUser(String email, String password) {
        FirebaseHelper.loginUser(email, password, task -> {
            showProgress(false);
            if (task.isSuccessful() && task.getResult() != null) {
                // Login successful, navigate to main activity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else {
                // Login failed
                String errorMessage = task.getException() != null ?
                    task.getException().getMessage() : getString(R.string.error_login);
                showError(errorMessage);
            }
        });
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        loginButton.setEnabled(!show);
        emailEditText.setEnabled(!show);
        passwordEditText.setEnabled(!show);
    }

    private void showError(String message) {
        if (message.toLowerCase().contains("password")) {
            passwordInputLayout.setError(message);
            passwordEditText.requestFocus();
        } else if (message.toLowerCase().contains("email")) {
            emailInputLayout.setError(message);
            emailEditText.requestFocus();
        } else {
            emailInputLayout.setError(message);
            emailEditText.requestFocus();
        }
    }
}