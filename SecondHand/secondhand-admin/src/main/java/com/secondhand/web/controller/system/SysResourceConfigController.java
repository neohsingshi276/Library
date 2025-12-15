package com.secondhand.web.controller.system;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.secondhand.common.annotation.Anonymous;
import com.secondhand.common.annotation.Log;
import com.secondhand.common.core.controller.BaseController;
import com.secondhand.common.core.domain.AjaxResult;
import com.secondhand.common.core.page.TableDataInfo;
import com.secondhand.common.enums.BusinessType;
import com.secondhand.common.utils.poi.ExcelUtil;
import com.secondhand.system.domain.SysResourceConfig;
import com.secondhand.system.service.ISysResourceConfigService;

/**
 * 系统资源配置 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/system/resourceConfig")
public class SysResourceConfigController extends BaseController
{
    @Autowired
    private ISysResourceConfigService resourceConfigService;

    /**
     * 获取系统资源配置列表
     */
    @PreAuthorize("@ss.hasPermi('system:resourceConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysResourceConfig resourceConfig)
    {
        startPage();
        List<SysResourceConfig> list = resourceConfigService.selectResourceConfigList(resourceConfig);
        return getDataTable(list);
    }

    @Log(title = "系统资源配置", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:resourceConfig:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysResourceConfig resourceConfig)
    {
        List<SysResourceConfig> list = resourceConfigService.selectResourceConfigList(resourceConfig);
        ExcelUtil<SysResourceConfig> util = new ExcelUtil<SysResourceConfig>(SysResourceConfig.class);
        util.exportExcel(response, list, "系统资源配置数据");
    }

    /**
     * 根据资源编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:resourceConfig:query')")
    @GetMapping(value = "/{resourceId}")
    public AjaxResult getInfo(@PathVariable Long resourceId)
    {
        return success(resourceConfigService.selectResourceConfigById(resourceId));
    }

    /**
     * 根据场景和类型获取资源（公开接口，无需权限）
     */
    @Anonymous
    @GetMapping("/getBySceneAndType")
    public AjaxResult getBySceneAndType(@RequestParam String resourceScene, @RequestParam(required = false) String resourceType)
    {
        if (resourceType == null || resourceType.isEmpty())
        {
            // 如果不指定类型，返回所有类型的资源
            List<SysResourceConfig> list = resourceConfigService.selectResourceConfigList(new SysResourceConfig() {{
                setResourceScene(resourceScene);
                setStatus("0");
            }});
            return success(list);
        }
        else
        {
            // 指定类型，返回第一个资源
            SysResourceConfig resourceConfig = resourceConfigService.selectFirstResourceConfigBySceneAndType(resourceScene, resourceType);
            return success(resourceConfig);
        }
    }

    /**
     * 根据场景和类型获取资源列表（公开接口，无需权限）
     */
    @Anonymous
    @GetMapping("/listBySceneAndType")
    public AjaxResult listBySceneAndType(@RequestParam String resourceScene, @RequestParam String resourceType)
    {
        List<SysResourceConfig> list = resourceConfigService.selectResourceConfigBySceneAndType(resourceScene, resourceType);
        return success(list);
    }

    /**
     * 新增系统资源配置
     */
    @PreAuthorize("@ss.hasPermi('system:resourceConfig:add')")
    @Log(title = "系统资源配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysResourceConfig resourceConfig)
    {
        resourceConfig.setCreateBy(getUsername());
        return toAjax(resourceConfigService.insertResourceConfig(resourceConfig));
    }

    /**
     * 修改系统资源配置
     */
    @PreAuthorize("@ss.hasPermi('system:resourceConfig:edit')")
    @Log(title = "系统资源配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysResourceConfig resourceConfig)
    {
        resourceConfig.setUpdateBy(getUsername());
        return toAjax(resourceConfigService.updateResourceConfig(resourceConfig));
    }

    /**
     * 删除系统资源配置
     */
    @PreAuthorize("@ss.hasPermi('system:resourceConfig:remove')")
    @Log(title = "系统资源配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{resourceIds}")
    public AjaxResult remove(@PathVariable Long[] resourceIds)
    {
        return toAjax(resourceConfigService.deleteResourceConfigByIds(resourceIds));
    }
}

