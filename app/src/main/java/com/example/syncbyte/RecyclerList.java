package com.example.syncbyte;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class RecyclerList extends RecyclerView.Adapter<RecyclerList.ViewHolder>{

    private List<ReportModal> dataList;

    public RecyclerList(List<ReportModal> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem = layoutInflater.inflate(R.layout.list_single_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.checkInDate.setText(dataList.get(position).checkInDate);
        holder.checkInTime.setText(dataList.get(position).checkInTime);
        holder.checkOutDate.setText(dataList.get(position).checkOutDate);
        holder.checkOuTime.setText(dataList.get(position).checkOutTime);
        holder.deletHelper.setText(String.valueOf(dataList.get(position).checkInTimeFromDatabase));

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView checkInDate, checkInTime, checkOutDate,checkOuTime;
        TextView deletHelper;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkInDate = itemView.findViewById(R.id.checkInDate);
            checkInTime = itemView.findViewById(R.id.checkInTime);
            checkOutDate = itemView.findViewById(R.id.checkOutDate);
            checkOuTime = itemView.findViewById(R.id.checkOutTime);
            deletHelper = itemView.findViewById(R.id.checkInTimeFromDatabase);
        }

    }
}
