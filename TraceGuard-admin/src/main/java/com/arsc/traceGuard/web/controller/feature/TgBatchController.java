package com.arsc.traceGuard.web.controller.feature;

import com.arsc.traceGuard.common.annotation.Log;
import com.arsc.traceGuard.common.core.controller.BaseController;
import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.common.core.page.TableDataInfo;
import com.arsc.traceGuard.common.enums.BusinessType;
import com.arsc.traceGuard.common.utils.poi.ExcelUtil;
import com.arsc.traceGuard.feature.domain.TgBatch;
import com.arsc.traceGuard.feature.service.ITgBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 防伪码批次管理Controller
 * 
 * @author zhangcj
 * @date 2026-01-26
 */
@RestController
@RequestMapping("/feature/batch")
public class TgBatchController extends BaseController
{
    @Autowired
    private ITgBatchService tgBatchService;

    /**
     * 查询防伪码批次管理列表
     */
    @GetMapping("/list")
    public TableDataInfo list(TgBatch tgBatch)
    {
        startPage();
        List<TgBatch> list = tgBatchService.selectTgBatchList(tgBatch);
        return getDataTable(list);
    }

    /**
     * 导出防伪码批次管理列表
     */
    @Log(title = "防伪码批次管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TgBatch tgBatch)
    {
        List<TgBatch> list = tgBatchService.selectTgBatchList(tgBatch);
        ExcelUtil<TgBatch> util = new ExcelUtil<TgBatch>(TgBatch.class);
        util.exportExcel(response, list, "防伪码批次管理数据");
    }

    /**
     * 获取防伪码批次管理详细信息
     */
    @GetMapping(value = "/{batchId}")
    public AjaxResult getInfo(@PathVariable("batchId") Long batchId)
    {
        return success(tgBatchService.selectTgBatchByBatchId(batchId));
    }

    /**
     * 新增防伪码批次管理
     */
    @Log(title = "防伪码批次管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TgBatch tgBatch)
    {
        return toAjax(tgBatchService.insertTgBatch(tgBatch));
    }

    /**
     * 修改防伪码批次管理
     */
    @Log(title = "防伪码批次管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TgBatch tgBatch)
    {
        return toAjax(tgBatchService.updateTgBatch(tgBatch));
    }

    /**
     * 删除防伪码批次管理
     */
    @Log(title = "防伪码批次管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{batchIds}")
    public AjaxResult remove(@PathVariable Long[] batchIds)
    {
        return toAjax(tgBatchService.deleteTgBatchByBatchIds(batchIds));
    }
}
