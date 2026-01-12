package com.arsc.traceGuard.feature.service;

import java.util.List;
import java.util.Map;

import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.feature.domain.TgTraceCode;

/**
 * 防伪码管理 Service接口
 * @author arsc
 */
public interface ITgTraceCodeService
{
    /**
     * 查询防伪码
     */
    public TgTraceCode selectTgTraceCodeByCodeId(Long codeId);

    /**
     * 根据码值查询 (用于H5扫码)
     */
    public TgTraceCode selectTgTraceCodeByCodeValue(String codeValue);

    /**
     * 查询防伪码列表
     */
    public List<TgTraceCode> selectTgTraceCodeList(TgTraceCode tgTraceCode);

    /**
     * 批量生成防伪码 (核心业务)
     * @param productId 产品ID
     * @param batchNo 批次号
     * @param count 数量
     * @return 生成结果消息
     */
    public void generateCodes(Long productId, String batchNo, Integer count);

    /**
     * 验证防伪码 (H5调用)
     * @param codeValue 码值
     * @param ip 用户IP
     * @param userAgent 浏览器标识
     * @return 验证结果（包含真伪状态、产品信息等）
     */
    public AjaxResult verifyCode(String codeValue, String ip, String userAgent);

    /**
     * 修改防伪码
     */
    public int updateTgTraceCode(TgTraceCode tgTraceCode);

    /**
     * 批量删除防伪码
     */
    public int deleteTgTraceCodeByCodeIds(Long[] codeIds);

    /**
     * 查询某产品的批次统计
     */
    public List<TgTraceCode> selectBatchList(TgTraceCode tgTraceCode);

    /**
     * 查询指定批次的所有码
     */
    public List<TgTraceCode> selectListByBatch(Long productId, String batchNo);

    public Map<String, Object> selectTraceCodeStats(TgTraceCode tgTraceCode);
}