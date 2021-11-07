package com.tevin.inside;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangePassword extends AppCompatActivity
{
    private EditText currentPassword, newPassword, confirmPassword;
    private Button changePasswordBTN;

    private FirebaseDatabase mDatabase;

    private FirebaseAuth mAuth;

    private DatabaseReference mRef;

    private FirebaseUser user;

    private String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        confirmPassword = findViewById(R.id.confirm_password);
        changePasswordBTN = findViewById(R.id.submitPasswordChange);

        mDatabase = FirebaseDatabase.getInstance();

        mRef = mDatabase.getReference().child("Users");

        user = FirebaseAuth.getInstance().getCurrentUser();

        userID = user.getUid();

        changePasswordBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String current_password = currentPassword.getText().toString().trim();
                String password_new = newPassword.getText().toString().trim();
                String confirm_password_new = confirmPassword.getText().toString().trim();
                if (TextUtils.isEmpty(current_password) && TextUtils.isEmpty(password_new) && TextUtils.isEmpty(confirm_password_new))
                {
                    Toast.makeText(ChangePassword.this, "Change Something", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (!TextUtils.isEmpty(current_password) && TextUtils.isEmpty(password_new) && TextUtils.isEmpty(confirm_password_new))
                    {
                        newPassword.setError("This field is required");
                        newPassword.requestFocus();
                    }
                    else if (TextUtils.isEmpty(current_password) && !TextUtils.isEmpty(password_new) && TextUtils.isEmpty(confirm_password_new)) {
                        currentPassword.setError("This field is required");
                        currentPassword.requestFocus();
                    }
                    else if (!TextUtils.isEmpty(current_password) && !TextUtils.isEmpty(password_new) && TextUtils.isEmpty(confirm_password_new)) {
                        confirmPassword.setError("Confirm Password to change");
                        confirmPassword.requestFocus();
                    }
                    else if (!TextUtils.isEmpty(current_password) && TextUtils.isEmpty(password_new) && !TextUtils.isEmpty(confirm_password_new)) {
                        newPassword.setError("This field is required");
                        newPassword.requestFocus();
                    }
                    else if (TextUtils.isEmpty(current_password) && !TextUtils.isEmpty(password_new) && !TextUtils.isEmpty(confirm_password_new)) {
                        currentPassword.setError("This field is required");
                        currentPassword.requestFocus();
                    }
                    else if (TextUtils.isEmpty(current_password) && TextUtils.isEmpty(password_new) && !TextUtils.isEmpty(confirm_password_new)) {
                        newPassword.setError("This field is required");
                        newPassword.requestFocus();
                    }
                    else {
                        mRef.child(userID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User userProfile = snapshot.getValue(User.class);
                                if (userProfile != null) {
                                    String email = userProfile.getEmail();
                                    Log.d("Current Pass", currentPassword.getText().toString());
                                    Log.d("Email", email);
                                    AuthCredential credential = EmailAuthProvider
                                            .getCredential(email, currentPassword.getText().toString());
                                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful())
                                            {
                                                Log.d("Change", "User re-authenticated.");
                                                Toast.makeText(ChangePassword.this, "Re-Authentication Success", Toast.LENGTH_SHORT).show();
                                                user.updatePassword(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(ChangePassword.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                                                            mAuth = FirebaseAuth.getInstance();
                                                            mAuth.signOut();
                                                            Intent toLogin = new Intent(ChangePassword.this, LoginActivity.class);
                                                            startActivity(toLogin);
                                                            finish();
                                                        } else {
                                                            Toast.makeText(ChangePassword.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }
                                            else {
                                                Toast.makeText(ChangePassword.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }
        });
    }
}