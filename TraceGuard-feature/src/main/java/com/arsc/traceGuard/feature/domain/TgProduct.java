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

    /** 质检参数 (JSON) */
    private String qualityJson;

    /** 检测报告PDF文件 */
    private String reportFile;

    /** 检测报告预览图 */
    private String reportImage;

    /** 使用攻略 (富文本) */
    private String usageContent;

    /** 企业简介 (富文本) */
    private String companyContent;

    /** 产品图片*/
    private String productImage;

    /**公司地址*/
    private String address;

    /**公司logo*/
    private String companyLogo;

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getReportImg() {
        return reportImg;
    }

    public void setReportImg(String reportImg) {
        this.reportImg = reportImg;
    }

    public String getCompanyInfo() {
        return companyInfo;
    }

    public void setCompanyInfo(String companyInfo) {
        this.companyInfo = companyInfo;
    }

    public String getContactUs() {
        return contactUs;
    }

    public void setContactUs(String contactUs) {
        this.contactUs = contactUs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getQualityJson() {
        return qualityJson;
    }

    public void setQualityJson(String qualityJson) {
        this.qualityJson = qualityJson;
    }

    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    public String getReportImage() {
        return reportImage;
    }

    public void setReportImage(String reportImage) {
        this.reportImage = reportImage;
    }

    public String getUsageContent() {
        return usageContent;
    }

    public void setUsageContent(String usageContent) {
        this.usageContent = usageContent;
    }

    public String getCompanyContent() {
        return companyContent;
    }

    public void setCompanyContent(String companyContent) {
        this.companyContent = companyContent;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    @Override
    public String toString() {
        return "TgProduct{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", instruction='" + instruction + '\'' +
                ", reportImg='" + reportImg + '\'' +
                ", companyInfo='" + companyInfo + '\'' +
                ", contactUs='" + contactUs + '\'' +
                ", status='" + status + '\'' +
                ", delFlag='" + delFlag + '\'' +
                ", qualityJson='" + qualityJson + '\'' +
                ", reportFile='" + reportFile + '\'' +
                ", reportImage='" + reportImage + '\'' +
                ", usageContent='" + usageContent + '\'' +
                ", companyContent='" + companyContent + '\'' +
                '}';
    }
}
