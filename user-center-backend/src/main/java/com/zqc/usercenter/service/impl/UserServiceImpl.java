package com.zqc.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zqc.usercenter.common.ErrorCode;
import com.zqc.usercenter.exception.BusinessException;
import com.zqc.usercenter.mapper.UserMapper;
import com.zqc.usercenter.model.domain.User;
import com.zqc.usercenter.model.domain.request.UserUpdateRequest;
import com.zqc.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zqc.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author ZQC
 * @date 2025-10-26
 * @project user-center-backend
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;

    // https://www.code-nav.cn/

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】'；：\"\"'。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码不匹配");
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 更新用户信息
     *
     * @param userUpdateRequest
     * @return
     */
    @Override
    public User updateUser(UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = this.getById(userUpdateRequest.getId());
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        // 更新用户信息
        if (StringUtils.isNotBlank(userUpdateRequest.getUsername())) {
            user.setUsername(userUpdateRequest.getUsername());
        }
        if (userUpdateRequest.getAvatarUrl() != null) {
            user.setAvatarUrl(userUpdateRequest.getAvatarUrl());
        }
        if (userUpdateRequest.getGender() != null) {
            user.setGender(userUpdateRequest.getGender());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getPhone())) {
            user.setPhone(userUpdateRequest.getPhone());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getEmail())) {
            user.setEmail(userUpdateRequest.getEmail());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getPlanetCode())) {
            user.setPlanetCode(userUpdateRequest.getPlanetCode());
        }
        if (StringUtils.isNotBlank(userUpdateRequest.getBio())) {
            // 这里需要在User实体中添加bio字段
            // user.setBio(userUpdateRequest.getBio());
        }

        user.setUpdateTime(new Date());
        boolean updateResult = this.updateById(user);
        if (!updateResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新用户信息失败");
        }

        return this.getSafetyUser(user);
    }

    /**
     * 修改密码
     *
     * @param userId 用户ID
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @return
     */
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        if (userId == null || StringUtils.isAnyBlank(oldPassword, newPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        // 验证原密码
        String encryptedOldPassword = DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes());
        if (!encryptedOldPassword.equals(user.getUserPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码错误");
        }

        // 更新新密码
        String encryptedNewPassword = DigestUtils.md5DigestAsHex((SALT + newPassword).getBytes());
        user.setUserPassword(encryptedNewPassword);
        user.setUpdateTime(new Date());

        return this.updateById(user);
    }

    /**
     * 上传头像
     *
     * @param userId 用户ID
     * @param avatarFile 头像文件
     * @return
     */
    @Override
    public String uploadAvatar(Long userId, MultipartFile avatarFile) {
        if (userId == null || avatarFile == null || avatarFile.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像文件不能为空");
        }

        try {
            // 创建上传目录 - 使用项目根目录下的uploads文件夹
            String projectRoot = System.getProperty("user.dir");
            String uploadDir = projectRoot + "/uploads/avatars/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成文件名
            String originalFilename = avatarFile.getOriginalFilename();
            if (originalFilename == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件名不能为空");
            }
            String fileExtension = originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
            String newFilename = "avatar_" + userId + "_" + System.currentTimeMillis() + fileExtension;

            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(avatarFile.getInputStream(), filePath);

            // 返回访问URL - 使用静态资源访问路径
            String avatarUrl = "/uploads/avatars/" + newFilename;

            // 更新用户头像URL
            User user = this.getById(userId);
            if (user != null) {
                user.setAvatarUrl(avatarUrl);
                user.setUpdateTime(new Date());
                this.updateById(user);
            }

            return avatarUrl;
        } catch (IOException e) {
            log.error("上传头像失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "头像上传失败");
        }
    }

    /**
     * 获取用户活动记录
     *
     * @param userId 用户ID
     * @return
     */
    @Override
    public List<Map<String, Object>> getUserActivities(Long userId) {
        List<Map<String, Object>> activities = new ArrayList<>();

        User user = this.getById(userId);
        if (user != null) {
            // 注册活动
            Map<String, Object> registerActivity = new HashMap<>();
            registerActivity.put("id", 1);
            registerActivity.put("title", "用户注册");
            registerActivity.put("description", "用户成功注册账号");
            registerActivity.put("type", "register");
            registerActivity.put("createTime", user.getCreateTime());
            activities.add(registerActivity);

            // 登录活动（模拟最近几次登录）
            for (int i = 0; i < 3; i++) {
                Map<String, Object> loginActivity = new HashMap<>();
                loginActivity.put("id", 2 + i);
                loginActivity.put("title", "用户登录");
                loginActivity.put("description", "用户通过账号密码登录系统");
                loginActivity.put("type", "login");
                loginActivity.put("createTime", new Date(System.currentTimeMillis() - (i + 1) * 24 * 60 * 60 * 1000));
                activities.add(loginActivity);
            }
        }

        return activities;
    }

    /**
     * 获取系统统计信息
     *
     * @return
     */
    @Override
    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        // 获取用户总数
        long totalUsers = this.count();
        stats.put("totalUsers", totalUsers);

        // 获取活跃用户数（模拟）
        long activeUsers = (long) (totalUsers * 0.7);
        stats.put("activeUsers", activeUsers);

        // 今日登录数（模拟）
        long todayLogins = (long) (totalUsers * 0.1);
        stats.put("todayLogins", todayLogins);

        // 用户增长率（模拟）
        stats.put("userGrowth", 12.5);
        stats.put("activeGrowth", 8.2);
        stats.put("loginGrowth", 3.1);

        // 获取用户列表用于表格显示
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("createTime");
        queryWrapper.last("LIMIT 10");
        List<User> userList = this.list(queryWrapper);

        // 转换为安全用户信息
        List<Map<String, Object>> safeUserList = userList.stream().map(this::convertToMap).collect(Collectors.toList());
        stats.put("userList", safeUserList);

        return stats;
    }

    /**
     * 将User对象转换为Map
     *
     * @param user
     * @return
     */
    private Map<String, Object> convertToMap(User user) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("username", user.getUsername());
        userMap.put("userAccount", user.getUserAccount());
        userMap.put("avatarUrl", user.getAvatarUrl());
        userMap.put("gender", user.getGender());
        userMap.put("phone", user.getPhone());
        userMap.put("email", user.getEmail());
        userMap.put("userStatus", user.getUserStatus());
        userMap.put("planetCode", user.getPlanetCode());
        userMap.put("userRole", user.getUserRole());
        userMap.put("createTime", user.getCreateTime());
        userMap.put("updateTime", user.getUpdateTime());
        userMap.put("lastLoginTime", user.getUpdateTime()); // 暂时使用updateTime
        return userMap;
    }

}
