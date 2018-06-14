package com.ocpay.wallet.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * Created by y on 2018/5/10.
 */

@Entity
public class NotificationBean implements Serializable {

    @Id(autoincrement = true)
    private Long id;

    public String title;

    public String content;

    public String description;

    public String timeStamp;

    public String receiveTimestamp;

    public boolean read;

    public String notificationType;

    public String txHash;

    public String transceiver;

    public String transceiverDescription;

    public String blockNumber;


    @Generated(hash = 458000328)
    public NotificationBean(Long id, String title, String content, String description, String timeStamp, String receiveTimestamp, boolean read, String notificationType,
                            String txHash, String transceiver, String transceiverDescription, String blockNumber) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.description = description;
        this.timeStamp = timeStamp;
        this.receiveTimestamp = receiveTimestamp;
        this.read = read;
        this.notificationType = notificationType;
        this.txHash = txHash;
        this.transceiver = transceiver;
        this.transceiverDescription = transceiverDescription;
        this.blockNumber = blockNumber;
    }


    private static final long serialVersionUID = 536871008L;


    public NotificationBean(String title, String content, String timeStamp, boolean read) {
        this.title = title;
        this.content = content;
        this.timeStamp = timeStamp;
        this.read = read;
    }

    public NotificationBean() {

    }

    public NotificationBean(String title, String content, String description, String timeStamp, String notificationType, String transceiver, boolean read) {
        this.title = title;
        this.content = content;
        this.description = description;
        this.timeStamp = timeStamp;
        this.notificationType = notificationType;
        this.transceiver = transceiver;
    }

    public NotificationBean(String title, String content, String description, String timeStamp, boolean read, String notiType) {
        this.title = title;
        this.content = content;
        this.description = description;
        this.timeStamp = timeStamp;
        this.read = read;
        this.notificationType = notiType;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }


    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getRead() {
        return this.read;
    }

    public String getNotificationType() {
        return this.notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTxHash() {
        return this.txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getReceiveTimestamp() {
        return this.receiveTimestamp;
    }

    public void setReceiveTimestamp(String receiveTimestamp) {
        this.receiveTimestamp = receiveTimestamp;
    }

    public String getTransceiver() {
        return this.transceiver;
    }

    public void setTransceiver(String transceiver) {
        this.transceiver = transceiver;
    }

    public String getTransceiverDescription() {
        return this.transceiverDescription;
    }

    public void setTransceiverDescription(String transceiverDescription) {
        this.transceiverDescription = transceiverDescription;
    }

    public String getBlockNumber() {
        return this.blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }
}
