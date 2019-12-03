package com.pinyougou.page.service.impl;

//import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbGoodsDesc;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;


import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("${pagedir}")
    private String pagedir;

    @Autowired
    private FreeMarkerConfig freeMarkerConfig;


    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Override
    public boolean generateHtml(Long goodsId) {
        Configuration configuration = freeMarkerConfig.getConfiguration();
        try {
            Template template = configuration.getTemplate("item.ftl");
            // 创建数据模型
            Map dataModel = new HashMap();
            // 1.商品主表数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goods",goods);
            //2.商品扩展表数据
            TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);
            dataModel.put("goodsDesc",goodsDesc);
            // 3.商品分类（生成面包屑）
            String Category1 = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
            String Category2 = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
            String Category3 = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

            dataModel.put("Category1",Category1);
            dataModel.put("Category2",Category2);
            dataModel.put("Category3",Category3);

            // 4.读取SKU列表。 添加SKU列表
            TbItemExample example = new TbItemExample();
            TbItemExample.Criteria criteria = example.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId); //SPU列表
            criteria.andStatusEqualTo("1"); // 状态有效
            example.setOrderByClause("is_default desc"); // 按是否默认字段进行降序排序，目的是返回的结果的第一条为默认SKU
            List<TbItem> itemList = itemMapper.selectByExample(example);

            dataModel.put("itemList",itemList);
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(pagedir+goodsId+".html"),"utf-8");

           // Writer out = new FileWriter(pagedir+goodsId+".html");
            // 进行模板输出
             template.process(dataModel,out);
             out.close();
             return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean deleteHtml(Long[] goodsIds) {
        try {
            for (Long goodsId : goodsIds) {
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }
}
