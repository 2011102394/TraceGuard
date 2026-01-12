package com.arsc.traceGuard.feature.domain;


import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.arsc.traceGuard.common.annotation.Excel;
import com.arsc.traceGuard.common.core.domain.BaseEntity;

/**
 * 防伪码对象 tg_trace_code
 * * @author arsc
 * @date 2026-01-12
 */
public class TgTraceCode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 防伪码ID */
    private Long codeId;

    /** 关联产品ID */
    @Excel(name = "关联产品ID")
    private Long productId;

    /** 防伪码唯一值(UUID) */
    @Excel(name = "防伪码唯一值")
    private String codeValue;

    /** 生产批次号 */
    @Excel(name = "生产批次号")
    private String batchNo;

    /** 扫码状态（0未扫描 1已扫描） */
    @Excel(name = "扫码状态", readConverterExp = "0=未扫描,1=已扫描")
    private String scanState;

    /** 被扫次数 */
    @Excel(name = "被扫次数")
    private Long scanCount;

    /** 首次扫描时间 (新增) */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "首次扫描时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date firstScanTime;

    /** 首次扫描IP (新增) */
    @Excel(name = "首次扫描IP")
    private String firstScanIp;

    /** 首次扫描地址(省市) (新增) */
    @Excel(name = "首次扫描地址")
    private String firstScanLoc;

    /** 码状态（0正常 1作废） */
    @Excel(name = "码状态", readConverterExp = "0=正常,1=作废")
    private String status;

    /** * 产品名称 (非数据库表字段，由关联查询得出)
     */
    @Excel(name = "产品名称") // 加这个注解，导出Excel时也会自动带上
    private String productName;

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    // ==========================================
    // 业务扩展字段 (非数据库字段，用于前端展示或传参)
    // ==========================================

    /** 该批次的码数量 (用于批次列表展示) */
    private Long codeCount;

    // ==========================================
    // Getter & Setter 方法
    // ==========================================

    public void setCodeId(Long codeId)
    {
        this.codeId = codeId;
    }

    public Long getCodeId()
    {
        return codeId;
    }
    public void setProductId(Long productId)
    {
        this.productId = productId;
    }

    public Long getProductId()
    {
        return productId;
    }
    public void setCodeValue(String codeValue)
    {
        this.codeValue = codeValue;
    }

    public String getCodeValue()
    {
        return codeValue;
    }
    public void setBatchNo(String batchNo)
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo()
    {
        return batchNo;
    }
    public void setScanState(String scanState)
    {
        this.scanState = scanState;
    }

    public String getScanState()
    {
        return scanState;
    }
    public void setScanCount(Long scanCount)
    {
        this.scanCount = scanCount;
    }

    public Long getScanCount()
    {
        return scanCount;
    }
    public void setFirstScanTime(Date firstScanTime)
    {
        this.firstScanTime = firstScanTime;
    }

    public Date getFirstScanTime()
    {
        return firstScanTime;
    }
    public void setFirstScanIp(String firstScanIp)
    {
        this.firstScanIp = firstScanIp;
    }

    public String getFirstScanIp()
    {
        return firstScanIp;
    }
    public void setFirstScanLoc(String firstScanLoc)
    {
        this.firstScanLoc = firstScanLoc;
    }

    public String getFirstScanLoc()
    {
        return firstScanLoc;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public Long getCodeCount() {
        return codeCount;
    }

    public void setCodeCount(Long codeCount) {
        this.codeCount = codeCount;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("codeId", getCodeId())
                .append("productId", getProductId())
                .append("codeValue", getCodeValue())
                .append("batchNo", getBatchNo())
                .append("scanState", getScanState())
                .append("scanCount", getScanCount())
                .append("firstScanTime", getFirstScanTime())
                .append("firstScanIp", getFirstScanIp())
                .append("firstScanLoc", getFirstScanLoc())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}