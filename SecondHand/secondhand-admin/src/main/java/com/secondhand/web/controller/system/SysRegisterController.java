package com.secondhand.web.controller.system;

import com.secondhand.common.core.domain.entity.Client;
import com.secondhand.common.core.domain.model.ClientRegisterDTO;
import com.secondhand.common.utils.bean.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.domain.model.RegisterBody;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.framework.web.service.SysRegisterService;
import com.secondhand.system.service.ISysConfigService;

/**
 * 注册验证
 *
 * @author ruoyi
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    /*
    * 管理端用户注册
    * */
    @PostMapping("/register")
    public AjaxResult register(@RequestBody RegisterBody user)
    {
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }

    /** 用户端用户邮箱注册*/
    @PostMapping("/email/register")
    public AjaxResult register(@RequestBody ClientRegisterDTO user)
    {
        String msg = registerService.emailRegister(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
