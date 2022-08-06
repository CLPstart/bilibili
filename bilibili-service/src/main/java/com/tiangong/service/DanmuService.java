package com.tiangong.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tiangong.dao.DanmuDao;
import com.tiangong.domain.Danmu;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-19  09:41
 * @Description: 弹幕功能业务层
 * @Version: 1.0
 */
@Service
public class DanmuService {

    @Autowired
    private DanmuDao danmuDao;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String DANMU_KEY = "dm-video-";

    /**
    * @description: 添加弹幕业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/19 10:14
    * @param: danmu
    **/
    public void addDanmu(Danmu danmu){
        danmuDao.addDanmu(danmu);
    }

    /**
    * @description: 异步添加弹幕
    * @author: ChenLipeng
    * @date: 2022/7/19 15:03
    * @param: danmu
    **/
    @Async
    public void asyncAddDanmu(Danmu danmu){
        danmuDao.addDanmu(danmu);
    }

    /**
    * @description: 获取弹幕业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/19 10:15
    * @param: params
    * @return: java.util.List<com.tiangong.domain.Danmu>
    **/
    public List<Danmu> getDanmus(Long videoId,
                                 String startTime,
                                 String endTime) throws ParseException {
        //生成弹幕的key
        String key = DANMU_KEY + videoId;
        //从redis中查询value
        String value = redisTemplate.opsForValue().get(key);
        //实例化弹幕列表
        List<Danmu> list;
        if (!StringUtil.isNullOrEmpty(value)){
            //如果redis中有值就把其赋值给弹幕列表
            list = JSONArray.parseArray(value, Danmu.class);
            //判断开始时间和结束时间是否存在
            if (!StringUtil.isNullOrEmpty(startTime) && !StringUtil.isNullOrEmpty(endTime)){
                //解析时间
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate = sdf.parse(startTime);
                Date endDate = sdf.parse(endTime);
                List<Danmu> childList = new ArrayList<>();
                //删选符合时间段的弹幕
                for (Danmu danmu : list){
                    Date createTime = danmu.getCerateTime();
                    if (createTime.after(startDate) && createTime.before(endDate)){
                        childList.add(danmu);
                    }
                }
                list = childList;
            }
        }else{
            //如果redis中没有弹幕信息，那么就从mysql中查询
            Map<String, Object> params = new HashMap<>();
            params.put("videoId", videoId);
            params.put("startTime", startTime);
            params.put("endTime", endTime);
            list = danmuDao.getDanmus(params);
            //再把查询出来的结果写回redis
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
        }
        return list;
    }

    /**
    * @description: 将弹幕写入redis
    * @author: ChenLipeng
    * @date: 2022/7/19 10:15
    * @param: danmu
    **/
    public void addDanmuToRedis(Danmu danmu){
        //定义key
        String key = "danmu-video-" + danmu.getVideoId();
        //获取value
        String value = redisTemplate.opsForValue().get(key);
        //定义弹幕列表
        List<Danmu> list = new ArrayList<>();
        //判断该视频是否有弹幕，如果存在那么就把弹幕写入弹幕列表
        if(!StringUtil.isNullOrEmpty(value)){
            list = JSONArray.parseArray(value, Danmu.class);
        }
        //在弹幕列表中添加新的弹幕
        list.add(danmu);
        //将新弹幕写入redis
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(list));
    }

}
