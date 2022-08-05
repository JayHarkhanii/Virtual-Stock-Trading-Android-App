package com.example.stocks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewModel>
   {
    private ArrayList<ArrayList<String>> list;
    public static final String shared_prefs = "sharedPrefs";
    private Context mContext;
    public static final String watchlist = "user_watchlist";
    public HashMap<String, String> user_watchlist = new HashMap<String, String >();
    RecyclerViewAdapter adapter2;


    public void setData(Context context, ArrayList<ArrayList<String>> list){
        Log.d("watchlist_list_data", list.toString());
        this.list = list;
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_watchlist_recyclerview, parent, false);

        return new MyViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewModel holder, int position) {
        Log.d("holder_watchlist", list.get(position).toString());

        String current_ticker = list.get(position).get(0);
        holder.wtl_ticker.setText(list.get(position).get(0));
        holder.wtl_company_name.setText(list.get(position).get(1));
        holder.wtl_price.setText(list.get(position).get(2));
        holder.wtl_price_change.setText(list.get(position).get(3));

        if(list.get(position).get(3).contains("-")){
            holder.wtl_pc_icon.setImageResource(R.drawable.ic_trending_down);
            holder.wtl_price_change.setTextColor(Color.parseColor("#FF0000"));
        }
        else if(list.get(position).get(3).contains("0.00%")){
            holder.wtl_price_change.setTextColor(Color.parseColor("#000000"));
        }
        else {
            holder.wtl_pc_icon.setImageResource(R.drawable.ic_trending_up);
            holder.wtl_price_change.setTextColor(Color.parseColor("#009933"));
        }

        ImageView watchlist_right_arrow = (ImageView) holder.itemView.findViewById(R.id.watchlist_right_arrow);
        watchlist_right_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(), company_details.class);
                intent.putExtra("input_ticker", current_ticker);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

//    @Override
//    public void onRowMoved(int from, int to) {
//        Log.d("aaa", String.valueOf(from));
//        Log.d("aaab", String.valueOf(to));
//        if(from < to){
//            for(int i = from; i < to; i++){
//                Collections.swap(list, i, i+1);
//            }
//        }
//        else{
//            for(int i = from; i > to; i--){
//                Collections.swap(list, i, i-1);
//            }
//        }
//        notifyItemMoved(from, to);
//    }
//
//    @Override
//    public void onRowSelected(MyViewModel myViewHolder) {
//        myViewHolder.itemView.setBackgroundColor(Color.WHITE);
//    }
//
//    @Override
//    public void onRowClear(MyViewModel myViewHolder) {
////        myViewHolder.itemView.setBackgroundColor(Color.WHITE);
//    }
//
////    // TODO: on row swiped not working
//    @Override
//    public void onRowSwiped(MyViewModel myViewHolder) {
//
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences(shared_prefs, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        Gson gson = new Gson();
//
//        int index = myViewHolder.getAdapterPosition();
//        String ticker = list.get(index).get(0);
//
//        String watchlist_json = sharedPreferences.getString(watchlist, "");
//
//        if(!watchlist_json.isEmpty() && !watchlist_json.equals("")){
//            Type type = new TypeToken<HashMap<String, String>>() {}.getType();
//            user_watchlist = gson.fromJson(watchlist_json, type);
//            Log.d("watchlist_1", user_watchlist.toString());
//        }
//
//        if(!user_watchlist.isEmpty() && user_watchlist.containsKey(ticker)){
//            user_watchlist.remove(ticker);
//        }
//
//        String updated_watchlist_json = gson.toJson(user_watchlist);
//
//        editor.putString(watchlist, updated_watchlist_json);
//        editor.apply();
//        Log.d("watchlist_2", user_watchlist.toString());
//
//
//        list.remove(index);
//
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public void onViewDetachedFromWindow(@NonNull MyViewModel holder) {
//        holder.itemView.clearAnimation();
//        super.onViewDetachedFromWindow(holder);
//    }

    class MyViewModel extends RecyclerView.ViewHolder{
        TextView wtl_ticker;
        TextView wtl_company_name;
        TextView wtl_price;
        ImageView wtl_pc_icon;
        TextView wtl_price_change;

        public MyViewModel(View view){
            super(view);
            wtl_ticker = view.findViewById(R.id.wtl_ticker);
            wtl_company_name = view.findViewById(R.id.wtl_company_name);
            wtl_price = view.findViewById(R.id.wtl_price);
            wtl_pc_icon = view.findViewById(R.id.wtl_pc_icon);
            wtl_price_change = view.findViewById(R.id.wtl_price_change);
        }
    }
}