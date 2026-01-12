package com.arsc.traceGuard.feature.domain;

import com.arsc.traceGuard.common.annotation.Excel;
import com.arsc.traceGuard.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * 防伪码对象 tg_trace_code
 */
public class TgTraceCode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 1. 序号 (对应数据库ID) */
    private Long codeId;

    /** 2. 产品名称 (关联查询字段) */
    @Excel(name = "产品名称", width = 20)
    private String productName;

    /** 3. 生成批次号 */
    @Excel(name = "生成批次号", width = 20)
    private String batchNo;

    /** 4. 防伪码状态 */
    @Excel(name = "防伪码状态", readConverterExp = "2=待激活,0=正常,1=作废", combo = {"待激活", "正常", "作废"})
    private String status;

    /** 5. 扫码状态 */
    @Excel(name = "扫码状态", readConverterExp = "0=未扫码,1=已扫码")
    private String scanState;

    /** 6. 扫码次数 */
    @Excel(name = "扫码次数", cellType = Excel.ColumnType.NUMERIC)
    private Long scanCount;

    /** 7. 首次扫码时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "首次扫码时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date firstScanTime;

    /** 8. 首次扫码IP */
    @Excel(name = "首次扫码IP", width = 15)
    private String firstScanIp;

    /** 9. 首次扫码地址 */
    // 如果数据库没有这个字段，导出时会是空的，建议加上这个属性占位
    @Excel(name = "首次扫码地址", width = 20)
    private String firstScanLoc;

    /** 10. 防伪二维码链接 (虚拟字段，不存库，由后端计算) */
    @Excel(name = "防伪二维码链接", width = 150)
    private String qrCodeUrl;

    // --- 以下字段不导出 (@Excel 注解已移除) ---

    /** 防伪码值(UUID) - 敏感信息不导出明文 */
    private String codeValue;

    /** 产品ID */
    private Long productId;

    private Long codeCount;

    public Long getCodeId() {
        return codeId;
    }

    public void setCodeId(Long codeId) {
        this.codeId = codeId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScanState() {
        return scanState;
    }

    public void setScanState(String scanState) {
        this.scanState = scanState;
    }

    public Long getScanCount() {
        return scanCount;
    }

    public void setScanCount(Long scanCount) {
        this.scanCount = scanCount;
    }

    public Date getFirstScanTime() {
        return firstScanTime;
    }

    public void setFirstScanTime(Date firstScanTime) {
        this.firstScanTime = firstScanTime;
    }

    public String getFirstScanIp() {
        return firstScanIp;
    }

    public void setFirstScanIp(String firstScanIp) {
        this.firstScanIp = firstScanIp;
    }

    public String getFirstScanLoc() {
        return firstScanLoc;
    }

    public void setFirstScanLoc(String firstScanLoc) {
        this.firstScanLoc = firstScanLoc;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(Long codeCount) {
        this.codeCount = codeCount;
    }

    @Override
    public String toString() {
        return "TgTraceCode{" +
                "codeId=" + codeId +
                ", productName='" + productName + '\'' +
                ", batchNo='" + batchNo + '\'' +
                ", status='" + status + '\'' +
                ", scanState='" + scanState + '\'' +
                ", scanCount=" + scanCount +
                ", firstScanTime=" + firstScanTime +
                ", firstScanIp='" + firstScanIp + '\'' +
                ", firstScanLoc='" + firstScanLoc + '\'' +
                ", qrCodeUrl='" + qrCodeUrl + '\'' +
                ", codeValue='" + codeValue + '\'' +
                ", productId=" + productId +
                ", codeCount=" + codeCount +
                '}';
    }
}