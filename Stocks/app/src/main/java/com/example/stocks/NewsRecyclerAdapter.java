package com.example.stocks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder> {

    Dialog company_news_dialog;
    private ArrayList<JSONObject> company_news_data = new ArrayList<JSONObject>();
    private Context mContext;
    private String news_url, facebook_url, twitter_url;

    public NewsRecyclerAdapter(Context context, ArrayList<JSONObject> company_news){
        mContext = context;
        company_news_data = company_news;
        Log.d("company_news_2", String.valueOf(company_news_data));
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0)return 1;
        else return 2;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if(viewType == 1){
            View inflate = layoutInflater.inflate(R.layout.company_news_recyclerview_1, null);
            ViewHolder viewHolder = new ViewHolder(inflate);
            return viewHolder;
        }
        
        else
        {
            View inflate = layoutInflater.inflate(R.layout.company_news_recyclerview, null);
            ViewHolder viewHolder = new ViewHolder(inflate);
            return viewHolder;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JSONObject current_news = company_news_data.get(position);
        try {
            holder.comp_news_source.setText(current_news.getString("source"));

            Long datetime = current_news.getLong("datetime");
            Long diff_in_hours = get_time_difference(datetime);

            holder.news_time.setText(diff_in_hours + " hours ago");
            holder.comp_news_headline.setText(current_news.getString("headline"));

            String image_url = current_news.getString("image");
            Glide.with(holder.itemView.getContext())
                    .load(image_url)
                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(60)))
                    .into(holder.comp_news_image);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog builder = new AlertDialog.Builder(mContext).create();

                // TODO: CardView Not rounded properly
                View dialogView = LayoutInflater.from(mContext).inflate(R.layout.company_news_dialog, null);
                TextView news_source = (TextView) dialogView.findViewById(R.id.news_source);
                TextView news_datetime = (TextView) dialogView.findViewById(R.id.news_datetime);
                TextView news_headline = (TextView) dialogView.findViewById(R.id.news_headline);
                TextView news_summary = (TextView) dialogView.findViewById(R.id.news_summary);
                ImageView google = (ImageView) dialogView.findViewById(R.id.google);
                ImageView twitter = (ImageView) dialogView.findViewById(R.id.twitter);
                ImageView facebook = (ImageView)dialogView.findViewById(R.id.facebook);

                try {
                    news_source.setText(current_news.getString("source"));
                    news_headline.setText(current_news.getString("headline"));
                    news_summary.setText(current_news.getString("summary"));
                    Long datetime = current_news.getLong("datetime");
                    String date = get_dialog_date(datetime);
                    news_datetime.setText(date);

                    //Set OnClickListeners for Image Views
                    google.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                news_url = current_news.getString("url");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent google_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news_url));
                            google_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mContext.startActivity(google_intent);
                        }
                    });

                    twitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                twitter_url = "https://twitter.com/intent/tweet?text=" + Uri.encode(current_news.getString("headline")) + "&url=" + Uri.encode(current_news.getString("url"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Intent twitter_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitter_url));
                            twitter_intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            mContext.startActivity(twitter_intent);
                        }
                    });

                    facebook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                facebook_url = "https://www.facebook.com/sharer/sharer.php?u=" + current_news.getString("url") + "&amp;src=sdkpreparse";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Intent facebook_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(facebook_url));
                            mContext.startActivity(facebook_intent);
                        }
                    });
                    
                    // Showing the news dialog
                    builder.setView(dialogView);
                    builder.setCancelable(false);
                    builder.show();
                    builder.setCanceledOnTouchOutside(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    
    @Override
    public int getItemCount() {
        return company_news_data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView comp_news_image;
        TextView news_time, comp_news_headline, comp_news_source;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            comp_news_image = (ImageView) itemView.findViewById(R.id.comp_news_image);
            news_time = (TextView) itemView.findViewById(R.id.news_time);
            comp_news_headline = (TextView) itemView.findViewById(R.id.comp_news_headline);
            comp_news_source = (TextView) itemView.findViewById(R.id.comp_news_source);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public Long get_time_difference(long datetime){
        long current_ts;

        current_ts = Instant.now().getEpochSecond();
        Log.d("current_time", String.valueOf(current_ts));

        long difference = current_ts - datetime;
        Long difference_in_hours = difference / (60 * 60);

//        String difference_in_hours = (String) DateUtils.getRelativeTimeSpanString(datetime, current_ts, 2);

        return difference_in_hours;
    }

    public String get_dialog_date(long datetime){
        Date date = new java.util.Date(datetime * 1000L);
        SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMMM dd, yyyy");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("America/Los_Angeles"));
        String formattedDate = sdf.format(date);

        return  formattedDate;
    }
}


