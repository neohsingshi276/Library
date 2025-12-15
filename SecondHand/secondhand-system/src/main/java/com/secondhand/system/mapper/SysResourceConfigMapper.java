package com.secondhand.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.secondhand.system.domain.SysResourceConfig;

/**
 * 系统资源配置 数据层
 * 
 * @author ruoyi
 */
public interface SysResourceConfigMapper
{
    /**
     * 查询系统资源配置信息
     * 
     * @param resourceId 系统资源配置ID
     * @return 系统资源配置信息
     */
    public SysResourceConfig selectResourceConfigById(Long resourceId);

    /**
     * 查询系统资源配置列表
     * 
     * @param resourceConfig 系统资源配置信息
     * @return 系统资源配置集合
     */
    public List<SysResourceConfig> selectResourceConfigList(SysResourceConfig resourceConfig);

    /**
     * 根据场景和类型查询启用的资源列表
     * 
     * @param resourceScene 资源场景
     * @param resourceType 资源类型
     * @return 系统资源配置集合
     */
    public List<SysResourceConfig> selectResourceConfigBySceneAndType(@Param("resourceScene") String resourceScene, @Param("resourceType") String resourceType);

    /**
     * 新增系统资源配置
     * 
     * @param resourceConfig 系统资源配置信息
     * @return 结果
     */
    public int insertResourceConfig(SysResourceConfig resourceConfig);

    /**
     * 修改系统资源配置
     * 
     * @param resourceConfig 系统资源配置信息
     * @return 结果
     */
    public int updateResourceConfig(SysResourceConfig resourceConfig);

    /**
     * 删除系统资源配置
     * 
     * @param resourceId 系统资源配置ID
     * @return 结果
     */
    public int deleteResourceConfigById(Long resourceId);

    /**
     * 批量删除系统资源配置
     * 
     * @param resourceIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteResourceConfigByIds(Long[] resourceIds);
}

