package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.models.WordPressPostModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterArticle extends RecyclerView.Adapter<AdapterArticle.MyHolder> {
    ArrayList<WordPressPostModel> wordPressPosts;
    Context context;

    public AdapterArticle(Context context, ArrayList<WordPressPostModel> wordPressPosts) {
        this.wordPressPosts = wordPressPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_item_news, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        WordPressPostModel post = wordPressPosts.get(position);

        String image = post.getJetpackFeaturedMediaUrl();
        String title = post.getTitle().getRendered();
        String link = post.getLink();
        String excerpt = post.getExcerpt().getRendered();
        String date = post.getDate();
        //String categories = post.getCategories().get(0).toString();
        //Log.e("cat", categories);

        holder.newstTitleTv.setText(title);
        holder.newsExcerptTv.setText(excerpt);
        holder.newsDateTv.setText(date);
        //holder.newsCategoryTv.setText(categories);

        try {
            Picasso.get().load(image)
                    .fit()
                    .into(holder.newsImageIv);
        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return wordPressPosts.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        //declare views from row_comments
        ImageView newsImageIv;
        TextView newstTitleTv, newsExcerptTv, newsDateTv, newsCategoryTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            newsImageIv = itemView.findViewById(R.id.newsImageIv);
            newstTitleTv = itemView.findViewById(R.id.newsTitleTv);
            newsExcerptTv = itemView.findViewById(R.id.newsExcerptTv);
            newsDateTv = itemView.findViewById(R.id.newsDateTv);
            newsCategoryTv = itemView.findViewById(R.id.newsCategoryTv);
        }
    }
}

