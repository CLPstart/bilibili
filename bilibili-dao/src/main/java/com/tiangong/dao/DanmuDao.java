package com.tiangong.dao;

import com.tiangong.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-19  09:28
 * @Description: t_danmu表的mapper映射
 * @Version: 1.0
 */
@Mapper
public interface DanmuDao {

    /**
    * @description: 添加弹幕
    * @author: ChenLipeng
    * @date: 2022/7/19 9:30
    * @param: danmu
    * @return: java.lang.Integer
    **/
    Integer addDanmu(Danmu danmu);

    /**
    * @description: 查找弹幕
    * @author: ChenLipeng
    * @date: 2022/7/19 9:30
    * @param: params
    * @return: java.util.List<com.tiangong.domain.Danmu>
    **/
    List<Danmu> getDanmus(Map<String, Object> params);
}
