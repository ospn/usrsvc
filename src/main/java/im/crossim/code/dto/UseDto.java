package im.crossim.code.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@ApiModel("使用激活码操作参数")
@Data
public class UseDto {

    @ApiModelProperty("激活码")
    @NotBlank(message = "参数code不能为空")
    private String code;

    @ApiModelProperty("类型，如果不传则不限制激活码的适用类型，1000=升级激活码；1100=降级激活码")
    private Integer type;

    @ApiModelProperty("参数")
    private JSONObject param;

}
