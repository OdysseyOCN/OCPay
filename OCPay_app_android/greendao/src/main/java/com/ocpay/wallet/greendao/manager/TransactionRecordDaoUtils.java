package com.ocpay.wallet.greendao.manager;


import android.content.Context;

import com.ocpay.wallet.greendao.TransactionRecord;
import com.ocpay.wallet.greendao.gen.TransactionRecordDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import static com.ocpay.wallet.greendao.manager.DaoManager.getInstance;


/**
 * Created by y on 2017/11/10.
 */

public class TransactionRecordDaoUtils {

    /**
     * @param foolBean
     * @param mContext
     * @return
     */
    public static boolean insertTxRecord(Context mContext, TransactionRecord foolBean) {
        long result = getInstance(mContext).getDaoSeesion().insert(foolBean);
        return result > 0;
    }

    /**
     * 删
     *
     * @param foolBean
     */
    public static void dealTxRecord(Context mContext, TransactionRecord foolBean) {
        getInstance(mContext).getDaoSeesion().delete(foolBean);
    }

    /**
     * 改
     *
     * @param foolBean
     */
    public static void updateTxRecord(Context mContext, TransactionRecord foolBean) {
        getInstance(mContext).getDaoSeesion().update(foolBean);
    }

    /**
     * 查
     *
     * @return
     */
    public static List<TransactionRecord> sqlAll(Context mContext) {
        return getInstance(mContext).getDaoSeesion().loadAll(TransactionRecord.class);
    }


    /**
     * @param mContext
     * @param walletAddress
     * @param tokenName
     * @param isPending
     * @return
     */
    public static List<TransactionRecord> sqlCustom(Context mContext, String walletAddress, String tokenName, Boolean isPending) {
        if (!walletAddress.startsWith("0x")) {
            walletAddress = "0x" + walletAddress;
        }
        Query<TransactionRecord> query = getInstance(mContext)
                .getInstance(mContext)
                .getDaoSeesion()
                .queryBuilder(TransactionRecord.class)
                .where(TransactionRecordDao.Properties.From.eq(walletAddress),
                        TransactionRecordDao.Properties.TokenName.eq(tokenName),
                        TransactionRecordDao.Properties.IsPending.eq(isPending)
                )
                .build();


        query.setParameter(0, walletAddress);
        query.setParameter(1, tokenName);
        query.setParameter(2, isPending);
        return query.list();
    }

    public static TransactionRecord sqlByTxHash(Context mContext, String txHash) {
        if (txHash == null || "".equals(txHash)) return null;

        Query<TransactionRecord> query = getInstance(mContext)
                .getInstance(mContext)
                .getDaoSeesion()
                .queryBuilder(TransactionRecord.class)
                .where(TransactionRecordDao.Properties.Hash.eq(txHash))
                .build();


        query.setParameter(0, txHash);
        List<TransactionRecord> list = query.list();
        if (list == null || list.size() <= 0) return null;
        return list.get(0);
    }


}
