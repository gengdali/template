package com.huizi.easydinner.ums.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @PROJECT_NAME: pallet_truck
 * @DESCRIPTION:车架号
 * @AUTHOR: 12615
 * @DATE: 2023/9/4 8:57
 */
@Data
public class UmsAdminChildrenExportDto {
    
    @ApiModelProperty(value = "编号")
    private String number;
}
