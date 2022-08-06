package com.tiangong.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @BelongsProject: bilibili
 * @BelongsPackage: com.tiangong.domain
 * @Author: ChenLipeng
 * @CreateTime: 2022-06-18  09:50
 * @Description: TODO
 * @Version: 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {

    private Integer total;

    private List<T> list;

}
