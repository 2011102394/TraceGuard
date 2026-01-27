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
     * 批量生成防伪码
     * @param type 防伪码类型 '0'-产品码 ‘1’-优惠券
     * @param couponId 优惠券ID
     * @param productId 产品ID
     * @param batchNo 批次号
     * @param count 数量
     * @param createBy 创建人 (新增参数)
     */
    public void generateCodes(String type, Long couponId,Long productId, String batchNo, Integer count, String createBy);

    /**
     * 验证防伪码 (H5调用)
     * @param codeValue 码值
     * @param ip 用户IP
     * @param userAgent 浏览器标识
     * @return 验证结果（包含真伪状态、产品信息等）
     */
    public AjaxResult verifyCode(String codeParam, String type, String ip, String userAgent);

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
    public List<TgTraceCode> selectListByBatch(Long productId, Long couponId, String batchNo);

    public Map<String, Object> selectTraceCodeStats(TgTraceCode tgTraceCode);

    /**
     * 批量修改防伪码状态
     * * @param batchNo 批次号
     * @param status 状态
     * @return 结果
     */
    public int updateBatchStatus(String batchNo, String status);

    /**
     * 删除指定批次的所有防伪码
     * @param batchNo 批次号
     * @return 结果
     */
    public int deleteTraceCodeByBatchNo(String batchNo);
}