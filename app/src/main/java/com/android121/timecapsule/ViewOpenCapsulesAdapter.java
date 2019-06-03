package com.android121.timecapsule;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewOpenCapsulesAdapter extends RecyclerView.Adapter<ViewOpenCapsulesAdapter.ViewOpenCapsulesHolder> {
    private ArrayList<CapsuleItem> openCapsules;
    private Context mContext;

    public static class ViewOpenCapsulesHolder extends RecyclerView.ViewHolder{
        View view;

        public ImageView mImageView;
        public TextView mCapsuleName;
        public TextView mOpenDate;

        public ViewOpenCapsulesHolder(View itemView) {
            super(itemView);

            this.view = itemView;

            mImageView = itemView.findViewById(R.id.imageView);
            mCapsuleName = itemView.findViewById(R.id.capsule_name);
            mOpenDate = itemView.findViewById(R.id.open_date);
        }
    }

    public ViewOpenCapsulesAdapter(ArrayList<CapsuleItem> openCapsules, Context context) {
        mContext = context;
        this.openCapsules = openCapsules;
    }

    @NonNull
    @Override
    public ViewOpenCapsulesAdapter.ViewOpenCapsulesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.capsule_item, viewGroup, false);
        return new ViewOpenCapsulesHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewOpenCapsulesAdapter.ViewOpenCapsulesHolder viewOpenCapsulesHolder, int position) {
        final CapsuleItem currentItem = openCapsules.get(position);

        viewOpenCapsulesHolder.mImageView.setImageResource(currentItem.getImageResource());
        viewOpenCapsulesHolder.mCapsuleName.setText(currentItem.getCapsuleName());
        viewOpenCapsulesHolder.mOpenDate.setText("Available Since:\n" + currentItem.getOpenDate());

        viewOpenCapsulesHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, OpenCapsuleActivity.class);
                intent.putExtra("capsuleId", currentItem.getCapsuleId());

                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return openCapsules.size();
    }
}
