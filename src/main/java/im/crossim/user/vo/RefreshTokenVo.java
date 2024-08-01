package im.crossim.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@ApiModel("刷新Token操作结果")
@Data
@Builder
public class RefreshTokenVo {

    @ApiModelProperty("登录Token")
    private String token;

    @ApiModelProperty("刷新Token")
    private String refreshToken;

}
