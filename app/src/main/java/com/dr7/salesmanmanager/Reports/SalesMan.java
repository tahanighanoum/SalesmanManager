package com.dr7.salesmanmanager.Reports;

public class SalesMan {
    private String userName;
    private String password;

    public SalesMan(){

    }

    public SalesMan(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
