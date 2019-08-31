package com.yyy.fengyupda.storage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.yyy.fengyupda.R;
import com.yyy.fengyupda.interfaces.OnItemClickListener;
import com.yyy.fengyupda.model.storage.StorageListOrder;
import com.yyy.fengyupda.util.StringUtil;

import java.util.List;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.VH> {
    Context context;
    List<StorageListOrder> list;

    public StorageAdapter(Context context, List<StorageListOrder> list) {
        this.context = context;
        this.list = list;
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_storage, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        try {
            String red;
            holder.tvCustom.setText(list.get(position).getSCustShortName());
            holder.tvCode.setText(list.get(position).getSBillNo());
            holder.tvDate.setText(StringUtil.getDate(list.get(position).getDDate()));
            if (list.get(position).getIRed() == 0)
                red = "";
            else
                red = "红冲";
            holder.tvRed.setText(red);
            holder.tvNum.setText(list.get(position).getIQty() + "");
            holder.tvStock.setText(list.get(position).getSStockName());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClick(v, position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class VH extends RecyclerView.ViewHolder {
        TextView tvCustom;
        TextView tvStock;
        TextView tvDate;
        TextView tvRed;
        TextView tvNum;
        TextView tvCode;

        public VH(View v) {
            super(v);
            tvCode = v.findViewById(R.id.tv_code);
            tvCustom = v.findViewById(R.id.tv_customer);
            tvDate = v.findViewById(R.id.tv_date);
            tvNum = v.findViewById(R.id.tv_number);
            tvRed = v.findViewById(R.id.tv_red);
            tvStock = v.findViewById(R.id.tv_stock);
        }
    }
}

