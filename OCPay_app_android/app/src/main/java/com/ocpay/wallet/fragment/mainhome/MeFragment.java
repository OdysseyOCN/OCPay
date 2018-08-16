package com.ocpay.wallet.fragment.mainhome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.AboutUsActivity;
import com.ocpay.wallet.activities.AccountSignInActivity;
import com.ocpay.wallet.activities.ContactUsActivity;
import com.ocpay.wallet.activities.ContactsActivity;
import com.ocpay.wallet.activities.MyAccountActivity;
import com.ocpay.wallet.activities.NotificationActivity;
import com.ocpay.wallet.activities.SystemSettingsActivity;
import com.ocpay.wallet.activities.WalletImportActivity;
import com.ocpay.wallet.activities.WalletManageActivity;
import com.ocpay.wallet.adapter.SettingsAdapter;
import com.ocpay.wallet.bean.SettingsBean;
import com.ocpay.wallet.databinding.FragmentMeBinding;
import com.ocpay.wallet.fragment.BaseFragment;
import com.ocpay.wallet.greendao.NotificationBean;
import com.ocpay.wallet.greendao.manager.NotificationBeanDaoUtils;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.OUserManager;
import com.snow.commonlibrary.recycleview.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.app.Activity.RESULT_OK;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_SIGN_IN_UPDATE;
import static com.ocpay.wallet.bean.SettingsBean.TYPE.ABOUT_US;
import static com.ocpay.wallet.bean.SettingsBean.TYPE.CONTACT;
import static com.ocpay.wallet.bean.SettingsBean.TYPE.CONTACT_US;
import static com.ocpay.wallet.bean.SettingsBean.TYPE.NOTIFICATION;
import static com.ocpay.wallet.bean.SettingsBean.TYPE.SYSTEM;
import static com.ocpay.wallet.bean.SettingsBean.TYPE.USER;

public class MeFragment extends BaseFragment<FragmentMeBinding> implements View.OnClickListener {

    private List<SettingsBean> settingsList;
    private SettingsAdapter settingsAdapter;
    private final int REQUEST_SIGN_IN = 0x3;

    @Override
    public int setContentView() {
        return R.layout.fragment_me;
    }

    @Override
    public void loadData() {

    }


    @Override
    public void onResume() {
        super.onResume();
        updateNotification();
    }

    private void updateNotification() {
        List<NotificationBean> notificationBeans = NotificationBeanDaoUtils.sqlUnRead(MyApp.getContext(), false);

        if (notificationBeans == null || notificationBeans.size() <= 0) {

            notifyNotificationNumber(0);
            return;
        }
        notifyNotificationNumber(notificationBeans.size());

    }

    private void notifyNotificationNumber(int count) {
        if (settingsList != null && settingsList.size() > 0 && settingsList.get(0).getType() == NOTIFICATION) {
            settingsList.get(0).setMessageCount(count);
            settingsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initActionBar();
        initView();
        initListener();
        initRxBus();


    }

    private void initRxBus() {

        Disposable disposable = RxBus.getInstance().toObservable(Constans.RXBUS.ACTION_UPDATE_NOTIFICATION_LIST, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        updateNotification();
                    }
                });

        addDisposable(disposable);


    }

    private void initListener() {
        bindingView.llImportWallet.setOnClickListener(this);
        bindingView.llManageWallet.setOnClickListener(this);

        settingsAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView.Adapter adapter, Object data, int position) {
                if (data instanceof SettingsBean) {
                    SettingsBean.TYPE type = ((SettingsBean) data).getType();
                    actionSetting(type);
                }
            }
        });


    }

    private void actionSetting(SettingsBean.TYPE type) {
        if (getActivity() == null) return;
        switch (type) {
            case HELP:

                break;
            case USER:
                if (OUserManager.getInstance().hasToken()) {
                    MyAccountActivity.startMyAccountActivity(getActivity());
                } else {
                    AccountSignInActivity.startForResultAccountSignInActivity(getContext(), this, REQUEST_SIGN_IN);
                }
                break;
            case CONTACT:
                ContactsActivity.startContactsActivity(getActivity());
                break;
            case SYSTEM:
                SystemSettingsActivity.startSystemSettingsActivity(getActivity());
                break;
            case NOTIFICATION:
                NotificationActivity.startNotificationActivity(getActivity());
                break;
            case ABOUT_US:
                AboutUsActivity.startAboutActivity(getActivity());
                break;
            case CONTACT_US:
                ContactUsActivity.startContactUsActivity(getActivity());
                break;
        }


    }

    private void initActionBar() {
        bindingView.includeActionBar.ivBack.setVisibility(View.GONE);
        bindingView.includeActionBar.actionBarTitle.setText(R.string.fragment_title_me);
        bindingView.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        bindingView.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);

    }

    private void initData() {
        settingsList = new ArrayList<>();
        settingsList.add(new SettingsBean(NOTIFICATION, R.mipmap.ic_settings_message, getString(R.string.settings_message_center)));
        settingsList.add(new SettingsBean(CONTACT, R.mipmap.ic_settings_contacts, getString(R.string.settings_contacts)));
        settingsList.add(new SettingsBean(SYSTEM, R.mipmap.ic_settings, getString(R.string.settings_system)));
        settingsList.add(new SettingsBean(USER, R.mipmap.ic_settings_user, getString(R.string.settings_user)));
//        settingsList.add(new SettingsBean(HELP, R.mipmap.ic_settings_help, getString(R.string.settings_help)));
        settingsList.add(new SettingsBean(CONTACT_US, R.mipmap.ic_contact_us, getString(R.string.settings_contact_us)));
        settingsList.add(new SettingsBean(ABOUT_US, R.mipmap.ic_settings_about, getString(R.string.settings_about_us)));
    }

    private void initView() {
        settingsAdapter = new SettingsAdapter(getContext());

        bindingView.rlSettings.setAdapter(settingsAdapter);
        bindingView.rlSettings.setLayoutManager(new LinearLayoutManager(getContext()));
        settingsAdapter.setData(settingsList);


    }

    @Override
    public void onClick(View v) {
        if (getActivity() == null) return;
        switch (v.getId()) {
            case R.id.ll_manage_wallet:
                WalletManageActivity.startWalletMgActivity(getActivity());
                break;
            case R.id.ll_import_wallet:
                WalletImportActivity.startActivity(getActivity());
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    MyAccountActivity.startMyAccountActivity(getActivity());
                }
                break;
        }


    }

}
