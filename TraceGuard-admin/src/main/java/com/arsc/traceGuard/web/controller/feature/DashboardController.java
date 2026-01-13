package com.arsc.traceGuard.web.controller.feature;

import com.arsc.traceGuard.common.core.controller.BaseController;
import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.feature.domain.vo.IndexDataVo;
import com.arsc.traceGuard.feature.mapper.TgProductMapper;
import com.arsc.traceGuard.feature.mapper.TgScanLogMapper;
import com.arsc.traceGuard.feature.mapper.TgTraceCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/feature/dashboard")
public class DashboardController extends BaseController {

    @Autowired
    private TgProductMapper productMapper;
    @Autowired
    private TgTraceCodeMapper traceCodeMapper;
    @Autowired
    private TgScanLogMapper scanLogMapper;

    @GetMapping("/data")
    public AjaxResult getIndexData() {
        IndexDataVo vo = new IndexDataVo();

        // 1. 卡片指标
        vo.setTotalProduct(productMapper.countTotalProduct());
        vo.setTotalCode(traceCodeMapper.countTotalCode());
        vo.setTotalScan(scanLogMapper.countTotalScan());
        vo.setAbnormalScan(scanLogMapper.countAbnormalScan());

        // 2. 趋势图数据处理 (补全日期，防止某天没数据导致断层)
        List<Map<String, Object>> rawTrend = scanLogMapper.selectScanTrend();
        Map<String, Long> trendMap = new HashMap<>();
        for (Map<String, Object> m : rawTrend) {
            trendMap.put(m.get("dateStr").toString(), Long.parseLong(m.get("count").toString()));
        }

        List<String> dateList = new ArrayList<>();
        List<Long> countList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (int i = 6; i >= 0; i--) {
            String dateKey = today.minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            dateList.add(dateKey);
            countList.add(trendMap.getOrDefault(dateKey, 0L));
        }
        vo.setDateList(dateList);
        vo.setScanTrend(countList);

        // 3. 排名与动态
        vo.setProductRank(traceCodeMapper.selectProductScanRank());
        vo.setRecentLogs(scanLogMapper.selectRecentLogs());

        List<String> locations = scanLogMapper.selectLocationList();
        Map<String, Integer> provinceCount = new HashMap<>();

        for (String loc : locations) {
            String province = cleanProvinceName(loc);
            if (province != null) {
                provinceCount.put(province, provinceCount.getOrDefault(province, 0) + 1);
            }
        }

        List<Map<String, Object>> mapData = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : provinceCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", entry.getKey());
            item.put("value", entry.getValue());
            mapData.add(item);
        }
        vo.setProvinceStat(mapData);

        return AjaxResult.success(vo);

    }

    /**
     * 辅助方法：从地址字符串中提取标准的省份名称
     * ECharts 地图匹配需要 "陕西"、"北京" 这种标准名，不要 "省/市" 后缀
     */
    private String cleanProvinceName(String address) {
        if (address == null || address.length() < 2) return null;

        // 特殊处理自治区和直辖市
        if (address.startsWith("内蒙古")) return "内蒙古";
        if (address.startsWith("黑龙江")) return "黑龙江";
        // 一般省份取前两个字 (如 陕西省 -> 陕西, 北京市 -> 北京)
        return address.substring(0, 2);
    }
}