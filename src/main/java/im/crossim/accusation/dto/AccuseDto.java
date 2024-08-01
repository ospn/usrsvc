package im.crossim.accusation.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@ApiModel("举报操作参数")
@Data
public class AccuseDto {

    @ApiModelProperty("举报目标OSN ID")
    @NotNull(message = "param targetOsnId cannot be null")
    private String targetOsnId;

    @ApiModelProperty("举报内容")
    @NotNull(message = "param content cannot be null")
    private String content;

}
