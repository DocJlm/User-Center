package com.zqc.usercenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zqc.usercenter.model.domain.User;
import com.zqc.usercenter.model.domain.request.UserUpdateRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author ZQC
 * @date 2025-10-26
 * @project user-center-backend
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest
     * @return 更新后的用户信息
     */
    User updateUser(UserUpdateRequest userUpdateRequest);

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 上传头像
     *
     * @param userId 用户ID
     * @param avatarFile 头像文件
     * @return 头像URL
     */
    String uploadAvatar(Long userId, MultipartFile avatarFile);

    /**
     * 获取用户活动记录
     *
     * @param userId 用户ID
     * @return 活动记录列表
     */
    List<Map<String, Object>> getUserActivities(Long userId);

    /**
     * 获取系统统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getSystemStats();
}