package com.arsc.traceGuard.feature.service.impl;

import java.util.List;
import com.arsc.traceGuard.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.arsc.traceGuard.feature.mapper.TgCouponMapper;
import com.arsc.traceGuard.feature.domain.TgCoupon;
import com.arsc.traceGuard.feature.service.ITgCouponService;

/**
 * 优惠券管理Service业务层处理
 * 
 * @author zhangcj
 * @date 2026-01-26
 */
@Service
public class TgCouponServiceImpl implements ITgCouponService 
{
    @Autowired
    private TgCouponMapper tgCouponMapper;

    /**
     * 查询优惠券管理
     * 
     * @param couponId 优惠券管理主键
     * @return 优惠券管理
     */
    @Override
    public TgCoupon selectTgCouponByCouponId(Long couponId)
    {
        return tgCouponMapper.selectTgCouponByCouponId(couponId);
    }

    /**
     * 查询优惠券管理列表
     * 
     * @param tgCoupon 优惠券管理
     * @return 优惠券管理
     */
    @Override
    public List<TgCoupon> selectTgCouponList(TgCoupon tgCoupon)
    {
        return tgCouponMapper.selectTgCouponList(tgCoupon);
    }

    /**
     * 新增优惠券管理
     * 
     * @param tgCoupon 优惠券管理
     * @return 结果
     */
    @Override
    public int insertTgCoupon(TgCoupon tgCoupon)
    {
        tgCoupon.setCreateTime(DateUtils.getNowDate());
        return tgCouponMapper.insertTgCoupon(tgCoupon);
    }

    /**
     * 修改优惠券管理
     * 
     * @param tgCoupon 优惠券管理
     * @return 结果
     */
    @Override
    public int updateTgCoupon(TgCoupon tgCoupon)
    {
        tgCoupon.setUpdateTime(DateUtils.getNowDate());
        return tgCouponMapper.updateTgCoupon(tgCoupon);
    }

    /**
     * 批量删除优惠券管理
     * 
     * @param couponIds 需要删除的优惠券管理主键
     * @return 结果
     */
    @Override
    public int deleteTgCouponByCouponIds(Long[] couponIds)
    {
        return tgCouponMapper.deleteTgCouponByCouponIds(couponIds);
    }

    /**
     * 删除优惠券管理信息
     * 
     * @param couponId 优惠券管理主键
     * @return 结果
     */
    @Override
    public int deleteTgCouponByCouponId(Long couponId)
    {
        return tgCouponMapper.deleteTgCouponByCouponId(couponId);
    }
}
