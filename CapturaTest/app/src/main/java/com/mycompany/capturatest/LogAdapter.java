package com.mycompany.capturatest;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.ItemViewHolder> {
    private List<LogItem> logItems;

    LogAdapter(List<LogItem> logItems) {
        this.logItems = logItems;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent,
                false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.itemNo.setText(String.format("%d", logItems.get(position).logItemNo));
        if (logItems.get(position).discrepancy != null)
            holder.discrepancy.setText(logItems.get(position).discrepancy);
        else
            holder.discrepancy.setText("En blanco");
        if (logItems.get(position).correctiveAction != null)
            holder.correctiveAction.setText(logItems.get(position).correctiveAction);
        else
            holder.correctiveAction.setText("En blanco");
    }

    @Override
    public int getItemCount() {
        return logItems.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView itemNo;
        TextView discrepancy;
        TextView correctiveAction;

        ItemViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cv);
            itemNo = itemView.findViewById(R.id.logItemNumber);
            discrepancy = itemView.findViewById(R.id.discrepancy);
            correctiveAction = itemView.findViewById(R.id.corrAction);
        }
    }
}
