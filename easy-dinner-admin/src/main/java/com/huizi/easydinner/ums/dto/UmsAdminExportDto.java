package com.huizi.easydinner.ums.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @PROJECT_NAME: easy-dinner
 * @DESCRIPTION:导出对象
 * @AUTHOR: 12615
 * @DATE: 2023/9/6 10:10
 */
@Data
public class UmsAdminExportDto {

    @Excel(name = "id", needMerge = true, width = 20)
    @ApiModelProperty(name = "id")
    private Long id;

    @Excel(name = "用户名", needMerge = true, width = 20)
    @ApiModelProperty(name = "用户名")
    private String username;

    @ApiModelProperty(name = "密码")
    private String password;
    @ApiModelProperty(name = "头像")
    private String icon;

    @Excel(name = "邮箱", needMerge = true, width = 20)
    @ApiModelProperty(name = "邮箱")
    private String email;

    @Excel(name = "昵称", needMerge = true, width = 20)
    @ApiModelProperty(name = "昵称")
    private String nickName;

    @Excel(name = "备注信息", needMerge = true, width = 20)
    @ApiModelProperty(name = "备注信息")
    private String note;

    @Excel(name = "创建时间", needMerge = true, width = 20, exportFormat = "yyyyMMdd")
    @ApiModelProperty(name = "创建时间")
    private Date createTime;

    @Excel(name = "最后登录时间", needMerge = true, width = 20, exportFormat = "yyyyMMdd")
    @ApiModelProperty(name = "最后登录时间")
    private Date loginTime;

    @Excel(name = "帐号启用状态", replace = {"启用_1", "禁用_0"}, needMerge = true, width = 20)
    @ApiModelProperty(name = "帐号启用状态：0->禁用；1->启用")
    private Integer status;

    @ExcelCollection(name = "子级")
    @ApiModelProperty(name = "子级")
    private List<UmsAdminChildrenExportDto> children;
}
