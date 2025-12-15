package com.secondhand.web.controller.system;

import java.util.Map;

import com.secondhand.common.annotation.Log;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.domain.entity.Client;
import com.secondhand.common.core.domain.model.LoginUser;
import com.secondhand.common.enums.BusinessType;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.common.constant.CacheConstants;
import com.secondhand.common.core.redis.RedisCache;
import com.secondhand.common.utils.file.FileUtils;
import com.secondhand.common.utils.file.FileUploadUtils;
import com.secondhand.common.utils.file.MimeTypeUtils;
import com.secondhand.common.utils.oss.AliyunOssUploadUtils;
import com.secondhand.common.config.RuoYiConfig;
import com.secondhand.framework.config.ServerConfig;
import com.secondhand.framework.web.service.TokenService;
import com.secondhand.system.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 客户端个人信息 业务处理
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/client/profile")
public class ClientProfileController extends BaseController
{
    @Autowired
    private ClientService clientService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ServerConfig serverConfig;

    /**
     * 个人信息
     */
    @GetMapping
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser();
        Client client = loginUser.getClient();
        AjaxResult ajax = AjaxResult.success(client);
        ajax.put("roleGroup", "");
        ajax.put("postGroup", "");
        return ajax;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult updateProfile(@RequestBody Client client)
    {
        LoginUser loginUser = getLoginUser();
        Client currentClient = loginUser.getClient();
        // 如果修改了用户名，需要检查唯一性
        if (StringUtils.isNotEmpty(client.getUserName()) && !client.getUserName().equals(currentClient.getUserName()))
        {
            Client checkClient = new Client();
            checkClient.setUserId(currentClient.getUserId());
            checkClient.setUserName(client.getUserName());
            if (!clientService.checkUsernameUnique(checkClient))
            {
                return error("账号已被使用，请更换其他账号");
            }
            currentClient.setUserName(client.getUserName());
        }
        // 如果修改了昵称，需要检查唯一性
        if (StringUtils.isNotEmpty(client.getNickName()) && !client.getNickName().equals(currentClient.getNickName()))
        {
            Client checkClient = new Client();
            checkClient.setUserId(currentClient.getUserId());
            checkClient.setNickName(client.getNickName());
            if (!clientService.checkNameUnique(checkClient))
            {
                return error("昵称已被使用，请更换其他昵称");
            }
        }
        currentClient.setNickName(client.getNickName());
        currentClient.setEmail(client.getEmail());
        currentClient.setPhonenumber(client.getPhonenumber());
        currentClient.setSex(client.getSex());
        if (StringUtils.isNotEmpty(client.getPhonenumber()) && !clientService.checkPhoneUnique(currentClient))
        {
            return error("手机号码已被使用，请更换其他手机号");
        }
        if (StringUtils.isNotEmpty(client.getEmail()) && !clientService.checkEmailUnique(currentClient))
        {
            return error("邮箱已被使用，请更换其他邮箱");
        }
        if (clientService.updateUserProfile(currentClient) > 0)
        {
            // 更新缓存用户信息
            // LoginUser.getUsername() 方法会从 client.getUserName() 获取，所以更新 client 后会自动更新
            loginUser.setClient(currentClient);
            tokenService.setLoginUser(loginUser);
            return success(currentClient);
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
        if (clientService.resetUserPwd(userName, newPassword) > 0)
        {
            // 更新缓存用户密码
            loginUser.getClient().setPassword(newPassword);
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 头像上传
     */
    @CrossOrigin
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public AjaxResult avatar(@RequestParam("avatarfile") MultipartFile file) throws Exception
    {
        try
        {
            if (file == null || file.isEmpty())
            {
                return error("上传文件不能为空");
            }
            
            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null)
            {
                return error("文件名不能为空");
            }
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            String[] allowedExtensions = {"jpg", "jpeg", "png", "gif", "webp"};
            boolean isAllowed = false;
            for (String ext : allowedExtensions)
            {
                if (ext.equals(extension))
                {
                    isAllowed = true;
                    break;
                }
            }
            if (!isAllowed)
            {
                return error("仅支持图片文件格式（jpg、jpeg、png、gif、webp）");
            }
            
            LoginUser loginUser = getLoginUser();
            Client currentClient = loginUser.getClient();
            
            // 优先使用阿里云OSS上传
            String avatarUrl;
            try
            {
                avatarUrl = AliyunOssUploadUtils.uploadFile(file);
            }
            catch (Exception e)
            {
                // 如果OSS上传失败，使用本地存储
                String filePath = RuoYiConfig.getUploadPath();
                String fileName = FileUploadUtils.upload(filePath, file);
                avatarUrl = serverConfig.getUrl() + fileName;
            }
            
            // 更新用户头像
            currentClient.setAvatar(avatarUrl);
            if (clientService.updateUserProfile(currentClient) > 0)
            {
                // 更新缓存用户信息
                loginUser.setClient(currentClient);
                tokenService.setLoginUser(loginUser);
                AjaxResult ajax = AjaxResult.success();
                ajax.put("imgUrl", avatarUrl);
                return ajax;
            }
            return error("头像上传失败");
        }
        catch (Exception e)
        {
            return error("头像上传异常：" + e.getMessage());
        }
    }

    /**
     * 修改邮箱
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updateEmail")
    public AjaxResult updateEmail(@RequestBody Map<String, String> params)
    {
        String newEmail = params.get("newEmail");
        String code = params.get("code");
        LoginUser loginUser = getLoginUser();
        Client currentClient = loginUser.getClient();

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
        Client checkClient = new Client();
        checkClient.setEmail(newEmail);
        if (!clientService.checkEmailUnique(checkClient))
        {
            return error("邮箱已被使用");
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

        // 更新邮箱
        currentClient.setEmail(newEmail);
        if (clientService.updateUserProfile(currentClient) > 0)
        {
            // 删除验证码
            redisCache.deleteObject(verifyKey);
            // 更新缓存用户信息
            loginUser.setClient(currentClient);
            tokenService.setLoginUser(loginUser);
            return success();
        }
        return error("修改邮箱异常，请联系管理员");
    }
}

