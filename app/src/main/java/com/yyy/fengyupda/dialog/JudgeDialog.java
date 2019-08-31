package com.yyy.fengyupda.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.yyy.fengyupda.R;
import com.yyy.fengyupda.util.StringUtil;


public class JudgeDialog extends Dialog implements View.OnClickListener {
    Context context;
    private String title;
    private String content;
    private String positiveName;
    private String negativeName;
    private TextView tvTitle;
    private TextView tvContent;
    private TextView tvCancle;
    private TextView tvSubmit;
    OnCloseListener onCloseListener;

    public interface OnCloseListener {
        void onClick(boolean confirm);
    }

    public void setOnCloseListener(OnCloseListener onCloseListener) {
        this.onCloseListener = onCloseListener;
    }

    /**
     * 构造方法
     *
     * @param context
     */
    public JudgeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    /**
     * 构造方法
     *
     * @param context
     * @param themeResId dialog样式
     * @param content    设置内容
     */
    public JudgeDialog(Context context, int themeResId, String content) {
        super(context, themeResId);
        this.context = context;
        this.content = content;
    }

    /**
     * 构造方法
     *
     * @param context
     * @param themeResId dialog样式
     * @param content    设置内容
     * @param listener   设置监听
     */
    public JudgeDialog(Context context, int themeResId, String content, OnCloseListener listener) {
        super(context, themeResId);
        this.context = context;
        this.content = content;
        this.onCloseListener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_judge);
        setCanceledOnTouchOutside(false);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvCancle = findViewById(R.id.tv_cancel);
        tvSubmit = findViewById(R.id.tv_submit);
        if (StringUtil.isNotEmpty(title))
            tvTitle.setText(title);
        if (StringUtil.isNotEmpty(content))
            tvContent.setText(content);
        if (StringUtil.isNotEmpty(positiveName))
            tvSubmit.setText(positiveName);
        if (StringUtil.isNotEmpty(negativeName))
            tvCancle.setText(negativeName);
        tvCancle.setOnClickListener(this);
        tvSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
        switch (v.getId()) {
            case R.id.tv_cancel:
                if (onCloseListener != null) {
                    onCloseListener.onClick(false);
                }
                break;
            case R.id.tv_submit:
                if (onCloseListener != null) {
                    onCloseListener.onClick(true);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    public JudgeDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * 设置提示内容
     *
     * @param content
     * @return
     */
    public JudgeDialog setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * 设置确定按钮内容
     *
     * @param positiveName
     * @return
     */
    public JudgeDialog setPositiveName(String positiveName) {
        this.positiveName = positiveName;
        return this;
    }

    /**
     * 设置取消按钮内容
     *
     * @param negativeName
     * @return
     */
    public JudgeDialog setNegativeName(String negativeName) {
        this.negativeName = negativeName;
        return this;
    }
}
