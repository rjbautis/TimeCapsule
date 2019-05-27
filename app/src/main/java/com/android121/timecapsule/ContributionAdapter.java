package com.android121.timecapsule;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ContributionAdapter extends ArrayAdapter<ContributionItem> {

        private final Context context;
        private final List<ContributionItem> contributions;

        public ContributionAdapter(Context context, List<ContributionItem> contributions){
            super(context, R.layout.rowlayout, contributions);
            this.context = context;
            this.contributions = contributions;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View rowView = null;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.rowlayout, parent, false);

            // Displaying a textview
            TextView textView = (TextView) rowView.findViewById(R.id.label);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.image);
            ContributionItem contribution = contributions.get(position);
            if(contribution.type.equals("text")){
                textView.setText(contribution.content);
            } else if (contribution.type.equals("photo")){
                textView.setVisibility(View.GONE);
                Glide.with(context).load(contribution.content).into(imageView);
            }




            return rowView;
        }


}
