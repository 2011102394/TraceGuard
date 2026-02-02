package com.arsc.traceGuard.feature.service.impl;

import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.common.utils.DateUtils;
import com.arsc.traceGuard.common.utils.ip.AddressUtils;
import com.arsc.traceGuard.common.utils.ip.IpUtils;
import com.arsc.traceGuard.common.utils.sign.AesUtils;
import com.arsc.traceGuard.feature.domain.TgBatch;
import com.arsc.traceGuard.feature.domain.TgCoupon;
import com.arsc.traceGuard.feature.domain.TgProduct;
import com.arsc.traceGuard.feature.domain.TgScanLog;
import com.arsc.traceGuard.feature.domain.TgTraceCode;
import com.arsc.traceGuard.feature.mapper.TgCouponMapper;
import com.arsc.traceGuard.feature.mapper.TgProductMapper;
import com.arsc.traceGuard.feature.mapper.TgTraceCodeMapper;
import com.arsc.traceGuard.feature.service.ITgBatchService;
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
 *
 * @author arsc
 */
@Service
public class TgTraceCodeServiceImpl implements ITgTraceCodeService {
    @Autowired
    private TgTraceCodeMapper tgTraceCodeMapper;

    @Autowired
    private TgProductMapper tgProductMapper;

    @Autowired
    private TgCouponMapper tgCouponMapper;

    @Autowired
    private ITgScanLogService scanLogService;

    @Autowired
    private ITgBatchService batchService;

    /**
     * 批量生成防伪码实现 (支持追加生成)
     * 规则：总长度24位，批次号_补零_流水号
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateCodes(String type, Long couponId, Long productId, String batchNo, Integer count, String createBy) {
        List<TgTraceCode> buffer = new ArrayList<>();

        // 1. 准备前缀 (批次号 + "_")
        String safeBatchNo = (batchNo == null) ? "" : batchNo;
        String prefix = safeBatchNo;

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
            code.setBatchNo(safeBatchNo);
            code.setStatus("2");
            code.setScanState("0");
            code.setCateType(type);
            if("0".equals(type) && productId != null){
                code.setProductId(productId);
            }
            if("1".equals(type) && couponId !=null){
                code.setCouponId(couponId);
            }
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

        // 8. [新增] 检查并创建批次记录
        TgBatch existingBatch = batchService.selectTgBatchByBatchNo(safeBatchNo);
        if (existingBatch == null) {
            // 批次记录不存在，需要创建
            TgBatch newBatch = new TgBatch();
            newBatch.setBatchNo(safeBatchNo);
            newBatch.setBizType(type); // 0=产品, 1=优惠券
            
            // 设置关联ID和关联名称
            if ("0".equals(type) && productId != null) {
                newBatch.setRelationId(productId);
                // 查询产品名称
                TgProduct product = tgProductMapper.selectTgProductByProductId(productId);
                if (product != null) {
                    newBatch.setRelationName(product.getProductName());
                }
            } else if ("1".equals(type) && couponId != null) {
                newBatch.setRelationId(couponId);
                // 查询优惠券名称
                TgCoupon coupon = tgCouponMapper.selectTgCouponByCouponId(couponId);
                if (coupon != null) {
                    newBatch.setRelationName(coupon.getCouponName());
                }
            }
            
            // 设置初始值
            newBatch.setTotalCount(0L);
            newBatch.setScanCount(0L);
            newBatch.setActivatedCount(0L);
            newBatch.setStatus("0"); // 0=正常
            newBatch.setCreateBy(createBy);
            
            // 创建批次记录
            batchService.insertTgBatch(newBatch);
        }
        
        // 9. [新增] 同步更新批次统计信息
        // 更新生码总数，已激活数量默认为0（新生成的防伪码状态为待激活）
        batchService.updateBatchStats(safeBatchNo, Long.valueOf(count), 0L);
    }

    /**
     * 核心扫码验证逻辑 (集成扫码日志记录 + 地理位置返回)
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult verifyCode(String codeParam, String type, String ip, String userAgent) {
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
        Map<String, Object> result = new HashMap<>();
        result.put("batchNo", code.getBatchNo());
        result.put("code", code.getCodeValue());

        // 根据type参数实现分支逻辑
        if ("1".equals(type) || "1".equals(code.getCateType())) {
            // 优惠券防伪码验证流程
            TgCoupon coupon = tgCouponMapper.selectTgCouponByCouponId(code.getCouponId());
            result.put("coupon", coupon);
            result.put("companyInfo","齐峰花粉");

        } else {
            // 产品防伪码验证流程
            TgProduct product = tgProductMapper.selectTgProductByProductId(code.getProductId());
            result.put("product", product);
        }

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
    @Transactional(rollbackFor = Exception.class)
    public int deleteTraceCodeByBatchNo(String batchNo) {
        // 1. 删除防伪码
        int result = tgTraceCodeMapper.deleteTraceCodeByBatchNo(batchNo);
        
        // 2. 检查批次是否存在
        TgBatch existingBatch = batchService.selectTgBatchByBatchNo(batchNo);
        if (existingBatch != null) {
            // 3. 删除批次记录
            // 先查询批次ID
            List<TgBatch> batchList = batchService.selectTgBatchList(new TgBatch());
            for (TgBatch batch : batchList) {
                if (batch.getBatchNo().equals(batchNo)) {
                    batchService.deleteTgBatchByBatchId(batch.getBatchId());
                    break;
                }
            }
        }
        
        return result;
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
    @Transactional(rollbackFor = Exception.class)
    public int deleteTgTraceCodeByCodeIds(Long[] codeIds) {
        // 1. 查询要删除的防伪码信息，按批次统计数量
        Map<String, Long> batchDeleteCountMap = new HashMap<>();
        Map<String, Long> batchOriginalCountMap = new HashMap<>();
        
        for (Long codeId : codeIds) {
            TgTraceCode traceCode = tgTraceCodeMapper.selectTgTraceCodeByCodeId(codeId);
            if (traceCode != null && traceCode.getBatchNo() != null) {
                String batchNo = traceCode.getBatchNo();
                
                // 统计删除数量
                batchDeleteCountMap.put(batchNo, batchDeleteCountMap.getOrDefault(batchNo, 0L) + 1);
                
                // 统计批次原始数量（如果还没统计过）
                if (!batchOriginalCountMap.containsKey(batchNo)) {
                    TgTraceCode query = new TgTraceCode();
                    query.setBatchNo(batchNo);
                    List<TgTraceCode> batchList = tgTraceCodeMapper.selectBatchList(query);
                    if (!batchList.isEmpty()) {
                        for (TgTraceCode item : batchList) {
                            if (item.getBatchNo().equals(batchNo) && item.getCodeCount() != null) {
                                batchOriginalCountMap.put(batchNo, item.getCodeCount());
                                break;
                            }
                        }
                    }
                }
            }
        }
        
        // 2. 删除防伪码
        int result = tgTraceCodeMapper.deleteTgTraceCodeByCodeIds(codeIds);
        
        // 3. 检查并处理批次记录
        for (Map.Entry<String, Long> entry : batchDeleteCountMap.entrySet()) {
            String batchNo = entry.getKey();
            Long deleteCount = entry.getValue();
            Long originalCount = batchOriginalCountMap.getOrDefault(batchNo, 0L);
            
            // 检查是否删除了该批次的所有防伪码
            if (deleteCount >= originalCount) {
                // 删除批次记录
                TgBatch existingBatch = batchService.selectTgBatchByBatchNo(batchNo);
                if (existingBatch != null) {
                    batchService.deleteTgBatchByBatchId(existingBatch.getBatchId());
                }
            } else {
                // 只删除了部分防伪码，更新统计信息
                batchService.updateBatchStats(batchNo, -deleteCount, 0L);
            }
        }
        
        return result;
    }

    @Override
    public List<TgTraceCode> selectBatchList(TgTraceCode tgTraceCode) {
        return tgTraceCodeMapper.selectBatchList(tgTraceCode);
    }

    @Override
    public List<TgTraceCode> selectListByBatch(Long productId, Long couponId, String batchNo) {
        return tgTraceCodeMapper.selectListByBatch(productId, couponId, batchNo);
    }
}