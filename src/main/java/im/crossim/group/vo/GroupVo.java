package im.crossim.group.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("群组信息")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupVo {

    @ApiModelProperty("群组OSN ID")
    private String groupOsnId;

}
