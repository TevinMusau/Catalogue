package com.tevin.inside;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
{
    boolean isRotate = false;

    private FirebaseAuth mAuth;

    private LinearLayout mLayout;
    private TextView name;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        name = findViewById(R.id.cat_name);
        mLayout = findViewById(R.id.mylayout);

        FloatingActionButton fab = findViewById(R.id.fab);
        FloatingActionButton fabCat = findViewById(R.id.addCategory);

        ViewAnimation.init(fabCat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimation.rotateFab(v, !isRotate);
                if (isRotate) {
                    ViewAnimation.showIn(fabCat);
                } else {
                    ViewAnimation.showOut(fabCat);
                }
            }
        });
        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Add a Category", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        fabCat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(MainActivity.this, "Add a Category", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        fabCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCategory = new Intent(MainActivity.this, AddCategory.class);
                startActivity(toCategory);
                Toast.makeText(MainActivity.this, "Add Category", Toast.LENGTH_SHORT).show();
            }
        });

        DatabaseReference itemsReference = FirebaseDatabase.getInstance().getReference("Items");

        DatabaseReference userCategories = FirebaseDatabase.getInstance().getReference("Categories");
        if (mAuth.getInstance().getCurrentUser() == null)
        {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Query query = userCategories.orderByChild("UserID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getChildrenCount() == 1) {
                            Log.d("Here", "Theres only 1 ");
                            for (DataSnapshot sn : snapshot.getChildren()) {
                                final LinearLayout layout = new LinearLayout(MainActivity.this);
                                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                                params.setMargins(20, 10, 0, 10);
                                layout.setLayoutParams(params);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                final TextView textView = new TextView(MainActivity.this);
                                textView.setId(View.generateViewId());
                                textView.setText(sn.child("CategoryName").getValue().toString());
                                textView.setTextSize(25);
                                textView.setGravity(Gravity.CENTER);
                                textView.setPadding(0, 8, 0, 10);
                                mLayout.addView(textView);

                                Query query = itemsReference.orderByChild("UserID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Query qn = itemsReference.orderByChild("ItemCategoryName").equalTo(textView.getText().toString());
                                            qn.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if (snapshot.exists()) {
                                                        if (snapshot.getChildrenCount() == 1) {
                                                            final LinearLayout layout_btn_category = new LinearLayout(MainActivity.this);
                                                            layout_btn_category.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                            layout_btn_category.setOrientation(LinearLayout.VERTICAL);
                                                            Button addItemsInCategory = new Button(MainActivity.this);
                                                            addItemsInCategory.setId(View.generateViewId());
                                                            addItemsInCategory.setText("+Add Item");
                                                            addItemsInCategory.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    String categoryName = sn.child("CategoryName").getValue().toString();
                                                                    Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                                    toAddItem.putExtra("CategoryName", categoryName);
                                                                    startActivity(toAddItem);
                                                                }
                                                            });
                                                            mLayout.addView(addItemsInCategory);

                                                            final LinearLayout recycler_layout = new LinearLayout(MainActivity.this);
                                                            recycler_layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                            recycler_layout.setOrientation(LinearLayout.VERTICAL);
                                                            final RecyclerView recyclerView = new RecyclerView(MainActivity.this);
                                                            recyclerView.setId(View.generateViewId());
                                                            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                                            recyclerView.setLayoutManager(layoutManager);

                                                            Query q3 = itemsReference.orderByChild("ItemCategoryName").equalTo(textView.getText().toString());
                                                            q3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                                                        Items item = snap.getValue(Items.class);
                                                                        if (item != null) {
                                                                            ArrayList<String> images = new ArrayList<>();
                                                                            ArrayList<String> names = new ArrayList<>();
                                                                            ArrayList<String> quantities = new ArrayList<>();

                                                                            String name = item.getItem_name();
                                                                            String image = item.getItem_image();
                                                                            String quantity = item.getItem_quantity();
                                                                            Log.d("Values", "Key: " + name + ", " + image + ", " + quantity);

                                                                            images.add(item.getItem_image());
                                                                            names.add(item.getItem_name());
                                                                            quantities.add(item.getItem_quantity());

                                                                            RecyclerView.Adapter adapter = new DisplayItemsAdapter(MainActivity.this, images, names, quantities, 1);

                                                                            recyclerView.setAdapter(adapter);
                                                                            mLayout.addView(recyclerView);
                                                                        }
                                                                        else {
                                                                            Log.d("Error", "Loading failed");
                                                                            Toast.makeText(MainActivity.this, "Error Loading Items", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                }
                                                            });
                                                        }
                                                        else if (snapshot.getChildrenCount() > 1) {
                                                            final LinearLayout layout_btn_category = new LinearLayout(MainActivity.this);
                                                            layout_btn_category.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                            layout_btn_category.setOrientation(LinearLayout.VERTICAL);
                                                            Button addItemsInCategory = new Button(MainActivity.this);
                                                            addItemsInCategory.setId(View.generateViewId());
                                                            addItemsInCategory.setText("+Add Item");
                                                            addItemsInCategory.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    String categoryName = sn.child("CategoryName").getValue().toString();
                                                                    Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                                    toAddItem.putExtra("CategoryName", categoryName);
                                                                    startActivity(toAddItem);
                                                                }
                                                            });
                                                            mLayout.addView(addItemsInCategory);

                                                            final LinearLayout recycler_layout = new LinearLayout(MainActivity.this);
                                                            recycler_layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                            recycler_layout.setOrientation(LinearLayout.VERTICAL);
                                                            final RecyclerView recyclerView = new RecyclerView(MainActivity.this);
                                                            recyclerView.setId(View.generateViewId());
                                                            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                                            recyclerView.setLayoutManager(layoutManager);

                                                            int counter = (int) snapshot.getChildrenCount();

                                                            ArrayList<String> images = new ArrayList<>();
                                                            ArrayList<String> names = new ArrayList<>();
                                                            ArrayList<String> quantities = new ArrayList<>();

                                                            Query q3 = itemsReference.orderByChild("ItemCategoryName").equalTo(textView.getText().toString());
                                                            q3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                                                        Items item = snap.getValue(Items.class);
                                                                        if (item != null) {
                                                                            images.add(item.getItem_image());
                                                                            names.add(item.getItem_name());
                                                                            quantities.add(item.getItem_quantity());

                                                                            RecyclerView.Adapter adapter = new DisplayItemsAdapter(MainActivity.this, images, names, quantities, counter);
                                                                            i++;
                                                                            recyclerView.setAdapter(adapter);

                                                                            Toast.makeText(MainActivity.this, "There are > 1 items in this category", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                    mLayout.addView(recyclerView);
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError error) {

                                                                }
                                                            });
                                                        }
                                                    }
                                                    else {
                                                        final LinearLayout layout_btn = new LinearLayout(MainActivity.this);
                                                        layout_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                        layout_btn.setOrientation(LinearLayout.VERTICAL);
                                                        Button addItems = new Button(MainActivity.this);
                                                        addItems.setId(View.generateViewId());
                                                        Log.d("New: ", "Created BTN");
                                                        addItems.setText("+Add Item");
                                                        addItems.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {
                                                                String categoryName = sn.child("CategoryName").getValue().toString();
                                                                Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                                toAddItem.putExtra("CategoryName", categoryName);
                                                                startActivity(toAddItem);
                                                            }
                                                        });
                                                        mLayout.addView(addItems);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        } else {
                                            final LinearLayout layout_btn = new LinearLayout(MainActivity.this);
                                            layout_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                            layout_btn.setOrientation(LinearLayout.VERTICAL);
                                            Button addItems = new Button(MainActivity.this);
                                            addItems.setId(View.generateViewId());
                                            Log.d("New: ", "Created BTN");
                                            addItems.setText("+Add Item");
                                            addItems.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String categoryName = sn.child("CategoryName").getValue().toString();
                                                    Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                    toAddItem.putExtra("CategoryName", categoryName);
                                                    startActivity(toAddItem);
                                                }
                                            });
                                            addItems.setTextSize(20);
                                            mLayout.addView(addItems);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                        else if (snapshot.getChildrenCount() > 1) {
                            Log.d("Here", "Theres > 1 ");
                            for (DataSnapshot sn : snapshot.getChildren()) {
                                final LinearLayout layout = new LinearLayout(MainActivity.this);
                                LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                                params.setMargins(20, 10, 0, 10);
                                layout.setLayoutParams(params);
                                layout.setOrientation(LinearLayout.VERTICAL);
                                final TextView textView = new TextView(MainActivity.this);
                                textView.setId(View.generateViewId());
                                textView.setText(sn.child("CategoryName").getValue().toString());
                                textView.setTextSize(20);
                                textView.setTextColor(getResources().getColor(R.color.heading_color));
                                textView.setTypeface(null, Typeface.BOLD);
                                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                                textView.setPadding(0, 8, 0, 10);

                                Query query = itemsReference.orderByChild("UserID").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            if (snapshot.getChildrenCount() == sn.getChildrenCount()) {
                                                Log.d("All", "We are all");
                                                Query qn = itemsReference.orderByChild("ItemCategoryName").equalTo(textView.getText().toString());
                                                qn.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            if (snapshot.getChildrenCount() == 1) {
                                                                final LinearLayout recycler_layout = new LinearLayout(MainActivity.this);
                                                                recycler_layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                                recycler_layout.setOrientation(LinearLayout.VERTICAL);
                                                                final RecyclerView recyclerView = new RecyclerView(MainActivity.this);
                                                                recyclerView.setId(View.generateViewId());
                                                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                                                recyclerView.setLayoutManager(layoutManager);

                                                                ArrayList<String> images = new ArrayList<>();
                                                                ArrayList<String> names = new ArrayList<>();
                                                                ArrayList<String> quantities = new ArrayList<>();

                                                                Query q3 = itemsReference.orderByChild("ItemCategoryName").equalTo(textView.getText().toString());
                                                                q3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                                                            Items item = snap.getValue(Items.class);
                                                                            if (item != null) {
                                                                                images.add(item.getItem_image());
                                                                                names.add(item.getItem_name());
                                                                                quantities.add(item.getItem_quantity());

                                                                                RecyclerView.Adapter adapter = new DisplayItemsAdapter(MainActivity.this, images, names, quantities, 1);
                                                                                recyclerView.setAdapter(adapter);
                                                                                mLayout.addView(textView);

                                                                                final LinearLayout layout_btn_category = new LinearLayout(MainActivity.this);
                                                                                layout_btn_category.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                                                layout_btn_category.setOrientation(LinearLayout.VERTICAL);
                                                                                Button addItemsInCategory = new Button(MainActivity.this);
                                                                                addItemsInCategory.setId(View.generateViewId());
                                                                                addItemsInCategory.setText("+Add Item");
                                                                                Log.d("CategoryBTN", "Added");
                                                                                addItemsInCategory.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        String categoryName = sn.child("CategoryName").getValue().toString();
                                                                                        Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                                                        toAddItem.putExtra("CategoryName", categoryName);
                                                                                        startActivity(toAddItem);
                                                                                    }
                                                                                });
                                                                                mLayout.addView(addItemsInCategory);
                                                                                mLayout.addView(recyclerView);
                                                                            } else {
                                                                                Log.d("Error", "Loading failed");
                                                                                Toast.makeText(MainActivity.this, "Error Loading Items", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }
                                                            else if (snapshot.getChildrenCount() > 1) {
                                                                mLayout.addView(textView);
                                                                final LinearLayout layout_btn_category = new LinearLayout(MainActivity.this);
                                                                layout_btn_category.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                                layout_btn_category.setOrientation(LinearLayout.VERTICAL);
                                                                Button addItemsInCategory = new Button(MainActivity.this);
                                                                addItemsInCategory.setId(View.generateViewId());
                                                                addItemsInCategory.setText("+Add Item");
                                                                addItemsInCategory.setEms(10);
                                                                addItemsInCategory.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        String categoryName = sn.child("CategoryName").getValue().toString();
                                                                        Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                                        toAddItem.putExtra("CategoryName", categoryName);
                                                                        startActivity(toAddItem);
                                                                    }
                                                                });
                                                                mLayout.addView(addItemsInCategory);

                                                                final LinearLayout recycler_layout = new LinearLayout(MainActivity.this);
                                                                recycler_layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                                recycler_layout.setOrientation(LinearLayout.VERTICAL);
                                                                final RecyclerView recyclerView = new RecyclerView(MainActivity.this);
                                                                recyclerView.setId(View.generateViewId());
                                                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                                                recyclerView.setLayoutManager(layoutManager);

                                                                int counter = (int) snapshot.getChildrenCount();

                                                                ArrayList<String> images = new ArrayList<>();
                                                                ArrayList<String> names = new ArrayList<>();
                                                                ArrayList<String> quantities = new ArrayList<>();

                                                                Query q3 = itemsReference.orderByChild("ItemCategoryName").equalTo(textView.getText().toString());
                                                                q3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                                                            Items item = snap.getValue(Items.class);
                                                                            if (item != null) {
                                                                                images.add(item.getItem_image());
                                                                                names.add(item.getItem_name());
                                                                                quantities.add(item.getItem_quantity());

                                                                                RecyclerView.Adapter adapter = new DisplayItemsAdapter(MainActivity.this, images, names, quantities, counter);
                                                                                i++;
                                                                                recyclerView.setAdapter(adapter);

                                                                                Toast.makeText(MainActivity.this, "There are > 1 items in this category", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                        mLayout.addView(recyclerView);
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            }
                                                        }
                                                        else {
                                                            mLayout.addView(textView);
                                                            final LinearLayout layout_btn = new LinearLayout(MainActivity.this);
                                                            layout_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                            layout_btn.setOrientation(LinearLayout.VERTICAL);
                                                            Button addItems = new Button(MainActivity.this);
                                                            addItems.setId(View.generateViewId());
                                                            Log.d("New: ", "Created BTN");
                                                            addItems.setText("+Add Item");
                                                            addItems.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    String categoryName = sn.child("CategoryName").getValue().toString();
                                                                    Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                                    toAddItem.putExtra("CategoryName", categoryName);
                                                                    startActivity(toAddItem);
                                                                }
                                                            });
                                                            mLayout.addView(addItems);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            } else {
                                                Log.d("Some", "We are some");
                                                Query qn = itemsReference.orderByChild("ItemCategoryName").equalTo(textView.getText().toString());
                                                qn.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {
                                                            if (snapshot.getChildrenCount() == 1) {
                                                                final LinearLayout recycler_layout = new LinearLayout(MainActivity.this);
                                                                recycler_layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                                recycler_layout.setOrientation(LinearLayout.VERTICAL);
                                                                final RecyclerView recyclerView = new RecyclerView(MainActivity.this);
                                                                recyclerView.setId(View.generateViewId());
                                                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                                                recyclerView.setLayoutManager(layoutManager);

                                                                ArrayList<String> images = new ArrayList<>();
                                                                ArrayList<String> names = new ArrayList<>();
                                                                ArrayList<String> quantities = new ArrayList<>();

                                                                Query q3 = itemsReference.orderByChild("ItemCategoryName").equalTo(textView.getText().toString());
                                                                q3.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        for (DataSnapshot snap : snapshot.getChildren()) {
                                                                            Items item = snap.getValue(Items.class);
                                                                            if (item != null) {
                                                                                images.add(item.getItem_image());
                                                                                names.add(item.getItem_name());
                                                                                quantities.add(item.getItem_quantity());

                                                                                RecyclerView.Adapter adapter = new DisplayItemsAdapter(MainActivity.this, images, names, quantities, 1);
                                                                                recyclerView.setAdapter(adapter);
                                                                                mLayout.addView(textView);

                                                                                final LinearLayout layout_btn_category = new LinearLayout(MainActivity.this);
                                                                                layout_btn_category.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                                                layout_btn_category.setOrientation(LinearLayout.VERTICAL);
                                                                                Button addItemsInCategory = new Button(MainActivity.this);
                                                                                addItemsInCategory.setId(View.generateViewId());
                                                                                addItemsInCategory.setText("+Add Item");
                                                                                addItemsInCategory.setOnClickListener(new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        String categoryName = sn.child("CategoryName").getValue().toString();
                                                                                        Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                                                        toAddItem.putExtra("CategoryName", categoryName);
                                                                                        startActivity(toAddItem);
                                                                                    }
                                                                                });
                                                                                mLayout.addView(addItemsInCategory);

                                                                                mLayout.addView(recyclerView);
                                                                            } else {
                                                                                Log.d("Error", "Loading failed");
                                                                                Toast.makeText(MainActivity.this, "Error Loading Items", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
                                                            } else if (snapshot.getChildrenCount() > 1) {
                                                                mLayout.addView(textView);

                                                                final LinearLayout layout_btn_category = new LinearLayout(MainActivity.this);
                                                                layout_btn_category.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                                layout_btn_category.setOrientation(LinearLayout.VERTICAL);
                                                                Button addItemsInCategory = new Button(MainActivity.this);
                                                                addItemsInCategory.setId(View.generateViewId());
                                                                addItemsInCategory.setText("+Add Item");
                                                                addItemsInCategory.setOnClickListener(new View.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(View v) {
                                                                        String categoryName = sn.child("CategoryName").getValue().toString();
                                                                        Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                                        toAddItem.putExtra("CategoryName", categoryName);
                                                                        startActivity(toAddItem);
                                                                    }
                                                                });
                                                                mLayout.addView(addItemsInCategory);

                                                                final LinearLayout recycler_layout = new LinearLayout(MainActivity.this);
                                                                recycler_layout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                                recycler_layout.setOrientation(LinearLayout.VERTICAL);
                                                                final RecyclerView recyclerView = new RecyclerView(MainActivity.this);
                                                                recyclerView.setId(View.generateViewId());
                                                                LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
                                                                recyclerView.setLayoutManager(layoutManager);

                                                                ArrayList<String> images = new ArrayList<>();
                                                                ArrayList<String> names = new ArrayList<>();
                                                                ArrayList<String> quantities = new ArrayList<>();

                                                                for (DataSnapshot snap : snapshot.getChildren()) {
                                                                    Items item = snap.getValue(Items.class);
                                                                    if (item != null) {
                                                                        int counter = (int) snapshot.getChildrenCount();

                                                                        images.add(item.getItem_image());
                                                                        names.add(item.getItem_name());
                                                                        quantities.add(item.getItem_quantity());

                                                                        RecyclerView.Adapter adapter = new DisplayItemsAdapter(MainActivity.this, images, names, quantities, counter);
                                                                        i++;

                                                                        recyclerView.setAdapter(adapter);

                                                                        Toast.makeText(MainActivity.this, "There are > 1 items in this category", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                                mLayout.addView(recyclerView);
                                                            }
                                                        } else {
                                                            mLayout.addView(textView);
                                                            final LinearLayout layout_btn = new LinearLayout(MainActivity.this);
                                                            layout_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                                            layout_btn.setOrientation(LinearLayout.VERTICAL);
                                                            Button addItems = new Button(MainActivity.this);
                                                            addItems.setId(View.generateViewId());
                                                            Log.d("New: ", "Created BTN");
                                                            addItems.setText("+Add Item");
                                                            addItems.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    String categoryName = sn.child("CategoryName").getValue().toString();
                                                                    Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                                    toAddItem.putExtra("CategoryName", categoryName);
                                                                    startActivity(toAddItem);
                                                                }
                                                            });
                                                            mLayout.addView(addItems);
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }
                                        }
                                        else {
                                            mLayout.addView(textView);
                                            final LinearLayout layout_btn = new LinearLayout(MainActivity.this);
                                            layout_btn.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                                            layout_btn.setOrientation(LinearLayout.VERTICAL);
                                            Button addItems = new Button(MainActivity.this);
                                            addItems.setId(View.generateViewId());
                                            Log.d("New: ", "Created BTN");
                                            addItems.setText("+Add Item");
                                            addItems.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    String categoryName = sn.child("CategoryName").getValue().toString();

                                                    Intent toAddItem = new Intent(MainActivity.this, AddItem.class);
                                                    toAddItem.putExtra("CategoryName", categoryName);
                                                    startActivity(toAddItem);
                                                }
                                            });
                                            addItems.setTextSize(20);
                                            mLayout.addView(addItems);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    } else {
                        Log.d("Here", "Theres none");
                        final LinearLayout layout = new LinearLayout(MainActivity.this);
                        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                        params.setMargins(20, 10, 0, 10);
                        layout.setLayoutParams(params);
                        layout.setOrientation(LinearLayout.VERTICAL);
                        final TextView textView = new TextView(MainActivity.this);
                        textView.setId(View.generateViewId());
                        Log.d("Here1", "Lies Nothing");
                        textView.setText("No Categories");
                        textView.setTextSize(20);
                        textView.setTextColor(getResources().getColor(R.color.heading_color));
                        textView.setTypeface(null, Typeface.BOLD);
                        textView.setGravity(Gravity.CENTER_HORIZONTAL);
                        textView.setPadding(0, 8, 0, 10);

                        mLayout.addView(textView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.logout_main:
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                return true;

            case R.id.toProfile_main:
                Intent toProf = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(toProf);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}