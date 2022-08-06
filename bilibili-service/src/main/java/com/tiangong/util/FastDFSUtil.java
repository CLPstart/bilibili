package com.tiangong.util;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.tiangong.domain.exception.ConditionException;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.util
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-16  21:25
 * @Description: FastDFS的工具类
 * @Version: 1.0
 */
@Component
public class FastDFSUtil {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    @Autowired
    private AppendFileStorageClient appendFileStorageClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static final String DEFAULT_GROUP = "group1";

    private static final String PATH_KEY = "path-key:";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";

    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";

    private static final int SLICE_SIZE = 1024 * 1024 * 1024;

    @Value("${fdfs.http.storage-addr}")
    private String httpFdfsStorageAddr;

    /**
    * @description: 返回文件类型
    * @author: ChenLipeng
    * @date: 2022/7/16 21:44
    * @param: file
    * @return: java.lang.String
    **/
    public String getFileType(
            //Spring封装的文件类，可以代替File
            MultipartFile file){
        //判断文件是否存在
        if (file == null){
            throw new ConditionException("非法文件！");
        }
        //获取文件名
        String fileName = file.getOriginalFilename();
        //获取文件名最后一个点的位置
        int index = fileName.lastIndexOf(".");
        //返回文件类型
        return fileName.substring(index + 1);
    }

    /**
    * @description: 上传普通文件
    * @author: ChenLipeng
    * @date: 2022/7/16 21:46
    * @param: file
    * @return: java.lang.String
    **/
    public String uploadCommonFile(MultipartFile file) throws Exception {
        //设置一个空的metaData集合
        Set<MetaData> metaDataSet = new HashSet<>();
        //获取上传文件的类型
        String fileType = this.getFileType(file);
        //调用fastdfs上传文件并返回文件路径
        StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), fileType, metaDataSet);
        //返回文件相对路径
        return storePath.getPath();
    }

    /**
    * @description: 上传普通文件
    * @author: ChenLipeng
    * @date: 2022/8/5 15:23
    * @param: file
    * @param: fileType
    * @return: java.lang.String
    **/
    public String uploadCommonFile(File file, String fileType) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        StorePath storePath = fastFileStorageClient.uploadFile(new FileInputStream(file),
                file.length(), fileType, metaDataSet);
        return storePath.getPath();
    }

    /**
    * @description: 上传可以断点续传的文件
    * @author: ChenLipeng
    * @date: 2022/7/16 21:56
    * @param: file
    * @return: java.lang.String
    **/
    public String uploadAppenderFile(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    /**
    * @description: 添加分片文件
    * @author: ChenLipeng
    * @date: 2022/7/16 22:02
    * @param: file
    * @param: filePath
    * @param: offset
    **/
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws Exception {
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath, file.getInputStream(), file.getSize(), offset);
    }

    /**
    * @description: 分片上传文件
    * @author: ChenLipeng
    * @date: 2022/7/16 22:40
    * @param: file
    * @param: fileMd5
    * @param: slicesNo
    * @param: totalSlicesNo
    * @return: java.lang.String
    **/
    public String uploadFileBySlices(MultipartFile file,
                                     //每个文件独一无二的md5加密参数
                                     String fileMd5,
                                     //当前文件是第几片
                                     Integer slicesNo,
                                     //文件的总片数
                                     Integer totalSlicesNo) throws Exception {
        //判断参数是否合法
        if (file == null || slicesNo == null || totalSlicesNo == null){
            throw  new ConditionException("参数异常！");
        }
        //redis中存储文件路径的key
        String pathKey = PATH_KEY + fileMd5;
        //redis中存储当前文件大小的key
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
        //redis中存储当前文件片数的key
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;
        //从redis中获取文件当前上传的大小
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        //默认上传了0B
        Long uploadedSize = 0L;
        //如果redis留存上传的大小，则更新默认值
        if(!StringUtil.isNullOrEmpty(uploadedSizeStr)){
            uploadedSize = Long.valueOf(uploadedSizeStr);
        }
        //获取文件类型
        String fileType = this.getFileType(file);
        //如果当前上传文件是第一片。即刚开始上传文件
        if (slicesNo == 1){
            //调用上传断点续传文件的方法
            String path = this.uploadAppenderFile(file);
            //如果没有接收到返回值则上传失败
            if (StringUtil.isNullOrEmpty(path)){
                throw new ConditionException("上传失败！");
            }
            //在redis中存入当前上传文件的文件路径
            redisTemplate.opsForValue().set(pathKey, path);
            //在redis中存入上传了一片
            redisTemplate.opsForValue().set(uploadedNoKey, "1");
            //如果不是第一片
        }else{
            //获取已经上传好的文件的路径
            String filePath = redisTemplate.opsForValue().get(pathKey);
            //如果得不到文件路径则上传失败
            if (StringUtil.isNullOrEmpty(filePath)){
                throw new ConditionException("上传失败！");
            }
            //调用向后添加断点分片文件的方法
            this.modifyAppenderFile(file, filePath, uploadedSize);
            //上传的片数自增1
            redisTemplate.opsForValue().increment(uploadedNoKey);
        }
        //更新当前上传的文件大小并存入redis
        uploadedSize += file.getSize();
        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));
        //获取当前上传的片数
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        //初始化返回结果，如果全部上传成功则返回文件路径，否则返回空路径
        String resultPath = "";
        //判断当前上传片数是否等于总片数
        if (uploadedNo.equals(totalSlicesNo)){
            //如果相等则说明上传完成，把文件路径赋值给最终结果
            resultPath = redisTemplate.opsForValue().get(pathKey);
            //创建redis key列表
            List<String> keyList = Arrays.asList(uploadedNoKey, pathKey, uploadedSizeKey);
            //从redis中删除列表中的key即对应的值
            redisTemplate.delete(keyList);
        }
        //返回结果
        return resultPath;
    }

    /**
    * @description: 文件分片，实际开发在前端完成，此方法为模拟前端
    * @author: ChenLipeng
    * @date: 2022/7/17 10:14
    * @param: multipartFile
    **/
    public void convertFileToSlices(MultipartFile multipartFile) throws Exception {
        //获取文件名称
        String fileName = multipartFile.getOriginalFilename();
        //获取文件类型
        String fileType = this.getFileType(multipartFile);
        //生成临时文件file
        File file = this.multipartFileToFile(multipartFile);
        //获取文件长度
        long fileLength = file.length();
        //分片计数器
        int count = 1;
        //遍历文件，每次分片2M
        for (int i = 0; i < fileLength; i += SLICE_SIZE) {
            //以只读的权限创建文件随机存取类
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            //指针指向i位置
            randomAccessFile.seek(i);
            //创建字节数组，大小为2M
            byte[] bytes = new byte[SLICE_SIZE];
            //将当前分好的一片文件读入内存
            int len = randomAccessFile.read(bytes);
            //定义分片文件存储位置
            String path = "D:\\file\\tmp\\" + count + "." +fileType;
            //创建一个分片文件类
            File slice = new File(path);
            //开启文件输出流
            FileOutputStream fos = new FileOutputStream(slice);
            //将文件片写入硬盘
            fos.write(bytes, 0, len);
            //关流
            fos.close();
            randomAccessFile.close();
            //分片计数器自增
            count++;
        }
        //删除临时文件
        file.delete();
    }

    /**
    * @description: 将MultipartFile转换为File
    * @author: ChenLipeng
    * @date: 2022/7/17 9:59
    * @param: multipartFile
    * @return: java.io.File
    **/
    public File multipartFileToFile(MultipartFile multipartFile) throws Exception {
        //获取文件名称
        String originalFilename = multipartFile.getOriginalFilename();
        //通过正则划分文件名称及文件类型
        String[] fileName = originalFilename.split("\\.");
        //创建一个临时文件
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        //进行转换
        multipartFile.transferTo(file);
        //返回结果
        return file;
    }

    /**
    * @description: 通过文件相对路径删除文件
    * @author: ChenLipeng 
    * @date: 2022/7/16 21:48 
    * @param: filePath
    **/
    public void deleteFile(String filePath){
        //调用fastdfs删除文件
        fastFileStorageClient.deleteFile(filePath);
    }

    /**
    * @description: 在线观看视频方法
    * @author: ChenLipeng
    * @date: 2022/7/17 19:28
    * @param: request
    * @param: response
    * @param: path
    **/
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        //获取视频的详细信息
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        //获取视频大小
        long totalFileSize = fileInfo.getFileSize();
        //获取视频的url
        String url = httpFdfsStorageAddr + path;
        //得到请求头信息的枚举类
        Enumeration<String> headerNames = request.getHeaderNames();
        //创建空的请求头信息存放的Map
        Map<String, Object> headers = new HashMap<>();
        //遍历枚举类，并将相应的信息放入Map中
        while (headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        //从请求头中获取range
        String rangeStr = request.getHeader("Range");
        //创建空的range分割数组
        String[] range;
        //如果视频没有进行分片那么默认范围就是从开始到结束
        if (StringUtil.isNullOrEmpty(rangeStr)){
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }
        //将分片信息按照bytes字段以及-字段划分成3段
        range = rangeStr.split("bytes=|-");
        //创建开始点
        long begin = 0;
        //如果分割数组的大小大于2那么就存在开始字段
        if (range.length >= 2){
            begin = Long.parseLong(range[1]);
        }
        //创建结束字段
        long end = totalFileSize - 1;
        //如果分片信息大于等于3那么就存在结束字段
        if (range.length >= 3){
            end = Long.parseLong(range[2]);
        }
        //计算分片总长度
        long len = (end - begin) + 1;
        //定义响应头信息
        String contentRange = "bytes " + begin + "-" + end + "/" + totalFileSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int)len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        //调用http工具类实现在线视频播放
        HttpUtil.get(url, headers, response);
    }

    /**
    * @description: 下载文件方法
    * @author: ChenLipeng
    * @date: 2022/8/4 16:16
    * @param: url
    * @param: filePath
    **/
    public void downLoadFile(String url, String localPath) {
        //直接调用fastDFS的存储客户端下载
        fastFileStorageClient.downloadFile(
                DEFAULT_GROUP, //下载分组
                url, //下载地址
                new DownloadCallback<String>() { //回调函数的匿名内部类
                    //重写回调方法
                    @Override
                    public String recv(InputStream ins) throws IOException {
                        //实例化文件类
                        File file = new File(localPath);
                        //开启输出流
                        OutputStream os = new FileOutputStream(file);
                        //将输入流中的文件写入硬盘中
                        int len = 0;
                        byte[] buffer = new byte[1024];
                        while ((len = ins.read(buffer)) != -1){
                            os.write(buffer, 0, len);
                        }
                        //关闭流
                        os.close();
                        ins.close();
                        //返回成功结果
                        return "success";
                    }
                });
    }
}
