package com.secondhand.framework.web.service;

import com.secondhand.common.core.domain.entity.Client;
import com.secondhand.common.core.domain.model.ClientRegisterDTO;
import com.secondhand.common.exception.base.BaseException;
import com.secondhand.system.service.ClientService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.secondhand.common.constant.CacheConstants;
import com.secondhand.common.constant.Constants;
import com.secondhand.common.constant.UserConstants;
import com.secondhand.common.core.domain.entity.SysUser;
import com.secondhand.common.core.domain.model.RegisterBody;
import com.secondhand.common.core.redis.RedisCache;
import com.secondhand.common.exception.user.CaptchaException;
import com.secondhand.common.exception.user.CaptchaExpireException;
import com.secondhand.common.utils.MessageUtils;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.framework.manager.AsyncManager;
import com.secondhand.framework.manager.factory.AsyncFactory;
import com.secondhand.system.service.ISysConfigService;
import com.secondhand.system.service.ISysUserService;

/**
 * 注册校验方法
 *
 * @author ruoyi
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 管理端用户注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", username = registerBody.getUsername(), password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(username);

        // 验证码开关
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            validateCaptcha(username, registerBody.getCode(), registerBody.getUuid());
        }

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (!userService.checkUserNameUnique(sysUser))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else
        {
            sysUser.setNickName(username);
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag)
            {
                msg = "注册失败,请联系系统管理人员";
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    /**
     * 用户端用户注册
     */
    public String emailRegister(ClientRegisterDTO user) {
        String msg = "", username = user.getUserName(), nickName = user.getNickName(), phonenumber=user.getPhonenumber(), password = user.getPassword(), email = user.getEmail(),code = user.getCode();

        // 获取当前邮箱在redis存储的键值
        String verifyKey = CacheConstants.EMAIL_VERIFICATION_CODE_KEY + email;
        // 从redis中查看有没有该邮箱的验证码
        String  verifyCode = redisCache.getCacheObject(verifyKey);
        // 将注册对象值拷贝给用户端用户对象
        Client client = new Client();
        BeanUtils.copyProperties(user,client);

        if (StringUtils.isEmpty(username))
        {
            msg = "用户名不能为空";
        }
        else if (StringUtils.isEmpty(nickName))
        {
            msg = "昵称不能为空";
        }
        else if (StringUtils.isEmpty(password))
        {
            msg = "用户密码不能为空";
        }
        else if (StringUtils.isEmpty(email))
        {
            msg = "邮箱账号不能为空";
        }
        else if (StringUtils.isAnyBlank(verifyCode))
        {
            msg = "验证码过期，请重新获取";
        }
        else if (!verifyCode.equals(code)){
            msg = "验证码输入错误，请重新输入";
        }
        else if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            msg = "账户长度必须在2到20个字符之间";
        }
        else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            msg = "密码长度必须在5到20个字符之间";
        }
        else if (!clientService.checkUsernameUnique(client))
        {
            msg = "保存用户'" + username + "'失败，注册账号已存在";
        }
        else if (!clientService.checkEmailUnique(client))
        {
            msg = "保存用户'" + username + "'失败，邮箱账号已被使用";
        }
        else if (!clientService.checkNameUnique(client))
        {
            msg = "保存用户'" + username + "'失败，昵称已被使用";
        }
        else if (StringUtils.isNotEmpty(phonenumber) && !clientService.checkPhoneUnique(client))
        {
            msg = "保存用户'" + username + "'失败，手机号码已被使用";
        }
        else
        {
            client.setPassword(SecurityUtils.encryptPassword(password));
            boolean regFlag = clientService.insertClient(client) > 0;
            if (!regFlag)
            {
                msg = "注册失败,请联系系统管理人员";
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     *
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }

}
