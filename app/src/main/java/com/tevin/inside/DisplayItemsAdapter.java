package com.tevin.inside;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DisplayItemsAdapter extends RecyclerView.Adapter<DisplayItemsAdapter.ViewHolder>
{
    private static final String TAG = "DisplayItemsAdapter";

    //vars
    private ArrayList<String> itemImageURLs;
    private ArrayList<String> itemNames;
    private ArrayList<String> itemQuantities;
    private int item_count;
    private Context myContext;

    public DisplayItemsAdapter(Context myContext, ArrayList<String> itemImageURLs, ArrayList<String> itemNames, ArrayList<String> itemQuantities, int item_count) {
        this.itemImageURLs = itemImageURLs;
        this.itemNames = itemNames;
        this.itemQuantities = itemQuantities;
        this.myContext = myContext;
        this.item_count = item_count;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        Log.d(TAG, "onBindViewHolder: called");
        Log.d(TAG, "URL: "+itemImageURLs);
        Picasso.get().load(this.itemImageURLs.get(position)).into(holder.itemImage);

        holder.itemName.setText(itemNames.get(position));
        holder.itemQuantity.setText(itemQuantities.get(position));
    }

    @Override
    public int getItemCount() {
        return this.item_count;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView itemImage;
        TextView itemName;
        TextView itemQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemName = itemView.findViewById(R.id.item_name);
            itemQuantity = itemView.findViewById(R.id.item_quantity);
        }
    }
}
