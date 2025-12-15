package com.secondhand.system.domain;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.secondhand.common.annotation.Excel;
import com.secondhand.common.annotation.Excel.ColumnType;
import com.secondhand.common.core.domain.BaseEntity;

/**
 * 系统资源配置表 sys_resource_config
 * 
 * @author ruoyi
 */
public class SysResourceConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 资源ID */
    @Excel(name = "资源ID", cellType = ColumnType.NUMERIC)
    private Long resourceId;

    /** 资源类型（image图片 video视频） */
    @Excel(name = "资源类型", readConverterExp = "image=图片,video=视频")
    private String resourceType;

    /** 资源场景（login_app小程序登录页 login_app_email小程序邮箱登录页 login_admin管理端登录页 login_user用户端登录页 other其他） */
    @Excel(name = "资源场景")
    private String resourceScene;

    /** 资源名称 */
    @Excel(name = "资源名称")
    private String resourceName;

    /** 资源URL地址 */
    @Excel(name = "资源URL地址")
    private String resourceUrl;

    /** 显示顺序 */
    @Excel(name = "显示顺序", cellType = ColumnType.NUMERIC)
    private Integer sortOrder;

    /** 状态（0启用 1停用） */
    @Excel(name = "状态", readConverterExp = "0=启用,1=停用")
    private String status;

    public Long getResourceId()
    {
        return resourceId;
    }

    public void setResourceId(Long resourceId)
    {
        this.resourceId = resourceId;
    }

    @NotBlank(message = "资源类型不能为空")
    @Size(min = 0, max = 20, message = "资源类型长度不能超过20个字符")
    public String getResourceType()
    {
        return resourceType;
    }

    public void setResourceType(String resourceType)
    {
        this.resourceType = resourceType;
    }

    @NotBlank(message = "资源场景不能为空")
    @Size(min = 0, max = 50, message = "资源场景长度不能超过50个字符")
    public String getResourceScene()
    {
        return resourceScene;
    }

    public void setResourceScene(String resourceScene)
    {
        this.resourceScene = resourceScene;
    }

    @Size(min = 0, max = 100, message = "资源名称长度不能超过100个字符")
    public String getResourceName()
    {
        return resourceName;
    }

    public void setResourceName(String resourceName)
    {
        this.resourceName = resourceName;
    }

    @NotBlank(message = "资源URL地址不能为空")
    public String getResourceUrl()
    {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl)
    {
        this.resourceUrl = resourceUrl;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("resourceId", getResourceId())
            .append("resourceType", getResourceType())
            .append("resourceScene", getResourceScene())
            .append("resourceName", getResourceName())
            .append("resourceUrl", getResourceUrl())
            .append("sortOrder", getSortOrder())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}


