package com.example.stocks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class HistoricalChartFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HistoricalChartFragment() {
    }

    public static HistoricalChartFragment newInstance(String param1, String param2) {
        HistoricalChartFragment fragment = new HistoricalChartFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        company_details activity = (company_details) getActivity();
        String input_ticker = activity.fragment_data();

        if(!input_ticker.isEmpty()) {
            Log.d("hist_fragment_ticker", input_ticker);
        }

        View chartview = inflater.inflate(R.layout.fragment_historical_chart, container, false);
        WebView hist_chart_webview = (WebView) chartview.findViewById(R.id.hist_chart_webview);

        hist_chart_webview.getSettings().setJavaScriptEnabled(true);
        hist_chart_webview.loadUrl("file:///android_asset/Historical_Charts.html");

        hist_chart_webview.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url){
                hist_chart_webview.loadUrl("javascript:get_company_historical_charts('" + input_ticker + "')");
            }
        });

        return chartview;
    }
}