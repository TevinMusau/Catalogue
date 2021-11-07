package com.tevin.inside;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class EditDetails extends AppCompatActivity {
    private ImageButton imageButton;
    private TextView changeImage;
    private EditText editDisplayName;
    private Button submitEdits;

    private FirebaseAuth mAuth;

    private DatabaseReference mDatabaseUser;

    private StorageReference mStorageRef;

    FirebaseUser user;

    private Uri profileImageURI = null;

    private final static int GALLERY_REQ = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_details);


        // Initialize the view instances
        imageButton = findViewById(R.id.new_image);
        editDisplayName = findViewById(R.id.edit_display_name);
        submitEdits = findViewById(R.id.submitEdits);

        //Initializing Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        final String userID = mAuth.getCurrentUser().getUid();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery and get URI of what image is picked
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQ);
            }
        });



        /*On clicking Submit Button
         * Get name of photo and save it on a database reference for a specific user
         */
        submitEdits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String display_name_new = editDisplayName.getText().toString().trim();

                if (TextUtils.isEmpty(display_name_new) && profileImageURI == null)
                {
                    Toast.makeText(EditDetails.this, "Change an Item to Submit", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (!TextUtils.isEmpty(display_name_new) && profileImageURI == null) {
                        //Get the new display name and set it to the database
                        mDatabaseUser.child("displayName").setValue(display_name_new);
                    }
                    else if (profileImageURI != null && TextUtils.isEmpty(display_name_new)) {
                        uploadImage();
                    }
                    else if (!TextUtils.isEmpty(display_name_new) && profileImageURI != null) {
                        mDatabaseUser.child("displayName").setValue(display_name_new);
                        uploadImage();
                    }
                }
            }

            private void uploadImage() {
                if (profileImageURI != null) {
                    Log.d("Here", "Hello There!");
                    StorageReference profileImagePath = mStorageRef.child("profile_images").child(profileImageURI.getLastPathSegment());

                    //store the image in the storage reference and add an onSuccessListener
                    profileImagePath.putFile(profileImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null)
                            {
                                if (taskSnapshot.getMetadata().getReference() != null)
                                {
                                    //Get download Url from your storage
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();

                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("DownloadUri", "Successfully Retrieved");

                                            //Convert url to string
                                            final String profileImage = uri.toString();

                                            mDatabaseUser.push();

                                            mDatabaseUser.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    mDatabaseUser.child("ProfileImage").setValue(profileImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Toast.makeText(EditDetails.this, "Profile Successfully Updated", Toast.LENGTH_SHORT).show();

                                                                Intent toProfile = new Intent(EditDetails.this, ProfileActivity.class);
                                                                startActivity(toProfile);
                                                            }
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                }
                                            });
                                        }
                                    });
                                }
                            }
                        }
                    });
                } else {
                    Log.d("Profile Image", "No Profile Image");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //Getting the Uri of the image which has been selected
            profileImageURI = data.getData();
            imageButton.setImageURI(profileImageURI);
        }
    }
}