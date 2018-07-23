package com.ocpay.wallet.fragment.mainhome;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.R;
import com.ocpay.wallet.databinding.FragmentNearbyBinding;
import com.ocpay.wallet.fragment.BaseFragment;

public class NearbyFragment extends BaseFragment<FragmentNearbyBinding> {

    @Override
    public int setContentView() {
        return R.layout.fragment_nearby;
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
        bindingView.includeActionBar.actionBarTitle.setText(R.string.fragment_title_nearby);
        bindingView.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        bindingView.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        bindingView.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        bindingView.includeActionBar.ivBack.setVisibility(View.GONE);
        bindingView.includeActionBar.viewLine.setVisibility(View.VISIBLE);

    }
}
