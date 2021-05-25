package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.person.entity.Person;
import com.ai.apac.smartenv.system.service.IAddressBookService;
import com.ai.apac.smartenv.system.vo.ContactPersonVO;
import com.ai.apac.smartenv.system.vo.DeptVO;
import com.ai.apac.smartenv.system.wrapper.ContactPersonWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.log.annotation.ApiLog;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/4/16 11:15 上午
 **/
@RestController
@AllArgsConstructor
@Api(value = "通讯录", tags = "通讯录")
@RequestMapping("addressBook")
public class AddressBookController {

    private IAddressBookService addressBookService;

    /**
     * 获取部门树形结构
     *
     * @return
     */
    @GetMapping("/deptTree")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "获取通讯录部门树形结构", notes = "获取通讯录部门树形结构")
    @ApiLog(value = "获取通讯录部门树形结构")
    public R<DeptVO> tree(BladeUser bladeUser) {
        DeptVO tree = addressBookService.deptTree(bladeUser.getTenantId());
        return R.data(tree);
    }

    /**
     * 根据条件查询人员
     *
     * @param deptId
     * @param bladeUser
     * @return
     */
    @GetMapping("/contactPerson")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "deptId", value = "部门ID", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "name", value = "人员姓名", paramType = "query", dataType = "string"),
    })
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "根据条件查询人员", notes = "根据条件查询人员")
    @ApiLog(value = "根据条件查询通讯录联系人")
    public R<List<ContactPersonVO>> listContactPerson(@RequestParam(required = false) Long deptId, @RequestParam(required = false) String name, BladeUser bladeUser) {
        if(deptId == null && StringUtils.isBlank(name)){
            throw new ServiceException("部门ID和人员姓名不能都为空");
        }
        List<Person> personList = addressBookService.getContactPerson(bladeUser.getTenantId(), deptId, name);
        return R.data(ContactPersonWrapper.build().listVO(personList));
    }
}
