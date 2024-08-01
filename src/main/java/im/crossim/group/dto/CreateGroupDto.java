package im.crossim.group.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@ApiModel("创建群组操作参数")
@Data
public class CreateGroupDto {

    @ApiModelProperty("名称")
    @NotBlank(message = "param name cannot be blank")
    private String name;

    @ApiModelProperty("头像")
    private String portrait;

    @ApiModelProperty("用户OSN ID列表")
    private List<String> userList;

}
