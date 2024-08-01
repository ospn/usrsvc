package im.crossim.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import im.crossim.config.web.UserServiceConfig;
import im.crossim.project.entity.ProjectEntity;
import im.crossim.project.mapper.ProjectMapper;
import im.crossim.project.pojo.Project;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class ProjectService extends ServiceImpl<ProjectMapper, ProjectEntity> implements IService<ProjectEntity> {

    @Autowired
    private UserServiceConfig userServiceConfig;

    private static final String DEFAULT_PROJECT = "DEFAULT";

    public ProjectEntity getByProject(String project) {
        if (StringUtils.isEmpty(project)) {
            return null;
        }
        LambdaQueryWrapper<ProjectEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProjectEntity::getProject, project)
                .eq(ProjectEntity::getEnabled, true);
        return this.getOne(queryWrapper);
    }

    private Project buildFromConfig() {
        return Project.builder()
                .mainDapp(userServiceConfig.getLoginConfig().getMainDapp())
                .build();
    }

    private Project getDefault() {
        ProjectEntity projectEntity = getByProject(DEFAULT_PROJECT);
        if (projectEntity != null) {
            Project p = new Project();
            BeanUtils.copyProperties(projectEntity, p);
            return p;
        } else {
            return buildFromConfig();
        }
    }

    public Project get(String project) {
        ProjectEntity projectEntity = getByProject(project);
        if (projectEntity != null) {
            Project p = new Project();
            BeanUtils.copyProperties(projectEntity, p);
            return p;
        } else {
            return getDefault();
        }
    }

}
