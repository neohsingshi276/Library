package com.secondhand.web.controller.system;

import java.util.Map;

import com.secondhand.common.constant.CacheConstants;
import com.secondhand.common.utils.file.FileUtils;
import com.secondhand.common.utils.oss.AliyunOssUploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.secondhand.common.annotation.Log;
import com.secondhand.common.config.RuoYiConfig;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.domain.entity.SysUser;
import com.secondhand.common.core.domain.model.LoginUser;
import com.secondhand.common.enums.BusinessType;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.common.utils.file.FileUploadUtils;
import com.secondhand.common.utils.file.MimeTypeUtils;
import com.secondhand.common.core.redis.RedisCache;
import com.secondhand.framework.web.service.TokenService;
import com.secondhand.system.service.ISysUserService;

/**
 * 个人信息 业务处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 个人信息
     */
    @GetMapping
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser();
        SysUser user = loginUser.getUser();
        AjaxResult ajax = AjaxResult.success(user);
        ajax.put("roleGroup", userService.selectUserRoleGroup(loginUser.getUsername()));
        ajax.put("postGroup", userService.selectUserPostGroup(loginUser.getUsername()));
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateProfile(@RequestBody SysUser user)
    {
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();
        currentUser.setNickName(user.getNickName());
        currentUser.setEmail(user.getEmail());
        currentUser.setPhonenumber(user.getPhonenumber());
        currentUser.setSex(user.getSex());
        if (StringUtils.isNotEmpty(user.getPhonenumber()) && !userService.checkPhoneUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已存在");
        }
        if (StringUtils.isNotEmpty(user.getEmail()) && !userService.checkEmailUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已存在");
        }
        if (userService.updateUserProfile(currentUser) > 0)
        {
            // 更新缓存用户信息
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(@RequestBody Map<String, String> params)
    {
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        LoginUser loginUser = getLoginUser();
        String userName = loginUser.getUsername();
        String password = loginUser.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return error("新密码不能与旧密码相同");
        }
        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (userService.resetUserPwd(userName, newPassword) > 0)
        {
            // 更新缓存用户密码
            loginUser.getUser().setPassword(newPassword);
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 修改邮箱（通过验证码）
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateEmail")
    public AjaxResult updateEmail(@RequestBody Map<String, String> params)
    {
        String newEmail = params.get("newEmail");
        String code = params.get("code");
        LoginUser loginUser = getLoginUser();
        SysUser currentUser = loginUser.getUser();

        if (StringUtils.isEmpty(newEmail))
        {
            return error("新邮箱不能为空");
        }
        if (StringUtils.isEmpty(code))
        {
            return error("验证码不能为空");
        }

        // 验证邮箱格式
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!newEmail.matches(emailRegex))
        {
            return error("邮箱格式不正确");
        }

        // 验证邮箱是否已被使用
        SysUser checkUser = new SysUser();
        checkUser.setUserId(currentUser.getUserId());
        checkUser.setEmail(newEmail);
        if (!userService.checkEmailUnique(checkUser))
        {
            return error("邮箱账号已存在");
        }

        // 验证验证码
        String verifyKey = CacheConstants.EMAIL_VERIFICATION_CODE_KEY + newEmail;
        String verifyCode = redisCache.getCacheObject(verifyKey);
        if (StringUtils.isEmpty(verifyCode))
        {
            return error("验证码已过期，请重新获取");
        }
        if (!verifyCode.equals(code))
        {
            return error("验证码错误");
        }

        currentUser.setEmail(newEmail);
        if (userService.updateUserProfile(currentUser) > 0)
        {
            // 删除验证码
            redisCache.deleteObject(verifyKey);
            // 更新缓存用户信息
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改邮箱异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @CrossOrigin
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        if (!file.isEmpty())
        {
            LoginUser loginUser = getLoginUser();
//            String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
            String url = AliyunOssUploadUtils.uploadFile(file);
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", FileUtils.getName(url));
            ajax.put("newFileName", FileUtils.getName(url));
            ajax.put("originalFilename", file.getOriginalFilename());
            if (userService.updateUserAvatar(loginUser.getUsername(), url))
            {
                ajax.put("imgUrl", url);
                // 更新缓存用户头像
                loginUser.getUser().setAvatar(url);
                tokenService.setLoginUser(loginUser);
                return ajax;
            }
        }
        return error("上传图片异常，请联系管理员");
    }
}
