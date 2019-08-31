package com.yyy.fengyupda.model.login;

public class LoginBean {

    /**
     * success : true
     * message :
     */

    private boolean success;
    private String message;
    private TablesBean tables;
    private NumBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public TablesBean getTables() {
        return tables;
    }

    public void setTables(TablesBean tables) {
        this.tables = tables;
    }
    public NumBean getData() {
        return data;
    }

    public void setData(NumBean data) {
        this.data = data;
    }

}
