package com.tiangong.dao;

import com.tiangong.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.dao
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-17  13:51
 * @Description: 视频相关数据层
 * @Version: 1.0
 */
@Mapper
public interface VideoDao {

    /**
    * @description: 添加一个视频
    * @author: ChenLipeng
    * @date: 2022/7/17 17:15
    * @param: video
    * @return: java.lang.Integer
    **/
    Integer addVideos(Video video);

    /**
    * @description: 批量添加视频标签
    * @author: ChenLipeng
    * @date: 2022/7/17 17:16
    * @param: videoTagList
    * @return: java.lang.Integer
    **/
    Integer batchAddVideoTags(List<VideoTag> videoTagList);

    /**
    * @description: 获取分页查询总数
    * @author: ChenLipeng
    * @date: 2022/7/17 17:29
    * @param: params
    * @return: java.lang.Integer
    **/
    Integer pageCountVideos(Map<String, Object> params);

    /**
    * @description: 查询分页列表
    * @author: ChenLipeng
    * @date: 2022/7/17 17:35
    * @param: params
    * @return: java.util.List<com.tiangong.domain.Video>
    **/
    List<Video> pageListVideos(Map<String, Object> params);

    /**
    * @description: 通过视频id从数据库中查找视频
    * @author: ChenLipeng
    * @date: 2022/7/17 21:10
    * @param: videoId
    * @return: com.tiangong.domain.Video
    **/
    Video getVideoById(Long videoId);

    /**
    * @description: 根据视频id和用户id查找对应点赞记录
    * @author: ChenLipeng
    * @date: 2022/7/17 21:16
    * @param: userId
    * @param: videoId
    * @return: com.tiangong.domain.VideoLike
    **/
    VideoLike getVideoLikeByVideoIdAndUserId(@Param("userId") Long userId,@Param("videoId") Long videoId);

    /**
    * @description: 添加点赞信息
    * @author: ChenLipeng
    * @date: 2022/7/17 21:22
    * @param: videoLike
    **/
    Integer addVideoLike(VideoLike videoLike);

    /**
    * @description: 删除点赞方法
    * @author: ChenLipeng
    * @date: 2022/7/17 21:34
    * @param: videoId
    * @param: userId
    **/
    Integer deleteVideoLike(@Param("videoId") Long videoId,@Param("userId") Long userId);

    /**
    * @description: 查询视频被点赞的数量
    * @author: ChenLipeng
    * @date: 2022/7/17 21:44
    * @param: videoId
    * @return: java.lang.Long
    **/
    Long getVideoLikes(Long videoId);

    /**
    * @description: 从收藏表中删除视频收藏
    * @author: ChenLipeng
    * @date: 2022/7/18 9:47
    * @param: videoId
    * @param: userId
    **/
    Integer deleteVideoCollection(@Param("videoId") Long videoId, @Param("userId") Long userId);

    /**
    * @description: 添加视频收藏
    * @author: ChenLipeng
    * @date: 2022/7/18 9:50
    * @param: videoCollection
    **/
    Integer addVideoCollection(VideoCollection videoCollection);

    /**
    * @description: 查询视频总收藏数量
    * @author: ChenLipeng
    * @date: 2022/7/18 10:06
    * @param: videoId
    * @return: java.lang.Long
    **/
    Long getVideoCollections(Long videoId);

    /**
    * @description: 通过视频id和用户id查询该视频是否被该用户点赞
    * @author: ChenLipeng
    * @date: 2022/7/18 10:10
    * @param: videoId
    * @param: userId
    * @return: com.tiangong.domain.VideoCollection
    **/
    VideoCollection getVideoCollectionByVideoIdAndUserId(@Param("videoId") Long videoId,@Param("userId") Long userId);

    /**
    * @description: 获取当前用户对当前视频的投币信息
    * @author: ChenLipeng
    * @date: 2022/7/18 11:11
    * @param: videoId
    * @param: userId
    * @return: com.tiangong.domain.VideoCoin
    **/
    VideoCoin getVideoCoinByVideoIdAndUserId(@Param("videoId") Long videoId,@Param("userId") Long userId);

    /**
    * @description: 给当前视频添加投币信息
    * @author: ChenLipeng
    * @date: 2022/7/18 11:15
    * @param: videoCoin
    * @return: java.lang.Integer
    **/
    Integer addVideoCoin(VideoCoin videoCoin);

    /**
    * @description: 更新视频投币表
    * @author: ChenLipeng
    * @date: 2022/7/18 11:34
    * @param: videoCoin
    * @return: java.lang.Integer
    **/
    Integer updateVideoCoin(VideoCoin videoCoin);

    /**
    * @description: 查询总投币数
    * @author: ChenLipeng
    * @date: 2022/7/18 12:01
    * @param: videoId
    * @param: userId
    * @return: java.lang.Long
    **/
    Long getVideoCoins(@Param("videoId") Long videoId, @Param("userId") Long userId);

    /**
    * @description: 添加一条视频评论
    * @author: ChenLipeng
    * @date: 2022/7/18 12:49
    * @param: videoComment
    **/
    Integer addVideoComment(VideoComment videoComment);

    /**
    * @description: 查询视频的评论数量
    * @author: ChenLipeng
    * @date: 2022/7/18 13:18
    * @param: params
    * @return: java.lang.Integer
    **/
    Integer pageCountVideoComments(Map<String, Object> params);

    /**
    * @description: 分页查询评论
    * @author: ChenLipeng
    * @date: 2022/7/18 13:24
    * @param: params
    * @return: java.util.List<com.tiangong.domain.VideoComment>
    **/
    List<VideoComment> pageListVideoComments(Map<String, Object> params);

    /**
    * @description: 批量查询二级评论
    * @author: ChenLipeng
    * @date: 2022/7/18 13:30
    * @param: parentIdList
    * @return: java.util.List<com.tiangong.domain.VideoComment>
    **/
    List<VideoComment> batchGetVideoCommentsByRootId(List<Long> parentIdList);

    /**
    * @description: 查询视频详情
    * @author: ChenLipeng
    * @date: 2022/7/18 15:01
    * @param: videoId
    * @return: com.tiangong.domain.Video
    **/
    Video getVideoDetails(Long videoId);

    /**
    * @description: 根据特定参数获取当天观看记录
    * @author: ChenLipeng
    * @date: 2022/7/22 10:03
    * @param: params
    * @return: com.tiangong.domain.VideoView
    **/
    VideoView getVideoView(Map<String, Object> params);

    /**
    * @description: 添加一条观看及记录
    * @author: ChenLipeng
    * @date: 2022/7/22 10:10
    * @param: videoView
    * @return: java.lang.Integer
    **/
    Integer addVideoView(VideoView videoView);

    /**
    * @description: 查询视频播放量
    * @author: ChenLipeng
    * @date: 2022/7/22 10:15
    * @param: videoId
    * @return: java.lang.Integer
    **/
    Integer getVideoViewCounts(Long videoId);

    /**
    * @description: 获取用户的偏好
    * @author: ChenLipeng
    * @date: 2022/7/22 15:11
    * @param: userId
    * @return: java.util.List<com.tiangong.domain.UserPreference>
    **/
    List<UserPreference> getUserPreference(Long userId);

    /**
    * @description: 批量查询视频
    * @author: ChenLipeng
    * @date: 2022/7/22 15:28
    * @param: itemIds
    * @return: java.util.List<com.tiangong.domain.Video>
    **/
    List<Video> batchGetVideosByIds(@Param("idList") List<Long> itemIds);

    /**
    * @description: 批量添加视频剪影图
    * @author: ChenLipeng
    * @date: 2022/8/5 15:31
    * @param: pictures
    **/
    void batchAddVideoBinaryPictures(@Param("pictureList") List<VideoBinaryPicture> pictures);
}
