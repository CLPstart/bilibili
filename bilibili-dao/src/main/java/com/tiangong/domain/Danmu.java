package com.tiangong.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-19  09:23
 * @Description: t_danmu表的实体类映射
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Danmu {

    private Long id;

    private Long userId;

    private Long videoId;

    private String content;

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public String getContent() {
        return content;
    }

    public String getDanmuTime() {
        return danmuTime;
    }

    public Date getCerateTime() {
        return cerateTime;
    }

    private String danmuTime;

    private Date cerateTime;

}
