package com.arsc.traceGuard.feature.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.common.utils.DateUtils;
import com.arsc.traceGuard.common.utils.SecurityUtils;
import com.arsc.traceGuard.common.utils.uuid.IdUtils;
import com.arsc.traceGuard.feature.domain.TgProduct;
import com.arsc.traceGuard.feature.domain.TgTraceCode;
import com.arsc.traceGuard.feature.mapper.TgProductMapper;
import com.arsc.traceGuard.feature.mapper.TgTraceCodeMapper;
import com.arsc.traceGuard.feature.service.ITgTraceCodeService;

/**
 * 防伪码管理 Service业务层处理
 * @author arsc
 */
@Service
public class TgTraceCodeServiceImpl implements ITgTraceCodeService
{
    @Autowired
    private TgTraceCodeMapper tgTraceCodeMapper;

    @Autowired
    private TgProductMapper tgProductMapper;

    /**
     * 批量生成防伪码实现
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateCodes(Long productId, String batchNo, Integer count) {
        List<TgTraceCode> buffer = new ArrayList<>();
        String creator = SecurityUtils.getUsername();

        for (int i = 0; i < count; i++) {
            TgTraceCode code = new TgTraceCode();
            code.setProductId(productId);
            code.setBatchNo(batchNo);
            // 使用简化的UUID作为防伪码值 (去掉了横线)
            code.setCodeValue(IdUtils.simpleUUID());
            code.setCreateBy(creator);

            buffer.add(code);

            // 每1000条批量插入一次，防止内存溢出
            if (buffer.size() >= 1000) {
                tgTraceCodeMapper.batchInsertTgTraceCode(buffer);
                buffer.clear();
            }
        }
        // 处理剩余数据
        if (!buffer.isEmpty()) {
            tgTraceCodeMapper.batchInsertTgTraceCode(buffer);
        }
    }

    /**
     * 核心扫码验证逻辑
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult verifyCode(String codeValue, String ip, String userAgent) {
        TgTraceCode code = tgTraceCodeMapper.selectTgTraceCodeByCodeValue(codeValue);

        // 1. 码不存在或作废
        if (code == null || "1".equals(code.getStatus())) {
            return AjaxResult.error(400, "防伪码不存在或已失效，请谨防假冒！");
        }

        // 2. 获取产品信息
        TgProduct product = tgProductMapper.selectTgProductByProductId(code.getProductId());

        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        result.put("batchNo", code.getBatchNo());

        // 3. 判断是否首次扫描
        if ("0".equals(code.getScanState())) {
            // === 首次 ===
            code.setScanState("1");
            code.setScanCount(1L);
            code.setFirstScanTime(DateUtils.getNowDate());
            code.setFirstScanIp(ip);
            // 这里可以接入IP转地址工具
            // code.setFirstScanLoc(AddressUtils.getRealAddressByIP(ip));

            tgTraceCodeMapper.updateTgTraceCode(code);

            result.put("authStatus", "SUCCESS"); // 正品
            result.put("isFirst", true);
            result.put("scanTime", code.getFirstScanTime());
        } else {
            // === 非首次 ===
            code.setScanCount(code.getScanCount() + 1);
            tgTraceCodeMapper.updateTgTraceCode(code);

            result.put("authStatus", "WARNING"); // 警告
            result.put("isFirst", false);
            result.put("firstScanTime", code.getFirstScanTime());
            result.put("scanCount", code.getScanCount());
        }

        return AjaxResult.success(result);
    }

    @Override
    public Map<String, Object> selectTraceCodeStats(TgTraceCode tgTraceCode) {
        return tgTraceCodeMapper.selectTraceCodeStats(tgTraceCode);
    }

    // --- 标准CRUD实现 ---

    @Override
    public TgTraceCode selectTgTraceCodeByCodeId(Long codeId) {
        return tgTraceCodeMapper.selectTgTraceCodeByCodeId(codeId);
    }

    @Override
    public TgTraceCode selectTgTraceCodeByCodeValue(String codeValue) {
        return tgTraceCodeMapper.selectTgTraceCodeByCodeValue(codeValue);
    }

    @Override
    public List<TgTraceCode> selectTgTraceCodeList(TgTraceCode tgTraceCode) {
        return tgTraceCodeMapper.selectTgTraceCodeList(tgTraceCode);
    }

    @Override
    public int updateTgTraceCode(TgTraceCode tgTraceCode) {
        tgTraceCode.setUpdateTime(DateUtils.getNowDate());
        return tgTraceCodeMapper.updateTgTraceCode(tgTraceCode);
    }

    @Override
    public int deleteTgTraceCodeByCodeIds(Long[] codeIds) {
        return tgTraceCodeMapper.deleteTgTraceCodeByCodeIds(codeIds);
    }

    @Override
    public List<TgTraceCode> selectBatchList(Long productId) {
        return tgTraceCodeMapper.selectBatchList(productId);
    }

    @Override
    public List<TgTraceCode> selectListByBatch(Long productId, String batchNo) {
        return tgTraceCodeMapper.selectListByBatch(productId, batchNo);
    }
}