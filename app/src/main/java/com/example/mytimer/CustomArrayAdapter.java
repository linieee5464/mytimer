package com.example.mytimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
/* Customised ArrayAdapter class to populate the list view*/
/* Inherit the ArrayAdapter class*/
/* Inheritance, polymorphism, encapsulation*/
public class CustomArrayAdapter extends ArrayAdapter<Exercise> {
    private Context context;
    private int layoutID;
    private ArrayList<Exercise> exercises;

    public CustomArrayAdapter(@NonNull Context context,
                          int resource,
                          @NonNull List<Exercise> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutID = resource;
        this.exercises = (ArrayList<Exercise>) objects;
    }

    @Override
    public int getCount() {
        return exercises.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView,
                        @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(layoutID, null, false);
        }
        ImageView imageView = convertView.findViewById(R.id.ex_image);
        TextView ex_name = convertView.findViewById(R.id.ex_name);
        //TextView ex_length = convertView.findViewById(R.id.ex_length);
        TextView ex_desc = convertView.findViewById(R.id.ex_desc);
        Exercise ex = exercises.get(position);
        imageView.setImageBitmap(ex.getImage());
        ex_name.setText(ex.getName());
        //ex_length.setText(ex.getLength());
        ex_desc.setText(ex.getDescription());
        return convertView;
    }
}
