package com.arsc.traceGuard.feature.domain.dto;

public class CodeGenerateReq {
    private Long productId;
    private String batchNo;
    private Integer count;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
}
