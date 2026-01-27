package com.arsc.traceGuard.feature.domain.dto;

public class CodeGenerateReq {
    /**
     * 产品编码
     */
    private Long productId;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 防伪码数量
     */
    private Integer count;


    /**
     * 防伪码类型 - 0 产品防伪码  1 优惠券防伪码
     */
    private String type;

    /**
     * 优惠券编码
     */
    private Long couponId;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getBatchNo() { return batchNo; }
    public void setBatchNo(String batchNo) { this.batchNo = batchNo; }
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    @Override
    public String toString() {
        return "CodeGenerateReq{" +
                "productId=" + productId +
                ", batchNo='" + batchNo + '\'' +
                ", count=" + count +
                ", type='" + type + '\'' +
                ", couponId=" + couponId +
                '}';
    }
}
