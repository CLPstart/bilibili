package com.tiangong.service;

import com.tiangong.dao.repository.UserInfoRepository;
import com.tiangong.dao.repository.VideoRepository;
import com.tiangong.domain.UserInfo;
import com.tiangong.domain.Video;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.service
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-21  09:51
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class ElasticSearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
    * @description: 向ES中添加一条视频文档
    * @author: ChenLipeng 
    * @date: 2022/7/21 12:43 
    * @param: video
    **/
    public void addVideo(Video video){
        videoRepository.save(video);
    }

    /**
    * @description: 按照关键词查找对应视频
    * @author: ChenLipeng
    * @date: 2022/7/21 12:44
    * @param: keyWord
    * @return: com.tiangong.domain.Video
    **/
    public Video getVideos(String keyWord){
        return videoRepository.findByTitleLike(keyWord);
    }

    /**
    * @description: 向ES中添加一条用户详细信息
    * @author: ChenLipeng
    * @date: 2022/7/21 12:44
    * @param: userInfo
    **/
    public void addUserInfo(UserInfo userInfo){
        userInfoRepository.save(userInfo);
    }

    /**
    * @description: 从ES中删除所有记录
    * @author: ChenLipeng
    * @date: 2022/7/21 12:44
    * @param: video
    **/
    public void deleteVideos(){
        videoRepository.deleteAll();
    }

    /**
    * @description: 多关键词分页查询
    * @author: ChenLipeng
    * @date: 2022/7/21 12:45
    * @param: keyword
    * @param: pageNo
    * @param: pageSize
    * @return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
    **/
    public List<Map<String,Object>> getContents(String keyword,
                                                Integer pageNo,
                                                Integer pageSize) throws IOException {
        //设置ES中的索引
        String[] indices = {"videos", "user-infos"};
        //实例化查询请求
        SearchRequest searchRequest = new SearchRequest(indices);
        //实例化搜索源数据
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //设置分页参数
        sourceBuilder.from(pageNo - 1);
        sourceBuilder.size(pageSize);
        //实例化查询匹配字
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "nick", "description");
        //设置查询匹配字段
        sourceBuilder.query(matchQueryBuilder);
        //设置搜搜索数据源
        searchRequest.source(sourceBuilder);
        //设置超时时间
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //高亮显示
        String[] array = {"title", "nick", "description"};
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String key : array) {
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
        //如果要多个字段高亮要置为false
        highlightBuilder.requireFieldMatch(false);
        //设置前端显示样式
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //构件结果集
        List<Map<String, Object>> results = new ArrayList<>();
        for(SearchHit hit : searchResponse.getHits()) {
            //处理高亮字段
            Map<String, HighlightField> highlightBuilderFields = hit.getHighlightFields();
            Map<String, Object> sourchMap = hit.getSourceAsMap();
            for (String key : array){
                HighlightField field = highlightBuilderFields.get(key);
                if(field != null){
                    Text[] fragments = field.fragments();
                    String str = Arrays.toString(fragments);
                    str = str.substring(1, str.length() -1);
                    sourchMap.put(key, str);
                }
            }
            results.add(sourchMap);
        }
        return results;
    }

}
