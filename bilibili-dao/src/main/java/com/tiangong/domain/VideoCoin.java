package com.tiangong.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-18  10:46
 * @Description: t_video_coin表的实体类映射
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoCoin {

    private Long id;

    private Long userId;

    private Long videoId;

    private Integer amount;

    private Date createTime;

    private Date updateTime;

}
