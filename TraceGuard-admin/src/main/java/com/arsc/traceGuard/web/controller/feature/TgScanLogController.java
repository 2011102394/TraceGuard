package com.arsc.traceGuard.feature.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.arsc.traceGuard.common.annotation.Log;
import com.arsc.traceGuard.common.core.controller.BaseController;
import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.common.enums.BusinessType;
import com.arsc.traceGuard.feature.domain.TgScanLog;
import com.arsc.traceGuard.feature.service.ITgScanLogService;
import com.arsc.traceGuard.common.utils.poi.ExcelUtil;
import com.arsc.traceGuard.common.core.page.TableDataInfo;

/**
 * 防伪扫描记录Controller
 */
@RestController
@RequestMapping("/feature/scanlog")
public class TgScanLogController extends BaseController
{
    @Autowired
    private ITgScanLogService tgScanLogService;

    /**
     * 查询防伪扫描记录列表
     */
    @GetMapping("/list")
    public TableDataInfo list(TgScanLog tgScanLog)
    {
        startPage();
        List<TgScanLog> list = tgScanLogService.selectTgScanLogList(tgScanLog);
        return getDataTable(list);
    }

    /**
     * 导出防伪扫描记录列表
     */
    @Log(title = "防伪扫描记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TgScanLog tgScanLog)
    {
        List<TgScanLog> list = tgScanLogService.selectTgScanLogList(tgScanLog);
        ExcelUtil<TgScanLog> util = new ExcelUtil<TgScanLog>(TgScanLog.class);
        util.exportExcel(response, list, "防伪扫描记录数据");
    }

    /**
     * 删除防伪扫描记录
     */
    @Log(title = "防伪扫描记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable Long[] logIds)
    {
        return toAjax(tgScanLogService.deleteTgScanLogByLogIds(logIds));
    }
}