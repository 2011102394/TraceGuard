package com.arsc.traceGuard.feature.service.impl;

import java.util.List;
import com.arsc.traceGuard.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.arsc.traceGuard.feature.mapper.TgBatchMapper;
import com.arsc.traceGuard.feature.domain.TgBatch;
import com.arsc.traceGuard.feature.service.ITgBatchService;

/**
 * 防伪码批次管理Service业务层处理
 * 
 * @author zhangcj
 * @date 2026-01-26
 */
@Service
public class TgBatchServiceImpl implements ITgBatchService 
{
    @Autowired
    private TgBatchMapper tgBatchMapper;

    /**
     * 查询防伪码批次管理
     * 
     * @param batchId 防伪码批次管理主键
     * @return 防伪码批次管理
     */
    @Override
    public TgBatch selectTgBatchByBatchId(Long batchId)
    {
        return tgBatchMapper.selectTgBatchByBatchId(batchId);
    }

    /**
     * 查询防伪码批次管理列表
     * 
     * @param tgBatch 防伪码批次管理
     * @return 防伪码批次管理
     */
    @Override
    public List<TgBatch> selectTgBatchList(TgBatch tgBatch)
    {
        return tgBatchMapper.selectTgBatchList(tgBatch);
    }

    /**
     * 新增防伪码批次管理
     * 
     * @param tgBatch 防伪码批次管理
     * @return 结果
     */
    @Override
    public int insertTgBatch(TgBatch tgBatch)
    {
        tgBatch.setCreateTime(DateUtils.getNowDate());
        return tgBatchMapper.insertTgBatch(tgBatch);
    }

    /**
     * 修改防伪码批次管理
     * 
     * @param tgBatch 防伪码批次管理
     * @return 结果
     */
    @Override
    public int updateTgBatch(TgBatch tgBatch)
    {
        tgBatch.setUpdateTime(DateUtils.getNowDate());
        return tgBatchMapper.updateTgBatch(tgBatch);
    }

    /**
     * 批量删除防伪码批次管理
     * 
     * @param batchIds 需要删除的防伪码批次管理主键
     * @return 结果
     */
    @Override
    public int deleteTgBatchByBatchIds(Long[] batchIds)
    {
        return tgBatchMapper.deleteTgBatchByBatchIds(batchIds);
    }

    /**
     * 删除防伪码批次管理信息
     * 
     * @param batchId 防伪码批次管理主键
     * @return 结果
     */
    @Override
    public int deleteTgBatchByBatchId(Long batchId)
    {
        return tgBatchMapper.deleteTgBatchByBatchId(batchId);
    }

    @Override
    public TgBatch selectTgBatchByBatchNo(String batchNo)
    {
        return tgBatchMapper.selectTgBatchByBatchNo(batchNo);
    }

    @Override
    public int updateBatchStats(String batchNo, Long addCount, Long addActivatedCount)
    {
        return tgBatchMapper.updateBatchStats(batchNo, addCount, addActivatedCount);
    }

    @Override
    public int updateBatchStats(String batchNo, long addCount, long activatedCount, long scanCount)
    {
        return tgBatchMapper.updateBatchStatsWithScanCount(batchNo, activatedCount, scanCount);
    }
}
