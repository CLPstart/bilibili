package com.tiangong.api;

import com.tiangong.domain.JsonResponse;
import com.tiangong.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.api
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-17  10:45
 * @Description: 文件上传表述层
 * @Version: 1.0
 */
@RestController
public class FileApi {

    @Autowired
    private FileService fileService;

    /**
    * @description: 通过文件的md5加密码查询文件在数据库中是否存在
    * @author: ChenLipeng
    * @date: 2022/7/17 13:24
    * @param: file
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/md5files")
    public JsonResponse<String> getFileMD5(MultipartFile file) throws Exception {
        String fileMD5 = fileService.getFileMD5(file);
        return new JsonResponse<>(fileMD5);
    }

    /**
    * @description: 上传分片文件
    * @author: ChenLipeng
    * @date: 2022/7/17 13:25
    * @param: slice
    * @param: fileMd5
    * @param: fileNo
    * @param: totalSliceNo
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PutMapping("/file-slices")
    public JsonResponse<String> uploadFileSlices(MultipartFile slice,
                                                 String fileMd5,
                                                 Integer fileNo,
                                                 Integer totalSliceNo) throws Exception{
        String filePath = fileService.uploadFileBySlices(slice, fileMd5, fileNo, totalSliceNo);
        return new JsonResponse<>(filePath);
    }

}
