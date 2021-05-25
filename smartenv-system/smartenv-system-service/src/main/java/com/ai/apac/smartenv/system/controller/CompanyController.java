package com.ai.apac.smartenv.system.controller;

import com.ai.apac.smartenv.oss.fegin.IOssClient;
import com.ai.apac.smartenv.system.cache.CompanyCache;
import com.ai.apac.smartenv.system.entity.Company;
import com.ai.apac.smartenv.system.service.ICompanyService;
import com.ai.apac.smartenv.system.vo.CompanyVO;
import com.ai.apac.smartenv.system.wrapper.CompanyWrapper;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
import org.springblade.core.secure.BladeUser;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.node.INode;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * @author qianlong
 * @description 公司管理
 * @Date 2020/11/26 8:12 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/company")
@Api(value = "公司管理", tags = "公司管理")
@Slf4j
public class CompanyController {

    private ICompanyService companyService;

    private IOssClient ossClient;

    /**
     * 新增公司
     *
     * @param company
     * @return
     */
    @PostMapping("")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "新增公司", notes = "新增公司")
    public R createCompany(@Valid @RequestBody Company company, BladeUser bladeUser) {
        @NotEmpty(message = "公司名称不能为空") String fullName = company.getFullName();
        log.info(fullName);
        company.setCreateUser(bladeUser.getUserId());
        company.setCreateDept(Long.valueOf(bladeUser.getDeptId()));
        company.setUpdateUser(bladeUser.getUserId());
        return R.status(companyService.saveCompany(company));
    }

    /**
     * 修改公司信息
     *
     * @param company
     * @return
     */
    @PutMapping("")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "修改公司信息", notes = "修改公司信息")
    public R updateCompany(@Validated @RequestBody Company company, BladeUser bladeUser) {
        company.setUpdateUser(bladeUser.getUserId());
        return R.status(companyService.updateCompany(company));
    }

    /**
     * 修改公司状态
     *
     * @param companyId
     * @param newStatus
     * @return
     */
    @PutMapping("/status")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "修改公司状态", notes = "修改公司状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId", value = "公司ID", paramType = "query", dataType = "long"),
            @ApiImplicitParam(name = "newStatus", value = "新状态(1-正常,2-锁定)", paramType = "query", dataType = "integer")
    })
    public R changeCompanyStatus(@RequestParam Long companyId, @RequestParam Integer newStatus, BladeUser bladeUser) {
        Company company = new Company();
        company.setId(companyId);
        company.setUpdateUser(bladeUser.getUserId());
        return R.status(companyService.changeCompanyStatus(company, newStatus));
    }

    /**
     * 根据主键获取公司信息
     *
     * @param companyId
     * @return
     */
    @GetMapping("/{companyId}")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "根据主键获取公司信息", notes = "根据主键获取公司信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "companyId", value = "公司ID", paramType = "query", dataType = "long")
    })
    public R<CompanyVO> getCompany(@PathVariable Long companyId) {
        Company company = companyService.getById(companyId);
        if(company == null){
            return R.data(null);
        }
        CompanyVO companyVO = CompanyWrapper.build().entityDetailVO(company);
        if (StringUtils.isNotBlank(company.getBusinessLicenseUrl())) {
            R<String> result = ossClient.getObjectLink("smartenv", companyVO.getBusinessLicenseUrl());
            companyVO.setFullBusinessLicenseUrl(result.getData());
        }
        return R.data(companyVO);
    }

    /**
     * 获取公司信息树结构列表
     *
     * @return
     */
    @GetMapping("/tree")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "获取公司信息树结构列表", notes = "获取公司信息树结构列表")
    public R<List<INode>> getCityTree() {
        List<Company> allCompany = companyService.list();
        return R.data(CompanyWrapper.build().listNodeVO(allCompany));
    }
}
