package com.tiangong.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-22  10:52
 * @Description: t_video_operation表的实体类映射
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoOperation {

    private Long id;

    private Long userId;

    private Long videoId;

    private String operationType;

    private Date createdTime;

}
