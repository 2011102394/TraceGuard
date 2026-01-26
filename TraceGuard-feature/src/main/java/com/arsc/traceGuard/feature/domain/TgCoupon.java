package com.arsc.traceGuard.feature.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.arsc.traceGuard.common.annotation.Excel;
import com.arsc.traceGuard.common.core.domain.BaseEntity;

/**
 * 优惠券管理对象 tg_coupon
 * 
 * @author zhangcj
 * @date 2026-01-26
 */
public class TgCoupon extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 优惠券ID */
    private Long couponId;

    /** 优惠券名称 */
    @Excel(name = "优惠券名称")
    private String couponName;

    /** 面值/折扣 */
    @Excel(name = "面值")
    private BigDecimal couponValue;

    /** 有效期开始 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期开始", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 有效期结束 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "有效期结束", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endTime;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    public void setCouponId(Long couponId) 
    {
        this.couponId = couponId;
    }

    public Long getCouponId() 
    {
        return couponId;
    }

    public void setCouponName(String couponName) 
    {
        this.couponName = couponName;
    }

    public String getCouponName() 
    {
        return couponName;
    }

    public void setCouponValue(BigDecimal couponValue) 
    {
        this.couponValue = couponValue;
    }

    public BigDecimal getCouponValue() 
    {
        return couponValue;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
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
            .append("couponId", getCouponId())
            .append("couponName", getCouponName())
            .append("couponValue", getCouponValue())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
