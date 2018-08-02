package com.ocpay.wallet.fragment.keystoreexport;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.R;
import com.ocpay.wallet.activities.ExportKeystoreActivity;
import com.ocpay.wallet.databinding.FragmentExportKeystoreBinding;
import com.ocpay.wallet.fragment.BaseFragment;
import com.ocpay.wallet.widget.dialog.WarmDialog;

import static com.snow.commonlibrary.utils.ShareUtils.toClipboardData;

public class ExportKeystoreFragment extends BaseFragment<FragmentExportKeystoreBinding> implements View.OnClickListener {

    private String jsonKeystore;

    @Override
    public int setContentView() {
        return R.layout.fragment_export_keystore;
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
        bindingView.tvKeystore.setText(jsonKeystore);
        bindingView.tvKeystore.setOnClickListener(this);
        bindingView.tvCopyKeystore.setOnClickListener(this);
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
            case R.id.tv_keystore:
            case R.id.tv_copy_keystore:
                copyKeystore();
                break;
        }

    }


    private void copyKeystore() {
        String tip = getResources().getString(R.string.address_copied_to_clipboard);
        toClipboardData(MyApp.getContext(), "", bindingView.tvKeystore.getText().toString().trim(),tip);

    }
}
