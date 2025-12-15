package com.secondhand.common.core.domain.model;

/**
 * 邮箱登录对象
 *
 * @author ruoyi
 */
public class EmailLoginBody
{
    /**
     * 邮箱账号
     */
    private String email;

    /**
     * 邮箱验证码
     */
    private String code;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }
}


