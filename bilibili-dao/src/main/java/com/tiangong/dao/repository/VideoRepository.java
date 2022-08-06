package com.tiangong.dao.repository;

import com.tiangong.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface VideoRepository extends ElasticsearchRepository<Video, Long> {


    /**
     * @return
     * @description: find by title like, 根据Video中的字段title进行like模糊查询
     * @author: ChenLipeng
     * @date: 2022/7/21 10:17
     * @param: keyWord
     */
    Video findByTitleLike(String keyWord);
}
