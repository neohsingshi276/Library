package com.secondhand.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.secondhand.common.annotation.DataSource;
import com.secondhand.common.core.text.Convert;
import com.secondhand.common.enums.DataSourceType;
import com.secondhand.common.utils.StringUtils;
import com.secondhand.system.domain.SysResourceConfig;
import com.secondhand.system.mapper.SysResourceConfigMapper;
import com.secondhand.system.service.ISysResourceConfigService;

/**
 * 系统资源配置 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysResourceConfigServiceImpl implements ISysResourceConfigService
{
    @Autowired
    private SysResourceConfigMapper resourceConfigMapper;

    /**
     * 查询系统资源配置信息
     * 
     * @param resourceId 系统资源配置ID
     * @return 系统资源配置信息
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public SysResourceConfig selectResourceConfigById(Long resourceId)
    {
        return resourceConfigMapper.selectResourceConfigById(resourceId);
    }

    /**
     * 查询系统资源配置列表
     * 
     * @param resourceConfig 系统资源配置信息
     * @return 系统资源配置集合
     */
    @Override
    public List<SysResourceConfig> selectResourceConfigList(SysResourceConfig resourceConfig)
    {
        return resourceConfigMapper.selectResourceConfigList(resourceConfig);
    }

    /**
     * 根据场景和类型查询启用的资源列表
     * 
     * @param resourceScene 资源场景
     * @param resourceType 资源类型
     * @return 系统资源配置集合
     */
    @Override
    public List<SysResourceConfig> selectResourceConfigBySceneAndType(String resourceScene, String resourceType)
    {
        return resourceConfigMapper.selectResourceConfigBySceneAndType(resourceScene, resourceType);
    }

    /**
     * 根据场景和类型查询第一个启用的资源
     * 
     * @param resourceScene 资源场景
     * @param resourceType 资源类型
     * @return 系统资源配置信息
     */
    @Override
    public SysResourceConfig selectFirstResourceConfigBySceneAndType(String resourceScene, String resourceType)
    {
        List<SysResourceConfig> list = resourceConfigMapper.selectResourceConfigBySceneAndType(resourceScene, resourceType);
        if (list != null && !list.isEmpty())
        {
            return list.get(0);
        }
        return null;
    }

    /**
     * 新增系统资源配置
     * 
     * @param resourceConfig 系统资源配置信息
     * @return 结果
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public int insertResourceConfig(SysResourceConfig resourceConfig)
    {
        if (resourceConfig.getSortOrder() == null)
        {
            resourceConfig.setSortOrder(0);
        }
        if (StringUtils.isEmpty(resourceConfig.getStatus()))
        {
            resourceConfig.setStatus("0");
        }
        return resourceConfigMapper.insertResourceConfig(resourceConfig);
    }

    /**
     * 修改系统资源配置
     * 
     * @param resourceConfig 系统资源配置信息
     * @return 结果
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public int updateResourceConfig(SysResourceConfig resourceConfig)
    {
        return resourceConfigMapper.updateResourceConfig(resourceConfig);
    }

    /**
     * 批量删除系统资源配置
     * 
     * @param resourceIds 需要删除的系统资源配置ID
     * @return 结果
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public int deleteResourceConfigByIds(Long[] resourceIds)
    {
        return resourceConfigMapper.deleteResourceConfigByIds(resourceIds);
    }

    /**
     * 删除系统资源配置信息
     * 
     * @param resourceId 系统资源配置ID
     * @return 结果
     */
    @Override
    @DataSource(DataSourceType.MASTER)
    public int deleteResourceConfigById(Long resourceId)
    {
        return resourceConfigMapper.deleteResourceConfigById(resourceId);
    }
}


