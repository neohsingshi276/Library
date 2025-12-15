package com.secondhand.web.controller.system;

import com.secondhand.common.constant.Constants;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.domain.entity.Client;
import com.secondhand.common.core.domain.model.ClientRegisterDTO;
import com.secondhand.common.core.domain.model.EmailLoginBody;
import com.secondhand.common.core.domain.model.LoginBody;
import com.secondhand.common.core.domain.model.LoginUser;
import com.secondhand.common.core.domain.model.RegisterBody;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.framework.web.service.SysLoginService;
import com.secondhand.framework.web.service.SysRegisterService;
import com.secondhand.framework.web.service.TokenService;
import com.secondhand.system.service.ISysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ClientLoginController extends BaseController {
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private ISysConfigService configService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/client/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.loginClient(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 注册方法
     *
     * @param registerBody 注册信息
     * @return 结果
     */
    @PostMapping("/client/register")
    public AjaxResult register(@RequestBody RegisterBody registerBody)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(registerBody);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }

    /**
     * 用户端邮箱注册方法
     *
     * @param clientRegisterDTO 注册信息
     * @return 结果
     */
    @PostMapping("/client/emailRegister")
    public AjaxResult emailRegister(@RequestBody ClientRegisterDTO clientRegisterDTO)
    {
        String msg = registerService.emailRegister(clientRegisterDTO);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }

    /**
     * 用户端邮箱验证码登录
     *
     * @param loginBody 登录信息（邮箱 + 验证码）
     * @return 结果
     */
    @PostMapping("/client/emailLogin")
    public AjaxResult emailLogin(@RequestBody EmailLoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        String token = loginService.loginClientByEmail(loginBody.getEmail(), loginBody.getCode());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取用户端用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/client/getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Client client = loginUser.getClient();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", client);
        // 设置默认角色和权限
        ajax.put("roles", new String[]{"ROLE_DEFAULT"});
        ajax.put("permissions", new String[]{});
        return ajax;
    }

    /**
     * 退出登录
     */
    @PostMapping("/client/logout")
    public AjaxResult logout(HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
        }
        return success();
    }
}
