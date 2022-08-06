package com.tiangong.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-18  10:44
 * @Description: t_user_coin表的映射
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoin {

    private Long id;

    private Long userId;

    private Integer amount;

    private Date createTime;

    private Date updateTime;

}
