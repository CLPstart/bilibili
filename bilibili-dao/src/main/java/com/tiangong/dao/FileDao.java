package com.tiangong.dao;

import com.tiangong.domain.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-17  13:11
 * @Description: t_file表的查询映射
 * @Version: 1.0
 */
@Mapper
public interface FileDao {

    /**
    * @description: 通过md5加密码从数据库中查找文件
    * @author: ChenLipeng
    * @date: 2022/7/17 13:30
    * @param: md5
    * @return: com.tiangong.domain.File
    **/
    File getFileByMD5(String md5);

    /**
    * @description: 向数据库添加文件
    * @author: ChenLipeng
    * @date: 2022/7/17 13:30
    * @param: dbFileMD5
    **/
    void addFile(File dbFileMD5);

}
