package com.example.face.class_for_code;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.example.face.List_Object;
import com.example.face.R;

import java.util.ArrayList;
import java.util.List;

public class MyCustomAdapter extends ArrayAdapter<Object_Information> {

    public class ViewHolder {
        TextView tx_name;
        TextView tx_email;
    }

    public MyCustomAdapter(Context context, List<Object_Information> objects) {
        super(context, R.layout.row, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        ViewHolder viewHolder;

        if (convertView == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.row, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.tx_name = row.findViewById(R.id.textView1);
            viewHolder.tx_email = row.findViewById(R.id.textView2);

            row.setTag(viewHolder);
        } else {
            row = convertView;
            viewHolder = (ViewHolder) row.getTag();
        }

        Object_Information item = getItem(position);
        viewHolder.tx_name.setText(item.get_name());
        viewHolder.tx_email.setText(item.get_email());

        return row;
    }

}
