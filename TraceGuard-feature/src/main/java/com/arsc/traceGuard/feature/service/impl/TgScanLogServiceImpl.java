package com.arsc.traceGuard.feature.service.impl;

import java.util.List;
import com.arsc.traceGuard.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.arsc.traceGuard.feature.mapper.TgScanLogMapper;
import com.arsc.traceGuard.feature.domain.TgScanLog;
import com.arsc.traceGuard.feature.service.ITgScanLogService;

/**
 * 防伪扫描记录Service业务层处理
 * * @author arsc
 */
@Service
public class TgScanLogServiceImpl implements ITgScanLogService
{
    @Autowired
    private TgScanLogMapper tgScanLogMapper;

    /**
     * 查询防伪扫描记录
     * * @param logId 防伪扫描记录主键
     * @return 防伪扫描记录
     */
    @Override
    public TgScanLog selectTgScanLogByLogId(Long logId)
    {
        return tgScanLogMapper.selectTgScanLogByLogId(logId);
    }

    /**
     * 查询防伪扫描记录列表
     * * @param tgScanLog 防伪扫描记录
     * @return 防伪扫描记录
     */
    @Override
    public List<TgScanLog> selectTgScanLogList(TgScanLog tgScanLog)
    {
        return tgScanLogMapper.selectTgScanLogList(tgScanLog);
    }

    /**
     * 新增防伪扫描记录
     * * @param tgScanLog 防伪扫描记录
     * @return 结果
     */
    @Override
    public int insertTgScanLog(TgScanLog tgScanLog)
    {
        // 如果没有传入创建时间，则默认当前时间
        if (tgScanLog.getCreateTime() == null) {
            tgScanLog.setCreateTime(DateUtils.getNowDate());
        }
        return tgScanLogMapper.insertTgScanLog(tgScanLog);
    }

    /**
     * 修改防伪扫描记录
     * * @param tgScanLog 防伪扫描记录
     * @return 结果
     */
    @Override
    public int updateTgScanLog(TgScanLog tgScanLog)
    {
        // 更新时自动刷新更新时间
        tgScanLog.setUpdateTime(DateUtils.getNowDate());
        return tgScanLogMapper.updateTgScanLog(tgScanLog);
    }

    /**
     * 批量删除防伪扫描记录
     * * @param logIds 需要删除的防伪扫描记录主键
     * @return 结果
     */
    @Override
    public int deleteTgScanLogByLogIds(Long[] logIds)
    {
        return tgScanLogMapper.deleteTgScanLogByLogIds(logIds);
    }

    /**
     * 删除防伪扫描记录信息
     * * @param logId 防伪扫描记录主键
     * @return 结果
     */
    @Override
    public int deleteTgScanLogByLogId(Long logId)
    {
        return tgScanLogMapper.deleteTgScanLogByLogId(logId);
    }
}