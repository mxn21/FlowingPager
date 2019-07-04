package com.mxn.soul.flowingpager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private int itemsCount = 0;

    FeedAdapter(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_feed, parent, false);

        return new CellFeedViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final CellFeedViewHolder holder = (CellFeedViewHolder) viewHolder;
        bindDefaultFeedItem(position, holder);
    }

    private void bindDefaultFeedItem(int position, CellFeedViewHolder holder) {
        if (position % 4 == 0) {
            holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_1);
            holder.title.setText(R.string.title1);
            holder.content.setText(R.string.content1);
            holder.author.setText(R.string.author1);
            holder.info.setText(R.string.info1);
        } else if(position % 4 == 1){
            holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_2);
            holder.title.setText(R.string.title2);
            holder.content.setText(R.string.content2);
            holder.author.setText(R.string.author2);
            holder.info.setText(R.string.info2);
        }else if(position % 4 == 2){
            holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_3);
            holder.title.setText(R.string.title3);
            holder.content.setText(R.string.content3);
            holder.author.setText(R.string.author3);
            holder.info.setText(R.string.info3);
        }else {
            holder.ivFeedCenter.setImageResource(R.drawable.img_feed_center_4);
            holder.title.setText(R.string.title4);
            holder.content.setText(R.string.content4);
            holder.author.setText(R.string.author4);
            holder.info.setText(R.string.info4);
        }
        holder.btnComments.setTag(position);
        holder.btnMore.setTag(position);
        holder.ivFeedCenter.setTag(holder);
        holder.btnLike.setTag(holder);
    }

    void updateItems() {
        itemsCount = 10;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    private static class CellFeedViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFeedCenter;
        ImageButton btnComments;
        ImageButton btnLike;
        ImageButton btnMore;
        TextView title;
        TextView content;
        TextView author;
        TextView info;

        CellFeedViewHolder(View view) {
            super(view);
            ivFeedCenter = (ImageView) view.findViewById(R.id.ivFeedCenter);
            btnComments = (ImageButton) view.findViewById(R.id.btnComments);
            btnLike = (ImageButton) view.findViewById(R.id.btnLike);
            btnMore = (ImageButton) view.findViewById(R.id.btnMore);
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
            author = (TextView) view.findViewById(R.id.author);
            info = (TextView) view.findViewById(R.id.info);
        }
    }


}
