package com.secondhand.web.controller.system;

import com.secondhand.common.annotation.Log;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.domain.entity.Client;
import com.secondhand.common.core.domain.model.LoginUser;
import com.secondhand.common.enums.BusinessType;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.common.utils.file.FileUtils;
import com.secondhand.common.utils.oss.AliyunOssUploadUtils;
import com.secondhand.framework.web.service.TokenService;
import com.secondhand.system.service.ClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.secondhand.common.core.domain.AjaxResult.error;
import static com.secondhand.common.core.domain.AjaxResult.success;
import static com.secondhand.common.utils.SecurityUtils.getLoginUser;

@RestController
@Slf4j
@RequestMapping("/client/info")
public class ClientUserController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private TokenService tokenService;

    /**
     * 获取个人信息
     */
    @GetMapping
    public AjaxResult profile()
    {
        LoginUser loginUser = getLoginUser();
        Client client = loginUser.getClient();
        client = clientService.selectClientByUserId(client.getUserId());
        AjaxResult ajax = success(client);
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
        Client currentUser = loginUser.getClient();
        currentUser.setNickName(client.getNickName());
        currentUser.setUserName(client.getUserName());
        currentUser.setEmail(client.getEmail());
        currentUser.setPhonenumber(client.getPhonenumber());
        currentUser.setSex(client.getSex());
        if (StringUtils.isNotEmpty(client.getNickName()) && !clientService.checkNameUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，该昵称已被使用");
        }
        if (StringUtils.isNotEmpty(client.getUserName()) && !clientService.checkUsernameUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，该账号已被使用");
        }
        if (StringUtils.isNotEmpty(client.getPhonenumber()) && !clientService.checkPhoneUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，手机号码已被使用");
        }
        if (StringUtils.isNotEmpty(client.getEmail()) && !clientService.checkEmailUnique(currentUser))
        {
            return error("修改用户'" + loginUser.getUsername() + "'失败，邮箱账号已被使用");
        }
        if (clientService.updateUserProfile(currentUser) > 0)
        {
            // 更新缓存用户信息
            System.out.println("存储的账号"+loginUser);
            tokenService.setLoginUser(loginUser);
            return success(client);
        }
        return error("修改个人信息异常，请联系管理员");
    }


    /**
     * 用户修改密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(String oldPassword, String newPassword)
    {
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
        if (!file.isEmpty())
        {
            LoginUser loginUser = getLoginUser();
            String url = AliyunOssUploadUtils.uploadFile(file);
            AjaxResult ajax = AjaxResult.success();
            ajax.put("url", url);
            ajax.put("fileName", FileUtils.getName(url));
            ajax.put("newFileName", FileUtils.getName(url));
            ajax.put("originalFilename", file.getOriginalFilename());
            if (clientService.updateUserAvatar(loginUser.getUsername(), url))
                ajax.put("imgUrl", url);
            // 更新缓存用户头像
            loginUser.getClient().setAvatar(url);
            tokenService.setLoginUser(loginUser);
            return ajax;
        }
        return error("上传图片异常，请联系管理员");
    }
}
