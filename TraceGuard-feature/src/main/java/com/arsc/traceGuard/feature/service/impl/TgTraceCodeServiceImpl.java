package com.arsc.traceGuard.feature.service.impl;

import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.common.utils.DateUtils;
import com.arsc.traceGuard.common.utils.ip.AddressUtils;
import com.arsc.traceGuard.common.utils.ip.IpUtils;
import com.arsc.traceGuard.common.utils.sign.AesUtils;
import com.arsc.traceGuard.feature.domain.TgProduct;
import com.arsc.traceGuard.feature.domain.TgScanLog;
import com.arsc.traceGuard.feature.domain.TgTraceCode;
import com.arsc.traceGuard.feature.mapper.TgProductMapper;
import com.arsc.traceGuard.feature.mapper.TgTraceCodeMapper;
import com.arsc.traceGuard.feature.service.ITgScanLogService;
import com.arsc.traceGuard.feature.service.ITgTraceCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 批量生成防伪码实现 (支持追加生成)
     * 规则：总长度24位，批次号_补零_流水号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateCodes(Long productId, String batchNo, Integer count,String createBy) {
        List<TgTraceCode> buffer = new ArrayList<>();

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

        // 4. [新增逻辑] 查询该批次当前最大的流水号
        long startSeq = 1L; // 默认为 1
        String lastCodeValue = tgTraceCodeMapper.selectMaxCodeValueByBatchNo(safeBatchNo);

        if (lastCodeValue != null && lastCodeValue.startsWith(prefix)) {
            try {
                // 截取后缀部分解析为数字
                String seqStr = lastCodeValue.substring(prefix.length());
                startSeq = Long.parseLong(seqStr) + 1; // 接着下一个数
            } catch (NumberFormatException e) {
                // 如果解析失败（比如旧数据格式不一致），为了安全起见，可能需要抛异常或忽略
                // 这里选择从 1 开始尝试，或者抛出异常防止覆盖
                throw new RuntimeException("该批次存在格式不符合规范的防伪码，无法自动追加，请更换新批次号");
            }
        }

        // 5. 校验生成的数量是否会导致流水号溢出
        // 最大可能的流水号是 startSeq + count - 1
        // 例如：seqLength=5 (最大99999)，start=99900, count=200 -> End=100100 -> 溢出
        String maxSeqStr = String.valueOf(startSeq + count - 1);
        if (maxSeqStr.length() > seqLength) {
            throw new RuntimeException("生成失败：批次[" + safeBatchNo + "]剩余容量不足，无法追加生成 " + count + " 个防伪码");
        }

        // 6. 定义格式化模板
        String formatPattern = "%0" + seqLength + "d";

        // 7. 循环生成 (从 startSeq 开始)
        for (int i = 0; i < count; i++) {
            long currentSeq = startSeq + i;

            TgTraceCode code = new TgTraceCode();
            code.setProductId(productId);
            code.setBatchNo(safeBatchNo);
            code.setStatus("2");
            code.setScanState("0");

            String seq = String.format(formatPattern, currentSeq);
            code.setCodeValue(prefix + seq);

            code.setCreateBy(createBy); // 使用传入的用户名

            buffer.add(code);

            if (buffer.size() >= 1000) {
                tgTraceCodeMapper.batchInsertTgTraceCode(buffer);
                buffer.clear();
            }
        }
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

        // [修改点1] 解析地理位置：如果是内网IP，默认西安；否则查询真实地址
        String realAddress;
        if (IpUtils.internalIp(ip)) {
            realAddress = "陕西省西安市";
        } else {
            realAddress = AddressUtils.getRealAddressByIP(ip);
        }
        scanLog.setScanLocation(realAddress);

        // Step 1: 解密
        String realCodeValue = null;
        try {
            realCodeValue = AesUtils.decrypt(codeParam);
        } catch (Exception e) { /* ignore */ }

        if (realCodeValue == null) {
            // 尝试直接使用明文
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

        // Step 3: 准备返回数据
        TgProduct product = tgProductMapper.selectTgProductByProductId(code.getProductId());
        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        result.put("batchNo", code.getBatchNo());

        // Step 4: 首次 vs 重复 (修改点：根据扫码情况设置日志状态和备注)
        if ("0".equals(code.getScanState())) {
            // === 首次扫码 ===
            code.setScanState("1");
            code.setScanCount(1L);
            code.setFirstScanTime(DateUtils.getNowDate());
            code.setFirstScanIp(ip);
            code.setFirstScanLoc(realAddress);

            tgTraceCodeMapper.updateTgTraceCode(code);

            // [修改点2] 首次扫码日志状态为正常
            scanLog.setStatus("0");
            scanLog.setRemark("扫码验证成功");

            result.put("authStatus", "SUCCESS");
            result.put("isFirst", true);
            result.put("scanTime", code.getFirstScanTime());
            result.put("firstScanLoc", realAddress);
        } else {
            // === 重复扫码 ===
            long newCount = code.getScanCount() + 1;
            code.setScanCount(newCount);
            tgTraceCodeMapper.updateTgTraceCode(code);

            // [修改点3] 重复扫码日志状态为异常，并备注次数
            scanLog.setStatus("1");
            scanLog.setRemark("第" + newCount + "次扫码(重复扫描警告)");

            result.put("authStatus", "WARNING");
            result.put("isFirst", false);
            result.put("firstScanTime", code.getFirstScanTime());
            result.put("scanCount", newCount);
            result.put("firstScanLoc", code.getFirstScanLoc());
        }

        // [修改点4] 最后统一插入日志，确保状态和备注正确
        scanLogService.insertTgScanLog(scanLog);

        return AjaxResult.success(result);
    }

    @Override
    public int deleteTraceCodeByBatchNo(String batchNo) {
        return tgTraceCodeMapper.deleteTraceCodeByBatchNo(batchNo);
    }

    @Override
    public int updateBatchStatus(String batchNo, String status) {
        return tgTraceCodeMapper.updateBatchStatus(batchNo, status);
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