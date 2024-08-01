package im.crossim.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@ApiModel("确认用户是否已存在操作结果")
@Data
@Builder
public class HasUserVo {

    @ApiModelProperty("用户是否已存在")
    private Boolean has;

}
