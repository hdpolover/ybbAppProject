package com.hdpolover.ybbproject.adapters;

import android.content.Context;
import android.text.Html;
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
        String excerpt = stripHtml(post.getExcerpt().getRendered());
        String date = post.getDate();

        holder.newstTitleTv.setText(title);
        holder.newsExcerptTv.setText(excerpt);
        holder.newsDateTv.setText(setPrettyDate(date));

        try {
            Picasso.get().load(image)
                    .fit()
                    .into(holder.newsImageIv);
        } catch (Exception e) {
            holder.newsImageIv.setVisibility(View.GONE);
        }
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
                month = "Jan";
                break;
            case "02":
                month = "Feb";
                break;
            case "03":
                month = "Mar";
                break;
            case "04":
                month = "Apr";
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
                month = "Aug";
                break;
            case "09":
                month = "Sep";
                break;
            case "10":
                month = "Oct";
                break;
            case "11":
                month = "Nov";
                break;
            case "12":
                month = "Des";
                break;
            default:
                break;
        }

        return date + " " + month + " "+ year;
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

