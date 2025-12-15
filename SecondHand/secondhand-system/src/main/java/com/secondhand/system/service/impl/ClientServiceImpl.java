package com.secondhand.system.service.impl;

import com.secondhand.common.constant.UserConstants;
import com.secondhand.common.core.domain.entity.Client;
import com.secondhand.common.utils.DateUtils;
import com.secondhand.common.utils.SecurityUtils;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.system.mapper.ClientMapper;
import com.secondhand.system.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientMapper clientMapper;


    /**
     * 根据用户id查询用户
     *
     * @param userId 用户Id
     * @return 用户
     */
    public Client selectClientByUserId(Long userId)
    {
        return clientMapper.selectUserById(userId);
    }

    /**
     * 查询用户列表
     *
     * @param Client 用户
     * @return 用户
     */
    @Override
    public List<Client> selectClientList(Client Client)
    {
        return clientMapper.selectClientList(Client);
    }

    /**
     * 新增用户
     *
     * @param Client 用户
     * @return 结果
     */
    @Override
    public int insertClient(Client Client)
    {
        Client.setCreateTime(DateUtils.getNowDate());
        return clientMapper.insertClient(Client);
    }

    /**
     * 修改用户
     *
     * @param Client 用户
     * @return 结果
     */
    @Override
    public int updateClient(Client Client)
    {
        Client.setUpdateTime(DateUtils.getNowDate());
        return clientMapper.updateClient(Client);
    }

    /**
     * 用户头像上传
     * */
    public boolean updateUserAvatar(String username, String avatar) {
        return clientMapper.updateUserAvatar(username, avatar) > 0;
    }

    /**
     * 用户修改密码
     * */
    public int resetUserPwd(String userName, String newPassword) {
        return clientMapper.resetUserPwd(userName, newPassword);
    }

    /**
     * 校验手机号的唯一性
     * */
    public boolean checkPhoneUnique(Client user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        Client info = clientMapper.checkPhoneUnique(user.getPhonenumber());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验账号是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public boolean checkUsernameUnique(Client user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        Client info = clientMapper.checkUserNameUnique(user.getUserName());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验邮箱是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public boolean checkEmailUnique(Client user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        Client info = clientMapper.checkEmailUnique(user.getEmail());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 校验昵称是否唯一
     *
     * @param user 用户信息
     * @return
     */
    public boolean checkNameUnique(Client user) {
        Long userId = StringUtils.isNull(user.getUserId()) ? -1L : user.getUserId();
        Client info = clientMapper.checkNameUnique(user.getNickName());
        if (StringUtils.isNotNull(info) && info.getUserId().longValue() != userId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 修改用户
     * */
    public int updateUserProfile(Client user) {
        return clientMapper.updateClient(user);
    }

    /**
     * 重置密码
     * */
    public int updateClientPwd(Client Client) {
        Client.setPassword(SecurityUtils.encryptPassword("123456"));
        return clientMapper.updateClient(Client);
    }

    /**
     * 根据用户名查询用户
     * */
    public Client searcgClient(String username) {
        return clientMapper.checkUserNameUnique(username);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户信息主键
     * @return 结果
     */
    @Override
    public int deleteClientByUserIds(Long[] userIds)
    {
        return clientMapper.deleteClientByUserIds(userIds);
    }

    /**
     * 删除用户信息信息
     *
     * @param userId 用户信息主键
     * @return 结果
     */
    @Override
    public int deleteClientByUserId(Long userId)
    {
        return clientMapper.deleteClientByUserId(userId);
    }

    /*
    * 修改状态
    * */
    @Override
    public int updateUserStatus(Client client) {
        return clientMapper.updateClient(client);
    }

    /*
    * 重置密码
    * */
    @Override
    public int resetPwd(Client client) {
        return clientMapper.updateClient(client);
    }

    @Override
    public int updateCreditScore(Long userId, Integer creditScore) {
        return clientMapper.updateCreditScore(userId, creditScore);
    }

    @Override
    public int increaseCreditScore(Long userId, Integer points) {
        return clientMapper.increaseCreditScore(userId, points);
    }

    @Override
    public int decreaseCreditScore(Long userId, Integer points) {
        return clientMapper.decreaseCreditScore(userId, points);
    }

    @Override
    public int increaseTotalAmount(Long userId, java.math.BigDecimal amount) {
        return clientMapper.increaseTotalAmount(userId, amount);
    }

    @Override
    public int increaseTotalOrders(Long userId) {
        return clientMapper.increaseTotalOrders(userId);
    }
}
