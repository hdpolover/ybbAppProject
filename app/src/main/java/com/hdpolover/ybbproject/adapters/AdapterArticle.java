package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hdpolover.ybbproject.R;
import com.hdpolover.ybbproject.WebViewNewsActivity;
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
        final String link = post.getLink();
        String excerpt = stripHtml(post.getExcerpt().getRendered());
        String date = post.getDate();

        holder.newstTitleTv.setText(title);
        holder.newsExcerptTv.setText(excerpt);
        holder.newsDateTv.setText(setPrettyDate(date));

        try {
            Picasso.get().load(image)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_ybb_news)
                    .into(holder.newsImageIv);
        } catch (Exception e) {
            //holder.newsImageIv.setVisibility(View.GONE);
            Picasso.get().load(R.drawable.placeholder_ybb_news)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_ybb_news)
                    .into(holder.newsImageIv);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewNewsActivity.class);
                intent.putExtra("link", link);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    public String stripHtml(String html) {
        return Html.fromHtml(html).toString();
    }

    private String setPrettyDate(String postDate) {
        String year = postDate.substring(0, 4);
        String month = postDate.substring(5, 7);
        String date = postDate.substring(8, 10);
        String time = postDate.substring(11, 16);

        switch (month) {
            case "01":
                month = "January";
                break;
            case "02":
                month = "February";
                break;
            case "03":
                month = "March";
                break;
            case "04":
                month = "April";
                break;
            case "05":
                month = "May";
                break;
            case "06":
                month = "June";
                break;
            case "07":
                month = "July";
                break;
            case "08":
                month = "August";
                break;
            case "09":
                month = "September";
                break;
            case "10":
                month = "October";
                break;
            case "11":
                month = "November";
                break;
            case "12":
                month = "Desember";
                break;
            default:
                break;
        }

        return month + " " + date + ", "+ year;
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
            //newsCategoryTv = itemView.findViewById(R.id.newsCategoryTv);
        }
    }
}

