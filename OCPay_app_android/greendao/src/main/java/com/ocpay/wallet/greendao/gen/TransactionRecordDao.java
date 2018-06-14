package com.ocpay.wallet.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ocpay.wallet.greendao.TransactionRecord;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "TRANSACTION_RECORD".
*/
public class TransactionRecordDao extends AbstractDao<TransactionRecord, Long> {

    public static final String TABLENAME = "TRANSACTION_RECORD";

    /**
     * Properties of entity TransactionRecord.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property BlockNumber = new Property(1, String.class, "blockNumber", false, "BLOCK_NUMBER");
        public final static Property TimeStamp = new Property(2, String.class, "timeStamp", false, "TIME_STAMP");
        public final static Property Hash = new Property(3, String.class, "hash", false, "HASH");
        public final static Property Nonce = new Property(4, String.class, "nonce", false, "NONCE");
        public final static Property BlockHash = new Property(5, String.class, "blockHash", false, "BLOCK_HASH");
        public final static Property TransactionIndex = new Property(6, String.class, "transactionIndex", false, "TRANSACTION_INDEX");
        public final static Property From = new Property(7, String.class, "from", false, "FROM");
        public final static Property To = new Property(8, String.class, "to", false, "TO");
        public final static Property Value = new Property(9, String.class, "value", false, "VALUE");
        public final static Property Gas = new Property(10, String.class, "gas", false, "GAS");
        public final static Property GasPrice = new Property(11, String.class, "gasPrice", false, "GAS_PRICE");
        public final static Property IsError = new Property(12, String.class, "isError", false, "IS_ERROR");
        public final static Property Txreceipt_status = new Property(13, String.class, "txreceipt_status", false, "TXRECEIPT_STATUS");
        public final static Property Input = new Property(14, String.class, "input", false, "INPUT");
        public final static Property ContractAddress = new Property(15, String.class, "contractAddress", false, "CONTRACT_ADDRESS");
        public final static Property CumulativeGasUsed = new Property(16, String.class, "cumulativeGasUsed", false, "CUMULATIVE_GAS_USED");
        public final static Property GasUsed = new Property(17, String.class, "gasUsed", false, "GAS_USED");
        public final static Property Confirmations = new Property(18, String.class, "confirmations", false, "CONFIRMATIONS");
        public final static Property IsPending = new Property(19, boolean.class, "isPending", false, "IS_PENDING");
        public final static Property TokenName = new Property(20, String.class, "tokenName", false, "TOKEN_NAME");
    }


    public TransactionRecordDao(DaoConfig config) {
        super(config);
    }
    
    public TransactionRecordDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"TRANSACTION_RECORD\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"BLOCK_NUMBER\" TEXT," + // 1: blockNumber
                "\"TIME_STAMP\" TEXT," + // 2: timeStamp
                "\"HASH\" TEXT," + // 3: hash
                "\"NONCE\" TEXT," + // 4: nonce
                "\"BLOCK_HASH\" TEXT," + // 5: blockHash
                "\"TRANSACTION_INDEX\" TEXT," + // 6: transactionIndex
                "\"FROM\" TEXT," + // 7: from
                "\"TO\" TEXT," + // 8: to
                "\"VALUE\" TEXT," + // 9: value
                "\"GAS\" TEXT," + // 10: gas
                "\"GAS_PRICE\" TEXT," + // 11: gasPrice
                "\"IS_ERROR\" TEXT," + // 12: isError
                "\"TXRECEIPT_STATUS\" TEXT," + // 13: txreceipt_status
                "\"INPUT\" TEXT," + // 14: input
                "\"CONTRACT_ADDRESS\" TEXT," + // 15: contractAddress
                "\"CUMULATIVE_GAS_USED\" TEXT," + // 16: cumulativeGasUsed
                "\"GAS_USED\" TEXT," + // 17: gasUsed
                "\"CONFIRMATIONS\" TEXT," + // 18: confirmations
                "\"IS_PENDING\" INTEGER NOT NULL ," + // 19: isPending
                "\"TOKEN_NAME\" TEXT);"); // 20: tokenName
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"TRANSACTION_RECORD\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, TransactionRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String blockNumber = entity.getBlockNumber();
        if (blockNumber != null) {
            stmt.bindString(2, blockNumber);
        }
 
        String timeStamp = entity.getTimeStamp();
        if (timeStamp != null) {
            stmt.bindString(3, timeStamp);
        }
 
        String hash = entity.getHash();
        if (hash != null) {
            stmt.bindString(4, hash);
        }
 
        String nonce = entity.getNonce();
        if (nonce != null) {
            stmt.bindString(5, nonce);
        }
 
        String blockHash = entity.getBlockHash();
        if (blockHash != null) {
            stmt.bindString(6, blockHash);
        }
 
        String transactionIndex = entity.getTransactionIndex();
        if (transactionIndex != null) {
            stmt.bindString(7, transactionIndex);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(8, from);
        }
 
        String to = entity.getTo();
        if (to != null) {
            stmt.bindString(9, to);
        }
 
        String value = entity.getValue();
        if (value != null) {
            stmt.bindString(10, value);
        }
 
        String gas = entity.getGas();
        if (gas != null) {
            stmt.bindString(11, gas);
        }
 
        String gasPrice = entity.getGasPrice();
        if (gasPrice != null) {
            stmt.bindString(12, gasPrice);
        }
 
        String isError = entity.getIsError();
        if (isError != null) {
            stmt.bindString(13, isError);
        }
 
        String txreceipt_status = entity.getTxreceipt_status();
        if (txreceipt_status != null) {
            stmt.bindString(14, txreceipt_status);
        }
 
        String input = entity.getInput();
        if (input != null) {
            stmt.bindString(15, input);
        }
 
        String contractAddress = entity.getContractAddress();
        if (contractAddress != null) {
            stmt.bindString(16, contractAddress);
        }
 
        String cumulativeGasUsed = entity.getCumulativeGasUsed();
        if (cumulativeGasUsed != null) {
            stmt.bindString(17, cumulativeGasUsed);
        }
 
        String gasUsed = entity.getGasUsed();
        if (gasUsed != null) {
            stmt.bindString(18, gasUsed);
        }
 
        String confirmations = entity.getConfirmations();
        if (confirmations != null) {
            stmt.bindString(19, confirmations);
        }
        stmt.bindLong(20, entity.getIsPending() ? 1L: 0L);
 
        String tokenName = entity.getTokenName();
        if (tokenName != null) {
            stmt.bindString(21, tokenName);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, TransactionRecord entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String blockNumber = entity.getBlockNumber();
        if (blockNumber != null) {
            stmt.bindString(2, blockNumber);
        }
 
        String timeStamp = entity.getTimeStamp();
        if (timeStamp != null) {
            stmt.bindString(3, timeStamp);
        }
 
        String hash = entity.getHash();
        if (hash != null) {
            stmt.bindString(4, hash);
        }
 
        String nonce = entity.getNonce();
        if (nonce != null) {
            stmt.bindString(5, nonce);
        }
 
        String blockHash = entity.getBlockHash();
        if (blockHash != null) {
            stmt.bindString(6, blockHash);
        }
 
        String transactionIndex = entity.getTransactionIndex();
        if (transactionIndex != null) {
            stmt.bindString(7, transactionIndex);
        }
 
        String from = entity.getFrom();
        if (from != null) {
            stmt.bindString(8, from);
        }
 
        String to = entity.getTo();
        if (to != null) {
            stmt.bindString(9, to);
        }
 
        String value = entity.getValue();
        if (value != null) {
            stmt.bindString(10, value);
        }
 
        String gas = entity.getGas();
        if (gas != null) {
            stmt.bindString(11, gas);
        }
 
        String gasPrice = entity.getGasPrice();
        if (gasPrice != null) {
            stmt.bindString(12, gasPrice);
        }
 
        String isError = entity.getIsError();
        if (isError != null) {
            stmt.bindString(13, isError);
        }
 
        String txreceipt_status = entity.getTxreceipt_status();
        if (txreceipt_status != null) {
            stmt.bindString(14, txreceipt_status);
        }
 
        String input = entity.getInput();
        if (input != null) {
            stmt.bindString(15, input);
        }
 
        String contractAddress = entity.getContractAddress();
        if (contractAddress != null) {
            stmt.bindString(16, contractAddress);
        }
 
        String cumulativeGasUsed = entity.getCumulativeGasUsed();
        if (cumulativeGasUsed != null) {
            stmt.bindString(17, cumulativeGasUsed);
        }
 
        String gasUsed = entity.getGasUsed();
        if (gasUsed != null) {
            stmt.bindString(18, gasUsed);
        }
 
        String confirmations = entity.getConfirmations();
        if (confirmations != null) {
            stmt.bindString(19, confirmations);
        }
        stmt.bindLong(20, entity.getIsPending() ? 1L: 0L);
 
        String tokenName = entity.getTokenName();
        if (tokenName != null) {
            stmt.bindString(21, tokenName);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public TransactionRecord readEntity(Cursor cursor, int offset) {
        TransactionRecord entity = new TransactionRecord( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // blockNumber
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // timeStamp
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // hash
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // nonce
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // blockHash
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // transactionIndex
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // from
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // to
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // value
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // gas
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // gasPrice
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // isError
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // txreceipt_status
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // input
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // contractAddress
            cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16), // cumulativeGasUsed
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // gasUsed
            cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18), // confirmations
            cursor.getShort(offset + 19) != 0, // isPending
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20) // tokenName
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, TransactionRecord entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setBlockNumber(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTimeStamp(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setHash(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setNonce(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBlockHash(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setTransactionIndex(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setFrom(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTo(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setValue(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setGas(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setGasPrice(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setIsError(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setTxreceipt_status(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setInput(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setContractAddress(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setCumulativeGasUsed(cursor.isNull(offset + 16) ? null : cursor.getString(offset + 16));
        entity.setGasUsed(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setConfirmations(cursor.isNull(offset + 18) ? null : cursor.getString(offset + 18));
        entity.setIsPending(cursor.getShort(offset + 19) != 0);
        entity.setTokenName(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(TransactionRecord entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(TransactionRecord entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(TransactionRecord entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
