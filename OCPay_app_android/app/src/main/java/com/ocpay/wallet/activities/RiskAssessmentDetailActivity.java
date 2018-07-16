package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.ActivityRiskAssessmentDetailBinding;

public class RiskAssessmentDetailActivity extends BaseActivity implements View.OnClickListener {


    private ActivityRiskAssessmentDetailBinding binding;

    public static void startRiskAssessmentDetailActivity(Activity activity) {

        Intent intent = new Intent(activity, RiskAssessmentDetailActivity.class);
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(RiskAssessmentDetailActivity.this, R.layout.activity_risk_assessment_detail);
        initActionBar();


    }


    private void initActionBar() {
        binding.includeActionBar.actionBarTitle.setText(R.string.activity_title_create_your_wallet);
        binding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_close_black);
        binding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.toolbar_menu_icon:
                ContactsCreateActivity.startContactsCreateActivity(RiskAssessmentDetailActivity.this);
                break;
            case R.id.iv_back:
                finish();
                break;

        }

    }


}
