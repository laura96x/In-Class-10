package com.example.laura_000.inclass10;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    private FirebaseAuth mAuth;

    Button logInButton, signUpButton;
    EditText emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Log In");

        logInButton = findViewById(R.id.buttonLogIn);
        signUpButton = findViewById(R.id.buttonSignUp1);

        emailEditText = findViewById(R.id.editEmailLogIn);
        passwordEditText = findViewById(R.id.editPasswordLogIn);

        logInButton.setOnClickListener(logInButtonListener);
        signUpButton.setOnClickListener(signUpButtonListener);

        mAuth = FirebaseAuth.getInstance();

        if (!isConnected()) {
            logInButton.setEnabled(false);
            signUpButton.setEnabled(false);
            Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    View.OnClickListener logInButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (email == null || "".equals(email) || email.length() < 3 || !email.contains("@")) {
                emailEditText.setError("Enter a valid email");
                return;
            }

            if (password == null || password.equals("")) {
                passwordEditText.setError("Enter a valid password");
                return;
            }

            if (password.length() < 6) {
                passwordEditText.setError("Password has to be 6 characters or more");
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            intent = new Intent(MainActivity.this, MessagesActivity.class);
                            intent.putExtra("id", user.getUid());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                    }

                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Task failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };

    View.OnClickListener signUpButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    };

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}
