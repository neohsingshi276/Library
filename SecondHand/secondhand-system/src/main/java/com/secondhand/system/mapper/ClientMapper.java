package com.secondhand.system.mapper;

import com.secondhand.common.core.domain.entity.Client;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户表 数据层
 *
 * @author ruoyi
 */
public interface ClientMapper
{
    /**
     * 查询用户信息列表
     *
     * @param client 用户信息
     * @return 用户信息集合
     */
    public List<Client> selectClientList(Client client);

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    public Client selectUserByUserName(String userName);

    /**
     * 通过用户ID查询用户
     *
     * @param userId 用户ID
     * @return 用户对象信息
     */
    public Client selectUserById(Long userId);

    /**
     * 新增用户信息
     *
     * @param client 用户信息
     * @return 结果
     */
    public int insertClient(Client client);

    /**
     * 修改用户信息
     *
     * @param client 用户信息
     * @return 结果
     */
    public int updateClient(Client client);

    /**
     * 重置用户密码
     *
     * @param userName 用户名
     * @param password 密码
     * @return 结果
     */
    public int resetUserPwd(@Param("userName") String userName, @Param("password") String password);

    /**
     * 校验用户名称是否唯一
     *
     * @param userName 用户名称
     * @return 结果
     */
    public Client checkUserNameUnique(String userName);

    /**
     * 校验手机号码是否唯一
     *
     * @param phonenumber 手机号码
     * @return 结果
     */
    public Client checkPhoneUnique(String phonenumber);

    /**
     * 校验email是否唯一
     *
     * @param email 用户邮箱
     * @return 结果
     */
    public Client checkEmailUnique(String email);

    /**
     * 校验nickName是否唯一
     *
     * @param nickName 用户昵称
     * @return 结果
     */
    Client checkNameUnique(String nickName);

    /**
     * 删除用户信息
     *
     * @param userId 用户信息主键
     * @return 结果
     */
    public int deleteClientByUserId(Long userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteClientByUserIds(Long[] userIds);

    /**
     * 用户头像上传
     * */
    int updateUserAvatar(@Param("userName")String userName, @Param("avatar")String avatar);

    /**
     * 更新用户信用分
     * @param userId 用户ID
     * @param creditScore 信用分
     * @return 结果
     */
    int updateCreditScore(@Param("userId") Long userId, @Param("creditScore") Integer creditScore);

    /**
     * 增加用户信用分
     * @param userId 用户ID
     * @param points 增加的分数
     * @return 结果
     */
    int increaseCreditScore(@Param("userId") Long userId, @Param("points") Integer points);

    /**
     * 减少用户信用分
     * @param userId 用户ID
     * @param points 减少的分数
     * @return 结果
     */
    int decreaseCreditScore(@Param("userId") Long userId, @Param("points") Integer points);

    /**
     * 增加累计交易金额
     * @param userId 用户ID
     * @param amount 增加的金额
     * @return 结果
     */
    int increaseTotalAmount(@Param("userId") Long userId, @Param("amount") java.math.BigDecimal amount);

    /**
     * 增加累计订单数
     * @param userId 用户ID
     * @return 结果
     */
    int increaseTotalOrders(@Param("userId") Long userId);

    /**
     * 统计用户总数
     */
    Long countAllClients();

    /**
     * 统计今日新增用户
     */
    Long countTodayClients();

    /**
     * 统计本月新增用户
     */
    Long countThisMonthClients();

    /**
     * 统计上月新增用户
     */
    Long countLastMonthClients();

    /**
     * 获取最近7天的用户增长趋势
     */
    List<Map<String, Object>> getDailyUserGrowth();
}
