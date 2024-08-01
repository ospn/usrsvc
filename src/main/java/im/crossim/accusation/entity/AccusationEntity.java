package im.crossim.accusation.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import im.crossim.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@ApiModel("投诉实体类")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_accusation", autoResultMap = true)
public class AccusationEntity extends BaseEntity {

    @ApiModelProperty("源OSN ID")
    private String sourceOsnId;

    @ApiModelProperty("目标OSN ID")
    private String targetOsnId;

    @ApiModelProperty("投诉内容")
    private String content;

}
