package com.pinyougou.page.service;

public interface ItemPageService {

    /**
     * 生成商品详情页
     * @return
     */
    public boolean generateHtml(Long goodsId);

    /**
     * 删除商品详细页
     * @param ids
     */
    public boolean deleteHtml(Long [] goodsIds);
}
