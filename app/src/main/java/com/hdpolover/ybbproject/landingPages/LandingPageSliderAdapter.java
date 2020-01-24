package com.hdpolover.ybbproject.landingPages;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.hdpolover.ybbproject.R;

public class LandingPageSliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public LandingPageSliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slide_images = {
        R.drawable.ybb_white_full,
            R.drawable.landing1,
            R.drawable.landing2,
            R.drawable.landing3
    };

    public String[] slide_headings = {
            "Welcome to  YBB",
            "Collecting",
            "Connecting",
            "Contributing"
    };

    public String[] slide_descs = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit"
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.landing_page_slider, container, false);

        ImageView lpImageIv = view.findViewById(R.id.landingPageIv);
        TextView lpHeadingTv = view.findViewById(R.id.lpHeadingTv);
        TextView lpDescTv = view.findViewById(R.id.lpDescTv);

        lpImageIv.setImageResource(slide_images[position]);
        lpHeadingTv.setText(slide_headings[position]);
        lpDescTv.setText(slide_descs[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }

}
