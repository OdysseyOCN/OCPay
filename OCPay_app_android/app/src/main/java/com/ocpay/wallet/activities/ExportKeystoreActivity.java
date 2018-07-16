package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.ActivityImportWalletBinding;
import com.ocpay.wallet.fragment.fragmentadapter.MyFragmentAdapter;
import com.ocpay.wallet.fragment.keystoreexport.ExportKeystoreFragment;
import com.ocpay.wallet.fragment.keystoreexport.KeystoreQRFragment;

import java.util.ArrayList;
import java.util.List;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_KEYSTORE;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_MNEMONIC;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_PRIVATE_KEY;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_WATCH;
import static com.ocpay.wallet.Constans.WALLET.WALLET_KEYSTORE;
import static com.ocpay.wallet.activities.QRReaderActivity.QR_CODE_MODE_READER;

/**
 * Created by y on 2018/4/21.
 */

public class ExportKeystoreActivity extends BaseActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {


    private ActivityImportWalletBinding binding;
    private ViewPager vpContent;
    private String jsonKeystore;

    public static void startExportKeystoreActivity(Activity activity, String jsonKeystore) {
        Intent intent = new Intent(activity, ExportKeystoreActivity.class);
        intent.putExtra(WALLET_KEYSTORE, jsonKeystore);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_import_wallet);
        jsonKeystore = getIntent().getStringExtra(WALLET_KEYSTORE);
        initActionbar();
        initContentFragment();
        initTable();
    }

    private void initActionbar() {
        vpContent = binding.vpContent;
        binding.include.actionBarTitle.setText(R.string.fragment_title_export_keystore);
        binding.include.ivBack.setImageResource(R.mipmap.ic_back);
        binding.include.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.include.ivBack.setOnClickListener(this);
        binding.include.toolbarMenuIcon.setVisibility(View.GONE);
        binding.include.viewLine.setVisibility(View.VISIBLE);


    }

    private void initTable() {
        binding.tabImportWallet.setTabMode(TabLayout.MODE_FIXED);
        binding.tabImportWallet.setupWithViewPager(binding.vpContent);
    }


    private void initContentFragment() {
        //fragment
        ArrayList<Fragment> mFragmentList = new ArrayList<>();
        mFragmentList.add(new ExportKeystoreFragment());
        mFragmentList.add(new KeystoreQRFragment());


        //title
        List<String> titleList = new ArrayList<>();
        titleList.add("Keystore");
        titleList.add("QRCode");


        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager(), mFragmentList, titleList);
        vpContent.setAdapter(adapter);
        ViewGroup.LayoutParams layoutParams = vpContent.getLayoutParams();
        layoutParams.height = ViewPager.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewPager.LayoutParams.MATCH_PARENT;
        vpContent.setLayoutParams(layoutParams);
        vpContent.setOffscreenPageLimit(2);
        vpContent.addOnPageChangeListener(this);
        vpContent.setCurrentItem(0);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.toolbar_menu_icon:
                QRReaderActivity.startQRReaderActivity(ExportKeystoreActivity.this, getQRRequestId(), QR_CODE_MODE_READER);
                break;
        }
    }


    public int getQRRequestId() {
        int id = vpContent.getCurrentItem();
        return id == 0 ? ACTION_IMPORT_WALLET_MNEMONIC :
                id == 1 ? ACTION_IMPORT_WALLET_KEYSTORE :
                        id == 2 ? ACTION_IMPORT_WALLET_PRIVATE_KEY :
                                id == 3 ? ACTION_IMPORT_WALLET_WATCH : -1;

    }

    public String getJsonKeystore() {
        return jsonKeystore;
    }
}
