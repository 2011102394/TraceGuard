package com.arsc.traceGuard.feature.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arsc.traceGuard.common.utils.ip.AddressUtils;
import com.arsc.traceGuard.common.utils.sign.AesUtils;
import com.arsc.traceGuard.feature.domain.TgScanLog;
import com.arsc.traceGuard.feature.service.ITgScanLogService;
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

    @Autowired
    private ITgScanLogService scanLogService;

    /**
     * 批量生成防伪码实现
     * 修改规则：总长度24位，批次号_补零_从1开始的流水号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateCodes(Long productId, String batchNo, Integer count) {
        List<TgTraceCode> buffer = new ArrayList<>();
        String creator = SecurityUtils.getUsername();

        // 1. 准备前缀 (批次号 + "_")
        String safeBatchNo = (batchNo == null) ? "" : batchNo;
        String prefix = safeBatchNo + "_";

        // 2. 计算流水号可用长度 (总长24 - 前缀长度)
        int totalLength = 24;
        int seqLength = totalLength - prefix.length();

        // 3. 校验长度是否合法
        if (seqLength <= 0) {
            throw new RuntimeException("生成失败：批次号[" + safeBatchNo + "]过长，无法满足24位防伪码要求");
        }
        // 校验生成的数量是否会导致流水号溢出 (例如只有3位空间却要生成2000个)
        if (String.valueOf(count).length() > seqLength) {
            throw new RuntimeException("生成失败：生成数量[" + count + "]超出当前批次号剩余长度限制");
        }

        // 4. 定义格式化模板，例如 "%015d" 代表补零至15位整数
        String formatPattern = "%0" + seqLength + "d";

        // 5. 循环生成 (从1开始)
        for (int i = 1; i <= count; i++) {
            TgTraceCode code = new TgTraceCode();
            code.setProductId(productId);
            code.setBatchNo(safeBatchNo);
            code.setStatus("2"); // 默认状态：待激活
            code.setScanState("0"); // 扫码状态：未扫码

            // 核心修改：生成固定格式 codeValue
            String seq = String.format(formatPattern, i);
            code.setCodeValue(prefix + seq);

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
     * 核心扫码验证逻辑 (集成扫码日志记录 + 地理位置返回)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult verifyCode(String codeParam, String ip, String userAgent) {
        // Step 0: 预备日志对象
        TgScanLog scanLog = new TgScanLog();
        scanLog.setScanIp(ip);
        scanLog.setBrowserInfo(userAgent);
        scanLog.setCreateTime(DateUtils.getNowDate());

        // 解析当前扫码的地理位置
        String realAddress = AddressUtils.getRealAddressByIP(ip);
        scanLog.setScanLocation(realAddress);

        // Step 1: 解密
        String realCodeValue = null;
        try {
            realCodeValue = AesUtils.decrypt(codeParam);
        } catch (Exception e) { /* ignore */ }

        if (realCodeValue == null) {
            // 尝试直接使用明文（兼容某些场景下未加密的传输，视业务需求而定，这里保持原逻辑主要依靠解密）
            // 如果解密失败，通常认为是伪造的链接参数
            scanLog.setCodeValue(codeParam);
            scanLog.setStatus("1");
            scanLog.setRemark("防伪码无法识别(解密失败)");
            scanLogService.insertTgScanLog(scanLog);
            return AjaxResult.error(400, "防伪码无法识别(校验失败)，系统判定为假冒产品！");
        }
        scanLog.setCodeValue(realCodeValue);

        // Step 2: 查库
        TgTraceCode code = tgTraceCodeMapper.selectTgTraceCodeByCodeValue(realCodeValue);

        if (code == null) {
            scanLog.setStatus("1");
            scanLog.setRemark("防伪码不存在");
            scanLogService.insertTgScanLog(scanLog);
            return AjaxResult.error(400, "防伪码不存在，请谨防假冒！");
        }

        if ("1".equals(code.getStatus())) {
            scanLog.setStatus("1");
            scanLog.setRemark("防伪码已作废");
            scanLogService.insertTgScanLog(scanLog);
            return AjaxResult.error(400, "该防伪码已作废，请注意辨别！");
        }

        if ("2".equals(code.getStatus())) {
            scanLog.setStatus("1");
            scanLog.setRemark("防伪码待激活");
            scanLogService.insertTgScanLog(scanLog);
            return AjaxResult.error(400, "该防伪码尚未激活，请联系厂商核实！");
        }

        // Step 3: 正常日志
        scanLog.setStatus("0");
        scanLog.setRemark("扫码验证成功");
        scanLogService.insertTgScanLog(scanLog);

        TgProduct product = tgProductMapper.selectTgProductByProductId(code.getProductId());

        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        result.put("batchNo", code.getBatchNo());

        // Step 4: 首次 vs 重复
        if ("0".equals(code.getScanState())) {
            // === 首次扫码 ===
            code.setScanState("1");
            code.setScanCount(1L);
            code.setFirstScanTime(DateUtils.getNowDate());
            code.setFirstScanIp(ip);
            code.setFirstScanLoc(realAddress); // [关键] 保存首次位置

            tgTraceCodeMapper.updateTgTraceCode(code);

            result.put("authStatus", "SUCCESS");
            result.put("isFirst", true);
            result.put("scanTime", code.getFirstScanTime());
            result.put("firstScanLoc", realAddress); // 返回当前位置
        } else {
            // === 重复扫码 ===
            code.setScanCount(code.getScanCount() + 1);
            tgTraceCodeMapper.updateTgTraceCode(code);

            result.put("authStatus", "WARNING");
            result.put("isFirst", false);
            result.put("firstScanTime", code.getFirstScanTime());
            result.put("scanCount", code.getScanCount());

            // [新增] 返回首次扫码的地理位置
            result.put("firstScanLoc", code.getFirstScanLoc());
        }

        return AjaxResult.success(result);
    }

    @Override
    public Map<String, Object> selectTraceCodeStats(TgTraceCode tgTraceCode) {
        return tgTraceCodeMapper.selectTraceCodeStats(tgTraceCode);
    }


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
    public List<TgTraceCode> selectBatchList(TgTraceCode tgTraceCode) {
        return tgTraceCodeMapper.selectBatchList(tgTraceCode);
    }

    @Override
    public List<TgTraceCode> selectListByBatch(Long productId, String batchNo) {
        return tgTraceCodeMapper.selectListByBatch(productId, batchNo);
    }
}