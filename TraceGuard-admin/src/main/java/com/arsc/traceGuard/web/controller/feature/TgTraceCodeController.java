package com.arsc.traceGuard.web.controller.feature;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.arsc.traceGuard.feature.domain.dto.CodeGenerateReq;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.arsc.traceGuard.common.annotation.Log;
import com.arsc.traceGuard.common.core.controller.BaseController;
import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.common.core.page.TableDataInfo;
import com.arsc.traceGuard.common.enums.BusinessType;
import com.arsc.traceGuard.common.utils.poi.ExcelUtil;
import com.arsc.traceGuard.feature.domain.TgTraceCode;
import com.arsc.traceGuard.feature.service.ITgTraceCodeService;

/**
 * 防伪码管理 Controller
 * @author arsc
 */
@RestController
@RequestMapping("/feature/code")
public class TgTraceCodeController extends BaseController
{
    @Autowired
    private ITgTraceCodeService tgTraceCodeService;

    /**
     * 查询防伪码列表 (全量查询，通常不推荐直接用，备用)
     */
    @GetMapping("/list")
    public TableDataInfo list(TgTraceCode tgTraceCode)
    {
        startPage();
        List<TgTraceCode> list = tgTraceCodeService.selectTgTraceCodeList(tgTraceCode);
        return getDataTable(list);
    }

    /**
     * 查询某产品的【生码批次】列表
     */
    @GetMapping("/batch/list")
    public TableDataInfo listBatch(@RequestParam Long productId)
    {
        // 这里不需要分页，或者手动假分页，因为是聚合结果
        List<TgTraceCode> list = tgTraceCodeService.selectBatchList(productId);
        return getDataTable(list);
    }

    /**
     * 批量生成防伪码
     */
    @Log(title = "防伪码生码", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody CodeGenerateReq req)
    {
        if (req.getProductId() == null || req.getCount() == null || req.getCount() <= 0) {
            return error("参数错误");
        }
        // 建议：对于超过1万条的生码，建议放入线程池异步执行
        tgTraceCodeService.generateCodes(req.getProductId(), req.getBatchNo(), req.getCount());
        return success("生码任务已完成，请刷新列表");
    }

    /**
     * 导出指定批次的防伪码 (给印刷厂)
     */
    @Log(title = "防伪码导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Long productId, String batchNo)
    {
        List<TgTraceCode> list = tgTraceCodeService.selectListByBatch(productId, batchNo);

        // 自定义导出内容，例如拼接URL
        // 这里直接导出对象，ExcelUtil会自动读取 @Excel 注解
        ExcelUtil<TgTraceCode> util = new ExcelUtil<TgTraceCode>(TgTraceCode.class);
        util.exportExcel(response, list, "防伪码_" + batchNo);
    }

    /**
     * 获取防伪码详细信息
     */
    @GetMapping(value = "/{codeId}")
    public AjaxResult getInfo(@PathVariable("codeId") Long codeId)
    {
        return success(tgTraceCodeService.selectTgTraceCodeByCodeId(codeId));
    }

    /**
     * 删除防伪码
     */
    @Log(title = "防伪码", businessType = BusinessType.DELETE)
    @DeleteMapping("/{codeIds}")
    public AjaxResult remove(@PathVariable Long[] codeIds)
    {
        return toAjax(tgTraceCodeService.deleteTgTraceCodeByCodeIds(codeIds));
    }

    /**
     * 获取防伪码统计信息
     */
    @GetMapping("/stats")
    public AjaxResult stats(TgTraceCode tgTraceCode)
    {
        Map<String, Object> stats = tgTraceCodeService.selectTraceCodeStats(tgTraceCode);
        return success(stats);
    }

}