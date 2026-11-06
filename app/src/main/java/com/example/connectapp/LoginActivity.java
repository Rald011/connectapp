package com.example.connectapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;
    private ProgressBar loginProgress;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Keep content clear of the status and navigation bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginRoot), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        // Connect to Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        loginProgress = findViewById(R.id.loginProgress);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        // LOGIN: sign in an existing account
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            if (!isValid(email, password)) return;

            setLoading(true);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        setLoading(false);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show();
                            goToHome();
                        } else {
                            showError("Login failed", task.getException());
                        }
                    });
        });

        // REGISTER: create a new account (also signs the user in)
        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            if (!isValid(email, password)) return;

            setLoading(true);
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        setLoading(false);
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show();
                            goToHome();
                        } else {
                            showError("Registration failed", task.getException());
                        }
                    });
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Already signed in? Skip straight to the home screen.
        if (mAuth.getCurrentUser() != null) {
            goToHome();
        }
    }

    private boolean isValid(String email, String password) {
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            return false;
        }
        return true;
    }

    private void setLoading(boolean loading) {
        loginProgress.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!loading);
        btnRegister.setEnabled(!loading);
    }

    private void showError(String title, Exception e) {
        String msg = (e != null && e.getMessage() != null) ? e.getMessage() : "Something went wrong";
        Toast.makeText(this, title + ": " + msg, Toast.LENGTH_LONG).show();
    }

    private void goToHome() {
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
