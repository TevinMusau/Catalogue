package com.tevin.inside;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddCategory extends AppCompatActivity {
    private EditText categoryName;
    private EditText categoryDescription;
    private Button createCategoryBTN;

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;

    private DatabaseReference categoriesReference;
    private DatabaseReference user_with_category;
    private  DatabaseReference itemsReference;

    private FirebaseUser user;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        categoryName = findViewById(R.id.category_name);
        categoryDescription = findViewById(R.id.category_description);
        createCategoryBTN = findViewById(R.id.createCategoryBTN);

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        categoriesReference = database.getReference().child("Categories");
        itemsReference = database.getReference().child("Items");

        user = FirebaseAuth.getInstance().getCurrentUser();

        userID = user.getUid();

        createCategoryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category_name = categoryName.getText().toString().trim();
                String category_description = categoryDescription.getText().toString().trim();

                if (TextUtils.isEmpty(category_name) && TextUtils.isEmpty(category_description)) {
                    categoryName.setError("This field is required");
                    categoryName.requestFocus();
                } else {
                    if (!TextUtils.isEmpty(category_name) && TextUtils.isEmpty(category_description)) {
                        categoryName.setError("This field is required");
                        categoryName.requestFocus();
                    } else if (TextUtils.isEmpty(category_name) && !TextUtils.isEmpty(category_description)) {
                        categoryDescription.setError("This field is required");
                        categoryDescription.requestFocus();
                    }
                    else {
                        DatabaseReference categoryIDref = FirebaseDatabase.getInstance().getReference("Categories").push();
                        categoryIDref.child("UserID").setValue(userID).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AddCategory.this, "New User Category", Toast.LENGTH_SHORT).show();
                            }
                        });
                        categoryIDref.child("CategoryName").setValue(category_name).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AddCategory.this, "Name Successfully Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                        categoryIDref.child("CategoryDescription").setValue(category_description).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AddCategory.this, "Description Successfully Uploaded", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Intent backToMain = new Intent(AddCategory.this, MainActivity.class);
                        backToMain.putExtra("CategoryName", category_name);
                        startActivity(backToMain);
                    }
                }

            }
        });
    }
}
