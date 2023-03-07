package com.qsmp.Cardviewpager_extra;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qsmp.ProductBean;
import com.qsmp.Product_Adapter;
import com.qsmp.R;
import com.qsmp.Scan_Beacon_Bean;

import java.util.List;


/**
 * adapter Created by farshid roohi on 12/12/17.
 */

public class Scan_Adapter extends BaseCardViewPagerItem<Scan_Beacon_Bean> {

    private Context mCtx;
    @Override
    public int getLayout() {
        return R.layout.item;
    }
    public Scan_Adapter(Context mCtx) {
        this.mCtx = mCtx;

    }

    @Override
    public void bindView(View view, Scan_Beacon_Bean item)
    {

        ImageView img_icon=view.findViewById(R.id.img_icon);
        TextView tvtitle=view.findViewById(R.id.tvtitle);


        tvtitle.setText(item.getTitle_Name());
        tvtitle.setText(item.getTitle_Name());

        Glide.with(mCtx)
                .asBitmap()
                .load(item.getMedia_Url())
                .into(img_icon);


        /*Animation animation = AnimationUtils.loadAnimation(mCtx.getApplicationContext(), R.anim.move);
        img_icon.startAnimation(animation);*/


        img_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url=item.getMedia_redirection_url();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                {
                    url = "https://" + url;
                }



                Log.d("Url",""+url);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mCtx.startActivity(browserIntent);

            }
        });




        //img_icon.setImageResource(item.getImg_res());
        //ViewGroup layoutRoot = view.findViewById(R.id.layout_root);
        //TextView txtTitle = view.findViewById(R.id.txt_title);

        //layoutRoot.setBackgroundColor(item.getBgColor());
        //txtTitle.setText(item.getTitle());
    }
}
