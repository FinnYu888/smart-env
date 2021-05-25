package com.ai.apac.smartenv.system.wrapper;

import com.ai.apac.smartenv.common.constant.PersonConstant;
import com.ai.apac.smartenv.common.utils.CommonUtil;
import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.system.cache.DeptCache;
import com.ai.apac.smartenv.system.cache.DictCache;
import com.ai.apac.smartenv.system.cache.StationCache;
import com.ai.apac.smartenv.system.entity.Tenant;
import com.ai.apac.smartenv.system.vo.ContactPersonVO;
import com.ai.apac.smartenv.system.vo.TenantVO;
import org.springblade.core.mp.support.BaseEntityWrapper;
import org.springblade.core.tool.utils.BeanUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author qianlong
 */
public class ContactPersonWrapper extends BaseEntityWrapper<Person, ContactPersonVO> {

    public static ContactPersonWrapper build() {
        return new ContactPersonWrapper();
    }

    @Override
    public ContactPersonVO entityVO(Person person) {

        ContactPersonVO contactPersonVO = Objects.requireNonNull(BeanUtil.copy(person, ContactPersonVO.class));
        String deptName = DeptCache.getDeptName(String.valueOf(person.getPersonDeptId()));
        contactPersonVO.setDeptName(deptName);

        String stationName = StationCache.getStationName(person.getPersonPositionId());
        contactPersonVO.setStationName(stationName);

        String personName = person.getPersonName();
        contactPersonVO.setNickName(CommonUtil.getNickName(personName));

        Integer isIncumbency = person.getIsIncumbency() == null ? 0 : person.getIsIncumbency();
		if (isIncumbency.equals(PersonConstant.IncumbencyStatus.IN)) {
			contactPersonVO.setIsIncumbencyName("在职");
		} else if (isIncumbency.equals(PersonConstant.IncumbencyStatus.UN)) {
			contactPersonVO.setIsIncumbencyName("离职");
		} else if (isIncumbency.equals(PersonConstant.IncumbencyStatus.TEMPORARY)) {
			contactPersonVO.setIsIncumbencyName("临时工");
		} else {
			contactPersonVO.setIsIncumbencyName("--");
		}
        return contactPersonVO;
    }

    @Override
    public List<ContactPersonVO> listVO(List<Person> list) {
        if (list == null || list.size() == 0) {
            return new ArrayList<ContactPersonVO>();
        }
        List<ContactPersonVO> collect = list.stream().map(this::entityVO).collect(Collectors.toList());
        return collect;
    }

}
