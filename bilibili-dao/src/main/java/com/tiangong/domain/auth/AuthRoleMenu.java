package com.tiangong.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain.auth
 * @Author: ChenLipeng
 * @CreateTime: 2022-07-12  16:53
 * @Description: t_auth_role_menu表的实体类映射
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRoleMenu {

    private Long id;

    private Long roleId;

    private Long menuId;

    private Date createTime;

    private AuthMenu authMenu;

}
