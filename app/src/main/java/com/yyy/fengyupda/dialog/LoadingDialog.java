package com.yyy.fengyupda.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.wang.avi.AVLoadingIndicatorView;
import com.yyy.fengyupda.R;


public class LoadingDialog {
    private static AVLoadingIndicatorView avi;
    /**
     * 加载数据对话框
     */
    public static Dialog mLoadingDialog;

    /**
     * 显示加载对话框
     *
     * @param context    上下文
     * @param msg        对话框显示内容
     * @param cancelable 对话框是否可以取消
     */

    public static Dialog showDialogForLoading(Activity context, String msg, boolean cancelable) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        avi = view.findViewById(R.id.avi);
        mLoadingDialog = new Dialog(context, R.style.LoadingDialogStyle);
        mLoadingDialog.setCancelable(cancelable);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        mLoadingDialog.show();
        avi.show();
        return mLoadingDialog;
    }

    public static Dialog showDialogForLoading(Activity context) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        avi = view.findViewById(R.id.avi);
        mLoadingDialog = new Dialog(context, R.style.LoadingDialogStyle);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(view, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        try {
            mLoadingDialog.show();
            avi.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return mLoadingDialog;
    }

    /**
     * 关闭加载对话框
     */
    public static void cancelDialogForLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
            avi.hide();
        }
    }

    public static void show() {
        mLoadingDialog.show();
        avi.show();
    }

}
