package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ocpay.wallet.R;
import com.ocpay.wallet.adapter.AreaCodeAdapter;
import com.ocpay.wallet.bean.CountryData;
import com.ocpay.wallet.databinding.ActivityCountrySelectBinding;
import com.ocpay.wallet.utils.CountryUtils;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.recycleview.BaseAdapter;

import java.util.ArrayList;
import java.util.Locale;

import static com.ocpay.wallet.utils.CountryUtils.COUNTRY_INDEX;

public class CountryCodeSelectedActivity extends BaseActivity implements View.OnClickListener {

    private ActivityCountrySelectBinding binding;
    private static final String Action_Request = "wallet_create_request";
    private int requestId;
    private AreaCodeAdapter areaCodeAdapter;

    public static void startContryCodeSelectedActivity(Activity activity, int requestCode) {
        if (activity == null) return;
        Intent intent = new Intent(activity, CountryCodeSelectedActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_country_select);
        requestId = getIntent().getIntExtra(Action_Request, -1);
        initActionBar();
        initView();
        initListener();

    }

    private void initView() {
        areaCodeAdapter = new AreaCodeAdapter(this);
        binding.rlCountry.setLayoutManager(new LinearLayoutManager(this));
        binding.rlCountry.setAdapter(areaCodeAdapter);
        ArrayList<CountryData> countryBean = CountryUtils.getCountryBean(this);
        Locale.getDefault();
        areaCodeAdapter.setData(countryBean);
        areaCodeAdapter.setSelectedIndex(COUNTRY_INDEX);

    }


    private void initListener() {
        areaCodeAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView.Adapter adapter, Object data, int position) {
                COUNTRY_INDEX = position;
                setResult(RESULT_OK);
                finish();
            }
        });

    }

    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(getResources().getString(R.string.activity_title_select_country));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_close_black);
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
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
        WarmDialog.getInstance(CountryCodeSelectedActivity.this).destroy();
        super.onDestroy();
    }
}
