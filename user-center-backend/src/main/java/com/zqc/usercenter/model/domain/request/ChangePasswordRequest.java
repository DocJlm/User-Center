package com.zqc.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 修改密码请求
 *
 * @author ZQC
 * @date 2025-10-29
 */
@Data
public class ChangePasswordRequest implements Serializable {

    /**
     * 原密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    private static final long serialVersionUID = 1L;
}