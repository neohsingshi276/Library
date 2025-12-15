package com.secondhand.web.controller.system;

import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.secondhand.common.constant.Constants;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.domain.entity.SysMenu;
import com.secondhand.common.core.domain.entity.SysUser;
import com.secondhand.common.core.domain.model.LoginBody;
import com.secondhand.common.core.domain.model.EmailLoginBody;
import com.secondhand.common.core.domain.model.LoginUser;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.framework.web.service.SysLoginService;
import com.secondhand.framework.web.service.SysPermissionService;
import com.secondhand.framework.web.service.TokenService;
import com.secondhand.system.service.ISysMenuService;

/**
 * 登录验证
 *
 * @author ruoyi
 */
@RestController
public class AdminLoginController
{
    @Autowired
    private SysLoginService loginService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private TokenService tokenService;

    /**
     * 登录方法
     *
     * @param loginBody 登录信息
     * @return 结果
     */
    @PostMapping("/admin/login")
    public AjaxResult login(@RequestBody LoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        // 生成令牌
        String token = loginService.loginAdmin(loginBody.getUsername(), loginBody.getPassword(), loginBody.getCode(),
                loginBody.getUuid());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 管理端邮箱验证码登录
     *
     * @param loginBody 登录信息（邮箱 + 验证码）
     * @return 结果
     */
    @PostMapping("/admin/emailLogin")
    public AjaxResult emailLogin(@RequestBody EmailLoginBody loginBody)
    {
        AjaxResult ajax = AjaxResult.success();
        String token = loginService.loginAdminByEmail(loginBody.getEmail(), loginBody.getCode());
        ajax.put(Constants.TOKEN, token);
        return ajax;
    }

    /**
     * 获取管理端用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/admin/getInfo")
    public AjaxResult getInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(user);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(user);
        if (!loginUser.getPermissions().equals(permissions))
        {
            loginUser.setPermissions(permissions);
            tokenService.refreshToken(loginUser);
        }
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 获取路由信息
     *
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public AjaxResult getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return AjaxResult.success(menuService.buildMenus(menus));
    }
}
