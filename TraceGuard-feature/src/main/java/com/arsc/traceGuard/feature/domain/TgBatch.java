package com.arsc.traceGuard.feature.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.arsc.traceGuard.common.annotation.Excel;
import com.arsc.traceGuard.common.core.domain.BaseEntity;

/**
 * 防伪码批次管理对象 tg_batch
 * 
 * @author zhangcj
 * @date 2026-01-26
 */
public class TgBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long batchId;

    /** 批次号 (唯一索引) */
    @Excel(name = "批次号 (唯一索引)")
    private String batchNo;

    /** 业务类型 (0=产品, 1=优惠券) */
    @Excel(name = "业务类型 (0=产品, 1=优惠券)")
    private String bizType;

    /** 关联ID (产品ID 或 优惠券ID) */
    @Excel(name = "关联ID (产品ID 或 优惠券ID)")
    private Long relationId;

    /** 冗余名称 (产品名/券名，避免列表查询联表) */
    @Excel(name = "冗余名称 (产品名/券名，避免列表查询联表)")
    private String relationName;

    /** 生码总数 */
    @Excel(name = "生码总数")
    private Long totalCount;

    /** 扫码总次数 (定期同步) */
    @Excel(name = "扫码总次数 (定期同步)")
    private Long scanCount;

    /** 已激活数量 */
    @Excel(name = "已激活数量")
    private Long activatedCount;

    /** 批次状态 (0正常 1冻结/作废) */
    @Excel(name = "批次状态 (0正常 1冻结/作废)")
    private String status;

    public void setBatchId(Long batchId) 
    {
        this.batchId = batchId;
    }

    public Long getBatchId() 
    {
        return batchId;
    }

    public void setBatchNo(String batchNo) 
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo() 
    {
        return batchNo;
    }

    public void setBizType(String bizType) 
    {
        this.bizType = bizType;
    }

    public String getBizType() 
    {
        return bizType;
    }

    public void setRelationId(Long relationId) 
    {
        this.relationId = relationId;
    }

    public Long getRelationId() 
    {
        return relationId;
    }

    public void setRelationName(String relationName) 
    {
        this.relationName = relationName;
    }

    public String getRelationName() 
    {
        return relationName;
    }

    public void setTotalCount(Long totalCount) 
    {
        this.totalCount = totalCount;
    }

    public Long getTotalCount() 
    {
        return totalCount;
    }

    public void setScanCount(Long scanCount) 
    {
        this.scanCount = scanCount;
    }

    public Long getScanCount() 
    {
        return scanCount;
    }

    public void setActivatedCount(Long activatedCount) 
    {
        this.activatedCount = activatedCount;
    }

    public Long getActivatedCount() 
    {
        return activatedCount;
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
            .append("batchId", getBatchId())
            .append("batchNo", getBatchNo())
            .append("bizType", getBizType())
            .append("relationId", getRelationId())
            .append("relationName", getRelationName())
            .append("totalCount", getTotalCount())
            .append("scanCount", getScanCount())
            .append("activatedCount", getActivatedCount())
            .append("status", getStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("updateBy", getUpdateBy())
            .toString();
    }
}
