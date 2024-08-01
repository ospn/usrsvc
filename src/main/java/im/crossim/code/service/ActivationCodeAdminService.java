package im.crossim.code.service;

import im.crossim.code.dto.admin.GenerateDto;
import im.crossim.code.entity.ActivationCodeEntity;
import im.crossim.code.enums.ActivationCodeModeEnum;
import im.crossim.code.vo.admin.GenerateVo;
import im.crossim.common.exception.SystemException;
import im.crossim.common.vo.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Slf4j
@Service
public class ActivationCodeAdminService {

    @Autowired
    private ActivationCodeService activationCodeService;

    private String generateCodeByAlphabet(int size, String prefix, String alphabet) {
        StringBuilder code = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < size; i++) {
            int index = random.nextInt(alphabet.length());
            code.append(alphabet.charAt(index));
        }

        if (StringUtils.isNotEmpty(prefix)) {
            code.insert(0, prefix);
        }

        return code.toString();
    }

    private String generateCodeByUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public ApiResult<GenerateVo> generate(GenerateDto dto) {
        // 通用参数。
        int mode = dto.getMode();
        String batchCode = dto.getBatchCode();
        int type = dto.getType();
        int count = dto.getCount();
        Date effectiveTimeStart = dto.getEffectiveTimeStart();
        Date effectiveTimeEnd = dto.getEffectiveTimeEnd();
        boolean enabled = dto.getEnabled();
        if (count <= 0) {
            throw new SystemException("参数count必须大于0");
        }

        // 字母表模式参数。
        String prefix = dto.getPrefix();
        Integer size = dto.getSize();
        String alphabet = dto.getAlphabet();
        if (mode == ActivationCodeModeEnum.ALPHABET.getValue()) {
            if (size == null) {
                throw new SystemException("参数size不能为空");
            }
            if (StringUtils.isEmpty(alphabet)) {
                throw new SystemException("参数alphabet不能为空");
            }
        }

        String batchId = UUID.randomUUID().toString().replace("-", "");

        // 生成激活码。
        List<ActivationCodeEntity> entityList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code;
            if (mode == ActivationCodeModeEnum.ALPHABET.getValue()) {
                code = generateCodeByAlphabet(size, prefix, alphabet);
            } else if (mode == ActivationCodeModeEnum.UUID.getValue()) {
                code = generateCodeByUUID();
            } else {
                throw new SystemException("参数mode的值错误");
            }

            ActivationCodeEntity entity = new ActivationCodeEntity();
            entity.setBatchId(batchId);
            entity.setBatchCode(batchCode);
            entity.setCode(code);
            entity.setType(type);
            entity.setEffectiveTimeStart(effectiveTimeStart);
            entity.setEffectiveTimeEnd(effectiveTimeEnd);
            entity.setUsed(false);
            entity.setUsedBy(null);
            entity.setUsedTime(null);
            entity.setEnabled(enabled);
            entity.setDeleted(false);

            entityList.add(entity);
        }

        // 批量插入。
        activationCodeService.saveBatch(entityList);

        return ApiResult.success();
    }

}
