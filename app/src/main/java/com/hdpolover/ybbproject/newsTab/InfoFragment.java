package com.hdpolover.ybbproject.newsTab;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.adapters.AdapterArticle;
import com.hdpolover.ybbproject.api.InitRetrofit;
import com.hdpolover.ybbproject.models.WordPressPostModel;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class InfoFragment extends Fragment {

    View view;
    InitRetrofit initRetrofitPost;
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    NestedScrollView nestedScrollView;

    public InfoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_info_fragment, container, false);

        initRetrofitPost = new InitRetrofit();
        recyclerView = view.findViewById(R.id.recyclerViewInfo);

        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayoutNews);
        nestedScrollView = view.findViewById(R.id.nestedScrollViewNews);

        initRetrofitPost.getInfoPost();
        initRetrofitPost.setOnRetrofitSuccess(new InitRetrofit.OnRetrofitSuccess() {
            @Override
            public void onSuccessGetData(ArrayList arrayList) {
                if (!arrayList.isEmpty()) {
                    Log.e("Size", String.valueOf(arrayList.size()));
                    showPost(arrayList);
                } else {
                    Log.e("Size", String.valueOf(arrayList.size()));
                }
            }
        });

        return view;
    }

    private void showPost(ArrayList<WordPressPostModel> wordPressPostModels) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        AdapterArticle adapterArticle = new AdapterArticle(getApplicationContext(), wordPressPostModels);
        recyclerView.setAdapter(adapterArticle);
        adapterArticle.notifyDataSetChanged();
        shimmerFrameLayout.stopShimmer();
        shimmerFrameLayout.setVisibility(View.GONE);
    }

    public void scrollUp(){
        nestedScrollView.smoothScrollTo(0,0);
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }
}
