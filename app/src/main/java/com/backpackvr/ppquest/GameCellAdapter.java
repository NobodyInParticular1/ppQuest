package com.backpackvr.ppquest;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

public class GameCellAdapter extends RecyclerView.Adapter<GameCellAdapter.MyViewHolder>{
    private static ArrayList<Game> mDataset;

    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    ImageLoader imageLoader = ImageLoader.getInstance();

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView size;
        public TextView price;
        public ImageView thumbnail;
        public TextView tags;

        public String game_id;

        public MyViewHolder(View game_cell, OnItemClickListener listener) {
            super(game_cell);

            game_cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();

                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(game_cell, position);
                        }
                    }

                }
            });

            title =  game_cell.findViewById(R.id.cell_title);
            size =  game_cell.findViewById(R.id.cell_size);
            price =  game_cell.findViewById(R.id.cell_price);
            thumbnail = game_cell.findViewById(R.id.cell_thumbnail);
            tags = game_cell.findViewById(R.id.cell_tags);

            View infoOverlay= game_cell.findViewById(R.id.infoOverlay);
            infoOverlay.bringToFront();
            infoOverlay.setVisibility(View.GONE);


            game_cell.setOnHoverListener(new View.OnHoverListener() {
                long hoverStart = 0;
                int k = 0;
                @Override
                public boolean onHover(View v, MotionEvent ev) {
                    int position = getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) {
                        return false;
                    }
                    int screenshotsSize = mDataset.get(position).images.size();

                    infoOverlay.setVisibility(View.VISIBLE);

                    if (ev.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
                        hoverStart =  System.currentTimeMillis();
                        k = 0;
                    }

                    if (ev.getAction() == MotionEvent.ACTION_HOVER_MOVE) {
                        float dt =  (float)(System.currentTimeMillis() - hoverStart) / (float)1000;
                        if (dt > 1f) {
                            k += 1;
                            if (k == screenshotsSize) {
                                k = 0;
                            }
                            hoverStart =  System.currentTimeMillis();
                            ((MainActivity)game_cell.getContext()).displayImage(thumbnail, mDataset.get(position).images.get(k));
                        }
                    }

                    if (ev.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
                        infoOverlay.setVisibility(View.GONE);
                        game_cell.setBackgroundResource(0);
                        ((MainActivity)game_cell.getContext()).displayImage(thumbnail, mDataset.get(position).images.get(0));
                    }

                    return false;
                }
            });
        }
    }

    public GameCellAdapter(ArrayList<Game> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public GameCellAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_cell, parent, false);
        MyViewHolder vh = new MyViewHolder(v, mListener);
        return vh;
    }

    public void setData(ArrayList<Game> list){
        this.mDataset.clear();
        this.mDataset.addAll(list);
        this.notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.title.setText((position + 1) + ". " + mDataset.get(position).title);
        holder.size.setText(mDataset.get(position).size);
        holder.price.setText(mDataset.get(position).price);
        holder.game_id = mDataset.get(position).game_id;
        holder.tags.setText(TextUtils.join(", ", mDataset.get(position).tags));
        imageLoader.displayImage(mDataset.get(position).images.get(0), holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
