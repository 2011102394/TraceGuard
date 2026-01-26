package com.arsc.traceGuard.web.controller.feature;

import com.arsc.traceGuard.common.annotation.Log;
import com.arsc.traceGuard.common.core.controller.BaseController;
import com.arsc.traceGuard.common.core.domain.AjaxResult;
import com.arsc.traceGuard.common.core.page.TableDataInfo;
import com.arsc.traceGuard.common.enums.BusinessType;
import com.arsc.traceGuard.common.utils.poi.ExcelUtil;
import com.arsc.traceGuard.feature.domain.TgCoupon;
import com.arsc.traceGuard.feature.service.ITgCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 优惠券管理Controller
 * 
 * @author zhangcj
 * @date 2026-01-26
 */
@RestController
@RequestMapping("/feature/coupon")
public class TgCouponController extends BaseController
{
    @Autowired
    private ITgCouponService tgCouponService;

    /**
     * 查询优惠券管理列表
     */
    @PreAuthorize("@ss.hasPermi('feature:coupon:list')")
    @GetMapping("/list")
    public TableDataInfo list(TgCoupon tgCoupon)
    {
        startPage();
        List<TgCoupon> list = tgCouponService.selectTgCouponList(tgCoupon);
        return getDataTable(list);
    }

    /**
     * 导出优惠券管理列表
     */
    @PreAuthorize("@ss.hasPermi('feature:coupon:export')")
    @Log(title = "优惠券管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TgCoupon tgCoupon)
    {
        List<TgCoupon> list = tgCouponService.selectTgCouponList(tgCoupon);
        ExcelUtil<TgCoupon> util = new ExcelUtil<TgCoupon>(TgCoupon.class);
        util.exportExcel(response, list, "优惠券管理数据");
    }

    /**
     * 获取优惠券管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('feature:coupon:query')")
    @GetMapping(value = "/{couponId}")
    public AjaxResult getInfo(@PathVariable("couponId") Long couponId)
    {
        return success(tgCouponService.selectTgCouponByCouponId(couponId));
    }

    /**
     * 新增优惠券管理
     */
    @PreAuthorize("@ss.hasPermi('feature:coupon:add')")
    @Log(title = "优惠券管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TgCoupon tgCoupon)
    {
        return toAjax(tgCouponService.insertTgCoupon(tgCoupon));
    }

    /**
     * 修改优惠券管理
     */
    @PreAuthorize("@ss.hasPermi('feature:coupon:edit')")
    @Log(title = "优惠券管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TgCoupon tgCoupon)
    {
        return toAjax(tgCouponService.updateTgCoupon(tgCoupon));
    }

    /**
     * 删除优惠券管理
     */
    @PreAuthorize("@ss.hasPermi('feature:coupon:remove')")
    @Log(title = "优惠券管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{couponIds}")
    public AjaxResult remove(@PathVariable Long[] couponIds)
    {
        return toAjax(tgCouponService.deleteTgCouponByCouponIds(couponIds));
    }
}
