package com.qsmp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Product_Adapter extends RecyclerView.Adapter<Product_Adapter.ViewHolder> {

    private List<ProductBean> list;
    private Context mCtx;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private boolean isfreez;
    private OnItemClickListenerData clickListener;
    private OnTimeSelectedListener listener;
    private int currentSelection = 0;
    public Product_Adapter(List<ProductBean> list, Context mCtx) {
        this.list = list;
        this.mCtx = mCtx;

    }

    public void setClickListener(OnItemClickListenerData itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_deal_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull final ViewHolder holder, int position) {
        ProductBean siteBean = list.get(position);

        holder.tvtitle.setText(siteBean.getName());
       // holder.tvtitle.setText(siteBean.getLanguagename());
        Glide.with(mCtx)
                .asBitmap()
                .load(siteBean.getImageurl())
                .into(holder.img_icon);
      //  holder.itemView.setSelected(currentSelection == holder.getAdapterPosition());
       // holder.tvEventTime1.setSelected(currentSelection == holder.getAdapterPosition());

        holder.tvtitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click_data",siteBean.getId());
            }
        });

        holder.itemView.setOnClickListener(view -> {
            //currentSelection = currentSelection == h.getAdapterPosition() ? -1 : h.getAdapterPosition()
            if (currentSelection != holder.getAdapterPosition()) {
                currentSelection = holder.getAdapterPosition();
                if (listener != null) {
                    listener.onTimeSelected(holder.getLayoutPosition(), siteBean);
                }
                notifyDataSetChanged();
            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvtitle;
        ImageView img_icon;

        FrameLayout itemview1;
        public ViewHolder(View itemView) {
            super(itemView);

            tvtitle = itemView.findViewById(R.id.tvtitle);
            img_icon = itemView.findViewById(R.id.img_icon);

           itemview1 = itemView.findViewById(R.id.itemview1);

           itemview1.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null)
                clickListener.onClick(view, list.get(getAdapterPosition()), getAdapterPosition());
        }
    }

    public void filterList(List<ProductBean> filterdNames) {
        list = filterdNames;
        notifyDataSetChanged();
    }

    public interface OnItemClickListenerData {
        public void onClick(View view, ProductBean groupclass, int postion);
    }

    public void setOnTimeSelectedListener(OnTimeSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnTimeSelectedListener {

        void onTimeSelected(int position, ProductBean time);

    }
}