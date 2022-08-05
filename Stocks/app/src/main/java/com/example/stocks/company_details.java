package com.example.stocks;
import androidx.appcompat.app.ActionBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import io.github.muddz.styleabletoast.StyleableToast;

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.*;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class company_details extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    PageAdapter pageAdapter;
    TabItem tab_hourly_charts;
    TabItem tab_historical_charts;
    ImageView favourites_icon;
    TextView toolbar_ticker;
    int[] tabIcons = {
            R.drawable.ic_hourly_price_icon,
            R.drawable.ic_historical_price_icon
    };
    public TextView ticker_name, stock_name, stock_price, stock_price_change, price_variation, hist;
    public TextView open_price, low_price, high_price, prev_close;
    public TextView ipo_start_date, industry, webpage, card_header;
    public TextView company_name, reddit_positive_mentions, reddit_total_mentions, reddit_negative_mentions;
    public TextView twitter_positive_mentions, twitter_negative_mentions, twitter_total_mentions;
    public ImageView ticker_icon, back_btn, stock_price_change_icon;
    EditText shares_input;
    private RequestQueue mQueue;
    long temp_reddit_tm = 0;
    long temp_reddit_pm = 0;
    long temp_reddit_nm = 0;
    long temp_twitter_tm = 0;
    long temp_twitter_pm = 0;
    long temp_twitter_nm = 0;
    String price, input_ticker;
    ArrayList<JSONObject> company_news = new ArrayList<JSONObject>();
    public RecyclerView company_news_rv;
    Button trade_btn, buy_btn, sell_btn;
    Dialog trade_dialog, congrats_dialog;
    TextView number_of_shares, curr_comp_price, total, money_left;
    Boolean first_time = true;
    int articles = 0;
    public String comp;
    public String company_price, company_last_price;
    public TextView shares_owned, average_cost, total_cost, change, market_value;
    HashMap<String, String> user_watchlist = new HashMap<String, String>();
    HashMap<String, String> user_portfolio = new HashMap<String, String>();
    Boolean called_first_time = false;
    Boolean data_loaded = false;
    Boolean ticker_in_watchlist = false;
    Boolean invalid_data;
    public String input_shares = "";
    public ArrayList<String> company_peers = new ArrayList<String>();
    HashMap<String, ArrayList<String>> stocks_price_details = new HashMap<String, ArrayList<String>>();

    public static final String shared_prefs = "sharedPrefs";
    public static final String watchlist = "user_watchlist";
    public static final String portfolio = "user_portfolio";
    public static final String price_details = "stock_prices_info";
    public static final String user_wallet = "user_wallet";
    public static final String stocks_list = "stock_list";
    public static final String change_in_price = "prices_change";
    public String latest_shares_owned;
    public LinkedHashSet<String> list_of_stocks = new LinkedHashSet<String>();
    public HashMap<String, Double> chart_prices = new HashMap<String, Double>();
    RelativeLayout progress_bar, company_details_layout;
    public long chart_latest_time;
    SharedPreferences stack;
    ActionBar actionBar;
    Toolbar appbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);

        Intent intent = getIntent();
        input_ticker = intent.getStringExtra("input_ticker");
        Log.d("Updated", input_ticker);

        ticker_name = (TextView) findViewById(R.id.ticker_name);

        progress_bar = (RelativeLayout) findViewById(R.id.progress_bar);
        company_details_layout = (RelativeLayout) findViewById(R.id.company_details_layout);

        company_details_layout.setVisibility(View.GONE);
        progress_bar.setVisibility(View.VISIBLE);


        SharedPreferences sharedPreferences = getSharedPreferences(shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String list = sharedPreferences.getString(stocks_list, "");

        if(!list.isEmpty() || !list.equals("")){
            Type type = new TypeToken<LinkedHashSet<String>>() {}.getType();
            list_of_stocks = gson.fromJson(list, type);
        }

        list_of_stocks.add(input_ticker);
        String json_str = gson.toJson(list_of_stocks);
        editor.putString(stocks_list, json_str);
        editor.apply();

        // TODO: Maintain the state of the activity when user clicks back button (see video DELL to AAPL)
        back_btn = (ImageView) findViewById(R.id.back_btn);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!list_of_stocks.isEmpty() && list_of_stocks.size() > 1){
                    list_of_stocks.remove(input_ticker);
                    Intent inner_intent = new Intent(company_details.this, company_details.class);
                    inner_intent.putExtra("input_ticker", list_of_stocks.stream().skip(list_of_stocks.size()-1).findFirst().get());
                    startActivity(inner_intent);
                }
                else {
                    list_of_stocks.clear();
//                    company_details.this.finish();
                    Intent intent = new Intent(company_details.this, HomeActivity.class);
                    startActivity(intent);
                }
                Log.d("final_stocks_list", list_of_stocks.toString());
                String temp = gson.toJson(list_of_stocks);
                editor.putString(stocks_list, temp);
                editor.apply();
                }

            });


        // Using Volley and Glide to load data
        mQueue = Volley.newRequestQueue(this);
        load_company_details_data();
        load_company_latest_prices();
        load_company_peers();
        load_insights_data();
        load_company_news();
        update_user_favourites();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                load_portfolio_details();
            }
        }, 1000);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tab_hourly_charts = (TabItem) findViewById(R.id.tab_hourly_charts);
        tab_historical_charts = (TabItem) findViewById(R.id.tab_historical_charts);

        viewPager = (ViewPager) findViewById(R.id.display_charts);

        pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        pageAdapter.addFrag(new DailyChartFragment());
        pageAdapter.addFrag(new HistoricalChartFragment());

        viewPager.setAdapter(pageAdapter);

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);

        // Reference: https://stackoverflow.com/questions/34562117/how-do-i-change-the-color-of-icon-of-the-selected-tab-of-tablayout
        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#0066ff"), PorterDuff.Mode.SRC_IN);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#0066ff"), PorterDuff.Mode.SRC_IN);

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_IN);
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        trade_dialog = new Dialog(company_details.this);
        trade_dialog.setCanceledOnTouchOutside(true);

        trade_btn = (Button) findViewById(R.id.trade_btn);
        trade_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_trade_dialog();
            }
        });

        WebView reco_chart = (WebView) findViewById(R.id.reco_chart);
        reco_chart.getSettings().setJavaScriptEnabled(true);
        reco_chart.loadUrl("file:///android_asset/Recommendation_Trends.html");

        reco_chart.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                reco_chart.loadUrl("javascript:get_recommendation_trends('" + ticker_name.getText().toString() + "')");
            }
        });

        WebView eps_surprises_chart = (WebView) findViewById(R.id.eps_surprises_chart);
        eps_surprises_chart.getSettings().setJavaScriptEnabled(true);
        eps_surprises_chart.loadUrl("file:///android_asset/Company_Earnings.html");

        eps_surprises_chart.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                eps_surprises_chart.loadUrl("javascript:get_company_earnings_data('" + ticker_name.getText().toString() + "')");
            }
        });

        data_loaded = true;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                data_loaded = false;
                progress_bar.setVisibility(View.GONE);
                company_details_layout.setVisibility(View.VISIBLE);
            }
        }, 3000);

//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                load_content();
//            }
//        }, 15000);

    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case android.R.id.home: {
//
//                stack = getSharedPreferences("MySharedPref", Context.MODE_MULTI_PROCESS);
//                String s2 = stack.getString("stacked", "");
//                int a = Integer.parseInt(s2);
//                if (a > 2) {
//                    a=a-1;
//                    SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_MULTI_PROCESS);
//                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
//                    myEdit.putString("stacked", String.valueOf(a));
//                    myEdit.commit();
//                    finish();
//                }
//                else{
//                    Intent i=new Intent(company_details.this,
//                            HomeActivity.class);
//                    startActivity(i);
//                }
//                return true;
//            }
////            case R.id.add_fav: {
////                MenuItem settingsItem = menu.findItem(R.id.add_fav);
////
////                if (added_favourites==false) {
////                    addintofavourites();
////                    added_favourites=true;
////                    settingsItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_24));
////                    Toast.makeText(SecondScreen.this, ticker+ " is added to favorites", Toast.LENGTH_SHORT).show();
////                    Toast.makeText(SecondScreen.this, Html.fromHtml("<font color='black' >" + ticker + " is added to favorites" + "</b></font>"), Toast.LENGTH_SHORT).show();
////                }
////                else{
////                    deleteintofavouries();
////                    added_favourites=false;
////                    settingsItem.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_baseline_star_border_24));
////                    Toast.makeText(SecondScreen.this, ticker+ " is removed from favorites", Toast.LENGTH_SHORT).show();
////
////                }
////            }
//                default:
//                return (super.onOptionsItemSelected(menuItem));
//
//        }
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.back_button:
//                finish();
//                return true;
//
//            case R.id.company_name:
//                return true;
//
//            case R.id.favourites_icon:
//                return true;
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void load_company_details_data(){
        stock_name = (TextView) findViewById(R.id.stock_name);
        ticker_icon = (ImageView) findViewById(R.id.ticker_icon);
        ipo_start_date = (TextView) findViewById(R.id.ipo_start_date);
        industry = (TextView) findViewById(R.id.industry);
        webpage = (TextView) findViewById(R.id.webpage);
        company_name = (TextView) findViewById(R.id.company_name);
        toolbar_ticker = (TextView) findViewById(R.id.toolbar_ticker);
//        price_variation = (TextView) findViewById(R.id.price_variation);


        String comp_info_url = "https://assignment-8-346123.wl.r.appspot.com/company_details/" + input_ticker;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, comp_info_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("Comp Details Resp Msg", "Response is: " + response);
                            ticker_name.setText(response.getString("ticker"));
                            stock_name.setText(response.getString("name"));
                            company_name.setText(response.getString("name"));
                            toolbar_ticker.setText(response.getString("ticker"));
                            //price_variation.setText(response.getString("ticker") + " Hourly Price Variation");
                            //hist.setText(response.getString("ticker") + " Historical");

                            // Using Glide to load images
                            Glide.with(getApplicationContext())
                                    .load(response.getString("logo")).placeholder(R.drawable.ic_default_image)
                                    .into(ticker_icon);

                            // About info:
                            String temp_date = get_formatted_date(response.getString("ipo"));
                            ipo_start_date.setText(temp_date);
                            industry.setText(response.getString("finnhubIndustry"));
                            webpage.setText(response.getString("weburl"));

                            webpage.setMovementMethod(LinkMovementMethod.getInstance());

                            webpage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                    browserIntent.setData(Uri.parse(String.valueOf(webpage)));
                                    startActivity(browserIntent);
                                }
                            });

                        } catch (JSONException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Comp Details Error Msg", "Error is: " + error.getMessage());
            }
        });

        mQueue.add(request);
    }

    private void load_company_latest_prices(){
        stock_price = (TextView) findViewById(R.id.stock_price);
        stock_price_change = (TextView) findViewById(R.id.stock_price_change);
        open_price = (TextView) findViewById(R.id.open_price);
        low_price = (TextView) findViewById(R.id.low_price);
        high_price = (TextView) findViewById(R.id.high_price);
        prev_close = (TextView) findViewById(R.id.prev_close);
        stock_price_change_icon = (ImageView) findViewById(R.id.stock_price_change_icon);

        String latest_price_url = "https://assignment-8-346123.wl.r.appspot.com/latest_price/" + input_ticker;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, latest_price_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            SharedPreferences sharedPreferences = getSharedPreferences(shared_prefs, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            Gson gson = new Gson();

                            Log.d("Latest Price Resp Msg", "Response is: " + response);
                            company_price = response.getString("c");
                            company_last_price = response.getString("pc");
                            chart_latest_time = response.getLong("t");

                            Log.d("chart_time", String.valueOf(chart_latest_time));

                            Double price_difference = Double.parseDouble(company_price) - Double.parseDouble(company_last_price);

                            String price_info = sharedPreferences.getString(change_in_price, "");

                            if(!price_info.isEmpty() || !price_info.equals("")){
                                Type type = new TypeToken<HashMap<String, Double>>() {}.getType();
                                chart_prices = gson.fromJson(price_info, type);
                            }

                            if(!chart_prices.isEmpty() && chart_prices.containsKey(input_ticker)){
                                chart_prices.replace(input_ticker, price_difference);
                            }
                            else{
                                chart_prices.put(input_ticker, price_difference);
                            }

                            String updated_price_list = gson.toJson(chart_prices);
                            editor.putString(change_in_price, updated_price_list);
                            editor.apply();

                            stock_price.setText("$" + response.getString("c"));
                            price = response.getString("c");
                            Log.d("price", price);

                            String temp_ch = get_formatted_prices(response.getString("d"));
                            String temp_cp = get_formatted_prices(response.getString("dp"));

                            String change = "$" + Double.parseDouble(temp_ch);
                            String percent_change = " (" + Double.parseDouble(temp_cp) + "%)";
                            stock_price_change.setText(change + percent_change);

                            if (response.getDouble("d") > 0){
                                stock_price_change_icon.setImageResource(R.drawable.ic_trending_up);
                                stock_price_change.setTextColor(Color.parseColor("#009933"));
                            }

                            else if (response.getDouble("d") < 0){
                                stock_price_change_icon.setImageResource(R.drawable.ic_trending_down);
                                stock_price_change.setTextColor(Color.parseColor("#FF0000"));
                            }

                            else{
                                stock_price_change.setTextColor(Color.parseColor("#000000"));
                            }

                            // Stats Data:
                            open_price.setText("$" + get_formatted_prices(response.getString("o")));
                            high_price.setText("$" + get_formatted_prices(response.getString("h")));
                            low_price.setText("$" + get_formatted_prices(response.getString("l")));
                            prev_close.setText("$" + get_formatted_prices(response.getString("pc")));

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Latest Price Error Msg", "Error is: " + error.getMessage());
            }
        });

        mQueue.add(request);
    }

    private void load_portfolio_details(){
        Log.d("called", "yeah");
        shares_owned = findViewById(R.id.shares_owned);
        market_value = findViewById(R.id.market_value);
        change = findViewById(R.id.change);
        total_cost = findViewById(R.id.total_cost);
        average_cost = findViewById(R.id.average_cost);

        SharedPreferences sharedPreferences = getSharedPreferences(shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String current_prices_details = sharedPreferences.getString(price_details, "");

        if(!current_prices_details.isEmpty() && !current_prices_details.equals("")){
            Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
            stocks_price_details = gson.fromJson(current_prices_details, type);
            Log.d("called_1", stocks_price_details.toString());

            if(!stocks_price_details.isEmpty() && stocks_price_details.containsKey(input_ticker)) {
                if (Integer.parseInt(stocks_price_details.get(input_ticker).get(0)) > 0) {
                    shares_owned.setText(stocks_price_details.get(input_ticker).get(0));
                    average_cost.setText("$" + stocks_price_details.get(input_ticker).get(1));
                    total_cost.setText("$" + stocks_price_details.get(input_ticker).get(2));
                    Log.d("change", String.valueOf(Double.parseDouble(stocks_price_details.get(input_ticker).get(3))));

                    try {
                        Log.d("pppp", company_price);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    Double market_price = Double.parseDouble(company_price) * Integer.parseInt(stocks_price_details.get(input_ticker).get(0));

                    Double price_change = Double.parseDouble(company_price) - Double.parseDouble(stocks_price_details.get(input_ticker).get(1));

                    change.setText("$" + get_formatted_prices(String.valueOf(price_change)));

                    market_value.setText("$" + get_formatted_prices(String.valueOf(market_price)));

                    Double diff = Double.parseDouble(get_formatted_prices(String.valueOf(market_price))) - Double.parseDouble(stocks_price_details.get(input_ticker).get(2));

                    if (diff > 0.0) {
                        change.setTextColor(Color.parseColor("#009933"));
                        market_value.setTextColor(Color.parseColor("#009933"));
                    }
                    else if (diff < 0.0) {
                        change.setTextColor(Color.parseColor("#FF0000"));
                        market_value.setTextColor(Color.parseColor("#FF0000"));
                    }
                    else{
                        change.setTextColor(Color.parseColor("#000000"));
                        market_value.setTextColor(Color.parseColor("#000000"));
                    }
                }
            }

            else{
                shares_owned.setText("0");
                average_cost.setText("$0.00");
                total_cost.setText("$0.00");
                change.setText("$0.00");
                market_value.setText("$0.00");

                change.setTextColor(Color.parseColor("#000000"));
                market_value.setTextColor(Color.parseColor("#000000"));
            }
        }

    }

    private void load_company_peers(){
        String comp_peers_url = "https://assignment-8-346123.wl.r.appspot.com/company_peers/" + input_ticker;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, comp_peers_url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Company Peers Resp Msg", "Response is: " + response);

                        try {
                            for(int i=0; i<response.length(); i++) {
                                company_peers.add(response.getString(i));
                            }
                            initPeersRecyclerView();

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Company Peers Error Msg", "Error is: " + error.getMessage());
            }
        });

        mQueue.add(request);
    }

    private void load_insights_data(){
        reddit_negative_mentions = (TextView) findViewById(R.id.reddit_negative_mentions);
        reddit_positive_mentions = (TextView) findViewById(R.id.reddit_positive_mentions);
        reddit_total_mentions = (TextView) findViewById(R.id.reddit_total_mentions);
        twitter_total_mentions = (TextView) findViewById(R.id.twitter_total_mentions);
        twitter_negative_mentions = (TextView) findViewById(R.id.twitter_negative_mentions);
        twitter_positive_mentions = (TextView) findViewById(R.id.twitter_positive_mentions);

        String comp_insights_url = "https://assignment-8-346123.wl.r.appspot.com/sentiments/" + input_ticker;
        Log.d("Reached", "Hiiiiii!!!!");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, comp_insights_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Comp Insights Resp Msg", "Response is: " + response);

                        try {
                            JSONArray reddit_array = response.getJSONArray("reddit");
                            JSONArray twitter_array = response.getJSONArray("twitter");
                            for (int i = 0; i < reddit_array.length(); i++){
                                JSONObject reddit_sentiment = reddit_array.getJSONObject(i);
                                temp_reddit_pm += reddit_sentiment.getInt("positiveMention");
                                temp_reddit_nm += reddit_sentiment.getInt("negativeMention");
                            }

                            for (int j = 0; j < twitter_array.length(); j++){
                                JSONObject twitter_sentiment = twitter_array.getJSONObject(j);
                                temp_twitter_pm += twitter_sentiment.getInt("positiveMention");
                                temp_twitter_nm += twitter_sentiment.getInt("negativeMention");
                            }

                            temp_reddit_tm = temp_reddit_nm + temp_reddit_pm;
                            temp_twitter_tm = temp_twitter_nm + temp_twitter_pm;

                            reddit_total_mentions.setText(String.valueOf(temp_reddit_tm));
                            reddit_negative_mentions.setText(String.valueOf(temp_reddit_nm));
                            reddit_positive_mentions.setText(String.valueOf(temp_reddit_pm));
                            twitter_total_mentions.setText(String.valueOf(temp_twitter_tm));
                            twitter_positive_mentions.setText(String.valueOf(temp_twitter_pm));
                            twitter_negative_mentions.setText(String.valueOf(temp_twitter_nm));

                            temp_twitter_pm = 0;
                            temp_twitter_nm = 0;
                            temp_twitter_tm = 0;
                            temp_reddit_pm = 0;
                            temp_reddit_nm = 0;
                            temp_reddit_tm = 0;

                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Comp Insights Error Msg", "Error is: " + error.getMessage());
            }
        });
        mQueue.add(request);
    }

    private void load_company_news(){
        String news_url = "https://assignment-8-346123.wl.r.appspot.com/complete_news/" + input_ticker;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, news_url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Company News Resp Msg", "Response is: " + response);
                        try {
                            int i = 0;
                            while (i < response.length() && articles < 20 ){
                                JSONObject news_response = response.getJSONObject(i);
                                String news_url = news_response.getString("url");
                                String news_source = news_response.getString("source");
                                Long news_datetime = news_response.getLong("datetime");
                                String news_image = news_response.getString("image");
                                String news_headline = news_response.getString("headline");
                                String news_summary = news_response.getString("summary");

                                if(news_datetime!= null && news_url != null && !news_url.isEmpty()
                                        && news_headline != null && !news_headline.isEmpty()
                                        &&news_image != null && !news_image.isEmpty()
                                        && news_summary != null && !news_summary.isEmpty()
                                        && news_source != null && !news_source.isEmpty()){
                                    articles++;
                                    company_news.add(news_response);
                                }
                                 i++;
                            }
                            Log.d("news_list", company_news.toString());


                            company_news_rv = (RecyclerView) findViewById(R.id.company_news_rv);
                            company_news_rv.setNestedScrollingEnabled(false);
                            NewsRecyclerAdapter newsRecyclerAdapter = new NewsRecyclerAdapter(company_details.this, company_news);
                            Log.d("company_news_1", company_news.toString());
                            company_news_rv.setAdapter(newsRecyclerAdapter);
                            company_news_rv.setLayoutManager(new LinearLayoutManager(company_details.this));

                        }
                        catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Company News Error Msg", "Error is: " + error.getMessage());
            }
        });
        mQueue.add(request);
    }

    private void open_trade_dialog() {
        trade_dialog.setContentView(R.layout.trade_dialog);
        trade_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        SharedPreferences sharedPreferences = getSharedPreferences(shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        number_of_shares = (TextView) trade_dialog.findViewById(R.id.number_of_shares);
        total = (TextView) trade_dialog.findViewById(R.id.total);
        curr_comp_price = (TextView) trade_dialog.findViewById(R.id.curr_comp_price);
        money_left = (TextView) trade_dialog.findViewById(R.id.money_left);
        card_header = (TextView) trade_dialog.findViewById(R.id.card_header);
        buy_btn = (Button) trade_dialog.findViewById(R.id.buy_btn);
        sell_btn = (Button) trade_dialog.findViewById(R.id.sell_btn);

        String p = stock_price.getText().toString();
        curr_comp_price.setText(p + "/share = ");

        String user_wallet_money = get_formatted_prices(sharedPreferences.getString(user_wallet, ""));

        money_left.setText("$" + user_wallet_money + " to buy " + ticker_name.getText().toString());
        card_header.setText("Trade " + company_name.getText().toString() + " shares");
        shares_input = (EditText) trade_dialog.findViewById(R.id.shares_input);

        shares_input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(String.valueOf(charSequence).matches("-?\\d+") && !String.valueOf(charSequence).isEmpty() &&
                        !shares_input.getText().toString().equals("-")) {

                    invalid_data = false;
                    buy_btn.setEnabled(true);
                    sell_btn.setEnabled(true);
                    Log.d("charseq", String.valueOf(charSequence));
                    number_of_shares.setText(String.valueOf(charSequence) + ".0*");
                    Log.d("shares", "Share price:" + Double.parseDouble(price));
                    double result = Double.parseDouble(charSequence.toString()) * Double.parseDouble(price);
                    result = Math.round(result * 100.0);
                    result = result / 100.0;
                    Log.d("result", String.valueOf(result));

                    total.setText(String.valueOf(result));
                    Log.d("total", total.getText().toString());
                }

                 else if(String.valueOf(charSequence).isEmpty()){
                     invalid_data = true;
                     buy_btn.setEnabled(true);
                     sell_btn.setEnabled(true);
                     number_of_shares.setText("0.0*");
                     total.setText("0.00");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!editable.toString().isEmpty()){
                    input_shares = editable.toString();
                }
                else{
                    input_shares = "";
                }
            }
        });


        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_buy_dialog(input_shares);
            }
        });

        sell_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_sell_dialog();
            }
        });

        trade_dialog.show();
    }

    private void open_buy_dialog(String s){
        double money_left;
        double current_total;

        SharedPreferences sharedPreferences = getSharedPreferences(shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        Log.d("input111", input_shares);

        if(input_shares.equals("") || input_shares.isEmpty() || input_shares.equals(0)){
            StyleableToast.makeText(getApplicationContext(), "Please enter a valid amount", R.style.exampleToast).show();
            return;
        }

        else if(!input_shares.matches("-?\\d+")){
            StyleableToast.makeText(company_details.this, "Please enter a valid amount", R.style.exampleToast).show();
            return;
        }

        else if (Integer.parseInt(input_shares) <= 0) {
            StyleableToast.makeText(getApplicationContext(), "Cannot buy non-positive shares", R.style.exampleToast).show();
            return;

        }

        money_left = Double.parseDouble(sharedPreferences.getString(user_wallet, "0"));
        current_total = Double.parseDouble(company_price) * Integer.parseInt(input_shares);
        Log.d("money_left", String.valueOf(money_left));
        Log.d("current_total", String.valueOf(current_total));

        if(!input_shares.equals("") && input_shares.matches("-?\\d+") && current_total > money_left){
            StyleableToast.makeText(company_details.this, "Not enough money to buy", R.style.exampleToast).show();
        }

        else if(!input_shares.equals("") && input_shares.matches("-?\\d+") && current_total < money_left){
            String current_stock_prices = sharedPreferences.getString(price_details, "");
            if(!current_stock_prices.equals("") || !current_stock_prices.isEmpty()){
                Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
                stocks_price_details = gson.fromJson(current_stock_prices, type);
            }

            if(!stocks_price_details.isEmpty() && stocks_price_details.containsKey(input_ticker))
            {
                ArrayList<String> new_data = new ArrayList<String>();
                String curr_shares = stocks_price_details.get(input_ticker).get(0);
                Double curr_avg_cost = Double.parseDouble(stocks_price_details.get(input_ticker).get(1));
                Double curr_total = Double.parseDouble(stocks_price_details.get(input_ticker).get(2));
                Double curr_change = Double.parseDouble(stocks_price_details.get(input_ticker).get(3));
                Double curr_market_value = Double.parseDouble(stocks_price_details.get(input_ticker).get(4));

                int new_shares = Integer.parseInt(curr_shares) + Integer.parseInt(input_shares);
                Double new_total = curr_total + (Integer.parseInt(input_shares) * Double.parseDouble(company_price));
                Double new_avg_cost = new_total / new_shares;
                Double new_market_value = Double.parseDouble(company_price) * new_shares;
                Double new_change = new_market_value - new_total;

                new_data.add(String.valueOf(new_shares));
                new_data.add(get_formatted_prices(String.valueOf(new_avg_cost)));
                new_data.add(get_formatted_prices(String.valueOf(new_total)));
                new_data.add(String.valueOf(new_change));
                new_data.add(get_formatted_prices(String.valueOf(new_market_value)));

                stocks_price_details.replace(input_ticker, new_data);

                // Updating the money in the wallet
                Double current_money_in_wallet = Double.parseDouble(sharedPreferences.getString(user_wallet, "0"));
                Double updated_money = current_money_in_wallet - (Integer.parseInt(input_shares) * Double.parseDouble(company_price));
                editor.putString(user_wallet, get_formatted_prices(String.valueOf(updated_money)));
                editor.apply();
                Log.d("money", sharedPreferences.getString(user_wallet, "0"));
            }

            else {
                ArrayList<String> first_time_data = new ArrayList<String>();
                String first_shares = input_shares;
                Double first_total = Double.parseDouble(company_price) * Integer.parseInt(input_shares);
                Double first_avg_cost = first_total / Integer.parseInt(input_shares);
                Double first_market_value = Double.parseDouble(company_price) * Integer.parseInt(first_shares);
                Double first_change = first_market_value - first_total;

                first_time_data.add(first_shares);
                first_time_data.add(get_formatted_prices(String.valueOf(first_avg_cost)));
                first_time_data.add(get_formatted_prices(String.valueOf(first_total)));
                first_time_data.add(String.valueOf(first_change));
                first_time_data.add(get_formatted_prices(String.valueOf(first_market_value)));

                stocks_price_details.put(input_ticker, first_time_data);

                // Updating the money in the wallet
                Double current_money_in_wallet = Double.parseDouble(sharedPreferences.getString(user_wallet, "0"));
                Double updated_money = current_money_in_wallet - Double.parseDouble(first_time_data.get(2));
                editor.putString(user_wallet, get_formatted_prices(String.valueOf(updated_money)));
                editor.apply();
                Log.d("money", sharedPreferences.getString(user_wallet, "0"));
            }
            Log.d("logic_buy", stocks_price_details.toString());

            String buy_pf_details = gson.toJson(stocks_price_details);
            editor.putString(price_details, buy_pf_details);
            editor.apply();

            String current_portfolio = sharedPreferences.getString(portfolio, "");
            if (!current_portfolio.equals("") && !current_portfolio.isEmpty()) {
                Type type = new TypeToken<HashMap<String, String>>() {}.getType();
                user_portfolio = gson.fromJson(current_portfolio, type);
            }

            if(!user_portfolio.isEmpty() && user_portfolio.containsKey(input_ticker)) {
                user_portfolio.replace(input_ticker, stocks_price_details.get(input_ticker).get(0));
//                Log.d("data_came", stocks_price_details.toString());
//                String current_shares = user_portfolio.get(input_ticker);
//                int updated_shares = Integer.parseInt(current_shares) + Integer.parseInt(input_shares);
//                user_portfolio.put(input_ticker, String.valueOf(updated_shares));
            }

            else{
                user_portfolio.put(input_ticker, stocks_price_details.get(input_ticker).get(0));
            }

            Log.d("user_portfolio_buy", user_portfolio.toString());

            String buy_json_string = gson.toJson(user_portfolio);
            editor.putString(portfolio, buy_json_string);
            editor.apply();

            trade_dialog.dismiss();

            // Congrats dialog after clicking the Buy Button
            congrats_dialog = new Dialog(company_details.this);
            congrats_dialog.setContentView(R.layout.congrats_dialog);
            congrats_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            Button done_btn = (Button) congrats_dialog.findViewById(R.id.done_btn);
            TextView congrats_msg_1 = (TextView) congrats_dialog.findViewById(R.id.congrats_msg_1);
            TextView congrats_msg_2 = (TextView) congrats_dialog.findViewById(R.id.congrats_msg_2);

            congrats_msg_1.setText("You have successfully bought " + input_shares);
            congrats_msg_2.setText("shares of " + ticker_name.getText().toString());

            done_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    congrats_dialog.dismiss();
                    load_portfolio_details();
                }
            });
            congrats_dialog.show();
            input_shares = "";
        }
    }

    private void open_sell_dialog() {

        SharedPreferences sharedPreferences = getSharedPreferences(shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        if(input_shares.isEmpty() || input_shares.equals("") || !input_shares.matches("-?\\d+")){
            StyleableToast.makeText(company_details.this, "Please enter a valid amount", R.style.exampleToast).show();
        }

        else if (Integer.parseInt(input_shares) <= 0) {
            StyleableToast.makeText(company_details.this, "Cannot sell non-positive shares", R.style.exampleToast).show();
        }

        else if(Integer.parseInt(input_shares) == 4000){
            StyleableToast.makeText(company_details.this, "Not enough shares to sell", R.style.exampleToast).show();
        }

        else{
            String current_stock_prices = sharedPreferences.getString(price_details, "");


            if(!current_stock_prices.equals("") || !current_stock_prices.isEmpty()){
                Type type = new TypeToken<HashMap<String, ArrayList<String>>>() {}.getType();
                stocks_price_details = gson.fromJson(current_stock_prices, type);
            }

           if(!stocks_price_details.isEmpty() && !stocks_price_details.containsKey(input_ticker)){
               StyleableToast.makeText(company_details.this, "Not enough shares to sell", R.style.exampleToast).show();
           }

            if(!stocks_price_details.isEmpty() && stocks_price_details.containsKey(input_ticker)){
                if(Integer.parseInt(stocks_price_details.get(input_ticker).get(0)) == 0){
                    StyleableToast.makeText(company_details.this, "Not enough shares to sell", R.style.exampleToast).show();
//                    sell_btn.setEnabled(false);
                }

                else if(Integer.parseInt(stocks_price_details.get(input_ticker).get(0)) < Integer.parseInt(input_shares)) {
                    StyleableToast.makeText(company_details.this, "Not enough shares to sell", R.style.exampleToast).show();
                }

                else {
                    ArrayList<String> updated_data = new ArrayList<String>();

                    String prev_shares = stocks_price_details.get(input_ticker).get(0);
                    Double prev_avg_cost = Double.parseDouble(stocks_price_details.get(input_ticker).get(1));
                    Double prev_total = Double.parseDouble(stocks_price_details.get(input_ticker).get(2));
                    Double prev_change = Double.parseDouble(stocks_price_details.get(input_ticker).get(3));
                    Double prev_market_value = Double.parseDouble(stocks_price_details.get(input_ticker).get(4));


                    Double new_total = prev_total - (Integer.parseInt(input_shares) * prev_avg_cost);
                    int new_shares = Integer.parseInt(prev_shares) - Integer.parseInt(input_shares);
                    Double new_avg_cost = new_total / new_shares;
                    Double new_market_value = Double.parseDouble(company_price) * new_shares;
                    Double new_change = new_market_value - new_total;

                    if(new_shares > 0){
                        updated_data.add(String.valueOf(new_shares));
                        updated_data.add(get_formatted_prices(String.valueOf(new_avg_cost)));
                        updated_data.add(get_formatted_prices(String.valueOf(new_total)));
                        updated_data.add(get_formatted_prices(String.valueOf(new_change)));
                        updated_data.add(get_formatted_prices(String.valueOf(new_market_value)));
                        stocks_price_details.replace(input_ticker, updated_data);

                    }

                    else{
                        stocks_price_details.remove(input_ticker);
                    }

                    String sell_pf_details = gson.toJson(stocks_price_details);
                    editor.putString(price_details, sell_pf_details);
                    editor.apply();
                    Log.d("logic_sell", stocks_price_details.toString());

                    // Updating the money in the wallet
                    Double current_money_in_wallet = Double.parseDouble(sharedPreferences.getString(user_wallet, "0"));
                    Double updated_money = current_money_in_wallet + (Integer.parseInt(input_shares) * prev_avg_cost);
                    editor.putString(user_wallet, get_formatted_prices(String.valueOf(updated_money)));
                    editor.apply();
                    Log.d("money_sell", sharedPreferences.getString(user_wallet, "0"));

                    // Portfolio update
                    String current_portfolio = sharedPreferences.getString(portfolio, "");
                    if (!current_portfolio.equals("") && !current_portfolio.isEmpty()) {
                        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
                        user_portfolio = gson.fromJson(current_portfolio, type);
                    }

                    if (!user_portfolio.isEmpty() && user_portfolio.containsKey(input_ticker)) {
                        String current_shares = user_portfolio.get(input_ticker);
                        Log.d("aaya1", stocks_price_details.toString());

                        if (stocks_price_details.containsKey(input_ticker) && Integer.parseInt(stocks_price_details.get(input_ticker).get(0)) > 0){
                            user_portfolio.replace(input_ticker, stocks_price_details.get(input_ticker).get(0));
                        }
                        else if(!stocks_price_details.containsKey(input_ticker)){
                            user_portfolio.remove(input_ticker);
                        }

                        Log.d("user_portfolio_sell", user_portfolio.toString());
                        String sell_json_string = gson.toJson(user_portfolio);
                        editor.putString(portfolio, sell_json_string);
                        editor.apply();

                        trade_dialog.dismiss();
                        congrats_dialog = new Dialog(company_details.this);
                        congrats_dialog.setContentView(R.layout.congrats_dialog);
                        congrats_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        TextView congrats_msg_1 = (TextView) congrats_dialog.findViewById(R.id.congrats_msg_1);
                        TextView congrats_msg_2 = (TextView) congrats_dialog.findViewById(R.id.congrats_msg_2);
                        Button done_btn = (Button) congrats_dialog.findViewById(R.id.done_btn);

                        congrats_msg_1.setText("You have successfully sold " + input_shares);
                        congrats_msg_2.setText("shares of " + ticker_name.getText().toString());

                        done_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                congrats_dialog.dismiss();
                                load_portfolio_details();
                            }
                        });
                        congrats_dialog.show();
                        input_shares = "";
                    }
                }
            }
        }

    }

    private String get_formatted_prices(String p){
        Double price = Double.valueOf(p);
        DecimalFormat f = new DecimalFormat("0.00");

        return String.valueOf(f.format(price));
    }

    private String get_formatted_date(String ipo_date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("mm-dd-yyyy");

        return sdf2.format(sdf.parse(ipo_date));
    }

    // Reference: https://stackoverflow.com/questions/39204258/saving-hash-map-into-sharedpreferences
    private void update_user_favourites(){
        Gson gson = new Gson();
        SharedPreferences sharedPreferences = getSharedPreferences(shared_prefs, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        favourites_icon = (ImageView) findViewById(R.id.favourites_icon);

        String temp = sharedPreferences.getString(watchlist, "");
        if(!temp.equals("") || !temp.isEmpty()){
            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            user_watchlist = gson.fromJson(temp, type);
        }

        if(!user_watchlist.isEmpty() && user_watchlist.containsKey(input_ticker)){
            favourites_icon.setImageResource(R.drawable.ic_star_fill);
            ticker_in_watchlist = true;
        }
        else{
            favourites_icon.setImageResource(R.drawable.ic_star_outline);
            ticker_in_watchlist = false;
        }

        favourites_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(ticker_in_watchlist == false){
                    StyleableToast.makeText(company_details.this, input_ticker + " is added to favorites", R.style.exampleToast).show();
                    ticker_in_watchlist = true;
                    favourites_icon.setImageResource(R.drawable.ic_star_fill);
                    String current_list = sharedPreferences.getString(watchlist, "");
                    Log.d("current list", current_list);

                    if(!current_list.isEmpty() || !current_list.equals("")){
                        Type type = new TypeToken<HashMap<String, String>>() {}.getType();
                        user_watchlist = gson.fromJson(current_list, type);
                    }

                    user_watchlist.put(ticker_name.getText().toString(), company_name.getText().toString());
                    Log.d("watchlist_after_insertion", user_watchlist.toString());
                    String json_string = gson.toJson(user_watchlist);
                    editor.putString(watchlist, json_string);
                    editor.apply();
                }

                else{
                    StyleableToast.makeText(company_details.this, input_ticker + " is removed from favorites", R.style.exampleToast).show();
                    ticker_in_watchlist = false;
                    favourites_icon.setImageResource(R.drawable.ic_star_outline);
                    String current_list = sharedPreferences.getString(watchlist, "");
                    Type type = new TypeToken<HashMap<String, String>>() {}.getType();
                    user_watchlist = gson.fromJson(current_list, type);

                    if(!user_watchlist.isEmpty() && user_watchlist.containsKey(input_ticker)){
                        user_watchlist.remove(input_ticker);
                    }
                    Log.d("watchlist_after_deletion", user_watchlist.toString());
                    String json_string = gson.toJson(user_watchlist);
                    editor.putString(watchlist, json_string);
                    editor.apply();
                }
            }
        });

    }

    private void send_data(){
        Intent intent1 = new Intent(company_details.this, HomeActivity.class);
        intent1.putExtra("ticker_symbol", ticker_name.getText().toString());
        intent1.putExtra("company_name", company_name.getText().toString());
        intent1.putExtra("stock_price", stock_price.getText().toString());
        intent1.putExtra("stock_price_change", stock_price_change.getText().toString());
        startActivity(intent1);
    }

    // Reference: https://www.youtube.com/watch?v=mAKyrMGgGU4
    public void load_content(){
        Log.d("Data Updated", "Hurray!!");
        load_company_details_data();
        load_company_latest_prices();
        load_company_peers();
        load_insights_data();
        load_company_news();
        update_user_favourites();
        trade_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open_trade_dialog();
            }
        });
        refresh_data(15000);
    }

    public void refresh_data(int millisec){
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                load_content();
            }
        };

        handler.postDelayed(runnable, millisec);
    }

    private void initPeersRecyclerView(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView company_peers_recycler = (RecyclerView) findViewById(R.id.company_peers_recycler);
        company_peers_recycler.setLayoutManager(layoutManager);
        CompanyPeerRecyclerAdapter peeradapter = new CompanyPeerRecyclerAdapter(this, company_peers);
        company_peers_recycler.setAdapter(peeradapter);
    }

    public String[] daily_fragment_data() {
        String[] result = new String[2];
        result[0] = input_ticker;
        result[1] = String.valueOf(chart_latest_time);
         return result;
    }

    public String fragment_data(){
        return input_ticker;
    }
}