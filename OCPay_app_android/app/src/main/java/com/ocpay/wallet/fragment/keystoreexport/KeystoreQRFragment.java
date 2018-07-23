package com.ocpay.wallet.fragment.keystoreexport;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.ExportKeystoreActivity;
import com.ocpay.wallet.databinding.FragmentKeystoreQrcodeBinding;
import com.ocpay.wallet.fragment.BaseFragment;
import com.ocpay.wallet.utils.qr.QRCodeUtils;
import com.ocpay.wallet.widget.dialog.WarmDialog;

public class KeystoreQRFragment extends BaseFragment<FragmentKeystoreQrcodeBinding> implements View.OnClickListener {

    private String jsonKeystore;

    @Override
    public int setContentView() {
        return R.layout.fragment_keystore_qrcode;
    }

    @Override
    public void loadData() {

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initRXBUS();
    }

    private void initView() {

        if (getActivity() instanceof ExportKeystoreActivity) {
            jsonKeystore = ((ExportKeystoreActivity) getActivity()).getJsonKeystore();
        }
        if (jsonKeystore == null) return;
        updateQRCode(jsonKeystore);
        bindingView.tvLockCover.setOnClickListener(this);


    }


    private void initRXBUS() {

    }


    @Override
    public void onDestroyView() {
        WarmDialog.getInstance(getActivity()).destroy();
        super.onDestroyView();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_lock_cover:
                bindingView.rlCodeCover.setVisibility(View.GONE);
                break;
        }

    }


    private void updateQRCode(String code) {
        QRCodeUtils.updateQRCode(bindingView.qrCode, 250, code);
    }
}
