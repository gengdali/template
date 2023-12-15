package com.huizi.easydinner.ums.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @PROJECT_NAME: easy-dinner
 * @DESCRIPTION:账号VO类
 * @AUTHOR: 12615
 * @DATE: 2023/8/11 16:24
 */
@Data
public class UmsAdminVo {
    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "头像")
    private String icon;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "备注信息")
    private String note;

    @ApiModelProperty(value = "帐号启用状态：0->禁用；1->启用")
    private Integer status;
}
