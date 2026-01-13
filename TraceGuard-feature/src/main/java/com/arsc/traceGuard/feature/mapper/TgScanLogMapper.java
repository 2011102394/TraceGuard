package com.arsc.traceGuard.feature.mapper;

import java.util.List;
import java.util.Map;

import com.arsc.traceGuard.feature.domain.TgScanLog;

/**
 * 防伪扫描记录Mapper接口
 */
public interface TgScanLogMapper
{
    /**
     * 查询防伪扫描记录
     */
    public TgScanLog selectTgScanLogByLogId(Long logId);

    /**
     * 查询防伪扫描记录列表
     */
    public List<TgScanLog> selectTgScanLogList(TgScanLog tgScanLog);

    /**
     * 新增防伪扫描记录
     */
    public int insertTgScanLog(TgScanLog tgScanLog);

    /**
     * 修改防伪扫描记录
     */
    public int updateTgScanLog(TgScanLog tgScanLog);

    /**
     * 删除防伪扫描记录
     */
    public int deleteTgScanLogByLogId(Long logId);

    /**
     * 批量删除防伪扫描记录
     */
    public int deleteTgScanLogByLogIds(Long[] logIds);

    public Long countTotalScan();

    public Long countAbnormalScan();

    public List<Map<String, Object>> selectScanTrend();

    public List<Map<String, Object>> selectRecentLogs();

    public List<String> selectLocationList();

}