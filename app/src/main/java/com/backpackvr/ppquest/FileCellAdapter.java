package com.backpackvr.ppquest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class FileCellAdapter extends RecyclerView.Adapter<FileCellAdapter.MyViewHolder>{
    private ArrayList<FileData> mDataset;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public String fileName;
        public TextView name;
        public ImageView icon;

        public MyViewHolder(View file_cell, OnItemClickListener listener) {
            super(file_cell);
            name =  file_cell.findViewById(R.id.file_name);
            icon =  file_cell.findViewById(R.id.file_icon);
            file_cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("TAG", "onClick");
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(file_cell, position);
                        }
                    }

                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public FileCellAdapter(ArrayList<FileData> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FileCellAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_cell, parent, false);
        MyViewHolder vh = new MyViewHolder(v, mListener);
        return vh;
    }

    public void setData(ArrayList<FileData> list){
        this.mDataset.clear();
        this.mDataset.addAll(list);
        this.notifyDataSetChanged();
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.name.setText(mDataset.get(position).name);
        holder.fileName = mDataset.get(position).name;
        if (mDataset.get(position).isFolder) {
            holder.icon.setImageResource(R.drawable.ic_folder);
        }
        else {
            String extension = holder.fileName.substring(holder.fileName.lastIndexOf("."));
            if (extension.equals(".apk")) {
                holder.icon.setImageResource(R.drawable.ic_apk);
            } else if (extension.equals(".zip")) {
                holder.icon.setImageResource(R.drawable.ic_zip);
            } else {
                holder.icon.setImageResource(R.drawable.ic_file);
            }

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
