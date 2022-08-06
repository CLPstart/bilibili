package com.tiangong.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-17  13:59
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoTag {

    private Long id;

    private Long videoId;

    private Long tagId;

    private Date createTime;

}
