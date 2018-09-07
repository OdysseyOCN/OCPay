package com.ocpay.wallet.bean; /**
 * Copyright 2018 bejson.com
 */


/**
 * Auto-generated: 2018-07-25 2:54:5
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class OCPWalletClone {

    private String mnemonicCIV;
    private String mnemonicSalt;
    private String path;
    private String pathSalt;
    private WalletFile walletFile;

    public void setMnemonicCIV(String mnemonicCIV) {
        this.mnemonicCIV = mnemonicCIV;
    }

    public String getMnemonicCIV() {
        return mnemonicCIV;
    }

    public void setMnemonicSalt(String mnemonicSalt) {
        this.mnemonicSalt = mnemonicSalt;
    }

    public String getMnemonicSalt() {
        return mnemonicSalt;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPathSalt(String pathSalt) {
        this.pathSalt = pathSalt;
    }

    public String getPathSalt() {
        return pathSalt;
    }

    public void setWalletFile(WalletFile walletFile) {
        this.walletFile = walletFile;
    }

    public WalletFile getWalletFile() {
        return walletFile;
    }


    public class Cipherparams {

        private String iv;

        public void setIv(String iv) {
            this.iv = iv;
        }

        public String getIv() {
            return iv;
        }

    }


    /**
     * Auto-generated: 2018-07-25 2:54:5
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class Kdfparams {

        private int dklen;
        private long n;
        private int p;
        private int r;
        private String salt;

        public void setDklen(int dklen) {
            this.dklen = dklen;
        }

        public int getDklen() {
            return dklen;
        }

        public void setN(long n) {
            this.n = n;
        }

        public long getN() {
            return n;
        }

        public void setP(int p) {
            this.p = p;
        }

        public int getP() {
            return p;
        }

        public void setR(int r) {
            this.r = r;
        }

        public int getR() {
            return r;
        }

        public void setSalt(String salt) {
            this.salt = salt;
        }

        public String getSalt() {
            return salt;
        }

    }


    /**
     * Auto-generated: 2018-07-25 2:54:5
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class Crypto {

        private String cipher;
        private Cipherparams cipherparams;
        private String ciphertext;
        private String kdf;
        private Kdfparams kdfparams;
        private String mac;

        public void setCipher(String cipher) {
            this.cipher = cipher;
        }

        public String getCipher() {
            return cipher;
        }

        public void setCipherparams(Cipherparams cipherparams) {
            this.cipherparams = cipherparams;
        }

        public Cipherparams getCipherparams() {
            return cipherparams;
        }

        public void setCiphertext(String ciphertext) {
            this.ciphertext = ciphertext;
        }

        public String getCiphertext() {
            return ciphertext;
        }

        public void setKdf(String kdf) {
            this.kdf = kdf;
        }

        public String getKdf() {
            return kdf;
        }

        public void setKdfparams(Kdfparams kdfparams) {
            this.kdfparams = kdfparams;
        }

        public Kdfparams getKdfparams() {
            return kdfparams;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public String getMac() {
            return mac;
        }

    }


    /**
     * Auto-generated: 2018-07-25 2:54:5
     *
     * @author bejson.com (i@bejson.com)
     * @website http://www.bejson.com/java2pojo/
     */
    public class WalletFile {

        private String address;
        private String id;
        private int version;
        private Crypto crypto;

        public void setAddress(String address) {
            this.address = address;
        }

        public String getAddress() {
            return address;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public int getVersion() {
            return version;
        }

        public void setCrypto(Crypto crypto) {
            this.crypto = crypto;
        }

        public Crypto getCrypto() {
            return crypto;
        }

    }
}