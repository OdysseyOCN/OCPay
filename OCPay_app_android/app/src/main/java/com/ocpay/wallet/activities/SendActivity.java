package com.ocpay.wallet.activities;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.Toast;

import com.ocpay.wallet.AnalyticsEvent;
import com.ocpay.wallet.Constans;
import com.ocpay.wallet.MyApp;
import com.ocpay.wallet.OCPWallet;
import com.ocpay.wallet.R;
import com.ocpay.wallet.bean.QRCodeBean;
import com.ocpay.wallet.bean.QRCodeTransaction;
import com.ocpay.wallet.bean.SignBean;
import com.ocpay.wallet.bean.ZipSignBean;
import com.ocpay.wallet.databinding.ActivitySendBinding;
import com.ocpay.wallet.greendao.TransactionRecord;
import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.manager.TokenBalanceDaoUtils;
import com.ocpay.wallet.greendao.manager.TransactionRecordDaoUtils;
import com.ocpay.wallet.http.client.EthScanHttpClientIml;
import com.ocpay.wallet.http.client.HttpClient;
import com.ocpay.wallet.http.rx.RxBus;
import com.ocpay.wallet.manager.AnalyticsManager;
import com.ocpay.wallet.utils.BalanceUtils;
import com.ocpay.wallet.utils.CommonUtils;
import com.ocpay.wallet.utils.TokenUtils;
import com.ocpay.wallet.utils.eth.OCPWalletUtils;
import com.ocpay.wallet.utils.eth.bean.OCPWalletFile;
import com.ocpay.wallet.utils.wallet.WalletStorage;
import com.ocpay.wallet.bean.response.EtherScanJsonrpcResponse;
import com.ocpay.wallet.utils.web3j.utils.RawTransactionUtils;
import com.ocpay.wallet.widget.dialog.PasswordConfirmDialog;
import com.ocpay.wallet.widget.dialog.QRCodeScanDialog;
import com.ocpay.wallet.widget.dialog.QRCodeSignDialog;
import com.ocpay.wallet.widget.dialog.TxDetailDialog;
import com.ocpay.wallet.widget.dialog.WarmDialog;
import com.snow.commonlibrary.log.MyLog;
import com.snow.commonlibrary.utils.RegularExpressionUtils;
import com.snow.commonlibrary.utils.StringUtil;
import com.snow.commonlibrary.widget.webview.WebViewActivity;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.protocol.core.methods.request.RawTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.ocpay.wallet.Constans.ERROR.WALLET_GET_NONCE_FAIL;
import static com.ocpay.wallet.Constans.ERROR.WALLET_INVALID_PASSWORD;
import static com.ocpay.wallet.Constans.ERROR.WALLET_NO_KEYSTORE;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_SELECT_CONTACT;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_SEND_ERROR;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_SEND_TRANSFER_ADDRESS;
import static com.ocpay.wallet.Constans.RXBUS.ACTION_TRANSACTION_SEND_TX;
import static com.ocpay.wallet.Constans.ETH_ADDRESS.OCN_TOKEN_ADDRESS;
import static com.ocpay.wallet.Constans.WALLET.TOKEN_BALANCE;
import static com.ocpay.wallet.Constans.WALLET.TOKEN_NAME;
import static com.ocpay.wallet.Constans.WALLET.TX_AMOUNT;
import static com.ocpay.wallet.Constans.WALLET.TX_TO;
import static com.ocpay.wallet.OCPWallet.wei2Gwei;
import static com.ocpay.wallet.activities.ContactsActivity.MODE_Select;
import static com.ocpay.wallet.activities.QRReaderActivity.QR_CODE_MODE_READER;
import static com.ocpay.wallet.utils.CommonUtils.Object2Json;
import static com.ocpay.wallet.utils.eth.OCPWalletUtils.getTransactionFee;
import static com.ocpay.wallet.widget.dialog.QRCodeScanDialog.TRANSACTION_MODE;
import static com.ocpay.wallet.widget.dialog.QRCodeSignDialog.MODE_SHOW_TRANSACTION_INFO;

public class SendActivity extends BaseActivity implements View.OnClickListener {

    private boolean isSimpleMode;

    private ActivitySendBinding binding;

    private String tokenName;

    private BigInteger gasLimit = OCPWallet.getMinGasLimit();

    private BigInteger gasPrice;

    private BigInteger customGas;
    private BigInteger customGasPrice;//gwei
    private String walletAddress;
    private BigInteger inputGasPrice;
    private BigInteger inputGasLimit;
    private BigDecimal tokenBalance;
    private boolean isWatching;
    private BigInteger nonce;

    private QRCodeTransaction qrCodeSignTransaction;
    private String inputData;


    //parse qrcode
    public static void startSendActivity(Activity activity, QRCodeBean bean) {
        String data = bean.getData();
        QRCodeTransaction qrCodeTransaction = CommonUtils.json2Object(data, QRCodeTransaction.class);
        if (qrCodeTransaction == null) {
            Toast.makeText(MyApp.getContext(), "parse fail", Toast.LENGTH_LONG).show();
            return;
        }

        if (StringUtil.isEmpty(qrCodeTransaction.getTokenName())) {
            Toast.makeText(MyApp.getContext(), "Token Name not null", Toast.LENGTH_LONG).show();
            return;
        }

        startSendActivity(activity, null, qrCodeTransaction.getTokenName(), qrCodeTransaction.getTransactionTo(), qrCodeTransaction.getAmount());

    }


    //stander start
    public static void startSendActivity(Activity activity, BigDecimal balance, String tokenName) {
        startSendActivity(activity, balance, tokenName, null, null);
    }


    public static void startSendActivity(Activity activity, BigDecimal balance, String tokenName, String transactionTo, String amount) {

        Intent intent = new Intent(activity, SendActivity.class);
        intent.putExtra(TOKEN_NAME, tokenName);
        if (balance == null) {
            balance = new BigDecimal(0);
        }
        if (!StringUtil.isEmpty(transactionTo)) {
            intent.putExtra(TX_TO, transactionTo);
        }
        if (!StringUtil.isEmpty(amount)) {
            intent.putExtra(TX_AMOUNT, amount);
        }
        intent.putExtra(TOKEN_BALANCE, balance.toString());
        activity.startActivity(intent);

    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(SendActivity.this, R.layout.activity_send);

        initArg();

        initActionBar();

        initListener();

        initView();

        initRxBus();

        initData();


    }

    private void initView() {
        WalletInfo currentWallet = OCPWallet.getCurrentWallet();
        int walletType = currentWallet.getWalletType();

        String txTo = getIntent().getStringExtra(TX_TO);
        if (!StringUtil.isEmpty(txTo) && txTo.startsWith("0x")) {
            binding.etSendWalletAddress.setText(txTo);
        }

        String txAmount = getIntent().getStringExtra(TX_AMOUNT);
        if (!StringUtil.isEmpty(txAmount)) {
            binding.tvTransferAmount.setText(txAmount);

        }


        int etColor = getResources().getColor(R.color.blue_43addc);
        isWatching = walletType == Constans.WALLET.WALLET_TYPE_WATCHING;
        if (isWatching) {
            binding.tvNextStep.setBackgroundColor(etColor);
            String tipOffline = getResources().getString(R.string.tip_offline_sign);
            int how = tipOffline.indexOf("How");
            SpannableString spannableString = new SpannableString(tipOffline);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getResources().getColor(R.color.color_text_main));
            spannableString.setSpan(foregroundColorSpan, how - 1, spannableString.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            binding.tvTipSign.setText(spannableString);
            binding.tvTipSign.setVisibility(View.VISIBLE);
        }
    }

    private void initArg() {
        tokenName = getIntent().getStringExtra(TOKEN_NAME);
        walletAddress = OCPWallet.getCurrentWallet().getWalletAddress();
        tokenBalance = TokenBalanceDaoUtils.getTokenBalance(MyApp.getContext(), walletAddress, tokenName);
        isSimpleMode = true;
    }

    private void initData() {
        binding.tvTipBalance.setText("Balance" + ":" + BalanceUtils.decimalFormat(new BigDecimal(tokenBalance.toString())));
    }

    private void initRxBus() {
        Observable<String> pwdObservable = RxBus.getInstance().toObservable(Constans.RXBUS.ACTION_TRANSACTION_CONFIRM_KEYSTORE, String.class);
        Observable<EtherScanJsonrpcResponse> nonceObservable = RxBus.getInstance()
                .toObservable(Constans.RXBUS.ACTION_TRANSACTION_GET_NONCE, EtherScanJsonrpcResponse.class);
        Disposable disposableSign = Observable
                .zip(pwdObservable, nonceObservable, new BiFunction<String, EtherScanJsonrpcResponse, ZipSignBean>() {
                    @Override
                    public ZipSignBean apply(String s, EtherScanJsonrpcResponse etherScanJsonrpcResponse) throws Exception {
                        /**  get nonce and pwd **/
                        return new ZipSignBean(s, etherScanJsonrpcResponse);
                    }
                })
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<ZipSignBean>() {
                    @Override
                    public void accept(ZipSignBean zipSignBean) throws Exception {
                        /**   keystore is exits **/
                        int status = 0;
                        OCPWalletFile ocpWallet = null;
                        ECKeyPair ecKeyPair = null;
                        try {
                            ocpWallet = WalletStorage.getInstance().getOCPWallet(walletAddress);
                        } catch (Exception e) {
                            e.printStackTrace();
                            status = WALLET_NO_KEYSTORE;
                        }
                        if (ocpWallet == null) {
                            status = WALLET_NO_KEYSTORE;
                        }
                        /**    get keypair **/
                        try {
                            ecKeyPair = Wallet.decrypt(zipSignBean.getPassword(), ocpWallet.getWalletFile());
                        } catch (CipherException e) {
                            e.printStackTrace();
                            status = WALLET_INVALID_PASSWORD;
                        }
                        if (ecKeyPair == null) status = WALLET_INVALID_PASSWORD;


                        /** nonce filter **/
                        try {
                            nonce = zipSignBean.getEtherScanJsonrpcResponse().getDecimalFromDex();
                        } catch (Exception e) {
                            e.printStackTrace();
                            status = WALLET_GET_NONCE_FAIL;
                        }
                        if (nonce == null) status = WALLET_GET_NONCE_FAIL;

                        if (status != 0) {
                            RxBus.getInstance().post(ACTION_SEND_ERROR, status);
                            return;
                        }

                        /**    sign    **/
                        SignBean signBean = OCPWalletUtils.getSignBean(ecKeyPair, binding.tvTransferAmount.getExactAmount().toString().trim(),
                                binding.etSendWalletAddress.getText().toString().trim(),
                                inputGasPrice.toString(),
                                inputGasLimit.toString(),
                                inputData,
                                getErc20Address(),
                                nonce
                        );
                        /**  send transaction **/
                        if (!signBean.getSign().startsWith("0x")) return;
                        EthScanHttpClientIml.sendTransaction(ACTION_TRANSACTION_SEND_TX, signBean.getSign());
                    }
                });
        addDisposable(disposableSign);


        /** post raw transaction **/
        Disposable txSendTx = RxBus.getInstance()
                .toObservable(ACTION_TRANSACTION_SEND_TX, EtherScanJsonrpcResponse.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<EtherScanJsonrpcResponse>() {
                    @Override
                    public void accept(EtherScanJsonrpcResponse response) throws Exception {
                        if (response == null || response.error != null) {
                            dismissLoading();
                            String errorMesg = (response == null || response.error == null) ? "error" : response.error.getMessage();
                            WarmDialog.showTip(SendActivity.this, errorMesg);
                            return;
                        }
                        if (response.result.startsWith("0x")) {
                            MyLog.i("transaction successful __" + response.result);
                            insertTransactionRecord(response);
                            TokenTransactionsActivity.startTokenTransactionActivity(SendActivity.this, tokenName, walletAddress, tokenBalance.toString());
                            finish();

                        }

                    }
                });
        addDisposable(txSendTx);

        Disposable statusResponse = RxBus.getInstance()
                .toObservable(ACTION_SEND_ERROR, Integer.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer s) throws Exception {
                        if (s == null) return;
                        switch (s) {
                            case WALLET_INVALID_PASSWORD:
                                dismissLoading();
                                WarmDialog.showTip(SendActivity.this, getString(R.string.wrong_password));
                                break;
                            case WALLET_NO_KEYSTORE:
                                dismissLoading();
                                WarmDialog.showTip(SendActivity.this, "Can't find  keystore");
                                break;
                            case WALLET_GET_NONCE_FAIL:
                                dismissLoading();
                                WarmDialog.showTip(SendActivity.this, "Get nonce failed !");
                                break;

                        }
                    }
                });
        addDisposable(statusResponse);


        //select contact

        Disposable contactSelect = RxBus.getInstance()
                .toObservable(ACTION_SELECT_CONTACT, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        binding.etSendWalletAddress.setText(s);
                    }
                });
        addDisposable(contactSelect);


    }

    private void insertTransactionRecord(EtherScanJsonrpcResponse response) {
        RawTransaction transaction = null;
        try {
            transaction = RawTransactionUtils.getTransaction(nonce, getErc20Address(), binding.tvTransferAmount.getExactAmount().toString().trim(), inputGasPrice.toString(), inputGasLimit.toString(), inputData, binding.etSendWalletAddress.getText().toString().trim());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (transaction != null) {
            TransactionRecord transactionRecord = new TransactionRecord();
            transactionRecord.setContractAddress(TokenUtils.getTokenAddress(tokenName));
            transactionRecord.setGasPrice(transaction.getGasPrice().toString());
            transactionRecord.setGasUsed(transaction.getGasLimit().toString());
            transactionRecord.setTo(transaction.getTo());
            transactionRecord.setInput(transaction.getData());
            transactionRecord.setBlockNumber("-");
            transactionRecord.setFrom(walletAddress);
            transactionRecord.setHash(response.result);
            transactionRecord.setValue(transaction.getValue().toString());
            transactionRecord.setTokenName(tokenName);
            transactionRecord.setIsPending(true);
            transactionRecord.setTimeStamp(System.currentTimeMillis() + "");
            transactionRecord.setNonce(nonce.toString());
            TransactionRecordDaoUtils.insertTxRecord(MyApp.getContext(), transactionRecord);
        }
    }

    private void initActionBar() {
        String title = tokenName + " " + getResources().getString(R.string.activity_transfer);
        binding.includeActionBar.actionBarTitle.setText(title);
        binding.includeActionBar.toolbarMenuIcon.setImageResource(R.mipmap.ic_bar_sacn);
        binding.includeActionBar.toolbarMenuIcon.setOnClickListener(this);
        binding.includeActionBar.ivBack.setImageResource(R.mipmap.ic_close_black);
        binding.includeActionBar.ivBack.setOnClickListener(this);
        binding.includeActionBar.actionBarTitle.setTextColor(getResources().getColor(R.color.color_text_main));
        binding.includeActionBar.viewLine.setVisibility(View.VISIBLE);

    }

    private void initListener() {
        binding.tvNextStep.setOnClickListener(this);
        binding.tvHowToDesignParameters.setOnClickListener(this);
        binding.tvTipSign.setOnClickListener(this);
        binding.ivContact.setOnClickListener(this);
        binding.ivHelp.setOnClickListener(this);
        binding.btnMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSimpleMode = !isSimpleMode;
                int simpleVisible = isSimpleMode ? View.VISIBLE : View.INVISIBLE;
                int customVisible = isSimpleMode ? View.INVISIBLE : View.VISIBLE;
                String modeChangeTip = isSimpleMode ? getString(R.string.action_enable_mode) : getString(R.string.action_turn_off_mode);
                binding.llSimpleInput.setVisibility(simpleVisible);
                binding.llCustomInput.setVisibility(customVisible);
                binding.tvHowToDesignParameters.setVisibility(customVisible);
                binding.btnMode.setText(modeChangeTip);

            }
        });
        Disposable disposable = RxBus.getInstance()
                .toObservable(ACTION_SEND_TRANSFER_ADDRESS, String.class)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        if (!s.startsWith("0x")) {
                            Toast.makeText(SendActivity.this, "Invalid Data", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        binding.etSendWalletAddress.setText(s);
                    }
                });
        addDisposable(disposable);


        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progressChanged(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.seekBar.setProgress(10);
        progressChanged(10);
    }

    /**
     * update  gasPrice ; seekBar only change gasPrice
     *
     * @param i
     */
    private void progressChanged(int i) {
        MyLog.i("process" + i);
        if (i == 0) i = 10;
        gasPrice = new BigInteger(i + "").multiply(OCPWallet.getMinGasPrice()).divide(new BigInteger("10"));
        BigDecimal transactionFee = getTransactionFee(new BigDecimal(gasPrice), new BigDecimal(gasLimit));
        binding.tvSeekBarGasFee.setText(transactionFee.toString() + " ETH");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.toolbar_menu_icon:
                QRReaderActivity.startQRReaderActivity(SendActivity.this, ACTION_SEND_TRANSFER_ADDRESS, QR_CODE_MODE_READER);
                break;
            case R.id.tv_next_step:
                boolean isInvalidData = checkData();
                if (isInvalidData) {
                    if (!isWatching) {
                        showTxDetailDialog();
                    } else {
                        showQRCodeDialog();
                    }
                }
                break;

            case R.id.tv_how_to_design_parameters:
                WebViewActivity.loadUrl(this, Constans.H5.Howtouseadvancetransfermode, null);
                break;

            case R.id.tv_tip_sign:
                WebViewActivity.loadUrl(this, Constans.H5.Howdoesofflinesigningwork, null);
                break;

            case R.id.iv_help:
                WebViewActivity.loadUrl(this, Constans.H5.WhatistheGasinEthereum, null);
                break;
            case R.id.iv_contact:
                ContactsActivity.startContactsActivity(this, MODE_Select);
                break;
        }
    }


    private void showQRCodeDialog() {


        String sendFrom = OCPWallet.getCurrentWallet().getWalletAddress();
        String sendTo = binding.etSendWalletAddress.getText().toString().trim();
        String amount = binding.tvTransferAmount.getExactAmount().toString().trim();
        qrCodeSignTransaction = new QRCodeTransaction(
                tokenName,
                sendFrom,
                sendTo,
                inputGasLimit.toString(),
                isSimpleMode ? wei2Gwei(new BigInteger(gasPrice.toString())).toString() : customGasPrice.toString(),
                TokenUtils.getTokenAddress(tokenName),
                amount
        );


        HttpClient.Builder
                .getEthScanServer()
                .getNonce(walletAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<EtherScanJsonrpcResponse>() {

                            @Override
                            public void onSubscribe(Disposable d) {
                                SendActivity.this.showLoading(false);
                            }

                            @Override
                            public void onNext(EtherScanJsonrpcResponse response) {
                                SendActivity.this.dismissLoading();
                                if (response == null || StringUtil.isEmpty(response.result)) {
                                    WarmDialog.showTip(SendActivity.this, "Get Nonce fail");
                                    return;
                                }
                                showTransactionQRCode(response);
                            }

                            @Override
                            public void onError(Throwable e) {
                                SendActivity.this.dismissLoading();
                                WarmDialog.showTip(SendActivity.this, e.getMessage());
                            }

                            @Override
                            public void onComplete() {

                            }
                        }
                );


    }

    /**
     * @param response
     */
    private void showTransactionQRCode(EtherScanJsonrpcResponse response) {
        nonce = response.getDecimalFromDex();
        qrCodeSignTransaction.setNonce(nonce.toString());
        QRCodeSignDialog dialog = QRCodeSignDialog.getInstance(this);

        dialog.show();
        dialog.changeType(MODE_SHOW_TRANSACTION_INFO);
        QRCodeBean qrCodeBean = new QRCodeBean(walletAddress, Constans.QRCODE.MODE_SHOW_ETH_TX_SIGN, "");
        qrCodeBean.setTransaction(qrCodeSignTransaction);

        dialog.setListener(new QRCodeSignDialog.OnNextListener() {
            @Override
            public void onNext() {
                AnalyticsManager.getInstance().sendEvent(AnalyticsEvent.ETHTransferoberserved_button_Nextstep, tokenName);
                getTransactionSign();
            }
        });
        dialog.setData(Object2Json(qrCodeBean));
    }

    /**
     *
     */
    private void getTransactionSign() {
        QRCodeScanDialog qrCodeScanDialog = QRCodeScanDialog.getInstance(SendActivity.this);

        qrCodeScanDialog.setListener(new QRCodeScanDialog.OnNextListener() {
            @Override
            public void onNext(String code) {
                if (StringUtil.isEmpty(code) || !code.startsWith("0x")) {
                    WarmDialog.showTip(SendActivity.this, "Wrong Sign");
                    return;
                }
                SendActivity.this.showLoading(false);
                EthScanHttpClientIml.sendTransaction(ACTION_TRANSACTION_SEND_TX, code);
            }
        });

        qrCodeScanDialog.show();
        qrCodeScanDialog.changeMode(TRANSACTION_MODE);


    }

    private void showTxDetailDialog() {

        TxDetailDialog txDetailDialog = TxDetailDialog.getInstance(SendActivity.this);
        txDetailDialog.setListener(new TxDetailDialog.OnNextListener() {
            @Override
            public void onNext() {
                showPwdDialog();
            }
        });
        Window win = txDetailDialog.getWindow();
        win.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        txDetailDialog.show();
        txDetailDialog.setData(getString(R.string.dialog_tx_detail_transfer),
                OCPWallet.getCurrentWallet().getWalletAddress(),
                binding.etSendWalletAddress.getText().toString().trim(),
                inputGasPrice,
                inputGasLimit,
                binding.tvTransferAmount.getExactAmount().toString().trim(),
                tokenName
        );


    }


    private void showPwdDialog() {
        PasswordConfirmDialog dialog = PasswordConfirmDialog.getInstance(SendActivity.this);
        dialog.setListener(new PasswordConfirmDialog.ConfirmListener() {
            @Override
            public void onConfirm(String pwd) {
                showLoading(false);
                setTip(getString(R.string.loading_tip_trading));

                RxBus.getInstance().post(Constans.RXBUS.ACTION_TRANSACTION_CONFIRM_KEYSTORE, pwd);
                EthScanHttpClientIml.getAddressNonce(Constans.RXBUS.ACTION_TRANSACTION_GET_NONCE, walletAddress);

            }
        });

        dialog.show();
    }

    private boolean checkData() {


        /** valid address **/
        boolean addressValid = RegularExpressionUtils.valid(binding.etSendWalletAddress.getText().toString().trim(), Constans.REGULAR.REGULAR_ETH_ADDRESS);
        if (!addressValid) {
            Toast.makeText(SendActivity.this, "Invalid Address", Toast.LENGTH_LONG).show();
            return false;
        }
        /** send same address **/
        if (OCPWallet.getCurrentWallet().getWalletAddress().equals(binding.etSendWalletAddress.getText().toString().trim())) {
            Toast.makeText(SendActivity.this, "Invalid Address", Toast.LENGTH_LONG).show();
            return false;
        }

        /** date update  && compare balance **/
        if (StringUtil.isEmpty(binding.tvTransferAmount.getExactAmount().toString()) || Double.valueOf(binding.tvTransferAmount.getExactAmount().toString()) < 0) {
            Toast.makeText(SendActivity.this, "Invalid  input", Toast.LENGTH_LONG).show();
            return false;
        }

        Double amount = Double.valueOf(binding.tvTransferAmount.getExactAmount().toString());
        if (!isSimpleMode) {
            customGasPrice = new BigInteger(binding.etCustomGasPrice.getText().toString());
            customGas = new BigInteger(binding.etCustomGas.getText().toString());
        }
        inputGasPrice = isSimpleMode ? gasPrice : OCPWallet.gwei2Wei(customGasPrice);
        inputGasLimit = isSimpleMode ? gasLimit : customGas;
        BigDecimal fee = OCPWalletUtils.getTransactionFee(new BigDecimal(inputGasPrice), new BigDecimal(inputGasLimit));
        if (tokenBalance.compareTo(fee.add(new BigDecimal(amount))) < 0) {
            Toast.makeText(SendActivity.this, "Not Enough Amount", Toast.LENGTH_LONG).show();
            return false;
        }

        /**    data hex **/
        boolean validHex = RegularExpressionUtils.valid(binding.etCustomHex.getText().toString().trim(), Constans.REGULAR.REGULAR_HEX);
        if (!isSimpleMode && !validHex && !StringUtil.isEmpty(binding.etCustomHex.getText().toString())) {
            Toast.makeText(SendActivity.this, "input error  eg: 0x123" + OCPWallet.getMinGasLimit().toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        inputData = isSimpleMode ? "" : (binding.etCustomHex.getText().toString() != null) ? binding.etCustomHex.getText().toString().trim() : "";

        /** check arg **/
        if (!isSimpleMode) {
            /** min gasPrice **/
            BigInteger customGasWei = OCPWallet.gwei2Wei(customGasPrice);
            if (customGasPrice == null || customGasWei.longValue() < OCPWallet.getMinGasPrice().longValue()) {
                BigDecimal bigDecimal = wei2Gwei(OCPWallet.getMinGasPrice());
                Toast.makeText(SendActivity.this, "gas price is to low,eg: " + bigDecimal.toString(), Toast.LENGTH_LONG).show();
                return false;
            }
            /** min gasLimit **/
            if (customGas == null || customGas.longValue() < OCPWallet.getMinGasLimit().longValue()) {
                Toast.makeText(SendActivity.this, "gas limit is to low,eg: " + OCPWallet.getMinGasLimit().toString(), Toast.LENGTH_LONG).show();
                return false;
            }

        }
        return true;
    }


    public String getErc20Address() {
        if ("ETH".equals(tokenName)) {
            return "";
        }
        return OCN_TOKEN_ADDRESS;
    }

    @Override
    protected void onDestroy() {
        PasswordConfirmDialog.getInstance(SendActivity.this).destroy();
        WarmDialog.getInstance(SendActivity.this).destroy();
        TxDetailDialog.getInstance(SendActivity.this).destroy();
        super.onDestroy();
    }


}
