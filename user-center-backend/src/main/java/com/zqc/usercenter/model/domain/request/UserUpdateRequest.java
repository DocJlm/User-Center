package com.zqc.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author ZQC
 * @date 2025-10-29
 */
@Data
public class UserUpdateRequest implements Serializable {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 个人简介
     */
    private String bio;

    private static final long serialVersionUID = 1L;
}