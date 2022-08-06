package com.tiangong.service;

import com.tiangong.dao.VideoDao;
import com.tiangong.domain.*;
import com.tiangong.domain.exception.ConditionException;
import com.tiangong.util.FastDFSUtil;
import com.tiangong.util.ImageUtil;
import com.tiangong.util.IpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-17  13:50
 * @Description: 视频功能业务层
 * @Version: 1.0
 */
@Service
public class VideoService {

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private userCoinService userCoinService;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    private static final int FRAME_NO = 256;

    @Autowired
    private ImageUtil imageUtil;

    /**
    * @description: 添加视频的业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/17 17:13
    * @param: video
    **/
    @Transactional
    public void addVideos(Video video) {
        //生成当前时间
        Date now = new Date();
        //设置创建时间
        video.setCreateTime(new Date());
        //调用数据层添加视频方法
        videoDao.addVideos(video);
        //获取视频id
        Long videoId = video.getId();
        //获取视频标签
        List<VideoTag> tagList = video.getVideoTagList();
        //给标签赋值
        tagList.forEach(item -> {
            item.setCreateTime(now);
            item.setVideoId(videoId);
        });
        //调用批量添加标签方法
        videoDao.batchAddVideoTags(tagList);
    }

    /**
    * @description: 分页查询瀑布流视频的业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/17 18:06
    * @param: size
    * @param: no
    * @param: area
    * @return: com.tiangong.domain.PageResult<com.tiangong.domain.Video>
    **/
    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        //判断前端参数是否有异常
        if(size == null || no == null){
            throw new ConditionException("参数异常");
        }
        //创建参数映射
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("area", area);
        //创建分页列表
        List<Video> list = new ArrayList<>();
        //查询视频总数
        Integer total = videoDao.pageCountVideos(params);
        //如果视频数量大于零那么就根据参数查询
        if (total > 0){
            list = videoDao.pageListVideos(params);
        }
        return new PageResult<>(total,list);
    }

    /**
    * @description: 在线观看视频业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/17 19:27
    * @param: request
    * @param: response
    * @param: url
    **/
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String url) throws Exception {
        //调用工具类方法
        fastDFSUtil.viewVideoOnlineBySlices(request, response, url);
    }

    /**
    * @description: 点赞的业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/17 21:26
    * @param: userId
    * @param: videoId
    **/
    public void addVideoLike(Long userId, Long videoId) {
        //根据视频id查询视频，判断视频是否合法
        Video video = videoDao.getVideoById(videoId);
        if (video == null) {
            throw new ConditionException("视频非法！");
        }
        //查询点赞表，判断是否已经赞过
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(userId, videoId);
        if (videoLike != null){
            throw new ConditionException("已经赞过！");
        }
        //点赞，并把信息存入点赞表
        videoLike = new VideoLike();
        videoLike.setVideoId(videoId);
        videoLike.setUserId(userId);
        videoLike.setCreateTime(new Date());
        videoDao.addVideoLike(videoLike);
    }

    /**
    * @description: 取消点赞的业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/17 21:38
    * @param: videoId
    * @param: userId
    **/
    public void deleteVideoLike(Long videoId, Long userId) {
        //调用数据层方法删除点赞信息
        videoDao.deleteVideoLike(videoId, userId);
    }

    /**
    * @description: 查询视频点赞数量及用户是否点赞过该视频的方法
    * @author: ChenLipeng
    * @date: 2022/7/17 21:51
    * @param: videoId
    * @param: userId
    * @return: java.util.Map<java.lang.String,java.lang.Object>
    **/
    public Map<String, Object> getVideoLikes(Long videoId, Long userId) {
        //查询视频点赞数量
        Long count = videoDao.getVideoLikes(videoId);
        //查询用户是否点赞过视频
        VideoLike videoLike = videoDao.getVideoLikeByVideoIdAndUserId(userId, videoId);
        boolean like = videoLike != null;
        //封装结果
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    /**
    * @description: 添加收藏业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/18 9:54
    * @param: videoCollection
    * @param: userId
    **/
    public void addVideoCollection(VideoCollection videoCollection, Long userId) {
        //获取视频id
        Long videoId = videoCollection.getVideoId();
        //获取分组id
        Long groupId = videoCollection.getGroupId();
        //判断参数是否正常
        if(videoId == null || groupId == null){
            throw new ConditionException("参数异常！");
        }
        //根据视频id判断视频是否合法
        Video video = videoDao.getVideoById(videoId);
        if (video == null){
            throw new ConditionException("非法视频！");
        }
        //先删除原有视频收藏
        videoDao.deleteVideoCollection(videoId, userId);
        //设置新收藏类
        videoCollection.setUserId(userId);
        videoCollection.setCreateTime(new Date());
        //再添加新收藏
        videoDao.addVideoCollection(videoCollection);
    }

    /**
    * @description: 取消收藏表述层
    * @author: ChenLipeng
    * @date: 2022/7/18 10:01
    * @param: videoId
    * @param: userId
    **/
    public void deleteVideoCollection(Long videoId, Long userId) {
        //调用数据层方法取消收藏
        videoDao.deleteVideoCollection(videoId, userId);
    }


    /**
    * @description: 查询视频收藏量以及视频是否被当前用户收藏的业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/18 10:13
    * @param: videoId
    * @param: userId
    * @return: java.util.Map<java.lang.String,java.lang.Object>
    **/
    public Map<String, Object> getVideoCollections(Long videoId, Long userId) {
        //调用数据层查询用户的收藏量
        Long count = videoDao.getVideoCollections(videoId);
        //调用数据层查询用户是否收藏当前视频
        VideoCollection videoCollection = videoDao.getVideoCollectionByVideoIdAndUserId(videoId, userId);
        boolean like = videoCollection != null;
        //返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    /**
    * @description: 投币的业务层
    * @author: ChenLipeng
    * @date: 2022/7/18 11:46
    * @param: videoCoin
    * @param: userId
    **/
    @Transactional
    public void addVideoCoins(VideoCoin videoCoin, Long userId) {
        //获取视频id
        Long videoId = videoCoin.getVideoId();
        //获取投币个数
        Integer amount = videoCoin.getAmount();
        //判断视频id是否合法
        if(videoId == null){
            throw new ConditionException("参数异常！");
        }
        //查询是否有对应id的视频
        Video video = videoDao.getVideoById(videoId);
        if(video == null){
            throw new ConditionException("视频非法！");
        }
        //获取用户拥有的硬币数
        Integer userCoinsAmount = userCoinService.getUserCoinsAmount(userId);
        //如果用户没有硬币那么默认值为0
        userCoinsAmount = userCoinsAmount == null ? 0 : userCoinsAmount;
        //投币数必须小于用户拥有的硬币数
        if (amount > userCoinsAmount){
            throw new ConditionException("硬币数量不足！");
        }
        //查询用户是否之前投过币
        VideoCoin dbVideoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        if (dbVideoCoin == null){
            //如果用户没投过币，那么就创建投币信息
            videoCoin.setUserId(userId);
            videoCoin.setCreateTime(new Date());
            videoDao.addVideoCoin(videoCoin);
        }else {
            //如果用户投过币，那么久更新信息
            Integer dbAmount = videoCoin.getAmount();
            dbAmount += amount;
            videoCoin.setUserId(userId);
            videoCoin.setAmount(dbAmount);
            videoCoin.setUpdateTime(new Date());
            videoDao.updateVideoCoin(videoCoin);
        }
        //更新用户有用的硬币数目
        userCoinService.updateUserCoinsAmount(userId, (userCoinsAmount - amount));
    }

    /**
    * @description: 获取视频投币总数以及当前用户是否对当前视频进行投币
    * @author: ChenLipeng
    * @date: 2022/7/18 12:07
    * @param: videoId
    * @param: userId
    * @return: java.util.Map<java.lang.String,java.lang.Object>
    **/
    public Map<String, Object> getVideoCoins(Long videoId, Long userId) {
        //获取视频投币总数
        Long count = videoDao.getVideoCoins(videoId, userId);
        //判断当前用户是否对当前视频投币
        VideoCoin videoCoin = videoDao.getVideoCoinByVideoIdAndUserId(videoId, userId);
        boolean like = videoCoin != null;
        //返回结果集
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("like", like);
        return result;
    }

    /**
    * @description: 发表视频评论的业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/18 13:09
    * @param: videoComment
    * @param: userId
    **/
    public void addVideoComment(VideoComment videoComment, Long userId) {
        //获取视频id并判断合法性
        Long videoId = videoComment.getVideoId();
        if (videoId == null){
            throw new ConditionException("参数异常！");
        }
        //根据id获取视频并判断视频是否存在
        Video video = videoDao.getVideoById(videoId);
        if (video == null){
            throw new ConditionException("视频非法！");
        }
        //调用数据层方法添加视频评论
        videoComment.setUserId(userId);
        videoComment.setCreateTime(new Date());
        videoDao.addVideoComment(videoComment);
    }

    /**
    * @description: 分页查询视频评论业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/18 14:01
    * @param: size
    * @param: no
    * @param: videoId
    * @return: com.tiangong.domain.PageResult<com.tiangong.domain.VideoComment>
    **/
    public PageResult<VideoComment> pageListVideoComments(Integer size, Integer no, Long videoId) {
        //通过id查询视频并判断视频是否合法
        Video video = videoDao.getVideoById(videoId);
        if(video == null){
            throw new ConditionException("非法视频！");
        }
        //生成分页查询参数
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no - 1) * size);
        params.put("limit", size);
        params.put("videoId", videoId);
        //查询一级评论总数
        Integer total = videoDao.pageCountVideoComments(params);
        //创建一级评论列表
        List<VideoComment> list = new ArrayList<>();
        //如果一级评论总数大于0
        if (total > 0){
            //把一级评论详细信息都查出来
            list = videoDao.pageListVideoComments(params);
            //查询所有一级评论的用户id
            List<Long> parentIdList = list.stream().map(VideoComment::getUserId).collect(Collectors.toList());
            //根据一级评论的用户id查询所有二级评论
            List<VideoComment> childCommentList = videoDao.batchGetVideoCommentsByRootId(parentIdList);
            //对一级评论用户id去重
            Set<Long> userIdList = list.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            //从二级评论用户id中获取所有用户id并去重
            Set<Long> replyUserIdList = childCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
            //将两类用户id合并
            userIdList.addAll(replyUserIdList);
            //根据用户id查询所有用户详细信息
            List<UserInfo> userInfoList = userService.batchGetUserInfoByUserIds(userIdList);
            //生成用户id与用户详细信息一一对应的映射
            Map<Long, UserInfo> userInfoMap = userInfoList.stream().collect(Collectors.toMap(UserInfo :: getUserId, userInfo -> userInfo));
            //遍历一级评论，并对每一条评论添加对应的二级评论
            list.forEach(comment ->{
                //获取一级评论id
                Long id = comment.getId();
                //创建二级评论列表
                List<VideoComment> childList = new ArrayList<>();
                //遍历所有二级评论，将根节点id与当前一级评论id相同的评论加入二级评论列表中
                childCommentList.forEach(child->{
                    if (id.equals(child.getReplyUserId())){
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserId()));
                        childList.add(child);
                    }
                });
                comment.setChildList(childList);
                comment.setUserInfo(userInfoMap.get(comment.getUserId()));
            });
        }
        return new PageResult<>(total, list);
    }

    /**
    * @description: 获取视频详情业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/18 15:07
    * @param: videoId
    * @return: java.util.Map<java.lang.String,java.lang.Object>
    **/
    public Map<String, Object> getVideoDetails(Long videoId) {
        //调用数据层方法获取视频详情
        Video video = videoDao.getVideoDetails(videoId);
        //获取创建视频用户id
        Long userId = video.getUserId();
        //根据用户id得到用户的详细信息
        User user = userService.getUserInfo(userId);
        UserInfo userInfo = user.getUserInfo();
        //生成返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("video", video);
        result.put("userInfo", userInfo);
        return result;
    }

    /**
    * @description: 添加观看记录业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/22 10:19
    * @param: videoView
    * @param: request
    **/
    public void addVideoView(VideoView videoView, HttpServletRequest request) {
        //获取用户及视频信息
        Long userId = videoView.getUserId();
        Long videoId = videoView.getVideoId();
        //生成clientId
        String agent = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        String clientId = String.valueOf(userAgent.getId());
        String ip = IpUtil.getIP(request);
        //生成查询参数
        Map<String, Object> params = new HashMap<>();
        if (userId != null){
            params.put("userId", userId);
        }else {
            params.put("clientId", clientId);
            params.put("ip", ip);
        }
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        params.put("today", sdf.format(now));
        params.put("videoId", videoId);
        //添加视频观看记录
        VideoView dbVideoView = videoDao.getVideoView(params);
        if (dbVideoView == null){
            //如果今天该用户没有观看
            videoView.setIp(ip);
            videoView.setClientId(clientId);
            videoView.setCreateTime(new Date());
            videoDao.addVideoView(videoView);
        }
    }

    /**
    * @description: 查询播放量业务层方法
    * @author: ChenLipeng
    * @date: 2022/7/22 10:21
    * @param: videoId
    * @return: java.lang.Integer
    **/
    public Integer getVideoViewCounts(Long videoId) {
        return videoDao.getVideoViewCounts(videoId);
    }

    /**
    * @description: 基于用户的协同推荐
    * @author: ChenLipeng
    * @date: 2022/7/22 15:44
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.Video>
    **/
    public List<Video> recommend(Long userId) throws TasteException {
        //根据用户获得其偏爱数据
        List<UserPreference> list = videoDao.getUserPreference(userId);
        //建立数据模型
        DataModel dataModel = this.createDataModel(list);
        //获取用户的相似程度
        UserSimilarity similarity = new UncenteredCosineSimilarity(dataModel);
        //得到两个与该用户最相似的用户
        UserNeighborhood userNeighborhood = new NearestNUserNeighborhood(2, similarity, dataModel);
        long[] ar = userNeighborhood.getUserNeighborhood(userId);
        //实例化推荐器
        Recommender recommender = new GenericUserBasedRecommender(dataModel, userNeighborhood, similarity);
        //每位用户推荐5条视频
        List<RecommendedItem> recommendedItems = recommender.recommend(userId, 5);
        //获取推荐视频的id
        List<Long> itemIds = recommendedItems.stream().map(RecommendedItem ::getItemID).collect(Collectors.toList());
        return videoDao.batchGetVideosByIds(itemIds);
    }

    /**
    * @description: 生成数据模型
    * @author: ChenLipeng
    * @date: 2022/7/22 15:05
    * @param: userPreferencesList
    * @return: org.apache.mahout.cf.taste.model.DataModel
    **/
    private DataModel createDataModel(List<UserPreference> userPreferencesList) {
        //实例化fastByIdMap对象
        FastByIDMap<PreferenceArray> fastByIDMap = new FastByIDMap<>();
        //将形参转换为Map集合
        Map<Long, List<UserPreference>> map = userPreferencesList
                .stream()
                .collect(Collectors.groupingBy(UserPreference :: getUserId));
        //获取每个用户的喜好集合
        Collection<List<UserPreference>> list = map.values();
        //遍历每个用户的喜好
        for (List<UserPreference> userPreferences : list){
            //建立喜好数组
            GenericPreference[] array = new GenericPreference[userPreferences.size()];
            //遍历当前用户的喜好
            for (int i = 0; i < userPreferences.size(); i++) {
                //获取当前用户对当前视频的喜好
                UserPreference userPreference = userPreferences.get(i);
                //生成喜好数组的一条数据
                GenericPreference item = new GenericPreference(userPreference.getUserId(),
                        userPreference.getVideoId(),
                        userPreference.getValue());
                array[i] = item;
            }
            //在fastByIdMap中添加用户与其喜好的对象
            fastByIDMap.put(array[0].getUserID(), new GenericUserPreferenceArray(Arrays.asList(array)));
        }
        //把用户与喜好匹配好的对象返回作为数据模型
        return new GenericDataModel(fastByIDMap);
    }

    /**
    * @description: 对视频截取视频帧
    * @author: ChenLipeng
    * @date: 2022/8/5 15:41
    * @param: videoId
    * @param: fileMd5
    * @return: java.util.List<com.tiangong.domain.VideoBinaryPicture>
    **/
    public List<VideoBinaryPicture> convertVideoToImage(Long videoId, String fileMd5) throws Exception {
        //获取视频文件
        File file = fileService.getFileByMd5(fileMd5);
        //生成本地服务器临时文件的路径
        String filePath = "D:\\BaiduNetdiskDownload\\tmp\\" + videoId + "." + file.getType();
        //从文件服务器中下载文件
        fastDFSUtil.downLoadFile(file.getUrl(), filePath);
        //调用javacv来截取视频帧
        FFmpegFrameGrabber fFmpegFrameGrabber = FFmpegFrameGrabber.createDefault(filePath);
        fFmpegFrameGrabber.start();
        //获取总帧数
        int ffLength = fFmpegFrameGrabber.getLengthInFrames();
        //创建帧率实体类
        Frame frame;
        //创建帧率转换为图片的实体类
        Java2DFrameConverter converter = new Java2DFrameConverter();
        //创建截取计数器
        int count = 1;
        //实例化帧数图片结果集
        List<VideoBinaryPicture> pictures = new ArrayList<>();
        //开始截取帧数
        for (int i = 1; i < ffLength; i++) {
            //确定当前帧的时间戳
            long timestamp = fFmpegFrameGrabber.getTimestamp();
            //获取当前帧
            frame = fFmpegFrameGrabber.grabImage();
            //如果计数器和索引值相同就记录当前帧
            if (count == i){
                if (frame == null){
                    throw new ConditionException("无效帧");
                }
                //将当前帧转化为图片
                BufferedImage bufferedImage = converter.getBufferedImage(frame);
                //开启输出流
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                //将图片写入输出流
                ImageIO.write(bufferedImage, "png", os);
                //将输出流中的数据写入输入流
                InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
                //输出黑白剪影文件
                java.io.File outputFile = java.io.File.createTempFile("convert-" + videoId + "-", ".png");
                //调用工具类得到二值剪影
                BufferedImage binaryImage = imageUtil.getBodyOutline(bufferedImage, inputStream);
                //将二值图写入输出流
                ImageIO.write(binaryImage, "png", outputFile);
                //上传视频剪影文件
                String imgUrl = fastDFSUtil.uploadCommonFile(outputFile, "png");
                VideoBinaryPicture videoBinaryPicture = new VideoBinaryPicture();
                videoBinaryPicture.setFrameNo(i);
                videoBinaryPicture.setUrl(imgUrl);
                videoBinaryPicture.setVideoId(videoId);
                videoBinaryPicture.setVideoTimestamp(timestamp);
                pictures.add(videoBinaryPicture);
                count += FRAME_NO;
                //删除临时文件
                outputFile.delete();
            }
        }
        //删除临时文件
        java.io.File tmpFile = new java.io.File(filePath);
        tmpFile.delete();
        //批量添加剪影
        videoDao.batchAddVideoBinaryPictures(pictures);
        return pictures;
    }
}
