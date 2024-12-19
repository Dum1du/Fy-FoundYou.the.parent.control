package com.ghost_byte.fy_foundyoutheparentcontroll;

public class MainRecItemsData {
    String uniqueCode;
    String userName;

    public MainRecItemsData(String uniqueCode, String userName){
        this.uniqueCode = uniqueCode;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
}
