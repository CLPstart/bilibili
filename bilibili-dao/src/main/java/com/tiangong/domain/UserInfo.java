package com.tiangong.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-13  13:40
 * @Description: t_user_info表的实体类
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "user-infos")
public class UserInfo {

    @Id
    private Long id;

    private Long userId;

    @Field(type = FieldType.Text)
    private String nick;

    private String avatar;

    private String sign;

    private String gender;

    private String birth;

    @Field(type = FieldType.Date)
    private Date createTime;

    @Field(type = FieldType.Date)
    private Date updateTime;

    private Boolean followed;

}
