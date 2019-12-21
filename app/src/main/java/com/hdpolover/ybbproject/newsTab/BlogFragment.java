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

public class BlogFragment extends Fragment {

    View view;
    InitRetrofit initRetrofitBlog;
    RecyclerView recyclerView;
    ShimmerFrameLayout shimmerFrameLayout;
    NestedScrollView nestedScrollView;

    public BlogFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.news_blog_fragment, container, false);

        initRetrofitBlog = new InitRetrofit();
        recyclerView = view.findViewById(R.id.recyclerViewBlog);

        shimmerFrameLayout = view.findViewById(R.id.shimmerFrameLayoutBlog);
        nestedScrollView = view.findViewById(R.id.nestedScrollViewBlog);

        initRetrofitBlog.getBlogPost();
        initRetrofitBlog.setOnRetrofitSuccess(new InitRetrofit.OnRetrofitSuccess() {
            @Override
            public void onSuccessGetData(ArrayList arrayList) {
                if (!arrayList.isEmpty()) {
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

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmer();
    }
}
