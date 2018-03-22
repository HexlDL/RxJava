package com.hexl.rxjava2.entity;

/**
 * Company: 
 * <p>
 * Description:
 *
 * @author hexl
 * @date 2017/8/27 on 22:41
 */
public class RegisterResponse {
    private String username;
    private String password;
    private String verifyCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }
}
