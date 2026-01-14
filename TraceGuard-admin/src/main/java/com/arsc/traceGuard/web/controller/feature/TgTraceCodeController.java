package com.arsc.traceGuard.web.controller.feature;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import javax.servlet.http.HttpServletResponse;

import com.arsc.traceGuard.common.utils.StringUtils;
import com.arsc.traceGuard.common.utils.sign.AesUtils;
import com.arsc.traceGuard.feature.domain.dto.CodeGenerateReq;
import com.arsc.traceGuard.framework.manager.AsyncManager;
import com.arsc.traceGuard.system.service.ISysConfigService;
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

    @Autowired
    private ISysConfigService configService; // 注入配置服务

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
    public TableDataInfo listBatch(TgTraceCode tgTraceCode)
    {
        // 这里不需要分页，或者手动假分页，因为是聚合结果
        List<TgTraceCode> list = tgTraceCodeService.selectBatchList(tgTraceCode);
        return getDataTable(list);
    }

    /**
     * 生成防伪码 (异步优化版)
     */
    @Log(title = "防伪码管理", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody com.arsc.traceGuard.feature.domain.dto.CodeGenerateReq req)
    {
        // 1. 获取当前登录用户名 (必须在主线程获取)
        String currentUsername = getUsername();

        // 2. 使用 RuoYi 的异步管理器执行任务
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                try {
                    // 调用 Service，传入用户名
                    tgTraceCodeService.generateCodes(req.getProductId(), req.getBatchNo(), req.getCount(), currentUsername);
                } catch (Exception e) {
                    // 异步任务中的异常建议记录日志，或者通过消息通知用户(如有)
                    e.printStackTrace();
                    logger.error("后台生码任务失败: {}", e.getMessage());
                }
            }
        });

        // 3. 立即返回，不等待任务结束
        return AjaxResult.success("生码任务已提交后台处理，请稍候刷新列表查看结果");
    }

    /**
     * 导出指定批次的防伪码
     */
    @Log(title = "防伪码导出", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TgTraceCode queryParams)
    {
        // 1. 从数据库参数配置中获取 H5 域名
        // 键名必须与后台【参数设置】里配置的一致
        String h5Domain = configService.selectConfigByKey("trace.h5.domain");

        // 简单校验一下，防止管理员没配参数导致空指针
        if (StringUtils.isEmpty(h5Domain)) {
            // 这里虽然是 void 方法，但如果没配置直接打印日志或设个默认值
            // 或者抛出异常提示前端
            logger.warn("未配置参数 [trace.h5.domain]，使用默认空地址");
            h5Domain = "";
        }

        // H5 页面路由 (这个通常是固定的，也可以配在参数里，这里暂时写死)
        String h5Path = "/h5/verify";

        // 2. 查询原始数据
        List<TgTraceCode> list = tgTraceCodeService.selectListByBatch(queryParams.getProductId(), queryParams.getBatchNo());

        // 3. 拼接 URL + 加密
        String baseUrl = h5Domain + h5Path + "?code=";

        for (TgTraceCode code : list) {
            // 加密
            String encryptedCode = AesUtils.encrypt(code.getCodeValue());
            // 拼接
            code.setQrCodeUrl(baseUrl + encryptedCode);
        }

        // 4. 导出
        ExcelUtil<TgTraceCode> util = new ExcelUtil<TgTraceCode>(TgTraceCode.class);
        util.exportExcel(response, list, "防伪二维码数据_" + queryParams.getBatchNo());
    }

    /**
     * 获取防伪码的二维码内容 (用于前端预览)
     */
    @GetMapping("/qr/{codeId}")
    public AjaxResult getQrCodeContent(@PathVariable("codeId") Long codeId)
    {
        TgTraceCode code = tgTraceCodeService.selectTgTraceCodeByCodeId(codeId);
        if (code == null) {
            return error("防伪码不存在");
        }

        // 1. 获取 H5 域名
        String h5Domain = configService.selectConfigByKey("trace.h5.domain");
        if (StringUtils.isEmpty(h5Domain)) {
            return error("请先在参数设置中配置防伪H5域名 [trace.h5.domain]");
        }

        // 2. 加密并拼接
        String h5Path = "/h5/verify";
        String encryptedCode = AesUtils.encrypt(code.getCodeValue());
        String fullUrl = h5Domain + h5Path + "?code=" + encryptedCode;

        return success( fullUrl);
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

    /**
     * 修改防伪码管理 (用于单个激活、作废等操作)
     */
    @Log(title = "防伪码管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TgTraceCode tgTraceCode)
    {
        return toAjax(tgTraceCodeService.updateTgTraceCode(tgTraceCode));
    }

    /**
     * 批量修改防伪码状态 (整批激活/作废)
     */
    @Log(title = "防伪码管理", businessType = BusinessType.UPDATE)
    @PutMapping("/batch/status")
    public AjaxResult updateBatchStatus(@RequestBody TgTraceCode tgTraceCode)
    {
        if (StringUtils.isEmpty(tgTraceCode.getBatchNo())) {
            return AjaxResult.error("批次号不能为空");
        }
        if (StringUtils.isEmpty(tgTraceCode.getStatus())) {
            return AjaxResult.error("目标状态不能为空");
        }
        return toAjax(tgTraceCodeService.updateBatchStatus(tgTraceCode.getBatchNo(), tgTraceCode.getStatus()));
    }

    /**
     * 删除批次 (删除该批次下所有防伪码)
     */
    @Log(title = "防伪码管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch/{batchNo}")
    public AjaxResult removeBatch(@PathVariable String batchNo)
    {
        return toAjax(tgTraceCodeService.deleteTraceCodeByBatchNo(batchNo));
    }
}