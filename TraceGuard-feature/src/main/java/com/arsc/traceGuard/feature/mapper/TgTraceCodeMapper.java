package com.arsc.traceGuard.feature.mapper;

import java.util.List;
import java.util.Map;

import com.arsc.traceGuard.feature.domain.TgTraceCode;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

/**
 * 防伪码管理 Mapper接口
 * * @author arsc
 * @date 2026-01-12
 */
public interface TgTraceCodeMapper
{

    // 在接口中添加方法
    public Map<String, Object> selectTraceCodeStats(TgTraceCode tgTraceCode);
    /**
     * 查询防伪码
     * * @param codeId 防伪码主键
     * @return 防伪码
     */
    public TgTraceCode selectTgTraceCodeByCodeId(Long codeId);

    /**
     * 根据唯一码值查询（核心业务）
     * @param codeValue UUID
     * @return
     */
    public TgTraceCode selectTgTraceCodeByCodeValue(String codeValue);

    /**
     * 查询防伪码列表
     * * @param tgTraceCode 防伪码
     * @return 防伪码集合
     */
    public List<TgTraceCode> selectTgTraceCodeList(TgTraceCode tgTraceCode);

    /**
     * 新增防伪码
     * * @param tgTraceCode 防伪码
     * @return 结果
     */
    public int insertTgTraceCode(TgTraceCode tgTraceCode);

    /**
     * 批量新增防伪码 (高性能插入)
     * @param list 码列表
     * @return 结果
     */
    public int batchInsertTgTraceCode(List<TgTraceCode> list);

    /**
     * 修改防伪码
     * * @param tgTraceCode 防伪码
     * @return 结果
     */
    public int updateTgTraceCode(TgTraceCode tgTraceCode);

    /**
     * 批量删除防伪码
     * * @param codeIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTgTraceCodeByCodeIds(Long[] codeIds);

    /**
     * 查询某产品的批次统计列表 (聚合查询)
     * @param tgTraceCode 防伪码对象
     * @return 包含batchNo和codeCount的对象列表
     */
    public List<TgTraceCode> selectBatchList(TgTraceCode tgTraceCode);

    /**
     * 根据批次号查询所有码 (用于导出)
     * @param productId 产品ID
     * @param batchNo 批次号
     * @return
     */
    public List<TgTraceCode> selectListByBatch(@Param("productId") Long productId, @Param("batchNo") String batchNo);

    public Long countTotalCode();

    public List<Map<String, Object>> selectProductScanRank();
}