package com.arsc.traceGuard.feature.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.arsc.traceGuard.feature.domain.TgBatch;

/**
 * 防伪码批次管理Mapper接口
 * 
 * @author zhangcj
 * @date 2026-01-26
 */
public interface TgBatchMapper 
{
    /**
     * 查询防伪码批次管理
     * 
     * @param batchId 防伪码批次管理主键
     * @return 防伪码批次管理
     */
    public TgBatch selectTgBatchByBatchId(Long batchId);

    /**
     * 查询防伪码批次管理列表
     * 
     * @param tgBatch 防伪码批次管理
     * @return 防伪码批次管理集合
     */
    public List<TgBatch> selectTgBatchList(TgBatch tgBatch);

    /**
     * 新增防伪码批次管理
     * 
     * @param tgBatch 防伪码批次管理
     * @return 结果
     */
    public int insertTgBatch(TgBatch tgBatch);

    /**
     * 修改防伪码批次管理
     * 
     * @param tgBatch 防伪码批次管理
     * @return 结果
     */
    public int updateTgBatch(TgBatch tgBatch);

    /**
     * 删除防伪码批次管理
     * 
     * @param batchId 防伪码批次管理主键
     * @return 结果
     */
    public int deleteTgBatchByBatchId(Long batchId);

    /**
     * 批量删除防伪码批次管理
     * 
     * @param batchIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTgBatchByBatchIds(Long[] batchIds);

    /**
     * 根据批次号查询批次
     * 
     * @param batchNo 批次号
     * @return 防伪码批次管理
     */
    public TgBatch selectTgBatchByBatchNo(String batchNo);

    /**
     * 更新批次统计信息（生码总数、已激活数量）
     * 
     * @param batchNo 批次号
     * @param addCount 新增的防伪码数量
     * @param addActivatedCount 新增的已激活数量
     * @return 结果
     */
    public int updateBatchStats(@Param("batchNo") String batchNo, @Param("addCount") Long addCount, @Param("addActivatedCount") Long addActivatedCount);

    public int updateBatchStatsWithScanCount(@Param("batchNo") String batchNo, @Param("activatedCount") long activatedCount, @Param("scanCount") long scanCount);
}
