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

public class EditAdapter extends RecyclerView.Adapter<EditAdapter.ExampleViewHolder> {

    private ArrayList<EditItem> mExampleList;
    private Context mContext;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder{
        View view;

        public ImageView mImageView;
        public TextView mCapsuleName;
        public TextView mOpenDate;

        public ExampleViewHolder(View itemView) {
            super(itemView);

            this.view = itemView;

            mImageView = itemView.findViewById(R.id.imageView);
            mCapsuleName = itemView.findViewById(R.id.capsule_name);
            mOpenDate = itemView.findViewById(R.id.open_date);
        }
    }

    public EditAdapter(ArrayList<EditItem> exampleList, Context context) {
        mContext = context;
        mExampleList = exampleList;
    }

    @NonNull
    @Override
    public EditAdapter.ExampleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.example_item, viewGroup, false);
        return new ExampleViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EditAdapter.ExampleViewHolder exampleViewHolder, int position) {
        final EditItem currentItem = mExampleList.get(position);

        exampleViewHolder.mImageView.setImageResource(currentItem.getImageResource());
        exampleViewHolder.mCapsuleName.setText(currentItem.getCapsuleName());
        exampleViewHolder.mOpenDate.setText("Open Until:\n" + currentItem.getOpenDate());

        exampleViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println(currentItem.getCapsuleId());

                Intent intent = new Intent(mContext, ContributeActivity.class);
                intent.putExtra("capsuleId", currentItem.getCapsuleId());

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mExampleList.size();
    }
}
