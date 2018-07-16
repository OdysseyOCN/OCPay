package com.ocpay.wallet.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.PointF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.dlazaro66.qrcodereaderview.QRCodeReaderView.OnQRCodeReadListener;
import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.QRCodeBean;
import com.ocpay.wallet.databinding.ActivityDecoderBinding;
import com.ocpay.wallet.databinding.ContentDecoderBinding;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.utils.CommonUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.utils.qr.QRCodeUtils;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.utils.RxAnimationTool;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import static com.ocpay.wallet.Constans.RXBUS.ACTION_CONTACTS_ADDRESS;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_KEYSTORE;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_MNEMONIC;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_PRIVATE_KEY;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_IMPORT_WALLET_WATCH;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_SEND_TRANSFER_ADDRESS;

public class QRReaderActivity extends BaseActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, OnQRCodeReadListener, View.OnClickListener {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;
    public static final int QR_CODE_MODE_PARSE = 1;
    public static final int QR_CODE_MODE_READER = 2;
    public static final String QR_REQEUST_CODE = "QR_REQEUST_CODE";
    public static final String QR_REQEUST_MODE = "QR_REQEUST_MODE";

    private ViewGroup mainLayout;

    private QRCodeReaderView qrCodeReaderView;
    private ActivityDecoderBinding dataBinding;
    private ContentDecoderBinding contentDecoderBinding;
    private boolean isRead;


    public static void startQRReaderActivity(Activity context, int requestCode, int requestMode) {
        Intent intent = new Intent(context, QRReaderActivity.class);
        intent.putExtra(QR_REQEUST_CODE, requestCode);
        intent.putExtra(QR_REQEUST_MODE, requestMode);
        context.startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_decoder);

        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_decoder);

        mainLayout = dataBinding.mainLayout;


        init();

    }

    private void init() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (qrCodeReaderView != null) {
            try {

                qrCodeReaderView.startCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (qrCodeReaderView != null) {
            try {

                qrCodeReaderView.stopCamera();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            initQRCodeReaderView();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        int requestCode = getIntent().getIntExtra(QR_REQEUST_CODE, -1);
        int requestMode = getIntent().getIntExtra(QR_REQEUST_MODE, -1);
        if (isRead) return;
        if (text.startsWith("http")) {
            isRead = true;
            WebViewActivity.loadUrl(QRReaderActivity.this, text);
            finish();
        }

        if (requestMode == QR_CODE_MODE_READER) {
            parseWalletAddress(text, requestCode);
        } else {
            try {
                QRCodeBean qrCodeBean = CommonUtils.json2Object(text.trim(), QRCodeBean.class);
                if (qrCodeBean == null) {
                    WarmDialog.showTip(this, "parse fail");
                    finish();
                    return;
                }
                QRCodeUtils.parseQRCode(this, qrCodeBean);

            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            isRead = true;
        }
        finish();
    }

    private void parseWalletAddress(String text, int requestCode) {
        isRead = true;
        switch (requestCode) {
            case ACTION_IMPORT_WALLET_WATCH:
            case ACTION_CONTACTS_ADDRESS:
            case ACTION_SEND_TRANSFER_ADDRESS:
                RxBus.getInstance().post(requestCode, QRCodeUtils.parseQRCodeToWalletAddress(text));
                break;
            case ACTION_IMPORT_WALLET_MNEMONIC:
            case ACTION_IMPORT_WALLET_KEYSTORE:
            case ACTION_IMPORT_WALLET_PRIVATE_KEY:
                RxBus.getInstance().post(requestCode, text);
                break;
            default:
                RxBus.getInstance().post(requestCode, text);
                break;
        }

    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(QRReaderActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private void initQRCodeReaderView() {
        contentDecoderBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.content_decoder, mainLayout, true);
        qrCodeReaderView = contentDecoderBinding.qrdecoderview;
        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        try {

            qrCodeReaderView.startCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }


        initActionBar();
        initScanerAnimation();
    }

    private void initActionBar() {
        contentDecoderBinding.includeActionBar.actionBarTitle.setText(R.string.qrcode_scan);
        contentDecoderBinding.includeActionBar.llToolbar.setBackgroundColor(getResources().getColor(R.color.white));
        contentDecoderBinding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        contentDecoderBinding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_back);
        contentDecoderBinding.includeActionBar.toolbarMenuIcon.setVisibility(View.GONE);
        contentDecoderBinding.includeActionBar.ivBack.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;

        }
    }


    private void initScanerAnimation() {
        RxAnimationTool.ScaleUpDowm(contentDecoderBinding.ivScanWeb);
    }

}