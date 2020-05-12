package com.dev420.datetimepicker.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dev420.datetimepicker.R;

import java.util.ArrayList;

public class PickerAdapter extends RecyclerView.Adapter<PickerAdapter.PickerViewHolder> {

    private ArrayList<String> data;
    private int firstVisible = 0;
    private OnPickerClickListener onPickerClickListener;

    public PickerAdapter() {
        this.data = new ArrayList<>();
    }

    public interface OnPickerClickListener{
        void onPickerClick(int position);
    }

    public void setOnPickerClickListener(OnPickerClickListener onPickerClickListener){
        this.onPickerClickListener = onPickerClickListener;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
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
        if (position == firstVisible - 1 || position == firstVisible + 5) {
            holder.tvDateOrTimeItem.setAlpha(0.1f);
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
            notifyDataSetChanged();
        }
    }

    class PickerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDateOrTimeItem;

        private PickerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateOrTimeItem = itemView.findViewById(R.id.tvDateOrTimeItem);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onPickerClickListener != null){
                        onPickerClickListener.onPickerClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
