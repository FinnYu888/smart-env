/*
 *      Copyright (c) 2018-2028, Chill Zhuang All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the dreamlu.net developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: Chill 庄骞 (smallchill@163.com)
 */
package com.ai.apac.smartenv.facility.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.constant.FacilityConstant;
import com.ai.apac.smartenv.common.constant.SystemConstant;
import com.ai.apac.smartenv.common.utils.BigDataHttpClient;
import com.ai.apac.smartenv.common.utils.CommonUtil;
import com.ai.apac.smartenv.common.utils.TimeUtil;
import com.ai.apac.smartenv.facility.entity.FacilityExt;
import com.ai.apac.smartenv.facility.entity.FacilityInfo;
import com.ai.apac.smartenv.facility.entity.FacilityTranstationDetail;
import com.ai.apac.smartenv.facility.mapper.FacilityInfoMapper;
import com.ai.apac.smartenv.facility.service.IFacilityExtService;
import com.ai.apac.smartenv.facility.service.IFacilityInfoService;
import com.ai.apac.smartenv.facility.service.IFacilityTranstationDetailService;
import com.ai.apac.smartenv.facility.vo.FacilityExtVO;
import com.ai.apac.smartenv.facility.vo.FacilityInfoExtVO;
import com.ai.apac.smartenv.facility.vo.FacilityInfoVO;
import com.ai.apac.smartenv.system.entity.Region;
import com.ai.apac.smartenv.system.feign.IDictClient;
import com.ai.apac.smartenv.system.feign.ISysClient;
import com.ai.apac.smartenv.workarea.entity.WorkareaNode;
import com.ai.apac.smartenv.workarea.feign.IWorkareaNodeClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.AllArgsConstructor;
import org.springblade.core.mp.base.BaseServiceImpl;
import org.springblade.core.mp.support.Condition;
import org.springblade.core.secure.utils.AuthUtil;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.ObjectUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.*;

/**
 *  服务实现类
 *
 * @author Blade
 * @since 2020-02-11
 */
@Service
@AllArgsConstructor
public class FacilityInfoServiceImpl extends BaseServiceImpl<FacilityInfoMapper, FacilityInfo> implements IFacilityInfoService {

	@Autowired
	private ISysClient sysClient;
	@Autowired
	private IWorkareaNodeClient workareaNodeClient;
	@Autowired
	private IDictClient dictClient;
	@Autowired
	private IFacilityExtService facilityExtService;
	@Autowired
	private IFacilityTranstationDetailService facilityTranstationDetailService;

	@Override
	public IPage<FacilityInfoVO> selectFacilityInfoPage(IPage<FacilityInfoVO> page, FacilityInfoVO facilityInfo) {
		return page.setRecords(baseMapper.selectFacilityInfoPage(page, facilityInfo));
	}
	@Override
	public  List<FacilityInfoVO> selectTranstationTotalList(IPage<FacilityInfoVO> page,String companyCode,String facilityName,String status,boolean statusFlag,String tenantId) {


	   /* Map<String,Object> map = new HashMap<>();
	    if (StringUtil.isNotBlank(facilityName)) {
	    	map.put("facilityName",facilityName);
		}
	    Long current = page.getCurrent();
        Long size = page.getSize();
        if (null != current && null != size && size>0 && current>=0) {
            map.put("current",(current-1)*size);
            map.put("size",size);
        }
        if (StringUtil.isNotBlank(status)){
        	map.put("status",status);
		}
        if (StringUtil.isNotBlank(companyCode)) {
			map.put("compaynCode",companyCode);
		}
		if (statusFlag) {
			map.put("statusFlag",statusFlag);
		}*/
	   QueryWrapper queryWrapper = new QueryWrapper();
		if (StringUtil.isNotBlank(facilityName)) {
			queryWrapper.like("info.facility_name",facilityName);
		}

		if (StringUtil.isNotBlank(status)){
			queryWrapper.eq("info.status",status);
		}
		if (StringUtil.isNotBlank(companyCode)) {
			queryWrapper.eq("info.company_code",companyCode);
		}
		queryWrapper.eq("info.is_deleted",0);
		if (StringUtil.isNotBlank(tenantId)) {
			queryWrapper.eq("info.tenant_id",tenantId);
		}
		if (statusFlag) queryWrapper.notIn("info.status",new ArrayList<String>(Arrays.asList(FacilityConstant.TranStationStatus.PLANNING,FacilityConstant.TranStationStatus.DROP)));
		List<FacilityInfoVO> list = baseMapper.selectTranstationTotalList(page,queryWrapper);

		return list;
	}



	@Override
	public boolean saveOrUpdateFacilityInfo(FacilityInfo facilityInfo) {
		//因为目前新增或更新没有选择业务区域，所以后端根据坐标点计算
		Point2D.Double point = new Point2D.Double();
		point.setLocation(Double.parseDouble(facilityInfo.getLng()),Double.parseDouble(facilityInfo.getLat()));
		List<Region> regions = sysClient.getRegionByType(SystemConstant.RegionType.TYPE_2, AuthUtil.getTenantId()).getData();
		if(ObjectUtil.isNotEmpty(regions) && regions.size() > 0 ){
			for(Region region:regions){
				List<Point2D.Double> pts = new ArrayList<Point2D.Double>();
				List<WorkareaNode> workareaNodes = workareaNodeClient.queryRegionNodesList(region.getId()).getData();
				if(ObjectUtil.isNotEmpty(workareaNodes) && workareaNodes.size() > 0){
					for(WorkareaNode workareaNode:workareaNodes){
						Point2D.Double point_ = new Point2D.Double();
						point_.setLocation(Double.parseDouble(workareaNode.getLongitude()),Double.parseDouble(workareaNode.getLatitudinal()));
						pts.add(point_);
					}
					if(CommonUtil.inRegion(pts,point)){
						facilityInfo.setRegionId(region.getId().toString());
						facilityInfo.setParentRegionId(region.getParentRegionId().toString());
						break;
					}
				}
			}
		}

		if(ObjectUtils.isEmpty(facilityInfo.getId())){
			return this.save(facilityInfo);
		}else {
			return this.updateById(facilityInfo);
		}
	}

	@Override
	public FacilityInfoExtVO getFacilityDetail(FacilityInfo facilityInfo) {
		FacilityInfo detail = this.getOne(Condition.getQueryWrapper(facilityInfo));
		if (null == detail) {
			return null;
		}
		FacilityInfoExtVO facilityInfoExtVO = new FacilityInfoExtVO();
		BeanUtil.copy(detail, facilityInfoExtVO);
		//转译中转站规模
		facilityInfoExtVO.setTranStationModel(getDictName(FacilityConstant.TranStationModel.CODE, detail.getExt1()));
		//转译中转站状态
		facilityInfoExtVO.setStatusName(getDictName(FacilityConstant.TranStationStatus.CODE, null != (detail.getStatus()) ? detail.getStatus().toString() : ""));
		Long facilityId = detail.getId();
		FacilityExt facilityExt = new FacilityExt();
		facilityExt.setFacilityId(facilityId);
		//获取扩展信息
		List<FacilityExt> facilityExts = facilityExtService.list(Condition.getQueryWrapper(facilityExt));
		List<FacilityExtVO> facilityExtVOS = new ArrayList<>();
		facilityExts.forEach(facilityext -> {
			FacilityExtVO extVO = new FacilityExtVO();
			BeanUtil.copy(facilityext, extVO);
			facilityExtVOS.add(extVO);
		});
		try {
			String startDate = TimeUtil.getFormattedDate(TimeUtil.getSysDate(), TimeUtil.YYYY_MM_DD);
			FacilityTranstationDetail vo = facilityTranstationDetailService.statisticalEveryDate(facilityId, startDate, null);
			facilityInfoExtVO.setOdorLevel(getOdorLevel(facilityInfoExtVO.getProjectNo()));
			if (null != vo) {
				facilityInfoExtVO.setGarbageWeight(vo.getGarbageWeight());
				facilityInfoExtVO.setTransTimes(vo.getTransferTimes());
			} else {
				facilityInfoExtVO.setGarbageWeight("0");
				facilityInfoExtVO.setTransTimes(0);
			}
		} catch (Exception e) {
			log.error(StrUtil.format("转换臭味级别报错:{}", e.getMessage()));
		}

		//获取臭味级别
		facilityInfoExtVO.setFacilityExtVOList(facilityExtVOS);
		return facilityInfoExtVO;
	}

	private String getDictName(String code, String dictKey) {
		R<String> value = dictClient.getValue(code, dictKey);
		if (value.isSuccess() && StringUtil.isNotBlank(value.getData())) {
			return value.getData();
		}
		return "";
	}

	/**
	 * 获取中转站臭味值
	 *0：无臭
	 *      * 0-1：轻微异味
	 *      * 1-2：检知阙值
	 *      * 2-3：认知阙值
	 *      * 3-4：明显检知
	 *      * 4-5: 强臭
	 *      * 5以上：剧臭
	 *     */
	private String getOdorLevel(String facilityNos) {

		if (StringUtil.isBlank(facilityNos)) {
			return "0.0(无臭)";
		}
		StringBuffer facilityBuffer = new StringBuffer();

		try {
			Map<String,Object> param = new HashMap<>();
			param.put(BigDataHttpClient.Odoy_Request.WTS_IDS,facilityNos);
			log.debug("request getOdoyLevel===="+facilityNos);
			String reStr = BigDataHttpClient.getBigDataBody(BigDataHttpClient.getOdlyLevelURL,param);
			log.debug(" response /smartenv-api/wts/odour/search==="+reStr);
			if (StringUtil.isNotBlank(reStr) && null != JSONUtil.parseObj(reStr).get(BigDataHttpClient.RESPONSE.CODE) && "0".equals(JSONUtil.parseObj(reStr).get(BigDataHttpClient.RESPONSE.CODE).toString())) {
				JSONObject data = (JSONObject)JSONUtil.parseObj(reStr).get("data");
				JSONArray wtsOdours = (JSONArray) data.get(BigDataHttpClient.Odoy_Request.WTS_ODOURS);

				JSONObject object = wtsOdours.getJSONObject(0);
				//{"deviceNumber":"867959034034859","facilityId":"zz190917173709","facilityName":"大西门垃圾中转站","lat":29.338291,"lng":115.766724,"senSorValue":3.5,"time":"2020-07-21 09:18:48"}
				Double odorLevel  = (Double)object.get("senSorValue");

				StringBuffer buffer = new StringBuffer(String.valueOf(odorLevel));

				int level =  odorLevel.intValue();
				level = odorLevel-level == 0?level:level + 1;


				String value = dictClient.getValue(FacilityConstant.FacilityOdorLevel.CODE,String.valueOf(level)).getData();
				buffer.append("(").append(value).append(")");

				return buffer.toString();
			}


		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage(),e);
		}
		return "0.0(无臭)";
	}
	
}
