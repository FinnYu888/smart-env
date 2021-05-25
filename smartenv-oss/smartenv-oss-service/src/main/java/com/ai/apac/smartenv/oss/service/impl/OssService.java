package com.ai.apac.smartenv.oss.service.impl;

import com.ai.apac.smartenv.oss.entity.Oss;
import com.ai.apac.smartenv.oss.mapper.OssMapper;
import com.ai.apac.smartenv.oss.service.IOssService;
import com.ai.apac.smartenv.oss.vo.OssVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springblade.core.log.exception.ServiceException;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.oss.minio.MinioTemplate;
import org.springblade.core.oss.model.BladeFile;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.utils.DateUtil;
import org.springblade.core.tool.utils.Func;
import org.springblade.core.tool.utils.StringPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/3/16 8:23 下午
 **/
@Service
public class OssService extends BaseServiceImpl<OssMapper, Oss> implements IOssService {

//    @Autowired
//    private MinioTemplate minioTemplate;

//    /**
//     * 根据规则获取文件名
//     *
//     * @param originalFilename
//     * @return
//     */
//    @Override
//    public String getFileNameByRule(String originalFilename) {
//        return "upload" + StringPool.SLASH + DateUtil.today() + StringPool.SLASH + originalFilename;
//    }

//    /**
//     * 上传文件
//     *
//     * @param bucketName
//     * @param fileName
//     * @param stream
//     * @return
//     */
//    @Override
//    public BladeFile putFile(String bucketName, String fileName, InputStream stream) {
//        String newFileName = getFileNameByRule(fileName);
//        return minioTemplate.overridePutFile(bucketName, newFileName, stream);
//    }

    @Override
    public IPage<OssVO> selectOssPage(IPage<OssVO> page, OssVO oss) {
        return page.setRecords(baseMapper.selectOssPage(page, oss));
    }

    @Override
    public boolean submit(Oss oss) {
        LambdaQueryWrapper<Oss> lqw = Wrappers.<Oss>query().lambda()
                .eq(Oss::getOssCode, oss.getOssCode()).eq(Oss::getTenantId, AuthUtil.getTenantId());
        Integer cnt = baseMapper.selectCount(Func.isEmpty(oss.getId()) ? lqw : lqw.notIn(Oss::getId, oss.getId()));
        if (cnt > 0) {
            throw new ServiceException("当前资源编号已存在!");
        }
        return this.saveOrUpdate(oss);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean enable(Long id) {
        //目前只取默认租户的数据
        List<Oss> ossRuleList = this.list();
        ossRuleList.forEach(ossRule -> {
            if (ossRule.getId().equals(id)) {
                ossRule.setStatus(2);
            } else {
                ossRule.setStatus(1);
            }
            this.updateById(ossRule);
        });
        return true;
//        // 先禁用
//        boolean temp1 = this.update(Wrappers.<Oss>update().lambda().set(Oss::getStatus, 1));
//        // 在启用
//        boolean temp2 = this.update(Wrappers.<Oss>update().lambda().set(Oss::getStatus, 2).eq(Oss::getId, id));
//        return temp1 && temp2;
    }
}
