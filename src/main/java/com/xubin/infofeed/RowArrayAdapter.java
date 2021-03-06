package com.xubin.infofeed;

import java.util.List;
import java.util.ArrayList;


import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;
import android.util.Log;
import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;


public class RowArrayAdapter<T> extends ArrayAdapter<T> {
    private LayoutInflater inflater = null;


    public RowArrayAdapter(Context context, ArrayList<T> data){
        super(context, 0, data);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Log.i("RowArrayAdapter", "Entering getView");
        View vi = convertView;
        if (null == vi){
            vi = inflater.inflate(R.layout.list_row, parent, false);
        }

        ImageView avatar =(ImageView)vi.findViewById(R.id.row_avatar);
        TextView author = (TextView)vi.findViewById(R.id.row_author);
        TextView publish_time = (TextView)vi.findViewById(R.id.row_time);
        TextView content = (TextView)vi.findViewById(R.id.row_content);


        RowStructure rs = (RowStructure)this.getItem(position);

        avatar.setImageBitmap(rs.avatar);
        author.setText(rs.author);
        publish_time.setText(rs.publish_time);
        content.setText(rs.content);

        return vi;
    }
}
