package com.tevin.inside;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private ImageView userImage;
    private TextView userDisplayName;
    private TextView userEmail;

    FirebaseDatabase mDatabase;

    private DatabaseReference mRef;

    private FirebaseUser user;

    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userImage = findViewById(R.id.userImage);
        userDisplayName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);

        mDatabase = FirebaseDatabase.getInstance();

        mRef = mDatabase.getReference().child("Users");

        user = FirebaseAuth.getInstance().getCurrentUser();

        userID = user.getUid();


        mRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null)
                {
                    String email = userProfile.getEmail();
                    String displayName = userProfile.getDisplayName();
                    if (snapshot.child("ProfileImage").getValue() == null)
                    {
                        Toast.makeText(ProfileActivity.this, "No Image Yet", Toast.LENGTH_SHORT).show();
                        userEmail.setText(email);
                        userDisplayName.setText(displayName);
                    }
                    else {
                        String image = snapshot.child("ProfileImage").getValue().toString();
                        Log.d("ImageURI: ", (image));

                        userEmail.setText(email);
                        userDisplayName.setText(displayName);

                        Picasso.get().load(image).into(userImage);
                    }

                }
                else {
                    Toast.makeText(ProfileActivity.this, "User no Longer Exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Something has happened", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(ProfileActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void editDetails(View view) {
        Intent editIntent = new Intent(ProfileActivity.this, EditDetails.class);
        startActivity(editIntent);
    }

    public void changePassword(View view) {
        Intent changePasswordIntent = new Intent(ProfileActivity.this, ChangePassword.class);
        startActivity(changePasswordIntent);
    }
}