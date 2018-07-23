package com.ocpay.wallet.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;

import com.snow.commonlibrary.utils.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by y on 2018/6/18.
 */

public class BalanceEditText extends android.support.v7.widget.AppCompatEditText implements TextWatcher {

    private String lastAmount;
    private OnAfterEditListen afterEditListen;


    public interface OnAfterEditListen {
        void afterEdit();

    }

    public BalanceEditText(Context context) {
        this(context, null);
    }

    public BalanceEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BalanceEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, android.R.attr.editTextStyle);
        init();
    }

    private void init() {
        addTextChangedListener(this);

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.toString() == null || s.toString().length() == 0 || s.toString().equals(lastAmount))
            return;
        String amountWithoutD = s.toString().replace(",", "");

        if (StringUtil.isEmpty(amountWithoutD) || amountWithoutD.endsWith(".")) return;
        try {
            if (lastAmount != null && new BigDecimal(amountWithoutD).compareTo(new BigDecimal(lastAmount)) == 0)
            return;
        } catch (NumberFormatException e) {
            setText("0");
            return;
        }

        lastAmount = amountWithoutD;
        NumberFormat numberInstance = DecimalFormat.getNumberInstance();
        numberInstance.setMaximumFractionDigits(8);
        numberInstance.setRoundingMode(RoundingMode.DOWN);
        String currency = numberInstance.format(new BigDecimal(amountWithoutD));
        try {
            setText(currency);
            setSelection(currency.length());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (afterEditListen != null) afterEditListen.afterEdit();
    }


    public String getExactAmount() {
        Editable text = getText();
        if (text.toString() == null || text.toString().length() == 0)
            return "0";
        String amount = text.toString().replace(",", "");
        try {
            return new BigDecimal(amount).toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public void setAfterEditListen(OnAfterEditListen afterEditListen) {
        this.afterEditListen = afterEditListen;
    }
}
