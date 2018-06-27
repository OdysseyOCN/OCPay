package com.ocpay.wallet.greendao.manager;


import android.content.Context;

import com.ocpay.wallet.greendao.Contact;
import com.ocpay.wallet.greendao.TransactionRecord;
import com.ocpay.wallet.greendao.gen.ContactDao;
import com.ocpay.wallet.greendao.gen.TransactionRecordDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import static com.ocpay.wallet.greendao.manager.DaoManager.getInstance;


/**
 * Created by y on 2017/11/10.
 */

public class ContactDaoUtils {

    /**
     * @param foolBean
     * @param mContext
     * @return
     */
    public static boolean insertContact(Context mContext, Contact foolBean) {
        long result = getInstance(mContext).getDaoSeesion().insert(foolBean);
        return result > 0;
    }

    /**
     * 删
     *
     * @param foolBean
     */
    public static void dealContact(Context mContext, Contact foolBean) {
        getInstance(mContext).getDaoSeesion().delete(foolBean);
    }

    /**
     * 改
     *
     * @param foolBean
     */
    public static void updateContact(Context mContext, Contact foolBean) {
        getInstance(mContext).getDaoSeesion().update(foolBean);
    }

    /**
     * 查
     *
     * @return
     */
    public static List<Contact> sqlAll(Context mContext) {
        return getInstance(mContext).getDaoSeesion().loadAll(Contact.class);
    }


    public static List<Contact> sqlCustom(Context mContext, String walletAddress) {
        if (!walletAddress.startsWith("0x")) {
            walletAddress = "0x" + walletAddress;
        }
        Query<Contact> query = getInstance(mContext)
                .getInstance(mContext)
                .getDaoSeesion()
                .queryBuilder(Contact.class)
                .where(
                        ContactDao.Properties.WalletAddress.eq(walletAddress)
                )
                .build();


        query.setParameter(0, walletAddress);
        return query.list();
    }


}
