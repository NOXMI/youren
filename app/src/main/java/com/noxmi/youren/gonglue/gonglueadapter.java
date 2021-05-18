package com.noxmi.youren.gonglue;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.noxmi.youren.R;

import java.util.List;

public class gonglueadapter extends RecyclerView.Adapter<gonglueadapter.ViewHolder> {

        private Context mContext;

        private List<gonglueinfo> mgongluelist;

        static class ViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            ImageView gonglueimage;
            TextView gonglueibiaoti;

            public ViewHolder(View view) {
                super(view);
                cardView = (CardView) view;
                gonglueimage = (ImageView) view.findViewById(R.id.gonglue_image);
                gonglueibiaoti = (TextView) view.findViewById(R.id.gonglue_biaoti);
            }
        }

        public gonglueadapter(List<gonglueinfo> gongluelist) {
            mgongluelist = gongluelist;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (mContext == null) {
                mContext = parent.getContext();
            }
            View view = LayoutInflater.from(mContext).inflate(R.layout.activity_gonglue, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            gonglueinfo gonglue = mgongluelist.get(position);
            holder.gonglueibiaoti.setText(gonglue.getBiaoti());
        }

        @Override
        public int getItemCount() {
            return mgongluelist.size();
        }

    }