package com.yyy.fengyupda.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.yyy.fengyupda.R;
import com.yyy.fengyupda.interfaces.OnItemClickListener;

import java.util.List;

public class MenuUsualAdapter extends RecyclerView.Adapter<MenuUsualAdapter.VH> {

    private List<Menu> menus = null;
    private Context mContext = null;


    /*实例化图片点击接口*/
    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public MenuUsualAdapter(List<Menu> menus, Context mContext) {
        this.menus = menus;
        this.mContext = mContext;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvMenu;
        ImageView ivMenu;

        public VH(View v) {
            super(v);
            tvMenu = v.findViewById(R.id.tv_menu);
            ivMenu = v.findViewById(R.id.iv_menu);
        }
    }


    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_usual, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VH holder, final int position) {
        try {
            holder.tvMenu.setText(menus.get(position).getStr());
            holder.ivMenu.setImageResource(menus.get(position).getImg());
            /*设置点击事件接口*/
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, position);
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return menus.size();

    }
}
