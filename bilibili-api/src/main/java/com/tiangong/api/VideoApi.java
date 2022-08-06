package com.tiangong.api;

import com.tiangong.api.support.UserSupport;
import com.tiangong.domain.*;
import com.tiangong.service.ElasticSearchService;
import com.tiangong.service.VideoService;
import org.apache.mahout.cf.taste.common.TasteException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.api
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-17  13:49
 * @Description: 视频表述层
 * @Version: 1.0
 */
@RestController
public class VideoApi {

    @Autowired
    private VideoService videoService;

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private ElasticSearchService elasticSearchService;

    /**
    * @description: 添加视频接口
    * @author: ChenLipeng
    * @date: 2022/7/17 17:11
    * @param: video
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/videos")
    public JsonResponse<String> addVideos(@RequestBody Video video){
        //获取当前用户id
        Long userId = userSupport.getCurrentUserId();
        //在视频中写入当前用户id
        video.setUserId(userId);
        //调用业务层方法添加视频
        videoService.addVideos(video);
        //在ElasticSearch中添加数据
        elasticSearchService.addVideo(video);
        return JsonResponse.success();
    }

    /**
    * @description: 分页查询视频
    * @author: ChenLipeng
    * @date: 2022/7/17 18:04
    * @param: size
    * @param: no
    * @param: area
    * @return: com.tiangong.domain.JsonResponse<com.tiangong.domain.PageResult<com.tiangong.domain.Video>>
    **/
    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer size, Integer no, String area){
        //根据前端传来的参数调用业务层方法进行分页查询
        PageResult<Video> result = videoService.pageListVideos(size, no, area);
        return new JsonResponse<>(result);
    }

    /**
    * @description: 在线观看视频表述层方法
    * @author: ChenLipeng
    * @date: 2022/7/17 19:27
    * @param: request
    * @param: response
    * @param: url
    **/
    @GetMapping("/video-slices")
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String url) throws Exception {
        //调用业务层方法
        videoService.viewVideoOnlineBySlices(request, response, url);
    }

    /**
    * @description: 点赞表述层
    * @author: ChenLipeng
    * @date: 2022/7/17 21:25
    * @param: videoId
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/video-likes")
    public JsonResponse<String> addVideoLike(@RequestParam Long videoId){
        //获取当前用户id
        Long userId = userSupport.getCurrentUserId();
        //调用业务层方法点赞
        videoService.addVideoLike(userId, videoId);
        return JsonResponse.success();
    }

    /**
    * @description: 取消点赞的表述层方法
    * @author: ChenLipeng
    * @date: 2022/7/17 21:37
    * @param: videoId
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @DeleteMapping("/video-likes")
    public JsonResponse<String> deleteVideoLike(@RequestParam Long videoId){
        //获取当前用户id
        Long userId = userSupport.getCurrentUserId();;
        //调用业务层方法删除点赞信息
        videoService.deleteVideoLike(videoId, userId);
        return JsonResponse.success();
    }

    /**
    * @description: 查询视频点赞数量表述层
    * @author: ChenLipeng
    * @date: 2022/7/17 21:48
    * @param: videoId
    * @return: com.tiangong.domain.JsonResponse<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    @GetMapping("/video-likes")
    public JsonResponse<Map<String, Object>> getVideoLikes(@RequestParam Long videoId){
        //因为不需要登陆也可以查看点赞，因此置用户id为空，同时因为查询视频是否被用户点赞需要用户id，因此把赋值操作放在try中
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored){}
        //调用业务层方法返回结果
        Map<String, Object> result = videoService.getVideoLikes(videoId, userId);
        return new JsonResponse<>(result);
    }

    /**
    * @description: 添加收藏表述层
    * @author: ChenLipeng
    * @date: 2022/7/18 9:56
    * @param: videoCollection
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/video-collections")
    public JsonResponse<String> addVideoCollection(@RequestBody VideoCollection videoCollection){
        //获得当前用户id
        Long userId = userSupport.getCurrentUserId();
        //添加用户收藏
        videoService.addVideoCollection(videoCollection, userId);
        return JsonResponse.success();
    }

    /**
    * @description: 取消收藏表述层
    * @author: ChenLipeng
    * @date: 2022/7/18 10:00
    * @param: videoId
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @DeleteMapping("/video-collections")
    public JsonResponse<String> deleteVideoCollection(@RequestParam Long videoId){
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoCollection(videoId, userId);
        return JsonResponse.success();
    }

    /**
    * @description: 查询视频收藏量表述层方法
    * @author: ChenLipeng
    * @date: 2022/7/18 10:04
    * @param: videoId
    * @return: com.tiangong.domain.JsonResponse<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    @GetMapping("/video-collections")
    public JsonResponse<Map<String, Object>> getVideoCollections(@RequestParam Long videoId){
        //获取当前用户id，由于允许游客查询所以id可以为空值
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored){}
        //调用业务层方法得到结果集
        Map<String, Object> result = videoService.getVideoCollections(videoId, userId);
        return new JsonResponse<>(result);
    }

    /**
    * @description: 投币的表述层接口
    * @author: ChenLipeng
    * @date: 2022/7/18 11:46
    * @param: videoCoin
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/video-coins")
    public JsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin){
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCoins(videoCoin, userId);
        return JsonResponse.success();
    }

    /**
    * @description: 获取硬币总数
    * @author: ChenLipeng
    * @date: 2022/7/18 12:06
    * @param: videoId
    * @return: com.tiangong.domain.JsonResponse<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    @GetMapping("/video-coins")
    public JsonResponse<Map<String, Object>> getVideoCoins(@RequestParam Long videoId){
        //获取当前用户id，由于允许游客查询所以id可以为空值
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        }catch (Exception ignored){}
        //调用业务层返回结果集
        Map<String, Object> result = videoService.getVideoCoins(videoId, userId);
        return new JsonResponse<>(result);
    }

    /**
    * @description: 发表视频评论
    * @author: ChenLipeng
    * @date: 2022/7/18 13:08
    * @param: videoComment
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/video-comments")
    public JsonResponse<String> addVideoComment(@RequestBody VideoComment videoComment){
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoComment(videoComment, userId);
        return JsonResponse.success();
    }

    /**
    * @description: 分页查询视频评论
    * @author: ChenLipeng
    * @date: 2022/7/18 14:00
    * @param: size
    * @param: no
    * @param: videoId
    * @return: com.tiangong.domain.JsonResponse<com.tiangong.domain.PageResult<com.tiangong.domain.VideoComment>>
    **/
    @GetMapping("/video-comments")
    public JsonResponse<PageResult<VideoComment>> pageListVideoComments(@RequestParam Integer size,
                                                                        @RequestParam Integer no,
                                                                        @RequestParam Long videoId){
        //调用业务层查询视频评论
        PageResult<VideoComment> result = videoService.pageListVideoComments(size, no, videoId);
        return new JsonResponse<>(result);
    }

    /**
    * @description: 视频详情表述层
    * @author: ChenLipeng
    * @date: 2022/7/18 14:56
    * @param: videoId
    * @return: com.tiangong.domain.JsonResponse<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    @GetMapping("video-details")
    public JsonResponse<Map<String, Object>> getVideoDetails(@RequestParam Long videoId){
        Map<String, Object> result = videoService.getVideoDetails(videoId);
        return new JsonResponse<>(result);
    }

    /**
    * @description: 添加视频观看记录表述层方法
    * @author: ChenLipeng
    * @date: 2022/7/22 10:17
    * @param: videoView
    * @param: request
    * @return: com.tiangong.domain.JsonResponse<java.lang.String>
    **/
    @PostMapping("/video-views")
    public JsonResponse<String> addVideoView(@RequestBody VideoView videoView,
                                             HttpServletRequest request){
        Long userId;
        try {
            //如果是用户登录那么就获取用户id
            userId = userSupport.getCurrentUserId();
            videoView.setUserId(userId);
            videoService.addVideoView(videoView, request);
        }catch (Exception e){
            //如果是游客登陆就是直接调用业务层方法
            videoService.addVideoView(videoView, request);
        }
        return JsonResponse.success();
    }

    /**
    * @description: 查看播放量
    * @author: ChenLipeng
    * @date: 2022/7/22 10:19
    * @param: videoId
    * @return: com.tiangong.domain.JsonResponse<java.lang.Integer>
    **/
    @GetMapping("/video-view-counts")
    public JsonResponse<Integer> getVideoViewCounts(@RequestParam Long videoId){
        //直接调用业务层方法查看播放量
        Integer count = videoService.getVideoViewCounts(videoId);
        return new JsonResponse<>(count);
    }

    /**
    * @description: 获取推荐
    * @author: ChenLipeng
    * @date: 2022/7/22 16:56
    * @return: com.tiangong.domain.JsonResponse<java.util.List<com.tiangong.domain.Video>>
    **/
    @GetMapping("/recommendations")
    public JsonResponse<List<Video>> recommend() throws TasteException {
        Long userId = userSupport.getCurrentUserId();
        List<Video> list = videoService.recommend(userId);
        return new JsonResponse<>(list);
    }

    /**
    * @description: 视频剪影
    * @author: ChenLipeng
    * @date: 2022/8/5 15:52
    * @param: videoId
    * @param: fileMd5
    * @return: com.tiangong.domain.JsonResponse<java.util.List<com.tiangong.domain.VideoBinaryPicture>>
    **/
    @GetMapping("/video-frames")
    public JsonResponse<List<VideoBinaryPicture>> captureVideoFrame(@RequestParam Long videoId,
                                                                    @RequestParam String fileMd5) throws Exception {
        List<VideoBinaryPicture> list = videoService.convertVideoToImage(videoId, fileMd5);
        return new JsonResponse<>(list);
    }

}
