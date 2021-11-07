package com.tevin.inside;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private EditText userEmail;
    private EditText userDisplayName;
    private EditText userPassword;
    private Button registerBTN;
    private TextView toLoginActivity;

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;

    private DatabaseReference userDetailsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = findViewById(R.id.enter_email);
        userDisplayName = findViewById(R.id.enter_display_name);
        userPassword = findViewById(R.id.enter_password);
        registerBTN = findViewById(R.id.registerBTN);
        toLoginActivity = findViewById(R.id.to_login_textView);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        userDetailsReference = database.getReference().child("Users");

        //Check if the current user was logged in before
        if (mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = userEmail.getText().toString().trim();
                String displayName = userDisplayName.getText().toString().trim();
                String password = userPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    userEmail.setError("Email is Required");
                    return;
                }
                if (TextUtils.isEmpty(displayName)) {
                    userDisplayName.setError("Display Name is Required");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    userPassword.setError("Password is Required");
                    return;
                }
                if (password.length() < 8) {
                    userPassword.setError("Password must be 8 or more characters long");
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        String userID = mAuth.getCurrentUser().getUid();

                        DatabaseReference current_user_db = userDetailsReference.child(userID);

                        current_user_db.child("DisplayName").setValue(displayName);
                        current_user_db.child("ProfileImage").setValue("Default");

                        if (task.isSuccessful()) {
                            /* The User class is a class which will hold the user info temporarily as it is sent to Firebase
                             * We need to relate it to each new registered user by getting their userID
                             * Through this, we will be able to get the user's details in the ProfileActivity
                             */
                            User user = new User(email, displayName);

                            //Getting the instance of current user (userID) and setting the value to the user object created above
                            FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //If user has been successfully registered in the database
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Account Creation Successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.putExtra("userEmail", email);
                                        intent.putExtra("userDisplayName", displayName);
                                        startActivity(intent);
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "An Error has Occurred: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        toLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(toLogin);
            }
        });
    }
}