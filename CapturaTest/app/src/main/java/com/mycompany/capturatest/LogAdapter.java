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
        holder.itemNo.setText(String.valueOf(logItems.get(position).getLogItemNumber()));
        if (logItems.get(position).getDiscrepancy().getDescription() != null)
            holder.discrepancy.setText(logItems.get(position).getDiscrepancy().getDescription());
        else
            holder.discrepancy.setText(R.string.blank_item);
        if (logItems.get(position).getCorrectiveAction().getDescription() != null)
            holder.correctiveAction.setText(logItems.get(position).getCorrectiveAction().getDescription());
        else
            holder.correctiveAction.setText(R.string.blank_item);
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
        TextView itemNo;
        TextView discrepancy;
        TextView correctiveAction;

        ItemViewHolder(View itemView) {
            super(itemView);
            itemNo = itemView.findViewById(R.id.logItemNumber);
            discrepancy = itemView.findViewById(R.id.discrepancy);
            correctiveAction = itemView.findViewById(R.id.corrAction);
        }
    }
}
