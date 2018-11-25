package com.example.laura_000.inclass10;

import android.content.Intent;
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
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Sign Up");

        Button cancel = findViewById(R.id.buttonCancelSU);
        Button signup = findViewById(R.id.buttonSignUpSU);

        mAuth = FirebaseAuth.getInstance();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }
    public void signup() {
        EditText firstText = findViewById(R.id.editFirstNameSU);
        EditText lastText = findViewById(R.id.editLastNameSU);
        EditText emailText = findViewById(R.id.editEmailSU);
        EditText passwordText = findViewById(R.id.editPasswordSU);
        EditText repeatPasswordText = findViewById(R.id.editPassword2SU);

        final String firstName = firstText.getText().toString();
        final String lastName = lastText.getText().toString();
        final String email = emailText.getText().toString();
        final String password = passwordText.getText().toString();
        String repeatPassword = repeatPasswordText.getText().toString();

        if (firstName == null || firstName.equals("")) {
            firstText.setError("Enter a valid first name");
            return;
        }

        if (lastName == null || lastName.equals("")) {
            lastText.setError("Enter a valid last name");
            return;
        }

        if (email == null || "".equals(email) || email.length() < 3 || !email.contains("@")) {
            emailText.setError("Enter a valid email");
            return;
        }

        if (email.length() < 3) {
            passwordText.setError("Email has to be 3 characters or more");
        }

        if (password == null || password.equals("")) {
            passwordText.setError("Enter a valid password");
            return;
        }

        if (password.length() < 6) {
            passwordText.setError("Password has to be 6 characters or more");
            return;
        }

        if (repeatPassword == null || repeatPassword.equals("")) {
            repeatPasswordText.setError("Enter a valid password");
            return;
        }

        if (!repeatPassword.equals(password)) {
            repeatPasswordText.setError("Passwords do not match");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();

                    User u = new User(firstName, lastName, email, password);
                    FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).setValue(u);

                    if (user != null) {
                        Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SignUpActivity.this, MessagesActivity.class);
                        intent.putExtra("id", user.getUid());
                        startActivity(intent);
                        finish();

                    }

                }
                else{
                    Toast.makeText(SignUpActivity.this, "Sign up failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
