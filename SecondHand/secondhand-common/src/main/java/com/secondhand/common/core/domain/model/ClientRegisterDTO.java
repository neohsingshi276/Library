package com.secondhand.common.core.domain.model;

import com.secondhand.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 用户端用户注册对象
 *
 * @author mmq
 * @date 2024-10-12
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegisterDTO {

    /** 用户账号 */
    private String userName;

    /** 用户昵称 */
    private String nickName;

    /** 用户邮箱 */
    private String email;

    /** 手机号码 */
    private String phonenumber;

    /** 用户性别（0男 1女 2未知） */
    private String sex;

    /** 密码 */
    private String password;

    /** 验证码 */
    private String code;
}
