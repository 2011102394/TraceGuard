package com.arsc.traceGuard.feature.service;

import java.util.List;
import com.arsc.traceGuard.feature.domain.TgCoupon;

/**
 * 优惠券管理Service接口
 * 
 * @author zhangcj
 * @date 2026-01-26
 */
public interface ITgCouponService 
{
    /**
     * 查询优惠券管理
     * 
     * @param couponId 优惠券管理主键
     * @return 优惠券管理
     */
    public TgCoupon selectTgCouponByCouponId(Long couponId);

    /**
     * 查询优惠券管理列表
     * 
     * @param tgCoupon 优惠券管理
     * @return 优惠券管理集合
     */
    public List<TgCoupon> selectTgCouponList(TgCoupon tgCoupon);

    /**
     * 新增优惠券管理
     * 
     * @param tgCoupon 优惠券管理
     * @return 结果
     */
    public int insertTgCoupon(TgCoupon tgCoupon);

    /**
     * 修改优惠券管理
     * 
     * @param tgCoupon 优惠券管理
     * @return 结果
     */
    public int updateTgCoupon(TgCoupon tgCoupon);

    /**
     * 批量删除优惠券管理
     * 
     * @param couponIds 需要删除的优惠券管理主键集合
     * @return 结果
     */
    public int deleteTgCouponByCouponIds(Long[] couponIds);

    /**
     * 删除优惠券管理信息
     * 
     * @param couponId 优惠券管理主键
     * @return 结果
     */
    public int deleteTgCouponByCouponId(Long couponId);
}
