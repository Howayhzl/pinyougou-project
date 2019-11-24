package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // 1.查询列表
        map.putAll(searchList(searchMap));

        // 2.分组查询商品分类列表
        List<String> categorylist = searchCategorylist(searchMap);
        map.put("categorylist",categorylist);
        return map;
    }

    // 查询列表，高亮显示
    private Map searchList(Map searchMap) {
        Map map = new HashMap();

        // 构建高亮选项
        HighlightQuery query = new SimpleHighlightQuery();
        HighlightOptions highlightOptions = new HighlightOptions().addField("item_title"); //高亮域
        highlightOptions.setSimplePrefix("<em style='color:red'>"); //前缀
        highlightOptions.setSimplePostfix("</em>");// 后缀

        query.setHighlightOptions(highlightOptions); // 为查询选项设置高亮域

        // 关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

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

}
