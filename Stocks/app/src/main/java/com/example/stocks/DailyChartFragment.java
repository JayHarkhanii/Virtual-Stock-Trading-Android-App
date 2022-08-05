package com.example.stocks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.Reference;
import java.lang.reflect.Type;
import java.util.HashMap;

public class DailyChartFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    public static final String shared_prefs = "sharedPrefs";
    public static final String change_in_price = "prices_change";
    public HashMap<String, Double> chart_prices = new HashMap<String, Double>();
    public Double price_difference = 0.0;

    public DailyChartFragment() {
    }
    public static DailyChartFragment newInstance(String param1, String param2) {
        DailyChartFragment fragment = new DailyChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

//    Reference: https://gist.github.com/wesleyduff/403fc3a24f5a0f4508ef0e5f55a95ae9
//    Reference: https://www.youtube.com/watch?v=0Xf4Dz8tO6c
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        company_details activity = (company_details) getActivity();
        String[] result = activity.daily_fragment_data();
        String input_ticker = result[0];
        long timestamp = Long.parseLong(result[1]);

        SharedPreferences sharedPreferences = activity.getSharedPreferences(shared_prefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        String price_info = sharedPreferences.getString(change_in_price, "");

        if(!price_info.isEmpty() || !price_info.equals("")){
            Type type = new TypeToken<HashMap<String, Double>>() {}.getType();
            chart_prices = gson.fromJson(price_info, type);
        }

        if(!chart_prices.isEmpty() && chart_prices.containsKey(input_ticker)){
            price_difference = chart_prices.get(input_ticker);
        }

        else{
            price_difference = 0.0;
        }

        View chartview = inflater.inflate(R.layout.fragment_daily_chart, container, false);
        WebView daily_chart_webview = (WebView) chartview.findViewById(R.id.daily_chart_webview);

        daily_chart_webview.getSettings().setJavaScriptEnabled(true);
        daily_chart_webview.loadUrl("file:///android_asset/Daily_Charts.html");

        // Reference: https://stackoverflow.com/questions/19001500/pass-variable-from-android-to-javascript-launched-in-webview
        daily_chart_webview.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                daily_chart_webview.loadUrl("javascript:get_company_daily_charts('" + input_ticker +"', '"+price_difference+"', '"+timestamp+"')");
            }
        });

        return chartview;
    }
}