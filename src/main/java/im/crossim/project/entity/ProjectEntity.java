package im.crossim.project.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import im.crossim.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("项目类")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_project", autoResultMap = true)
public class ProjectEntity extends BaseEntity {

    @ApiModelProperty("项目")
    private String project;

    @ApiModelProperty("主DAPP的DAPP INFO（BASE64编码）")
    private String mainDapp;

    @ApiModelProperty("是否可用")
    private Boolean enabled;

}
