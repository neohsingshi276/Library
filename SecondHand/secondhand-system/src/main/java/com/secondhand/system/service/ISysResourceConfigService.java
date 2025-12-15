package com.secondhand.system.service;

import java.util.List;
import com.secondhand.system.domain.SysResourceConfig;

/**
 * 系统资源配置 服务层
 *
 * @author ruoyi
 */
public interface ISysResourceConfigService
{
    /**
     * 查询系统资源配置信息
     *
     * @param resourceId 系统资源配置ID
     * @return 系统资源配置信息
     */
    SysResourceConfig selectResourceConfigById(Long resourceId);

    /**
     * 查询系统资源配置列表
     *
     * @param resourceConfig 系统资源配置信息
     * @return 系统资源配置集合
     */
    List<SysResourceConfig> selectResourceConfigList(SysResourceConfig resourceConfig);

    /**
     * 根据场景和类型查询启用的资源列表
     *
     * @param resourceScene 资源场景
     * @param resourceType  资源类型
     * @return 系统资源配置集合
     */
    List<SysResourceConfig> selectResourceConfigBySceneAndType(String resourceScene, String resourceType);

    /**
     * 根据场景和类型查询第一个启用的资源
     *
     * @param resourceScene 资源场景
     * @param resourceType  资源类型
     * @return 系统资源配置信息
     */
    SysResourceConfig selectFirstResourceConfigBySceneAndType(String resourceScene, String resourceType);

    /**
     * 新增系统资源配置
     *
     * @param resourceConfig 系统资源配置信息
     * @return 结果
     */
    int insertResourceConfig(SysResourceConfig resourceConfig);

    /**
     * 修改系统资源配置
     *
     * @param resourceConfig 系统资源配置信息
     * @return 结果
     */
    int updateResourceConfig(SysResourceConfig resourceConfig);

    /**
     * 批量删除系统资源配置
     *
     * @param resourceIds 需要删除的系统资源配置ID
     * @return 结果
     */
    int deleteResourceConfigByIds(Long[] resourceIds);

    /**
     * 删除系统资源配置信息
     *
     * @param resourceId 系统资源配置ID
     * @return 结果
     */
    int deleteResourceConfigById(Long resourceId);
}


