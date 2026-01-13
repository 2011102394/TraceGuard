package com.arsc.traceGuard.feature.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.arsc.traceGuard.common.annotation.Excel;
import com.arsc.traceGuard.common.core.domain.BaseEntity;

/**
 * 防伪扫描记录对象 tg_scan_log
 */
public class TgScanLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 日志ID */
    private Long logId;

    /** 关联防伪码 */
    @Excel(name = "防伪码")
    private String codeValue;

    /** 扫描者IP */
    @Excel(name = "扫描者IP")
    private String scanIp;

    /** 扫描位置 */
    @Excel(name = "扫描位置")
    private String scanLocation;

    /** 浏览器标识 */
    @Excel(name = "浏览器标识")
    private String browserInfo;

    /** 状态（0正常 1异常） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=异常")
    private String status;

    public void setLogId(Long logId)
    {
        this.logId = logId;
    }

    public Long getLogId()
    {
        return logId;
    }
    public void setCodeValue(String codeValue)
    {
        this.codeValue = codeValue;
    }

    public String getCodeValue()
    {
        return codeValue;
    }
    public void setScanIp(String scanIp)
    {
        this.scanIp = scanIp;
    }

    public String getScanIp()
    {
        return scanIp;
    }
    public void setScanLocation(String scanLocation)
    {
        this.scanLocation = scanLocation;
    }

    public String getScanLocation()
    {
        return scanLocation;
    }
    public void setBrowserInfo(String browserInfo)
    {
        this.browserInfo = browserInfo;
    }

    public String getBrowserInfo()
    {
        return browserInfo;
    }
    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("logId", getLogId())
                .append("codeValue", getCodeValue())
                .append("scanIp", getScanIp())
                .append("scanLocation", getScanLocation())
                .append("browserInfo", getBrowserInfo())
                .append("status", getStatus())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }
}