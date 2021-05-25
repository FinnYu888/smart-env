package com.ai.apac.smartenv.oss.controller;

import com.ai.apac.smartenv.oss.builder.OssBuilder;
import com.ai.apac.smartenv.oss.entity.Oss;
import com.ai.apac.smartenv.oss.service.IOssService;
import com.ai.apac.smartenv.oss.vo.OssVO;
import com.ai.apac.smartenv.oss.wrapper.OssWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.cache.utils.CacheUtil;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.mp.support.Query;
import org.springblade.core.oss.minio.MinioTemplate;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

import static org.springblade.core.cache.constant.CacheConstant.RESOURCE_CACHE;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/6 4:26 下午
 **/
@RestController
@AllArgsConstructor
@Api(value = "对象存储管理", tags = "对象存储管理")
@Slf4j
public class OssController extends BladeController {

//    private MinioTemplate minioTemplate;

    @Autowired
    private IOssService ossService;

    /**
     * 对象存储构建类
     */
    private OssBuilder ossBuilder;

    @SneakyThrows
    @PostMapping("/oss/object")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "上传文件对象", notes = "上传文件对象")
    public R<BladeFile> putObject(@ApiParam(value = "文件对象", required = true) @RequestParam MultipartFile uploadFile,
                                  @ApiParam(value = "文件桶名称", required = true, defaultValue = "smartenv") @RequestParam String bucketName) {
//        BladeFile uploadResult = ossService.putFile(bucketName, uploadFile.getOriginalFilename(), uploadFile.getInputStream());
        String originalFilename = uploadFile.getOriginalFilename();
        // 如果文件名一样会覆盖，文件名拼上当前时间
        long now = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
        String fileName = now + "." + uploadFile.getOriginalFilename().replaceAll("[\\\\/:*?\"<>|,]", "");
        BladeFile bladeFile = ossBuilder.template().putFile(bucketName, fileName, uploadFile.getInputStream());
        bladeFile.setOriginalName(originalFilename);

        return R.data(bladeFile);
    }

    @SneakyThrows
    @PostMapping("/public/oss/object")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "公众上传文件", notes = "公众上传文件")
    public R<BladeFile> publicPutObject(@ApiParam(value = "文件对象", required = true) @RequestParam MultipartFile uploadFile,
                                        @ApiParam(value = "文件桶名称", required = true, defaultValue = "smartenv") @RequestParam String bucketName) {
        return putObject(uploadFile, bucketName);
    }

    @SneakyThrows
    @GetMapping("/oss/objectLink")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "获取文件对象", notes = "获取文件对象")
    public String getObjectLink(@ApiParam(value = "", required = true) @RequestParam String fileName,
                                @ApiParam(value = "文件桶名称", required = true, defaultValue = "smartenv") @RequestParam String bucketName) {
//        String shareLink = minioTemplate.getShareLink(bucketName, fileName);
        String shareLink = ossBuilder.template().fileLink(bucketName, fileName);
        log.info("shareLink:{}", shareLink);
        return shareLink;
    }

    @SneakyThrows
    @GetMapping("/public/oss/objectLink")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "公众获取文件对象", notes = "公众获取文件对象")
    public String publicPutObject(@ApiParam(value = "", required = true) @RequestParam String fileName,
                                  @ApiParam(value = "文件桶名称", required = true, defaultValue = "smartenv") @RequestParam String bucketName) {
        return getObjectLink(fileName, bucketName);
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入oss")
    public R<OssVO> detail(Oss oss) {
        Oss detail = ossService.getOne(Condition.getQueryWrapper(oss));
        return R.data(OssWrapper.build().entityVO(detail));
    }

    /**
     * 分页
     */
    @GetMapping("/list")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "分页", notes = "传入oss")
    public R<IPage<OssVO>> list(Oss oss, Query query) {
        IPage<Oss> pages = ossService.page(Condition.getPage(query), Condition.getQueryWrapper(oss));
        return R.data(OssWrapper.build().pageVO(pages));
    }

    /**
     * 自定义分页
     */
    @GetMapping("/page")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "自定义分页", notes = "传入oss")
    public R<IPage<OssVO>> page(OssVO oss, Query query) {
        IPage<OssVO> pages = ossService.selectOssPage(Condition.getPage(query), oss);
        return R.data(pages);
    }

    /**
     * 新增
     */
    @PostMapping("/save")
    @ApiOperationSupport(order = 4)
    @ApiOperation(value = "新增", notes = "传入oss")
    public R save(@Valid @RequestBody Oss oss) {
        CacheUtil.clear(RESOURCE_CACHE);
        return R.status(ossService.save(oss));
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @ApiOperationSupport(order = 5)
    @ApiOperation(value = "修改", notes = "传入oss")
    public R update(@Valid @RequestBody Oss oss) {
        CacheUtil.clear(RESOURCE_CACHE);
        return R.status(ossService.updateById(oss));
    }

    /**
     * 新增或修改
     */
    @PostMapping("/submit")
    @ApiOperationSupport(order = 6)
    @ApiOperation(value = "新增或修改", notes = "传入oss")
    public R submit(@Valid @RequestBody Oss oss) {
        CacheUtil.clear(RESOURCE_CACHE);
        return R.status(ossService.submit(oss));
    }


    /**
     * 删除
     */
    @PostMapping("/remove")
    @ApiOperationSupport(order = 7)
    @ApiOperation(value = "逻辑删除", notes = "传入ids")
    public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
        CacheUtil.clear(RESOURCE_CACHE);
        return R.status(ossService.deleteLogic(Func.toLongList(ids)));
    }


    /**
     * 启用
     */
    @PostMapping("/enable")
    @ApiOperationSupport(order = 8)
    @ApiOperation(value = "配置启用", notes = "传入id")
    public R enable(@ApiParam(value = "主键", required = true) @RequestParam Long id) {
        CacheUtil.clear(RESOURCE_CACHE);
        return R.status(ossService.enable(id));
    }
}
