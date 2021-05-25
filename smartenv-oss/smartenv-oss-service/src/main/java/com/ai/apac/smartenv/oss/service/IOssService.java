package com.ai.apac.smartenv.oss.service;

import com.ai.apac.smartenv.oss.entity.Oss;
import com.ai.apac.smartenv.oss.vo.OssVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springblade.core.mp.base.BaseService;

/**
 * @author qianlong
 * @Description //TODO
 * @Date 2020/3/16 8:22 下午
 **/
public interface IOssService extends BaseService<Oss>  {

//    /**
//     * 根据规则获取文件名
//     * @param originalFilename
//     * @return
//     */
//    String getFileNameByRule(String originalFilename);

//    /**
//     * 上传文件
//     * @param bucketName
//     * @param fileName
//     * @param stream
//     * @return
//     */
//    BladeFile putFile(String bucketName, String fileName, InputStream stream);

    /**
     * 自定义分页
     *
     * @param page
     * @param oss
     * @return
     */
    IPage<OssVO> selectOssPage(IPage<OssVO> page, OssVO oss);

    /**
     * 提交oss信息
     *
     * @param oss
     * @return
     */
    boolean submit(Oss oss);

    /**
     * 启动配置
     *
     * @param id
     * @return
     */
    boolean enable(Long id);
}
