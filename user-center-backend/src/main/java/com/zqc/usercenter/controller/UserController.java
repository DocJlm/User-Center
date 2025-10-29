package com.zqc.usercenter.controller;

/**
 * @author ZQC
 * @date 2025-10-26
 * @project user-center-backend
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zqc.usercenter.common.BaseResponse;
import com.zqc.usercenter.common.ErrorCode;
import com.zqc.usercenter.common.ResultUtils;
import com.zqc.usercenter.exception.BusinessException;
import com.zqc.usercenter.model.domain.User;
import com.zqc.usercenter.model.domain.request.UserLoginRequest;
import com.zqc.usercenter.model.domain.request.UserRegisterRequest;
import com.zqc.usercenter.model.domain.request.UserUpdateRequest;
import com.zqc.usercenter.model.domain.request.ChangePasswordRequest;
import com.zqc.usercenter.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

import static com.zqc.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.zqc.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest
                .getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }


    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "缺少管理员权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }



    /**
     * 更新用户信息
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PutMapping("/update")
    public BaseResponse<User> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 获取当前登录用户
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        // 设置用户ID
        userUpdateRequest.setId(currentUser.getId());

        User updatedUser = userService.updateUser(userUpdateRequest);
        return ResultUtils.success(updatedUser);
    }

    /**
     * 修改密码
     *
     * @param changePasswordRequest
     * @param request
     * @return
     */
    @PostMapping("/change-password")
    public BaseResponse<Boolean> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest request) {
        if (changePasswordRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        boolean result = userService.changePassword(currentUser.getId(), changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());
        return ResultUtils.success(result);
    }

    /**
     * 上传头像
     *
     * @param avatarFile
     * @param request
     * @return
     */
    @PostMapping("/upload-avatar")
    public BaseResponse<String> uploadAvatar(@RequestParam("avatar") MultipartFile avatarFile, HttpServletRequest request) {
        if (avatarFile == null || avatarFile.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "头像文件不能为空");
        }

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        String avatarUrl = userService.uploadAvatar(currentUser.getId(), avatarFile);
        return ResultUtils.success(avatarUrl);
    }

    /**
     * 获取用户活动记录
     *
     * @param userId
     * @param request
     * @return
     */
    @GetMapping("/activities/{userId}")
    public BaseResponse<List<Map<String, Object>>> getUserActivities(@PathVariable Long userId, HttpServletRequest request) {
        // 检查权限：只能查看自己的活动记录或管理员可查看所有
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        if (!currentUser.getId().equals(userId) && currentUser.getUserRole() != ADMIN_ROLE) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        List<Map<String, Object>> activities = userService.getUserActivities(userId);
        return ResultUtils.success(activities);
    }

    /**
     * 获取系统统计信息
     *
     * @param request
     * @return
     */
    @GetMapping("/system-stats")
    public BaseResponse<Map<String, Object>> getSystemStats(HttpServletRequest request) {
        // 仅管理员可查看系统统计
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "需要管理员权限");
        }

        Map<String, Object> stats = userService.getSystemStats();
        return ResultUtils.success(stats);
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}
