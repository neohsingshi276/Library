package com.secondhand.framework.web.service;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.secondhand.common.constant.CacheConstants;
import com.secondhand.common.constant.Constants;
import com.secondhand.common.constant.UserConstants;
import com.secondhand.common.core.domain.entity.SysUser;
import com.secondhand.common.core.domain.entity.Client;
import com.secondhand.common.core.domain.model.LoginUser;
import com.secondhand.common.core.redis.RedisCache;
import com.secondhand.common.enums.UserStatus;
import com.secondhand.common.exception.ServiceException;
import com.secondhand.common.exception.user.BlackListException;
import com.secondhand.common.exception.user.CaptchaException;
import com.secondhand.common.exception.user.CaptchaExpireException;
import com.secondhand.common.exception.user.UserNotExistsException;
import com.secondhand.common.exception.user.UserPasswordNotMatchException;
import com.secondhand.common.utils.DateUtils;
import com.secondhand.common.utils.MessageUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.common.utils.ip.IpUtils;
import com.secondhand.framework.manager.AsyncManager;
import com.secondhand.framework.manager.factory.AsyncFactory;
import com.secondhand.framework.security.context.AuthenticationContextHolder;
import com.secondhand.system.service.ISysConfigService;
import com.secondhand.system.service.ISysUserService;
import com.secondhand.system.service.ClientService;
import com.secondhand.system.mapper.ClientMapper;

/**
 * 登录校验方法
 *
 * @author ruoyi
 */
@Component
public class SysLoginService
{
    @Autowired
    private TokenService tokenService;

    @Resource
    private AuthenticationManager adminAuthenticationManager;

    @Resource
    @Qualifier("ClientAuthenticationManager")
    private AuthenticationManager clientAuthenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientDetailsServiceImpl clientDetailsService;

    @Autowired
    private ClientMapper clientMapper;

    /**
     * 管理端用户登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String loginAdmin(String username, String password, String code, String uuid)
    {
        // 验证码校验
        validateCaptcha(username, code, uuid);
        // 登录前置校验
        loginPreCheck(username, password);
        // 用户验证
        Authentication authentication = null;
        try
        {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            // 该方法会去调用UserDetailsServiceImpl.loadUserByUsername
            authentication = adminAuthenticationManager.authenticate(authenticationToken);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        finally
        {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        recordLoginInfo(loginUser.getUserId());
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 管理端邮箱验证码登录
     *
     * @param email 邮箱账号
     * @param code  邮箱验证码
     * @return 结果
     */
    public String loginAdminByEmail(String email, String code)
    {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(code))
        {
            throw new ServiceException("邮箱或验证码不能为空");
        }

        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }

        // 校验邮箱验证码
        String verifyKey = CacheConstants.EMAIL_VERIFICATION_CODE_KEY + email;
        String emailCode = redisCache.getCacheObject(verifyKey);
        if (emailCode == null)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, "邮箱验证码已过期"));
            throw new ServiceException("邮箱验证码已过期，请重新获取");
        }
        if (!code.equals(emailCode))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, "邮箱验证码错误"));
            throw new ServiceException("邮箱验证码错误");
        }
        // 使用一次后删除验证码
        redisCache.deleteObject(verifyKey);

        // 通过邮箱查询用户（只获取userId和email）
        SysUser emailUser = userService.selectUserByEmail(email);
        if (StringUtils.isNull(emailUser))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new UserNotExistsException();
        }
        
        // 通过userId重新加载完整的用户信息（包括部门、角色等）
        SysUser user = userService.selectUserById(emailUser.getUserId());
        if (StringUtils.isNull(user))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new UserNotExistsException();
        }
        if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("user.password.delete")));
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("user.blocked")));
            throw new ServiceException(MessageUtils.message("user.blocked"));
        }

        // 构建 LoginUser 并生成 token
        LoginUser loginUser = (LoginUser) userDetailsService.createLoginUser(user);
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_SUCCESS, "邮箱登录成功"));
        recordLoginInfo(loginUser.getUserId());
        return tokenService.createToken(loginUser);
    }


    /**
     * 用户端用户登录验证
     *
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public String loginClient(String username, String password, String code, String uuid)
    {
        // 验证码校验
        validateCaptcha(username, code, uuid);
        // 登录前置校验
        loginPreCheck(username, password);
        // 用户验证
        Authentication authentication = null;
        try
        {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
            AuthenticationContextHolder.setContext(authenticationToken);
            authentication = clientAuthenticationManager.authenticate(authenticationToken);
        }
        catch (Exception e)
        {
            if (e instanceof BadCredentialsException)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
                throw new UserPasswordNotMatchException();
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, e.getMessage()));
                throw new ServiceException(e.getMessage());
            }
        }
        finally
        {
            AuthenticationContextHolder.clearContext();
        }
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success")));
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 生成token
        return tokenService.createToken(loginUser);
    }

    /**
     * 用户端邮箱验证码登录
     *
     * @param email 邮箱账号
     * @param code  邮箱验证码
     * @return 结果
     */
    public String loginClientByEmail(String email, String code)
    {
        if (StringUtils.isEmpty(email) || StringUtils.isEmpty(code))
        {
            throw new ServiceException("邮箱或验证码不能为空");
        }

        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }

        // 校验邮箱验证码
        String verifyKey = CacheConstants.EMAIL_VERIFICATION_CODE_KEY + email;
        String emailCode = redisCache.getCacheObject(verifyKey);
        if (emailCode == null)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, "邮箱验证码已过期"));
            throw new ServiceException("邮箱验证码已过期，请重新获取");
        }
        if (!code.equals(emailCode))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, "邮箱验证码错误"));
            throw new ServiceException("邮箱验证码错误");
        }
        // 使用一次后删除验证码
        redisCache.deleteObject(verifyKey);

        // 通过邮箱查询用户（只获取userId和email）
        Client emailClient = clientMapper.checkEmailUnique(email);
        if (StringUtils.isNull(emailClient))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new UserNotExistsException();
        }
        
        // 通过userId重新加载完整的用户信息
        Client client = clientService.selectClientByUserId(emailClient.getUserId());
        if (StringUtils.isNull(client))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists")));
            throw new UserNotExistsException();
        }
        if ("1".equals(client.getStatus()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_FAIL, MessageUtils.message("user.blocked")));
            throw new ServiceException(MessageUtils.message("user.blocked"));
        }

        // 构建 LoginUser 并生成 token
        LoginUser loginUser = (LoginUser) clientDetailsService.createLoginUser(client);
        AsyncManager.me().execute(AsyncFactory.recordLogininfor(email, Constants.LOGIN_SUCCESS, "邮箱登录成功"));
        return tokenService.createToken(loginUser);
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
        boolean captchaEnabled = configService.selectCaptchaEnabled();
        if (captchaEnabled)
        {
            String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
            String captcha = redisCache.getCacheObject(verifyKey);
            if (captcha == null)
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire")));
                throw new CaptchaExpireException();
            }
            redisCache.deleteObject(verifyKey);
            if (!code.equalsIgnoreCase(captcha))
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.error")));
                throw new CaptchaException();
            }
        }
    }

    /**
     * 登录前置校验
     * @param username 用户名
     * @param password 用户密码
     */
    public void loginPreCheck(String username, String password)
    {
        // 用户名或密码为空 错误
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("not.null")));
            throw new UserNotExistsException();
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH)
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.not.match")));
            throw new UserPasswordNotMatchException();
        }
        // IP黑名单校验
        String blackStr = configService.selectConfigByKey("sys.login.blackIPList");
        if (IpUtils.isMatchedIp(blackStr, IpUtils.getIpAddr()))
        {
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(username, Constants.LOGIN_FAIL, MessageUtils.message("login.blocked")));
            throw new BlackListException();
        }
    }

    /**
     * 记录管理端登录信息
     *
     * @param userId 用户ID
     */
    public void recordLoginInfo(Long userId)
    {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        sysUser.setLoginIp(IpUtils.getIpAddr());
        sysUser.setLoginDate(DateUtils.getNowDate());
        userService.updateUserProfile(sysUser);
    }
}
