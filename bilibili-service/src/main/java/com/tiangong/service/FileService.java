package com.tiangong.service;

import com.tiangong.dao.FileDao;
import com.tiangong.domain.File;
import com.tiangong.util.FastDFSUtil;
import com.tiangong.util.MD5Util;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-17  10:45
 * @Description: 文件上传业务层
 * @Version: 1.0
 */
@Service
public class FileService {

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private FileDao fileDao;

    /**
    * @description: 上传分片文件
    * @author: ChenLipeng
    * @date: 2022/7/17 13:26
    * @param: slice
    * @param: fileMD5
    * @param: sliceNo
    * @param: totalSliceNo
    * @return: java.lang.String
    **/
    public String uploadFileBySlices(MultipartFile slice,
                                     String fileMD5,
                                     Integer sliceNo,
                                     Integer totalSliceNo) throws Exception {
        //通过md5加密码判断文件是否被上传过
        File dbFileMD5 = fileDao.getFileByMD5(fileMD5);
        //如果文件不为空则说明文件被上传过，那么直接返回结果
        if(dbFileMD5 != null){
            return dbFileMD5.getUrl();
        }
        //如果数据库中没有对应记录那么就上传文件
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);
        //如果全部上传完成就将上传后的文件存入数据库
        if(!StringUtil.isNullOrEmpty(url)){
            dbFileMD5 = new File();
            dbFileMD5.setCreateTime(new Date());
            dbFileMD5.setMd5(fileMD5);
            dbFileMD5.setUrl(url);
            dbFileMD5.setType(fastDFSUtil.getFileType(slice));
            fileDao.addFile(dbFileMD5);
        }
        return url;
    }

    /**
    * @description: 得到md5加密码
    * @author: ChenLipeng
    * @date: 2022/7/17 13:29
    * @param: file
    * @return: java.lang.String
    **/
    public String getFileMD5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }

    /**
    * @description: 通过md5加密码从数据库中查找文件
    * @author: ChenLipeng
    * @date: 2022/7/17 13:29
    * @param: fileMd5
    * @return: com.tiangong.domain.File
    **/
    public File getFileByMd5(String fileMd5) {
        return fileDao.getFileByMD5(fileMd5);
    }

}
