package com.arsc.traceGuard.web.controller.feature;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.arsc.traceGuard.common.annotation.Log;
import com.arsc.traceGuard.common.core.controller.BaseController;
import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.common.enums.BusinessType;
import com.arsc.traceGuard.feature.domain.TgProduct;
import com.arsc.traceGuard.feature.service.ITgProductService;
import com.arsc.traceGuard.common.utils.poi.ExcelUtil;
import com.arsc.traceGuard.common.core.page.TableDataInfo;

/**
 * 产品信息Controller
 * 
 * @author zhangcj
 * @date 2026-01-06
 */
@RestController
@RequestMapping("/feature/product")
public class TgProductController extends BaseController
{
    @Autowired
    private ITgProductService tgProductService;

    /**
     * 查询产品信息列表
     */
    @PreAuthorize("@ss.hasPermi('feature:product:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgProduct tgProduct)
    {
        startPage();
        List<TgProduct> list = tgProductService.selectTgProductList(tgProduct);
        return getDataTable(list);
    }

    /**
     * 导出产品信息列表
     */
    @PreAuthorize("@ss.hasPermi('feature:product:export')")
    @Log(title = "产品信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TgProduct tgProduct)
    {
        List<TgProduct> list = tgProductService.selectTgProductList(tgProduct);
        ExcelUtil<TgProduct> util = new ExcelUtil<TgProduct>(TgProduct.class);
        util.exportExcel(response, list, "产品信息数据");
    }

    /**
     * 获取产品信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('feature:product:query')")
    @GetMapping(value = "/{productId}")
    public AjaxResult getInfo(@PathVariable("productId") Long productId)
    {
        return success(tgProductService.selectTgProductByProductId(productId));
    }

    /**
     * 新增产品信息
     */
    @PreAuthorize("@ss.hasPermi('feature:product:add')")
    @Log(title = "产品信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TgProduct tgProduct)
    {
        return toAjax(tgProductService.insertTgProduct(tgProduct));
    }

    /**
     * 修改产品信息
     */
    @PreAuthorize("@ss.hasPermi('feature:product:edit')")
    @Log(title = "产品信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TgProduct tgProduct)
    {
        return toAjax(tgProductService.updateTgProduct(tgProduct));
    }

    /**
     * 删除产品信息
     */
    @PreAuthorize("@ss.hasPermi('feature:product:remove')")
    @Log(title = "产品信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{productIds}")
    public AjaxResult remove(@PathVariable Long[] productIds)
    {
        return toAjax(tgProductService.deleteTgProductByProductIds(productIds));
    }
}
