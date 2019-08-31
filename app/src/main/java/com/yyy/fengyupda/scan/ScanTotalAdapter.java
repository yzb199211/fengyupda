package com.yyy.fengyupda.scan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.yyy.fengyupda.R;
import com.yyy.fengyupda.model.storage.StorageScanBean;

import java.util.List;

public class ScanTotalAdapter extends RecyclerView.Adapter<ScanTotalAdapter.VH> {
    Context context;
    List<StorageScanBean> list;

    public ScanTotalAdapter(Context context, List<StorageScanBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_scan_total, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.tvTitle.setText("款号：" + list.get(position).getsStyleNo());
        holder.tvCount.setText("数量：" + list.get(position).getCount());
        holder.tvColor.setText(list.get(position).getsColorName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvColor;
        TextView tvCount;

        public VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvColor = itemView.findViewById(R.id.tv_color);
            tvCount = itemView.findViewById(R.id.tv_count);
        }
    }
}
