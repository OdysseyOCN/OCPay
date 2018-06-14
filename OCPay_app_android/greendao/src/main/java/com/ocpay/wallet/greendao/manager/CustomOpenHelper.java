package com.ocpay.wallet.greendao.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.github.yuweiguocn.library.greendao.MigrationHelper;
import com.ocpay.wallet.greendao.Contact;
import com.ocpay.wallet.greendao.TokenBalance;
import com.ocpay.wallet.greendao.gen.ContactDao;
import com.ocpay.wallet.greendao.gen.DaoMaster;
import com.ocpay.wallet.greendao.gen.NotificationBeanDao;
import com.ocpay.wallet.greendao.gen.TokenBalanceDao;
import com.ocpay.wallet.greendao.gen.TransactionRecordDao;
import com.ocpay.wallet.greendao.gen.WalletInfoDao;

import org.greenrobot.greendao.database.Database;

public class CustomOpenHelper extends DaoMaster.OpenHelper {

    public CustomOpenHelper(Context context, String name) {
        super(context, name);
    }

    public CustomOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
                @Override
                public void onCreateAllTables(Database db, boolean ifNotExists) {
                    DaoMaster.createAllTables(db, ifNotExists);
                }

                @Override
                public void onDropAllTables(Database db, boolean ifExists) {
                    DaoMaster.dropAllTables(db, ifExists);
                }
                //注意此处的参数StudentDao.class，很重要（一开始没注意，给坑了一下），它就是需要升级的table的Dao,
                //不填的话数据丢失，
                // 这里可以放多个Dao.class，也就是可以做到很多table的安全升级，Good~
            }, WalletInfoDao.class, TransactionRecordDao.class, ContactDao.class, NotificationBeanDao.class, TokenBalanceDao.class);
        }
    }
}