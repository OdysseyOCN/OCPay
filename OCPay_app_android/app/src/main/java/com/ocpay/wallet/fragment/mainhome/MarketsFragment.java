package com.ocpay.wallet.fragment.mainhome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.FragmentMarketsBinding;
import com.ocpay.wallet.fragment.BaseFragment;

public class MarketsFragment extends BaseFragment<FragmentMarketsBinding> {

    @Override
    public int setContentView() {
        return R.layout.fragment_markets;
    }

    @Override
    public void loadData() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initActionBar();
    }

    private void initActionBar() {
        bindingView.includeActionBar.actionBarTitle.setText(R.string.fragment_title_markets);
        bindingView.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        bindingView.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        bindingView.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        bindingView.includeActionBar.ivBack.setVisibility(View.GONE);
        bindingView.includeActionBar.viewLine.setVisibility(View.VISIBLE);

    }

}
