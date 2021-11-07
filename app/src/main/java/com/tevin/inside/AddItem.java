package com.tevin.inside;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddItem extends AppCompatActivity {
    private ImageButton itemImageButton;
    private EditText itemName;
    private EditText itemQuantity;
    private Button submitItemBTN;

    private DatabaseReference itemsReference;

    private StorageReference mStorageRef;

    private final static int GALLERY_REQ = 1;

    private Uri itemImageURI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        itemImageButton = findViewById(R.id.image_item);
        itemName = findViewById(R.id.item_name_add);
        itemQuantity = findViewById(R.id.item_quantity_add);
        submitItemBTN = findViewById(R.id.submitItemBTN);

        itemsReference = FirebaseDatabase.getInstance().getReference("Items").push();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        itemImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Open Gallery and get URI of what image is picked
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_REQ);
            }
        });

        submitItemBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item_name = itemName.getText().toString().trim();
                String item_quantity = itemQuantity.getText().toString();
                itemsReference.child("ItemName").setValue(item_name);
                itemsReference.child("ItemQuantity").setValue(item_quantity);
                Intent category_name = getIntent();
                String name_category = category_name.getStringExtra("CategoryName");
                itemsReference.child("ItemCategoryName").setValue(name_category);
                uploadImage();
            }

            private void uploadImage() {
                if (itemImageURI != null) {
                    StorageReference itemImagePath = mStorageRef.child("item_images").child(itemImageURI.getLastPathSegment());
                    itemImagePath.putFile(itemImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            if (taskSnapshot.getMetadata() != null) {
                                if (taskSnapshot.getMetadata().getReference() != null) {
                                    //Get download Url from your storage
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.d("DownloadUri", "Successfully Retrieved");

                                            //Convert url to string
                                            final String itemImage = uri.toString();

                                            itemsReference.child("UserID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                            itemsReference.child("ItemImage").setValue(itemImage);

                                            Intent i = new Intent(AddItem.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //Getting the Uri of the image which has been selected
            itemImageURI = data.getData();
            itemImageButton.setImageURI(itemImageURI);
        }
    }
}