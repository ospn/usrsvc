package im.crossim.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("注册操作结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVo {

    @ApiModelProperty("登录信息")
    private LoginVo loginInfo;

}
