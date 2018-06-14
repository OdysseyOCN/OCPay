package com.ocpay.wallet.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import java.io.Serializable;

/**
 * Created by y on 2018/5/10.
 */
@Entity
public class Contact implements Serializable {

    @Id(autoincrement = true)
    private Long id;
    @Unique
    String firstName;
    String familyName;
    @Unique
    String walletAddress;
    String phoneName;
    String note;
    String email;
    private static final long serialVersionUID = 536871008L;

    @Generated(hash = 467854964)
    public Contact(Long id, String firstName, String familyName, String walletAddress, String phoneName, String note, String email) {
        this.id = id;
        this.firstName = firstName;
        this.familyName = familyName;
        this.walletAddress = walletAddress;
        this.phoneName = phoneName;
        this.note = note;
        this.email = email;
    }

    @Generated(hash = 672515148)
    public Contact() {
    }


    public Contact(String firstName, String walletAddress) {
        this.firstName = firstName;
        this.walletAddress = walletAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public String getNote() {
        return note;
    }

    public String getEmail() {
        return email;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
