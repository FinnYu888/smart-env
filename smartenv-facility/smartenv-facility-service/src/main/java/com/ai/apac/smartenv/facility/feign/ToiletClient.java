package com.ai.apac.smartenv.facility.feign;

import com.ai.apac.smartenv.facility.dto.ToiletQueryDTO;
import com.ai.apac.smartenv.facility.entity.ToiletInfo;
import com.ai.apac.smartenv.facility.service.IToiletInfoService;
import com.ai.apac.smartenv.facility.vo.ToiletInfoVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ai.apac.smartenv.facility.wrapper.ToiletInfoWrapper;

import lombok.AllArgsConstructor;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@ApiIgnore
@RestController
@AllArgsConstructor
public class ToiletClient implements IToiletClient {

    private IToiletInfoService toiletInfoService;

    @Override
    @GetMapping(TOILET_VO)
    public R<ToiletInfoVO> getToilet(Long id) {
        return R.data(toiletInfoService.getToiletDetailsById(id));
    }

    @Override
    public R<Integer> countAllToilet(String tenantId) {
        return R.data(toiletInfoService.count(new LambdaQueryWrapper<ToiletInfo>().eq(ToiletInfo::getTenantId, tenantId)
                .eq(ToiletInfo::getIsDeleted, 0)));
    }

    @Override
    @PostMapping(TOILET_VO_BY_CONDITION)
    public R<List<ToiletInfoVO>> listToiletVOByCondition(@RequestBody ToiletQueryDTO queryDTO) {
        return R.data(toiletInfoService.listToiletInfosByCondition(queryDTO));
    }

    @Override
    public R<List<ToiletInfoVO>> listToiletAll() {
        return R.data(ToiletInfoWrapper.build().listVO(toiletInfoService.list()));
    }
}
