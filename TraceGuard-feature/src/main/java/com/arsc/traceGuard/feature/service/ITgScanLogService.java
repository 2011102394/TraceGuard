package com.arsc.traceGuard.feature.service;

import java.util.List;
import com.arsc.traceGuard.feature.domain.TgScanLog;

/**
 * 防伪扫描记录Service接口
 * * @author arsc
 */
public interface ITgScanLogService
{
    /**
     * 查询防伪扫描记录
     * * @param logId 防伪扫描记录主键
     * @return 防伪扫描记录
     */
    public TgScanLog selectTgScanLogByLogId(Long logId);

    /**
     * 查询防伪扫描记录列表
     * * @param tgScanLog 防伪扫描记录
     * @return 防伪扫描记录集合
     */
    public List<TgScanLog> selectTgScanLogList(TgScanLog tgScanLog);

    /**
     * 新增防伪扫描记录
     * * @param tgScanLog 防伪扫描记录
     * @return 结果
     */
    public int insertTgScanLog(TgScanLog tgScanLog);

    /**
     * 修改防伪扫描记录
     * * @param tgScanLog 防伪扫描记录
     * @return 结果
     */
    public int updateTgScanLog(TgScanLog tgScanLog);

    /**
     * 批量删除防伪扫描记录
     * * @param logIds 需要删除的防伪扫描记录主键集合
     * @return 结果
     */
    public int deleteTgScanLogByLogIds(Long[] logIds);

    /**
     * 删除防伪扫描记录信息
     * * @param logId 防伪扫描记录主键
     * @return 结果
     */
    public int deleteTgScanLogByLogId(Long logId);
}