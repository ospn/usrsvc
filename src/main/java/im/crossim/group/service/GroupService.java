package im.crossim.group.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.crossim.common.enums.ResultCodeEnum;
import im.crossim.common.exception.BusinessException;
import im.crossim.common.exception.SystemException;
import im.crossim.common.utils.UserUtil;
import im.crossim.common.vo.ApiResult;
import im.crossim.common.vo.EmptyVo;
import im.crossim.common.vo.MyPage;
import im.crossim.config.web.UserServiceConfig;
import im.crossim.group.dto.CreateGroupDto;
import im.crossim.group.entity.GroupEntity;
import im.crossim.group.mapper.GroupMapper;
import im.crossim.group.vo.CreateGroupVo;
import im.crossim.group.vo.GroupVo;
import im.crossim.group.vo.ListGroupVo;
import im.crossim.im.service.ImService;
import im.crossim.user.entity.UserEntity;
import im.crossim.user.pojo.UserVipInfo;
import im.crossim.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class GroupService extends ServiceImpl<GroupMapper, GroupEntity> implements IService<GroupEntity> {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private ImService imService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserServiceConfig userServiceConfig;

    public void updateMaxMemberCount(int id, int maxMemberCount) {
        LambdaUpdateWrapper<GroupEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(GroupEntity::getMaxMemberCount, maxMemberCount)
                .eq(GroupEntity::getId, id);
        this.update(updateWrapper);
    }

    public GroupEntity getByGroupOsnId(String groupOsnId) {
        if (StringUtils.isEmpty(groupOsnId)) {
            return null;
        }

        LambdaQueryWrapper<GroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupEntity::getGroupOsnId, groupOsnId);

        return this.getOne(queryWrapper);
    }

    private long getUserGroupCount(int userId) {
        LambdaQueryWrapper<GroupEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GroupEntity::getUserId, userId)
                .eq(GroupEntity::getDeleted, false);

        return this.count(queryWrapper);
    }

    public JSONObject setGroupMax(String groupId, int max) {
        return imService.setGroupMax(groupId, max);
    }

    public ApiResult<CreateGroupVo> createGroup(CreateGroupDto dto) {
        String name = StringUtils.trim(dto.getName());
        String portrait = StringUtils.trim(dto.getPortrait());
        List<String> userList = dto.getUserList();

        if (name == null) {
            name = "Group";
        }
        if (portrait == null) {
            portrait = "";
        }
        if (userList == null) {
            userList = new ArrayList<>();
        }

        // 获取当前用户。
        UserEntity currentUser = UserUtil.getCurrentUser();

        // 获取当前用户的VIP信息。
        UserVipInfo userVipInfo = userService.getUserVipInfo(currentUser);

        // 检查用户群组数量是否超过上限。
        long userGroupCount = getUserGroupCount(currentUser.getId());
        if (userGroupCount >= userVipInfo.getMaxGroupCount()) {
            throw new BusinessException(ResultCodeEnum.MAX_VOLUME_EXCEEDED);
        }

        // 请求IM节点创建群。
        JSONObject groupInfo;
        String groupOsnId;
        if (userServiceConfig.getImNodeEnabled()) {
            groupInfo = imService.createGroup(
                    name,
                    currentUser.getOsnId(),
                    portrait,
                    userList,
                    userVipInfo.getMaxGroupUserCount()
            );
            if (groupInfo == null) {
                throw new BusinessException(ResultCodeEnum.IM_NODE_FAILED);
            }

            groupOsnId = groupInfo.getString("groupID");
            if (StringUtils.isEmpty(groupOsnId)) {
                throw new BusinessException(ResultCodeEnum.IM_NODE_FAILED);
            }
        } else {
            groupInfo = new JSONObject();
            groupOsnId = "";
        }

        // 将创建的群的数据插入到群内。
        GroupEntity groupEntity = new GroupEntity();
        groupEntity.setUserId(currentUser.getId());
        groupEntity.setName(name);
        groupEntity.setPortrait(portrait);
        groupEntity.setUserList(userList);
        groupEntity.setGroupOsnId(groupOsnId);
        groupEntity.setGroupInfo(groupInfo);
        groupEntity.setMaxMemberCount(userVipInfo.getMaxGroupUserCount());
        groupEntity.setDeleted(false);
        if (!this.save(groupEntity)) {
            throw new SystemException("failed to save group info to database");
        }

        return ApiResult.success(
                CreateGroupVo.builder()
                        .groupInfo(groupInfo)
                        .build()
        );
    }

    public ApiResult<EmptyVo> deleteGroup(String osnId) {
        UserEntity currentUser = UserUtil.getCurrentUser();

        UpdateWrapper<GroupEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(GroupEntity::getDeleted, true)
                .eq(GroupEntity::getUserId, currentUser.getId())
                .eq(GroupEntity::getGroupOsnId, osnId)
                .eq(GroupEntity::getDeleted, false);

        if (!this.update(updateWrapper)) {
            throw new BusinessException(ResultCodeEnum.NO_PRIVILEGE);
        }

        return ApiResult.success();
    }

    public ApiResult<ListGroupVo> listGroup(int current, int size) {
        UserEntity currentUser = UserUtil.getCurrentUser();

        QueryWrapper<GroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(GroupEntity::getUserId, currentUser.getId())
                .eq(GroupEntity::getDeleted, false);
        IPage<GroupEntity> page = this.page(
                new Page<>(current, size),
                queryWrapper
        );

        MyPage<GroupVo> myPage = MyPage.create(
                page,
                (record) -> GroupVo.builder()
                        .groupOsnId(record.getGroupOsnId())
                        .build()
        );

        return ApiResult.success(
                ListGroupVo.builder()
                        .page(myPage)
                        .build()
        );
    }

}
