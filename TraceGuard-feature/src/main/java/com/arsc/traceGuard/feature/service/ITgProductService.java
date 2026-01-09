package com.arsc.traceGuard.feature.service;

import java.util.List;
import com.arsc.traceGuard.feature.domain.TgProduct;

/**
 * 产品信息Service接口
 * 
 * @author zhangcj
 * @date 2026-01-06
 */
public interface ITgProductService 
{
    /**
     * 查询产品信息
     * 
     * @param productId 产品信息主键
     * @return 产品信息
     */
    public TgProduct selectTgProductByProductId(Long productId);

    /**
     * 查询产品信息列表
     * 
     * @param tgProduct 产品信息
     * @return 产品信息集合
     */
    public List<TgProduct> selectTgProductList(TgProduct tgProduct);

    /**
     * 新增产品信息
     * 
     * @param tgProduct 产品信息
     * @return 结果
     */
    public int insertTgProduct(TgProduct tgProduct);

    /**
     * 修改产品信息
     * 
     * @param tgProduct 产品信息
     * @return 结果
     */
    public int updateTgProduct(TgProduct tgProduct);

    /**
     * 批量删除产品信息
     * 
     * @param productIds 需要删除的产品信息主键集合
     * @return 结果
     */
    public int deleteTgProductByProductIds(Long[] productIds);

    /**
     * 删除产品信息信息
     * 
     * @param productId 产品信息主键
     * @return 结果
     */
    public int deleteTgProductByProductId(Long productId);
}
