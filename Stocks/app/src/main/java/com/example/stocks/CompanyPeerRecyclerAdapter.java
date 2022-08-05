package com.example.stocks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CompanyPeerRecyclerAdapter extends RecyclerView.Adapter<CompanyPeerRecyclerAdapter.ViewHolder>{

    private ArrayList<String> peers = new ArrayList<String>();
    private Context mContext;

    public CompanyPeerRecyclerAdapter(Context context, ArrayList<String> comp_peers){
        peers = comp_peers;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.company_peers_recyclerview, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.d("peer", peers.get(position).toString());
        if(position < peers.size()-1){
            holder.curr_comp_peer.setText(Html.fromHtml("<u>" + peers.get(position) + "</u>" + ", "));
        }
        else{
            holder.curr_comp_peer.setText(Html.fromHtml("<u>" + peers.get(position) + "</u>"));
        }

        holder.curr_comp_peer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, peers.get(position) + " Clicked", Toast.LENGTH_SHORT).show();
                String temp = peers.get(position);
                String input_ticker = temp.replace(",", "");
                Intent intent = new Intent(mContext, company_details.class);
                intent.putExtra("input_ticker", input_ticker);
                mContext.startActivity(intent);
                ((Activity)mContext).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView curr_comp_peer;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            curr_comp_peer = (TextView) itemView.findViewById(R.id.curr_comp_peer);
        }
    }
}
