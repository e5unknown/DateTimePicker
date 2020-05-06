package com.dev420.datetimepicker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PickerAdapter extends RecyclerView.Adapter<PickerAdapter.PickerViewHolder> {

    private ArrayList<String> data;
    private int firstVisible = 0;

    public PickerAdapter() {
        this.data = new ArrayList<>();
    }

    public ArrayList<String> getData() {
        return data;
    }

    public void setData(ArrayList<String> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PickerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_time_item, parent, false);
        return new PickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickerViewHolder holder, int position) {
        holder.tvDateOrTimeItem.setText(data.get(position));
        if (position == firstVisible || position == firstVisible + 4) {
            holder.tvDateOrTimeItem.setAlpha(0.3f);
            holder.tvDateOrTimeItem.setTextColor(holder.itemView.getResources().getColor(R.color.textColorPrimary));
        }
        if (position == firstVisible + 1 || position == firstVisible + 3) {
            holder.tvDateOrTimeItem.setAlpha(1f);
            holder.tvDateOrTimeItem.setTextColor(holder.itemView.getResources().getColor(R.color.textColorPrimary));
        }
        if (position == firstVisible + 2) {
            holder.tvDateOrTimeItem.setAlpha(1f);
            holder.tvDateOrTimeItem.setTextColor(holder.itemView.getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    // Обработка изменения позиции в отображении
    public void changeItemAppearance(int position) {
        if (firstVisible != position) {
            firstVisible = position;
            notifyItemChanged(firstVisible);
            notifyDataSetChanged();
        }
    }

    class PickerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDateOrTimeItem;

        private PickerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateOrTimeItem = itemView.findViewById(R.id.tvDateOrTimeItem);
        }
    }
}
