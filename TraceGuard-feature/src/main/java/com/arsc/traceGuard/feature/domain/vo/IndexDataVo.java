package com.arsc.traceGuard.feature.domain.vo;

import java.util.List;
import java.util.Map;

/**
 * 首页数据聚合 VO
 */
public class IndexDataVo {
    // 1. 核心卡片数据
    private long totalProduct;      // 产品总数
    private long totalCode;         // 赋码总量
    private long totalScan;         // 累计扫码
    private long abnormalScan;      // 异常扫码(高危)

    // 2. 图表数据
    private List<String> dateList;  // 近7天日期 (x轴)
    private List<Long> scanTrend;   // 扫码趋势 (y轴)

    // 3. 排名数据
    private List<Map<String, Object>> productRank; // 产品扫码排名

    // 4. 最新动态
    private List<Map<String, Object>> recentLogs;  // 最新日志

    // 省份扫码统计 (用于地图)
    private List<Map<String, Object>> provinceStat;

    // Getters & Setters ...
    public long getTotalProduct() { return totalProduct; }
    public void setTotalProduct(long totalProduct) { this.totalProduct = totalProduct; }
    public long getTotalCode() { return totalCode; }
    public void setTotalCode(long totalCode) { this.totalCode = totalCode; }
    public long getTotalScan() { return totalScan; }
    public void setTotalScan(long totalScan) { this.totalScan = totalScan; }
    public long getAbnormalScan() { return abnormalScan; }
    public void setAbnormalScan(long abnormalScan) { this.abnormalScan = abnormalScan; }
    public List<String> getDateList() { return dateList; }
    public void setDateList(List<String> dateList) { this.dateList = dateList; }
    public List<Long> getScanTrend() { return scanTrend; }
    public void setScanTrend(List<Long> scanTrend) { this.scanTrend = scanTrend; }
    public List<Map<String, Object>> getProductRank() { return productRank; }
    public void setProductRank(List<Map<String, Object>> productRank) { this.productRank = productRank; }
    public List<Map<String, Object>> getRecentLogs() { return recentLogs; }
    public void setRecentLogs(List<Map<String, Object>> recentLogs) { this.recentLogs = recentLogs; }

    public List<Map<String, Object>> getProvinceStat() {
        return provinceStat;
    }

    public void setProvinceStat(List<Map<String, Object>> provinceStat) {
        this.provinceStat = provinceStat;
    }
}