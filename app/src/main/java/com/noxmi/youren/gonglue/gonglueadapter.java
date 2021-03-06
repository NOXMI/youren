package com.noxmi.youren.gonglue;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.noxmi.youren.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class gonglueadapter extends RecyclerView.Adapter<gonglueadapter.ViewHolder> {

    private Context mContext;
    private List<gonglueinfo> mgongluelist;
    private OnItemClickListener mItemClickListener;
        static class ViewHolder extends RecyclerView.ViewHolder{
            CardView cardView;
            ImageView gonglueimage;
            TextView gonglueibiaoti,user,date,days,photonum,people,trip,fee,views,like,comment;
            CircleImageView gonglueCIMG;
            String lianjie;
            public ViewHolder(View view) {
                super(view);
                cardView = (CardView) view;
                gonglueimage = (ImageView) view.findViewById(R.id.gonglue_image);
                gonglueibiaoti = (TextView) view.findViewById(R.id.gonglue_biaoti);
                gonglueCIMG=(CircleImageView) view.findViewById(R.id.CIMAG);
                user=(TextView) view.findViewById(R.id.username);
                date=(TextView) view.findViewById(R.id.date);
                days=(TextView) view.findViewById(R.id.days);
                photonum=(TextView) view.findViewById(R.id.photonum);
                people=(TextView) view.findViewById(R.id.people);
                trip=(TextView) view.findViewById(R.id.trip);
                fee=(TextView) view.findViewById(R.id.fee);
                views=(TextView) view.findViewById(R.id.views);
                like=(TextView) view.findViewById(R.id.like);
                comment=(TextView) view.findViewById(R.id.comment);
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
            holder.gonglueimage.setImageBitmap(gonglue.getBM());
            holder.gonglueCIMG.setImageBitmap(gonglue.getBM2());
            holder.trip.setText("??????: "+gonglue.getTrip());
            holder.people.setText("??????: "+gonglue.getPeople());
            holder.photonum.setText("????????????: "+gonglue.getPhoto_nums());
            holder.user.setText(gonglue.getName());
            holder.date.setText("????????????: "+gonglue.getDate());
            holder.days.setText("????????????: "+gonglue.getDays());
            holder.fee.setText("??????: "+gonglue.getFee());
            holder.comment.setText("?????????: "+gonglue.getIcon_comment());
            holder.views.setText("?????????: "+gonglue.getIcon_view());
            holder.like.setText("?????????: "+gonglue.getIcon_love());
            holder.lianjie=gonglue.getTheurl();
            if(mItemClickListener!=null){
                holder.itemView.setOnClickListener((v) -> {
                    String url=holder.lianjie;
                    mItemClickListener.onItemClick(url);
                });
            }
        }

        @Override
        public int getItemCount() {
            return mgongluelist.size();
        }

    public interface OnItemClickListener {
        void onItemClick(String URL);
    }
    public void setOnItemClickListener(OnItemClickListener mItemClickListener){
        this.mItemClickListener =mItemClickListener;
    }
}