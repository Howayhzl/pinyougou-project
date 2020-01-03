package com.pinyougou.task;

import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    @Scheduled(cron="* * * * * ?")
    public void refreshSeckillGoods(){
        System.out.println("执行了秒杀商品增量更新 任务调度"+new Date());

        //查询缓存中的秒杀商品ID集合
        List goodsIdList =  new ArrayList( redisTemplate.boundHashOps("seckillGoods").keys());
        System.out.println(goodsIdList);

        TbSeckillGoodsExample example=new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo("1");// 审核通过的商品
        criteria.andStockCountGreaterThan(0);//库存数大于0
        criteria.andStartTimeLessThanOrEqualTo(new Date());//开始日期小于等于当前日期
        criteria.andEndTimeGreaterThanOrEqualTo(new Date());//截止日期大于等于当前日期

        if(goodsIdList.size()>0){
            criteria.andIdNotIn(goodsIdList);//排除缓存中已经存在的商品ID集合
        }

        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(example);
        //将列表数据装入缓存
        for(TbSeckillGoods seckillGoods:seckillGoodsList){
            redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            System.out.println("增量更新秒杀商品ID:"+seckillGoods.getId());
        }
        System.out.println(".....end....");

    }
}
