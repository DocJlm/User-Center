package com.zqc.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ZQC
 * @date 2025-10-26
 * @project user-center-backend
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 用户账号
     */
    private String userAccount;

    /**
     * 用户密码
     */
    private String userPassword;

    /**
     * 校验密码
     */
    private String checkPassword;

    /**
     * 星球编号
     */
    private String planetCode;
}
