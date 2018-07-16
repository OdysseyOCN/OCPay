package com.ocpay.wallet.activities;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.ActivityNotificationDetailBinding;
import com.ocpay.wallet.greendao.NotificationBean;
import com.ocpay.wallet.greendao.manager.NotificationBeanDaoUtils;
import com.snow.commonlibrary.utils.DateUtils;
import com.snow.commonlibrary.utils.StringUtil;

import java.text.ParseException;

import static com.ocpay.wallet.Constans.CONFIG.NOTIFICATION;
import static com.snow.commonlibrary.utils.DateUtils.getRelativeTimeSpan;

public class NotificationDetailActivity extends BaseActivity implements View.OnClickListener {


    private ActivityNotificationDetailBinding binding;
    private NotificationBean notificationBean;


    public static void startNotificationDetailActivity(Activity activity, NotificationBean notificationBean) {
        Intent intent = new Intent(activity, NotificationDetailActivity.class);
        intent.putExtra(NOTIFICATION, notificationBean);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(NotificationDetailActivity.this, R.layout.activity_notification_detail);
        initActionBar();
        initView();
        updateNotificationStatus();

    }

    private void updateNotificationStatus() {
        if (!notificationBean.getRead()) {
            notificationBean.setRead(true);
            NotificationBeanDaoUtils.update(MyApp.getContext(), notificationBean);
            try {
                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(Integer.valueOf(notificationBean.getId() + ""));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_notification_del);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
    }

    private void initView() {
        notificationBean = (NotificationBean) getIntent().getSerializableExtra(NOTIFICATION);
        if (getIntent() == null || notificationBean == null) return;
        binding.tvNotificationContent.setText(notificationBean.getContent());
        binding.tvNotificationTitle.setText(notificationBean.getTitle());
        if (!StringUtil.isEmpty(notificationBean.getTimeStamp())) {
            try {
                String relativeAndCustomSpan = DateUtils.getRelativeAndCustomSpan(Long.valueOf(notificationBean.getTimeStamp()));
                binding.tvNotificationTime.setText(relativeAndCustomSpan);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

        }

    }

    @Override
    protected void onDestroy() {
        notificationReturnHome();
        super.onDestroy();
    }
}
