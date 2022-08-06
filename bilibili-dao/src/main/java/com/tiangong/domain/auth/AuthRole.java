package com.tiangong.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain.auth
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  16:52
 * @Description: t_auth_role表的实体类映射
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRole {

    private Long id;

    private String name;

    private String code;

    private Date createTime;

    private Date updateTime;

}
