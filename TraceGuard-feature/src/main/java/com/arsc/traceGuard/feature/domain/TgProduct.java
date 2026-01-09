package com.arsc.traceGuard.feature.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.arsc.traceGuard.common.annotation.Excel;
import com.arsc.traceGuard.common.core.domain.BaseEntity;

/**
 * 产品信息对象 tg_product
 * 
 * @author zhangcj
 * @date 2026-01-06
 */
public class TgProduct extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 产品编码 */
    private Long productId;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 使用说明 */
    private String instruction;

    /** 检测报告 */
    private String reportImg;

    /** 公司介绍 */
    private String companyInfo;

    /** 联系方式 */
    private String contactUs;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 删除标志 */
    private String delFlag;

    public void setProductId(Long productId) 
    {
        this.productId = productId;
    }

    public Long getProductId() 
    {
        return productId;
    }

    public void setProductName(String productName) 
    {
        this.productName = productName;
    }

    public String getProductName() 
    {
        return productName;
    }

    public void setInstruction(String instruction) 
    {
        this.instruction = instruction;
    }

    public String getInstruction() 
    {
        return instruction;
    }

    public void setReportImg(String reportImg) 
    {
        this.reportImg = reportImg;
    }

    public String getReportImg() 
    {
        return reportImg;
    }

    public void setCompanyInfo(String companyInfo) 
    {
        this.companyInfo = companyInfo;
    }

    public String getCompanyInfo() 
    {
        return companyInfo;
    }

    public void setContactUs(String contactUs) 
    {
        this.contactUs = contactUs;
    }

    public String getContactUs() 
    {
        return contactUs;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("productId", getProductId())
            .append("productName", getProductName())
            .append("instruction", getInstruction())
            .append("reportImg", getReportImg())
            .append("companyInfo", getCompanyInfo())
            .append("contactUs", getContactUs())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
