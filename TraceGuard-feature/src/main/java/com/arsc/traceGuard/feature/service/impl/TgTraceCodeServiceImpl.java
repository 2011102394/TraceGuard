package com.arsc.traceGuard.feature.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arsc.traceGuard.common.utils.sign.AesUtils;
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
            code.setStatus("2");
            code.setScanState("0");
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
     * 核心扫码验证逻辑 (严格加密模式)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult verifyCode(String codeParam, String ip, String userAgent) {
        // 1. [安全校验] 尝试解密
        // 只有通过 AES 密钥成功解密的码才被认为是合法的入口
        String realCodeValue = AesUtils.decrypt(codeParam);

        // 如果解密结果为 null，说明 URL 参数被篡改，或者不是系统生成的合法密文
        if (realCodeValue == null) {
            return AjaxResult.error(400, "防伪码无法识别(校验失败)，系统判定为假冒产品！");
        }

        // 2. [数据库校验] 根据解密后的 UUID 查库
        TgTraceCode code = tgTraceCodeMapper.selectTgTraceCodeByCodeValue(realCodeValue);

        // 码不存在 或 状态为作废 (status=1)
        // [修改点]：状态检查逻辑升级
        if (code == null) {
            return AjaxResult.error(400, "防伪码不存在，请谨防假冒！");
        }
        // 检查状态
        if ("1".equals(code.getStatus())) {
            return AjaxResult.error(400, "该防伪码已作废，请注意辨别！");
        }
        // [新增]：如果状态是 2 (待激活)，拦截
        if ("2".equals(code.getStatus())) {
            return AjaxResult.error(400, "该防伪码尚未激活，请联系厂商核实！");
        }
        // 3. 获取关联产品信息
        TgProduct product = tgProductMapper.selectTgProductByProductId(code.getProductId());

        Map<String, Object> result = new HashMap<>();
        result.put("product", product);
        result.put("batchNo", code.getBatchNo());

        // 4. [溯源判定] 判断是否首次扫描
        if ("0".equals(code.getScanState())) {
            // === 首次扫码 (真品认证) ===
            code.setScanState("1"); // 标记已扫
            code.setScanCount(1L);
            code.setFirstScanTime(DateUtils.getNowDate());
            code.setFirstScanIp(ip);
            // TODO: 如果接入了 Ip2Region，可在此处解析 ip 对应的 firstScanLoc
            tgTraceCodeMapper.updateTgTraceCode(code);
            result.put("authStatus", "SUCCESS"); // 前端显示绿盾
            result.put("isFirst", true);
            result.put("scanTime", code.getFirstScanTime());
        } else {
            // === 重复扫码 (防伪预警) ===
            code.setScanCount(code.getScanCount() + 1);
            tgTraceCodeMapper.updateTgTraceCode(code); // 仅更新次数

            result.put("authStatus", "WARNING"); // 前端显示红盾
            result.put("isFirst", false);
            result.put("firstScanTime", code.getFirstScanTime()); // 返回首次时间供比对
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
    public List<TgTraceCode> selectBatchList(TgTraceCode tgTraceCode) {
        return tgTraceCodeMapper.selectBatchList(tgTraceCode);
    }

    @Override
    public List<TgTraceCode> selectListByBatch(Long productId, String batchNo) {
        return tgTraceCodeMapper.selectListByBatch(productId, batchNo);
    }
}