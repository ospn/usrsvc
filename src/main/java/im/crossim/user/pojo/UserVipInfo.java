package im.crossim.user.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVipInfo {

    private Integer maxGroupCount;
    private Integer maxGroupUserCount;

}
