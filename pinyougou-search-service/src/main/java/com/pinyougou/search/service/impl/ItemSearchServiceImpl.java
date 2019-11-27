package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service(timeout = 5000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;

    @Override
    public Map search(Map searchMap) {
        Map map = new HashMap();
        /*
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        map.put("rows",page.getContent());*/
        // 空格处理
        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords",keywords.replace(" ",""));

        // 1.查询列表
        map.putAll(searchList(searchMap));

        // 2.分组查询商品分类列表
        List<String> categorylist = searchCategorylist(searchMap);
        map.put("categorylist",categorylist);

        // 3.查询品牌和规格列表
        String category = (String) searchMap.get("category");
        if (!category.equals("")){
            map.putAll(searchBrandAndSpecList(category));
        }else {
            if (categorylist.size()>0){
                map.putAll(searchBrandAndSpecList(categorylist.get(0)));
            }
        }
        return map;
    }

    // 查询列表，高亮显示
    private Map searchList(Map searchMap) {
        Map map = new HashMap();

        // 构建高亮选项，高亮选项初始化
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title"); //高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>"); //前缀
        highlightOptions.setSimplePostfix("</em>");// 后缀

        query.setHighlightOptions(highlightOptions); // 为查询选项设置高亮域

        // 1.1关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        // 在获取结果集之前需要过滤
        // 1.2 按商品分类过滤
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        // 1.3 按品牌分类过滤
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }

        // 1.4按照规格过滤
        if(searchMap.get("spec")!=null){
            Map<String,String> specMap= (Map) searchMap.get("spec");
            for(String key:specMap.keySet() ){
                Criteria filterCriteria=new Criteria("item_spec_"+key).is( specMap.get(key) );
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        // 1.5按照价格过滤
        if(!"".equals(searchMap.get("price"))){
            String priceStr = (String) searchMap.get("price"); //500-1000
            String[] price = priceStr.split("-");
            if (!price[0].equals("0")){//如果最低介个不等于0
                Criteria filterCriteria=new Criteria("item_price").greaterThan(price[0]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            if (!price[1].equals("*")){//如果最高价格介个不等于*
                Criteria filterCriteria=new Criteria("item_price").greaterThan(price[1]);
                FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }

        }

        // 1.6 分页
        Integer pageNo = (Integer) searchMap.get("pageNo"); // 获取页码
        if (pageNo==null){
            pageNo =1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize"); // 获取页大小
        if (pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize); //起始索引
        query.setRows(pageSize); //每页记录数

        // ************ 获取高亮结果集 ****************************
        // 高亮页对象
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);

        // 高亮入口集合（每条记录的高亮入口）
        List<HighlightEntry<TbItem>> entryList = page.getHighlighted();

        for (HighlightEntry<TbItem> entry : entryList) {
            // 获取高亮列表（高亮域的个数）
            List<HighlightEntry.Highlight> highlightList = entry.getHighlights();

            /*for (HighlightEntry.Highlight highlight : highlightList) {
                List<String> sns= highlight.getSnipplets(); // 每个域可能存储多值
                System.out.println(sns);
            }*/
            if (highlightList.size()>0 && highlightList.get(0).getSnipplets().size()>0){
                TbItem item = entry.getEntity();
                item.setTitle(highlightList.get(0).getSnipplets().get(0));
            }


        }
        map.put("rows",page.getContent());
        map.put("totalPages",page.getTotalPages());
        map.put("total",page.getTotalElements());
        return map;
    }


    /**
     * 分组查询
     * @param searchMap
     * @return
     */
    public List<String> searchCategorylist(Map searchMap){
        List<String> list = new ArrayList<>();

        Query query = new SimpleQuery("*:*");
        // 根据关键字查询 相当于where
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords")); // where
        query.addCriteria(criteria);
        // 设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions); // 相当于groupby
        // 获取分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        // 获取分组结果对象
        GroupResult<TbItem> groupResultList = page.getGroupResult("item_category");
        // 获取分组入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResultList.getGroupEntries();
        // 获取分组入口集合
        List<GroupEntry<TbItem>> entries = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : entries) {
            String groupValue = entry.getGroupValue();
            list.add(groupValue); // 将分组的结果放入发list集合
        }
        return list;
    }

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据商品分类名称查询品牌列表和规格列表，并放入map
     * @param category
     * @return
     */
    public Map searchBrandAndSpecList(String category){
        Map map = new HashMap();

        // 1.根据商品名称获取模板ID
        Long templateId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (templateId != null){
            // 2.根据模板ID获取品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(templateId);
            map.put("brandList",brandList);
            System.out.println("品牌列表条数："+brandList.size());

            // 3.根据模板ID获取规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(templateId);
            map.put("specList",specList);
            System.out.println("规格列表条数："+specList.size());
        }

        return map;
    }

}
