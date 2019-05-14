package com.android121.timecapsule;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ContributionAdapter extends ArrayAdapter<String> {

        private final Context context;
        //private final List<String> types;
        private final List<String> contents;

        public ContributionAdapter(Context context, List<String> contents){
            super(context, R.layout.rowlayout, contents);
            this.context = context;
            //this.types = types;
            this.contents = contents;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View rowView = null;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            // Displaying a textview
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            textView.setText(contents.get(position));

            return rowView;
        }


}
