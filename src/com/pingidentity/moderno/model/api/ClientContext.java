package com.pingidentity.moderno.model.api;

import com.google.gson.Gson;

/**
 * Created by dheinisch on 6/4/17.
 */
public class ClientContext {
    private String msg;
    private TrxType transactionType;

    public ClientContext(String msg, TrxType transactionType) {
        this.msg = msg;
        this.transactionType = transactionType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public TrxType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TrxType transactionType) {
        this.transactionType = transactionType;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public enum TrxType {
        STEP_UP,
        AUTHENTICATION,
        QRCODE_AUTHENTICATION;
    }
}
