package com.example.marryzhi.yysteps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends ArrayAdapter<Step> {
    private int resourceId;

    public MyAdapter(Context context, int textViewResourceId, List<Step> objects){
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Step step = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

        TextView name = (TextView) view.findViewById(R.id.num_list);
        name.setText(step.getNum());
        TextView time = (TextView) view.findViewById(R.id.week_list);
        time.setText(step.getWeek());
        TextView date = (TextView) view.findViewById(R.id.date_list);
        date.setText(step.getDate());
        return view;
    }
}
