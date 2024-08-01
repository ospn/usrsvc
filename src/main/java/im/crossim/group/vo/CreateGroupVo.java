package im.crossim.group.vo;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@ApiModel("创建群组操作结果")
@Data
@Builder
public class CreateGroupVo {

    @ApiModelProperty("创建的群组信息，由IM返回")
    private JSONObject groupInfo;

}
