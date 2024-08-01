package im.crossim.user.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("刷新Token操作参数")
@Data
public class RefreshTokenDto {

    @ApiModelProperty("刷新Token")
    @NotBlank(message = "param refreshToken cannot be blank")
    private String refreshToken;

}
