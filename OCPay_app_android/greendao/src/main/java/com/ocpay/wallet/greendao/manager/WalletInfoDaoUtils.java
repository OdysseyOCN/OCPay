package com.ocpay.wallet.greendao.manager;


import android.content.Context;

import com.ocpay.wallet.greendao.WalletInfo;
import com.ocpay.wallet.greendao.gen.WalletInfoDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import static com.ocpay.wallet.greendao.manager.DaoManager.getInstance;


/**
 * Created by y on 2017/11/10.
 */

public class WalletInfoDaoUtils {

    /**
     * 增
     * `
     *
     * @param foolBean
     * @return
     */
    public static boolean insertWalletInfo(WalletInfo foolBean, Context mContext) {
        long result = getInstance(mContext).getDaoSeesion().insert(foolBean);
        return result > 0;
    }

    /**
     * 删
     *
     * @param foolBean
     */
    public static void dealWalletInfo(Context mContext, WalletInfo foolBean) {
        getInstance(mContext).getDaoSeesion().delete(foolBean);
    }

    /**
     * 改
     *
     * @param foolBean
     */
    public static void update(Context mContext, WalletInfo foolBean) {
        getInstance(mContext).getDaoSeesion().update(foolBean);
    }

    /**
     * 查
     *
     * @return
     */
    public static List<WalletInfo> sqlAll(Context mContext) {
        return getInstance(mContext).getDaoSeesion().loadAll(WalletInfo.class);
    }


//    public static List<WalletInfo> sqlBuilder(Context mContext, WalletInfo info) {
//
//        QueryBuilder<WalletInfo> walletInfoQueryBuilder = getInstance(mContext).getDaoSeesion().queryBuilder(WalletInfo.class);
//    }

    /**
     * @param mContext
     * @return
     */
    public static WalletInfo sqlByAddress(Context mContext, String walletAddress) {
        if (!walletAddress.startsWith("0x")) {
            walletAddress = "0x" + walletAddress;
        }
        Query<WalletInfo> query = getInstance(mContext)
                .getInstance(mContext)
                .getDaoSeesion()
                .queryBuilder(WalletInfo.class)
                .where(WalletInfoDao.Properties.WalletAddress.eq(walletAddress))
                .build();

        if (query.list() != null && query.list().size() >= 1) {
            return query.list().get(0);
        }
        return null;
    }


    /**
     * @param mContext
     * @return
     */
    public static WalletInfo sqlByWalletName(Context mContext, String walletName) {
        Query<WalletInfo> query = getInstance(mContext)
                .getInstance(mContext)
                .getDaoSeesion()
                .queryBuilder(WalletInfo.class)
                .where(WalletInfoDao.Properties.WalletName.eq(walletName))
                .build();

        if (query.list() != null && query.list().size() > 0) {
            return query.list().get(0);
        }
        return null;
    }
}
