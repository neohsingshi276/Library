package com.secondhand.system.service;

import com.secondhand.common.core.domain.entity.Client;

import java.util.List;

public interface ClientService {
    /**
     * 查询用户
     *
     * @param userId 用户主键
     * @return 用户
     */
    public Client selectClientByUserId(Long userId);

    /**
     * 查询用户列表
     *
     * @param client 用户
     * @return 用户集合
     */
    public List<Client> selectClientList(Client client);

    /**
     * 新增用户
     *
     * @param client 用户
     * @return 结果
     */
    public int insertClient(Client client);

    /**
     * 修改用户
     *
     * @param client 用户
     * @return 结果
     */
    public int updateClient(Client client);

    /**
     * 用户头像上传
     */
    boolean updateUserAvatar(String username, String url);

    /**
     * 用户修改密码
     */
    int resetUserPwd(String userName, String newPassword);

    /**
     * 判断手机号的唯一
     */
    boolean checkPhoneUnique(Client currentUser);

    /**
     * 判断该账号的唯一
     */
    boolean checkUsernameUnique(Client currentUser);

    /**
     * 判断邮箱的唯一
     */
    boolean checkEmailUnique(Client currentUser);

    /**
     * 判断昵称的唯一
     */
    boolean checkNameUnique(Client currentUser);

    /*
     * 修改用户数据
     * */
    int updateUserProfile(Client currentUser);

    /**
     * 重置密码
     * */
    int updateClientPwd(Client client);

    /**
     * 根据用户名查询用户
     * */
    Client searcgClient(String username);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的用户信息主键集合
     * @return 结果
     */
    public int deleteClientByUserIds(Long[] userIds);

    /**
     * 删除用户信息信息
     *
     * @param userId 用户信息主键
     * @return 结果
     */
    public int deleteClientByUserId(Long userId);

    /*
    * 状态修改
    * */
    int updateUserStatus(Client client);

    /*
    * 重置密码
    * */
    int resetPwd(Client client);

    /**
     * 更新用户信用分
     * @param userId 用户ID
     * @param creditScore 信用分
     * @return 结果
     */
    int updateCreditScore(Long userId, Integer creditScore);

    /**
     * 增加用户信用分
     * @param userId 用户ID
     * @param points 增加的分数
     * @return 结果
     */
    int increaseCreditScore(Long userId, Integer points);

    /**
     * 减少用户信用分
     * @param userId 用户ID
     * @param points 减少的分数
     * @return 结果
     */
    int decreaseCreditScore(Long userId, Integer points);

    /**
     * 增加累计交易金额
     * @param userId 用户ID
     * @param amount 增加的金额
     * @return 结果
     */
    int increaseTotalAmount(Long userId, java.math.BigDecimal amount);

    /**
     * 增加累计订单数
     * @param userId 用户ID
     * @return 结果
     */
    int increaseTotalOrders(Long userId);
}
