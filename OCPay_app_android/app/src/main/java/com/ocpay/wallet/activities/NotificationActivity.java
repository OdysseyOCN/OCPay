package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.adapter.NotificationAdapter;
import com.ocpay.wallet.greendao.NotificationBean;
import com.ocpay.wallet.databinding.ActivityNotificationBinding;
import com.ocpay.wallet.greendao.gen.NotificationBeanDao;
import com.ocpay.wallet.greendao.manager.NotificationBeanDaoUtils;
import com.snow.commonlibrary.recycleview.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.ocpay.wallet.Constans.CONFIG.NOTIFICATION;
import static com.ocpay.wallet.Constans.Noti.TYPE_NOTICE;
import static com.ocpay.wallet.Constans.Noti.TYPE_RISK_WARNING;
import static com.ocpay.wallet.Constans.Noti.TYPE_TX_RECEIVED;
import static com.ocpay.wallet.Constans.Noti.TYPE_TX_SEND_FAILED;
import static com.ocpay.wallet.Constans.Noti.TYPE_TX_SEND_SUCCESSFUL;

public class NotificationActivity extends BaseActivity implements View.OnClickListener {


    private ActivityNotificationBinding binding;
    private List<NotificationBean> notificationList;
    private NotificationAdapter notificationAdapter;


    public static void startNotificationActivity(Activity activity) {

        Intent intent = new Intent(activity, NotificationActivity.class);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(NotificationActivity.this, R.layout.activity_notification);
        initActionBar();
        initView();
        initData();
        initListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void initListener() {
        notificationAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView.Adapter adapter, Object data, int position) {
                if (data instanceof NotificationBean) {
                    switch (((NotificationBean) data).getNotificationType()) {
                        case TYPE_NOTICE:
                        case TYPE_RISK_WARNING:
                            NotificationDetailActivity.startNotificationDetailActivity(NotificationActivity.this, (NotificationBean) data);
                            break;
                        case TYPE_TX_RECEIVED:
                        case TYPE_TX_SEND_FAILED:
                        case TYPE_TX_SEND_SUCCESSFUL:
                            Intent intent = new Intent(NotificationActivity.this, TransactionDetailActivity.class);
                            intent.putExtra(NOTIFICATION, (NotificationBean) data);
                            NotificationActivity.this.startActivity(intent);
                            break;


                    }

                    notificationList.get(position).setRead(true);
                    NotificationBeanDaoUtils.update(MyApp.getContext(), notificationList.get(position));
                    notificationAdapter.notifyDataSetChanged();
                }
            }
        });


    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_notification);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.includeActionBar.tbTvMenu.setText(R.string.activity_notification_read_all);
        binding.includeActionBar.tbTvMenu.setVisibility(View.VISIBLE);
        binding.includeActionBar.tbTvMenu.setOnClickListener(this);
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        binding.includeActionBar.ivBack.setOnClickListener(this);


    }

    private void initData() {
        List<NotificationBean> notificationBeans = NotificationBeanDaoUtils.sqlAllDesc(MyApp.getContext());
        if (notificationBeans != null && notificationBeans.size() > 0) {
            notificationList = notificationBeans;
            notificationAdapter.setData(notificationList);
        }
        showBg();
    }

    private void showBg() {
        int visibleList = (notificationList == null || notificationList.size() <= 0) ? View.GONE : View.VISIBLE;
        int visibleNull = (notificationList == null || notificationList.size() <= 0) ? View.VISIBLE : View.GONE;
        binding.includeNoRecord.rlNullRecord.setVisibility(visibleNull);
        binding.rlNotification.setVisibility(visibleList);


    }


    private void initView() {
        notificationAdapter = new NotificationAdapter(NotificationActivity.this);
        binding.rlNotification.setAdapter(notificationAdapter);
        binding.rlNotification.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tb_tv_menu:
                readAll();
                break;
            case R.id.iv_back:
                finish();
                break;

        }

    }

    private void readAll() {
        if (notificationList != null && notificationList.size() > 0) {
            for (int i = 0; i < notificationList.size(); i++) {
                if (!notificationList.get(i).getRead()) {
                    notificationList.get(i).setRead(true);
                    NotificationBeanDaoUtils.update(MyApp.getContext(), notificationList.get(i));
                }
            }
        }
        if (notificationAdapter != null) notificationAdapter.notifyDataSetChanged();
    }
}
