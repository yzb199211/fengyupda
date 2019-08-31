package com.yyy.fengyupda.scan;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ruijie.uhflib.uhf.LinkInter;
import com.ruijie.uhflib.uhf.manage.LinkManage;
import com.yyy.fengyupda.R;
import com.yyy.fengyupda.util.Toasts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ruijie.com.uhflib.uhf.Linker;

public class ScanSettingActivity extends AppCompatActivity {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_right)
    ImageView ivRight;
    @BindView(R.id.tv_power)
    TextView tvPower;

    @BindView(R.id.sb_power)
    SeekBar sbPower1;
    @BindView(R.id.tv_right)
    TextView tvRight;

    int code;
    int dwellTime;
    private LinkInter mLinker;
    private SharedPreferences cfgSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLinker = LinkManage.getInstance(LinkManage.TYPE_HANDSET);
        cfgSP = getSharedPreferences("ServerConf", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_scan_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        code = intent.getIntExtra("code", 10);
        tvTitle.setText("设置");
        tvRight.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.GONE);
        Linker.AntCfg antCfg = mLinker.getAntCfg(0);
        Log.e("power", antCfg.powerLevel + "");
        dwellTime = antCfg.dwellTime;
        sbPower1.setProgress(antCfg.powerLevel / 10);
        sbPower1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPower.setText("功率：" + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @OnClick({R.id.iv_back, R.id.tv_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_right:
                try {
                    boolean isSucess = mLinker.enableAnt(0, sbPower1.getProgress(), dwellTime);
                    if (isSucess)
                        finish();
                    else
                        Toasts.showShort(ScanSettingActivity.this, "配置失败");
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO Auto-generated catch block
                    Toast.makeText(ScanSettingActivity.this,
                            "设置异常:" + e.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                break;
            default:
                break;
        }
    }
}
