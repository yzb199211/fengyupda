package com.yyy.fengyupda.scan;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.yyy.fengyupda.R;
import com.yyy.fengyupda.interfaces.OnItemClickListener;
import com.yyy.fengyupda.model.storage.StorageScanBean;

import java.util.List;

public class ScanNewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<StorageScanBean> list;
    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(@Nullable OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ScanNewAdapter(Context context, List<StorageScanBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_scan, parent, false);
            return new VH(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_scan1, parent, false);
            return new VH1(view);
        }

    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        try {
            if (list.get(position).getType() == 1) {
                VH holder = (VH) viewHolder;
                holder.tvTitle.setText("款式：" + list.get(position).getsStyleNo());
                holder.tvCode.setText("条码：" + list.get(position).getsBarCode());
                holder.tvSize.setText("尺码：" + list.get(position).getsSizeName());
                holder.tvColor.setText("颜色：" + list.get(position).getsColorName());
                holder.tvCount.setText("仓位：" + (TextUtils.isEmpty(list.get(position).getsBerChID()) ? "" : list.get(position).getsBerChID()));


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemClick(v, position);
                    }
                });
            } else if (list.get(position).getType() == 0) {
                VH1 holder = (VH1) viewHolder;
                holder.tvCode.setText("条码：" + list.get(position).getsBarCode());
                holder.tvCount.setText("仓位：" + list.get(position).getsBerChID());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null)
                            onItemClickListener.onItemClick(v, position);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvColor;
        TextView tvSize;
        TextView tvCount;
        TextView tvCode;
        TextView tvPos;
        LinearLayout llOne;
        LinearLayout llTwo;

        public VH(View v) {
            super(v);
            tvCode = v.findViewById(R.id.tv_code);
            tvTitle = v.findViewById(R.id.tv_title);
            tvColor = v.findViewById(R.id.tv_color);
            tvCount = v.findViewById(R.id.tv_count);
            tvSize = v.findViewById(R.id.tv_size);
            tvPos = v.findViewById(R.id.tv_stock_pos);
            llOne = v.findViewById(R.id.ll_one);
            llTwo = v.findViewById(R.id.ll_two);
        }
    }

    public static class VH1 extends RecyclerView.ViewHolder {
        TextView tvCode;
        TextView tvCount;

        public VH1(View v) {
            super(v);
            tvCode = v.findViewById(R.id.tv_code);
            tvCount = v.findViewById(R.id.tv_count);
        }
    }

}
