package com.tiangong.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-17  13:57
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "videos")
public class Video {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long userId;//用户id

    private String url; //视频链接

    private String thumbnail;//封面

    @Field(type = FieldType.Text)
    private String title; //标题

    private String type;// 0自制 1转载

    private String duration;//时长

    private String area;//分区

    private List<VideoTag> videoTagList;//标签列表

    @Field(type = FieldType.Text)
    private String description;//简介

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Date)
    private Date updateTime;

}
