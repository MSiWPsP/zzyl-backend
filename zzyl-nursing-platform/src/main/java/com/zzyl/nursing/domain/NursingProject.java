package com.zzyl.nursing.domain;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.zzyl.common.annotation.Excel;
import com.zzyl.common.core.domain.BaseEntity;

/**
 * 护理项目对象 nursing_project
 *
 * @author xucheng
 * @date 2026-08-17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "护理项目实体")
public class NursingProject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    @ApiModelProperty(value = "护理项目编号", example = "1")
    private Long id;

    /** 名称 */
    @ApiModelProperty(value = "护理项目名称", example = "日常护理")
    @Excel(name = "名称")
    private String name;

    /** 排序号 */
    @ApiModelProperty(value = "排序号", example = "1")
    @Excel(name = "排序号")
    private Integer orderNo;

    /** 单位 */
    @ApiModelProperty(value = "计价单位", example = "次")
    @Excel(name = "单位")
    private String unit;

    /** 价格 */
    @ApiModelProperty(value = "护理项目价格", example = "50.00")
    @Excel(name = "价格")
    private BigDecimal price;

    /** 图片 */
    @ApiModelProperty(value = "护理项目图片地址", example = "https://xxx.com/image.jpg")
    @Excel(name = "图片")
    private String image;

    /** 护理要求 */
    @ApiModelProperty(value = "护理要求", example = "每日护理一次")
    @Excel(name = "护理要求")
    private String nursingRequirement;

    /** 状态（0：禁用，1：启用） */
    @ApiModelProperty(value = "状态：0-禁用，1-启用", example = "1")
    @Excel(name = "状态", readConverterExp = "0=：禁用，1：启用")
    private Integer status;

}