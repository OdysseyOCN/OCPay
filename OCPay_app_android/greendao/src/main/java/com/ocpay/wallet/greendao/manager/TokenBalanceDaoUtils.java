package com.ocpay.wallet.greendao.manager;


import android.content.Context;

import com.ocpay.wallet.greendao.TokenBalance;
import com.ocpay.wallet.greendao.gen.TokenBalanceDao;

import org.greenrobot.greendao.query.Query;

import java.math.BigDecimal;
import java.util.List;

import static com.ocpay.wallet.greendao.manager.DaoManager.getInstance;


/**
 * Created by y on 2017/11/10.
 */

public class TokenBalanceDaoUtils {

    /**
     * 增
     * `
     *
     * @param foolBean
     * @return
     */
    public static boolean insertTokenBalance(TokenBalance foolBean, Context mContext) {
        long result = getInstance(mContext).getDaoSeesion().insert(foolBean);
        return result > 0;
    }

    /**
     * 删
     *
     * @param foolBean
     */
    public static void dealTokenBalance(Context mContext, TokenBalance foolBean) {
        getInstance(mContext).getDaoSeesion().delete(foolBean);
    }

    /**
     * 改
     *
     * @param foolBean
     */
    public static void update(Context mContext, TokenBalance foolBean) {
        TokenBalance tokenBalance = sqlTokenBalance(mContext, foolBean.getWalletAddress(), foolBean.getTokenName());
        if (tokenBalance == null) {
            insertTokenBalance(foolBean, mContext);
            return;
        }
        tokenBalance.setAmount(foolBean.getAmount());
        getInstance(mContext).getDaoSeesion().update(tokenBalance);
    }

    /**
     * 查
     *
     * @return
     */
    public static List<TokenBalance> sqlAll(Context mContext) {
        return getInstance(mContext).getDaoSeesion().loadAll(TokenBalance.class);
    }


    /**
     * @param mContext
     * @return
     */
    public static TokenBalance sqlTokenBalance(Context mContext, String walletAddress, String tokenName) {
        Query<TokenBalance> query = getInstance(mContext)
                .getInstance(mContext)
                .getDaoSeesion()
                .queryBuilder(TokenBalance.class)
                .where(TokenBalanceDao.Properties.WalletAddress.eq(walletAddress), TokenBalanceDao.Properties.TokenName.eq(tokenName))
                .build();


        query.setParameter(0, walletAddress);
        query.setParameter(1, tokenName);

        if (query.list() != null && query.list().size() >= 1) {
            return query.list().get(0);
        }
        return null;
    }


    /**
     * @param tokenBalance
     * @return
     */
    public static BigDecimal getTokenBalanceFromTokenBean(TokenBalance tokenBalance) {
        if (tokenBalance == null) return new BigDecimal(0);
        String amount = tokenBalance.getAmount();
        if (amount == null || amount.length() == 0) {
            return new BigDecimal(0);
        } else {
            try {
                return new BigDecimal(amount);
            } catch (Exception e) {
                return new BigDecimal(0);
            }
        }
    }

    /**
     * @param mContext
     * @param walletAddress
     * @param tokenName
     * @return
     */
    public static BigDecimal getTokenBalance(Context mContext, String walletAddress, String tokenName) {
        TokenBalance tokenBalance = sqlTokenBalance(mContext, walletAddress, tokenName);
        return getTokenBalanceFromTokenBean(tokenBalance);
    }


}
