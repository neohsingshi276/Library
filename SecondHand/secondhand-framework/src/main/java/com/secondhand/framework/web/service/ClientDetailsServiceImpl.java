package com.secondhand.framework.web.service;

import com.secondhand.common.core.domain.entity.Client;
import com.secondhand.common.core.domain.model.LoginUser;
import com.secondhand.common.exception.ServiceException;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.system.mapper.ClientMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component("ClientDetailsServiceImpl")
public class ClientDetailsServiceImpl  implements UserDetailsService{

    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ClientMapper clientMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client client = clientMapper.selectUserByUserName(username);
        if (StringUtils.isNull(client)) {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException("登录用户：" + username + " 不存在");
        }
        return createLoginUser(client);
    }

    public UserDetails createLoginUser(Client member) {
        return new LoginUser(member.getUserId(), member);
    }
}
