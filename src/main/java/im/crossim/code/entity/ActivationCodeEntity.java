package im.crossim.code.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import im.crossim.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@ApiModel("激活码实体类")
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "t_activation_code", autoResultMap = true)
public class ActivationCodeEntity extends BaseEntity {

    @ApiModelProperty("批次ID")
    private String batchId;

    @ApiModelProperty("批次号")
    private String batchCode;

    @ApiModelProperty("激活码")
    private String code;

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("有效时间开始")
    private Date effectiveTimeStart;

    @ApiModelProperty("有效时间结束")
    private Date effectiveTimeEnd;

    @ApiModelProperty("是否已被使用")
    private Boolean used;

    @ApiModelProperty("被使用的用户ID")
    private Integer usedBy;

    @ApiModelProperty("被使用的时间")
    private Date usedTime;

    @ApiModelProperty("是否可用")
    private Boolean enabled;

    @ApiModelProperty("是否删除")
    private Boolean deleted;

}
