package com.arsc.traceGuard.feature.service.impl;

import java.util.List;
import com.arsc.traceGuard.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.arsc.traceGuard.feature.mapper.TgProductMapper;
import com.arsc.traceGuard.feature.domain.TgProduct;
import com.arsc.traceGuard.feature.service.ITgProductService;

/**
 * 产品信息Service业务层处理
 * 
 * @author zhangcj
 * @date 2026-01-06
 */
@Service
public class TgProductServiceImpl implements ITgProductService 
{
    @Autowired
    private TgProductMapper tgProductMapper;

    /**
     * 查询产品信息
     * 
     * @param productId 产品信息主键
     * @return 产品信息
     */
    @Override
    public TgProduct selectTgProductByProductId(Long productId)
    {
        return tgProductMapper.selectTgProductByProductId(productId);
    }

    /**
     * 查询产品信息列表
     * 
     * @param tgProduct 产品信息
     * @return 产品信息
     */
    @Override
    public List<TgProduct> selectTgProductList(TgProduct tgProduct)
    {
        return tgProductMapper.selectTgProductList(tgProduct);
    }

    /**
     * 新增产品信息
     * 
     * @param tgProduct 产品信息
     * @return 结果
     */
    @Override
    public int insertTgProduct(TgProduct tgProduct)
    {
        tgProduct.setCreateTime(DateUtils.getNowDate());
        return tgProductMapper.insertTgProduct(tgProduct);
    }

    /**
     * 修改产品信息
     * 
     * @param tgProduct 产品信息
     * @return 结果
     */
    @Override
    public int updateTgProduct(TgProduct tgProduct)
    {
        tgProduct.setUpdateTime(DateUtils.getNowDate());
        return tgProductMapper.updateTgProduct(tgProduct);
    }

    /**
     * 批量删除产品信息
     * 
     * @param productIds 需要删除的产品信息主键
     * @return 结果
     */
    @Override
    public int deleteTgProductByProductIds(Long[] productIds)
    {
        return tgProductMapper.deleteTgProductByProductIds(productIds);
    }

    /**
     * 删除产品信息信息
     * 
     * @param productId 产品信息主键
     * @return 结果
     */
    @Override
    public int deleteTgProductByProductId(Long productId)
    {
        return tgProductMapper.deleteTgProductByProductId(productId);
    }
}
