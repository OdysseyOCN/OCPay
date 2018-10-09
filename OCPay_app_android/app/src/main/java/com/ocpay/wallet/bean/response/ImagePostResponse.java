package com.ocpay.wallet.bean.response;

/**
 * Auto-generated: 2018-06-12 17:12:5
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class ImagePostResponse {

    private ResponseData data;
    private boolean success;

    public void setData(ResponseData data) {
        this.data = data;
    }

    public ResponseData getData() {
        return data;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean getSuccess() {
        return success;
    }

    public class ResponseData {


        private String message;

        public void setMessage(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

    }


}