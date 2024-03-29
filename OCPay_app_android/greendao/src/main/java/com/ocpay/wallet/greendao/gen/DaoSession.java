package com.ocpay.wallet.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.ocpay.wallet.greendao.Contact;
import com.ocpay.wallet.greendao.NotificationBean;
import com.ocpay.wallet.greendao.TokenBalance;
import com.ocpay.wallet.greendao.TransactionRecord;
import com.ocpay.wallet.greendao.WalletInfo;

import com.ocpay.wallet.greendao.gen.ContactDao;
import com.ocpay.wallet.greendao.gen.NotificationBeanDao;
import com.ocpay.wallet.greendao.gen.TokenBalanceDao;
import com.ocpay.wallet.greendao.gen.TransactionRecordDao;
import com.ocpay.wallet.greendao.gen.WalletInfoDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig contactDaoConfig;
    private final DaoConfig notificationBeanDaoConfig;
    private final DaoConfig tokenBalanceDaoConfig;
    private final DaoConfig transactionRecordDaoConfig;
    private final DaoConfig walletInfoDaoConfig;

    private final ContactDao contactDao;
    private final NotificationBeanDao notificationBeanDao;
    private final TokenBalanceDao tokenBalanceDao;
    private final TransactionRecordDao transactionRecordDao;
    private final WalletInfoDao walletInfoDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        contactDaoConfig = daoConfigMap.get(ContactDao.class).clone();
        contactDaoConfig.initIdentityScope(type);

        notificationBeanDaoConfig = daoConfigMap.get(NotificationBeanDao.class).clone();
        notificationBeanDaoConfig.initIdentityScope(type);

        tokenBalanceDaoConfig = daoConfigMap.get(TokenBalanceDao.class).clone();
        tokenBalanceDaoConfig.initIdentityScope(type);

        transactionRecordDaoConfig = daoConfigMap.get(TransactionRecordDao.class).clone();
        transactionRecordDaoConfig.initIdentityScope(type);

        walletInfoDaoConfig = daoConfigMap.get(WalletInfoDao.class).clone();
        walletInfoDaoConfig.initIdentityScope(type);

        contactDao = new ContactDao(contactDaoConfig, this);
        notificationBeanDao = new NotificationBeanDao(notificationBeanDaoConfig, this);
        tokenBalanceDao = new TokenBalanceDao(tokenBalanceDaoConfig, this);
        transactionRecordDao = new TransactionRecordDao(transactionRecordDaoConfig, this);
        walletInfoDao = new WalletInfoDao(walletInfoDaoConfig, this);

        registerDao(Contact.class, contactDao);
        registerDao(NotificationBean.class, notificationBeanDao);
        registerDao(TokenBalance.class, tokenBalanceDao);
        registerDao(TransactionRecord.class, transactionRecordDao);
        registerDao(WalletInfo.class, walletInfoDao);
    }
    
    public void clear() {
        contactDaoConfig.clearIdentityScope();
        notificationBeanDaoConfig.clearIdentityScope();
        tokenBalanceDaoConfig.clearIdentityScope();
        transactionRecordDaoConfig.clearIdentityScope();
        walletInfoDaoConfig.clearIdentityScope();
    }

    public ContactDao getContactDao() {
        return contactDao;
    }

    public NotificationBeanDao getNotificationBeanDao() {
        return notificationBeanDao;
    }

    public TokenBalanceDao getTokenBalanceDao() {
        return tokenBalanceDao;
    }

    public TransactionRecordDao getTransactionRecordDao() {
        return transactionRecordDao;
    }

    public WalletInfoDao getWalletInfoDao() {
        return walletInfoDao;
    }

}
