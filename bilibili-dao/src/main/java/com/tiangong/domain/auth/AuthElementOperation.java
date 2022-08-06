package com.tiangong.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain.auth
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  16:44
 * @Description: t_auth_element_operation表的实体类映射
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthElementOperation {

    private Long id;

    private String elementName;

    private String elementCode;

    private String operationType;

    private Date createTime;

    private Date updateTime;
}
