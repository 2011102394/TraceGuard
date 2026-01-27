package com.arsc.traceGuard.feature.job;

import com.arsc.traceGuard.feature.domain.TgBatch;
import com.arsc.traceGuard.feature.mapper.TgTraceCodeMapper;
import com.arsc.traceGuard.feature.service.ITgBatchService;
import com.arsc.traceGuard.quartz.domain.SysJob;
import com.arsc.traceGuard.quartz.util.AbstractQuartzJob;
import com.arsc.traceGuard.common.utils.spring.SpringUtils;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 防伪码统计定时任务
 * 用于统计和更新扫码数据
 *
 * @author arsc
 */
public class TraceCodeStatsJob extends AbstractQuartzJob {
    private static final Logger log = LoggerFactory.getLogger(TraceCodeStatsJob.class);

    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception {
        log.info("开始执行防伪码统计定时任务");

        // 获取服务实例
        TgTraceCodeMapper traceCodeMapper = SpringUtils.getBean(TgTraceCodeMapper.class);
        ITgBatchService batchService = SpringUtils.getBean(ITgBatchService.class);

        try {
            // 1. 统计每个批次的扫码数量和激活数量
            List<Map<String, Object>> batchStats = traceCodeMapper.selectBatchScanStats();

            // 2. 遍历统计结果，更新到tg_batch表
            for (Map<String, Object> stats : batchStats) {
                String batchNo = (String) stats.get("batchNo");
                Long scanCount = (Long) stats.get("scanCount");
                Long activatedCount = (Long) stats.get("activatedCount");

                // 3. 查询批次是否存在
                TgBatch batch = batchService.selectTgBatchByBatchNo(batchNo);
                if (batch != null) {
                    // 4. 更新批次统计信息
                    batchService.updateBatchStats(batchNo, 0L, activatedCount, scanCount);
                    log.info("更新批次 {} 统计信息：扫码次数={}, 激活数量={}", batchNo, scanCount, activatedCount);
                }
            }

            log.info("防伪码统计定时任务执行完成");
        } catch (Exception e) {
            log.error("防伪码统计定时任务执行失败", e);
            throw e;
        }
    }
}
