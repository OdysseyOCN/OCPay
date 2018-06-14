package com.ocpay.wallet.greendao.manager;


import android.content.Context;

import com.ocpay.wallet.greendao.NotificationBean;
import com.ocpay.wallet.greendao.gen.NotificationBeanDao;

import org.greenrobot.greendao.query.Query;

import java.util.List;

import static com.ocpay.wallet.greendao.manager.DaoManager.getInstance;


/**
 * Created by y on 2017/11/10.
 */

public class NotificationBeanDaoUtils {

    /**
     * 增
     * `
     *
     * @param foolBean
     * @return
     */
    public static boolean insertNotificationBean(Context mContext, NotificationBean foolBean) {
        long result = getInstance(mContext).getDaoSeesion().insert(foolBean);
        return result > 0;
    }

    /**
     * 删
     *
     * @param foolBean
     */
    public static void dealNotificationBean(Context mContext, NotificationBean foolBean) {
        getInstance(mContext).getDaoSeesion().delete(foolBean);
    }

    /**
     * 改
     *
     * @param foolBean
     */
    public static void update(Context mContext, NotificationBean foolBean) {
        getInstance(mContext).getDaoSeesion().update(foolBean);
    }

    /**
     * 查
     *
     * @return
     */
    public static List<NotificationBean> sqlAll(Context mContext) {

        return getInstance(mContext).getDaoSeesion().loadAll(NotificationBean.class);
    }


    public static List<NotificationBean> sqlAllDesc(Context mContext) {
        Query<NotificationBean> query = getInstance(mContext)
                .getInstance(mContext)
                .getDaoSeesion()
                .queryBuilder(NotificationBean.class)
                .orderDesc(NotificationBeanDao.Properties.Id)
                .build();
        return query.list();
    }

    /**
     * @param mContext
     * @return
     */
    public static List<NotificationBean> sqlUnRead(Context mContext, Boolean isRead) {
        Query<NotificationBean> query = getInstance(mContext)
                .getInstance(mContext)
                .getDaoSeesion()
                .queryBuilder(NotificationBean.class)
                .where(NotificationBeanDao.Properties.Read.eq(isRead)
                )
                .build();
        query.setParameter(0, isRead);
        return query.list();
    }
}
