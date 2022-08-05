package com.example.stocks;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Portfolio_RecyclerViewAdapter extends RecyclerView.Adapter<Portfolio_RecyclerViewAdapter.MyViewModel>
        {

    private ArrayList<ArrayList<String>> list;
    Portfolio_RecyclerViewAdapter adapter1;

    public void setData(ArrayList<ArrayList<String>> list){
        Log.d("list_data", list.toString());
        this.list = list;
    }

    @NonNull
    @Override
    public Portfolio_RecyclerViewAdapter.MyViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { ;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_portfolio_recyclerview, parent, false);
        return new Portfolio_RecyclerViewAdapter.MyViewModel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Portfolio_RecyclerViewAdapter.MyViewModel holder, int position) {
        Log.d("holder_portfolio", String.valueOf(list.get(position)));

        String current_ticker = list.get(position).get(0);
        holder.pf_ticker.setText(list.get(position).get(0));
        holder.pf_number_of_shares.setText(list.get(position).get(1) + " shares");
        holder.pf_price.setText(list.get(position).get(2));
        holder.pf_price_change.setText(list.get(position).get(3));

        if(list.get(position).get(3).contains("-")){
            holder.pf_pc_icon.setImageResource(R.drawable.ic_trending_down);
            holder.pf_price_change.setTextColor(Color.parseColor("#FF0000"));
        }

        else if (list.get(position).get(3).contains("0.00%")){
            holder.pf_price_change.setTextColor(Color.parseColor("#000000"));
        }

        else {
            holder.pf_pc_icon.setImageResource(R.drawable.ic_trending_up);
            holder.pf_price_change.setTextColor(Color.parseColor("#009933"));
        }

        ImageView right_arrow = (ImageView) holder.itemView.findViewById(R.id.right_arrow);

        right_arrow.setOnClickListener(new View.OnClickListener() {
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

    // TODO: onRowMoved not working properly
//    @Override
//    public void onRowMoved(int from, int to) {
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
//
//        notifyItemMoved(from, to);
//    }


//    @Override
//    public void onRowSelected(RecyclerViewAdapter.MyViewModel myViewHolder) {
//        myViewHolder.itemView.setBackgroundColor(Color.WHITE);
//    }
//
//    @Override
//    public void onRowClear(RecyclerViewAdapter.MyViewModel myViewHolder) {
//        myViewHolder.itemView.setBackgroundColor(Color.WHITE);
//    }


    class MyViewModel extends RecyclerView.ViewHolder{
        TextView pf_ticker;
        TextView pf_number_of_shares;
        TextView pf_price;
        TextView pf_price_change;
        ImageView pf_pc_icon;

        public MyViewModel(View view){
            super(view);
            pf_ticker = view.findViewById(R.id.pf_ticker);
            pf_number_of_shares = view.findViewById(R.id.pf_number_of_shares);
            pf_price = view.findViewById(R.id.pf_price);
            pf_price_change = view.findViewById(R.id.pf_price_change);
            pf_pc_icon = view.findViewById(R.id.pf_pc_icon);
        }
    }
}
