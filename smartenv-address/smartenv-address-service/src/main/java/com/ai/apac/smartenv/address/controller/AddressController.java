package com.ai.apac.smartenv.address.controller;

import cn.hutool.core.collection.CollUtil;
import com.ai.apac.smartenv.address.dto.GisDTO;
import com.ai.apac.smartenv.address.entity.GisInfo;
import com.ai.apac.smartenv.address.service.IAddressService;
import com.ai.apac.smartenv.address.service.IGisInfoCacheService;
import com.ai.apac.smartenv.address.vo.GisInfoVO;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import com.ai.apac.smartenv.common.utils.BaiduMapUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springblade.core.boot.ctrl.BladeController;
import org.springblade.core.tool.api.R;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springblade.core.tool.utils.WebUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qianlong
 * @description //TODO
 * @Date 2020/2/29 7:24 下午
 **/
@RestController
@AllArgsConstructor
@RequestMapping("/address")
@Api(value = "地址管理", tags = "地址管理")
public class AddressController extends BladeController {

    @Autowired
    private IAddressService addressService;


    @Autowired
    private IGisInfoCacheService gisInfoCacheService;

    private BaiduMapUtils baiduMapUtils;

    /**
     * 新增 录像设备通道信息
     */
    @Deprecated
    @PostMapping("")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "新增", notes = "经纬度坐标")
    public R createAddress(String lon, String lat) {
        addressService.saveBaiDuAddress(lat, lon);
        return R.status(true);
    }

    @PostMapping("/getAddressByCoords")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "根据坐标获取地址", notes = "经纬度坐标")
    public R<BaiduMapReverseGeoCodingResult> getAddressByCoords(@RequestBody Coords coords) throws IOException {
        String coordsTypeStr = WebUtil.getHeader("coordsType");
        int coordsType = BaiduMapUtils.CoordsSystem.GC02.value;
        if (StringUtil.isNotBlank(coordsTypeStr)) {
            coordsType = Integer.parseInt(coordsTypeStr);
        }
        Coords baidu = coords;
        if (!BaiduMapUtils.CoordsSystem.BD09LL.equals(coordsType)) {
            try {

                Coords coordsCacheResult = gisInfoCacheService.getCoords(BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType), BaiduMapUtils.CoordsSystem.BD09LL, coords);
                if (coordsCacheResult != null) {
                    baidu = coordsCacheResult;

                } else {
                    List<Coords> coordList = new ArrayList<>();
                    coordList.add(coords);
                    List<Coords> result = baiduMapUtils.coordsToBaiduMapllAll(BaiduMapUtils.CoordsSystem.getCoordsSystem(coordsType), coordList);
                    if (CollectionUtil.isNotEmpty(result)) {
                        baidu = result.get(0);
                    }
                }
            } catch (IOException e) {
                return R.fail("转换为百度坐标失败");
            }
        }

        BaiduMapReverseGeoCodingResult address = addressService.getAddress(baidu);
        if (address == null) {
            address=BaiduMapUtils.getReverseGeoCoding(coords, BaiduMapUtils.CoordsSystem.BD09LL);
        }

        return R.data(address);
    }

    /**
     * 新增GIS信息
     *
     * @param gisList
     * @return
     */
    @PostMapping("/gisInfo")
    @ApiOperationSupport(order = 2)
    @ApiOperation(value = "新增GIS信息", notes = "新增GIS信息")
    public R saveGisInfo(@RequestBody List<GisDTO> gisList) {
        if(CollUtil.isEmpty(gisList)){
            return R.fail("GIS信息不能为空");
        }
        gisList.stream().forEach(gisDTO -> {
            addressService.saveGisInfo(gisDTO);
        });
        return R.success("保存GIS信息成功");
    }

    /**
     * 查询GIS信息
     *
     * @param areaCode
     * @return
     */
    @GetMapping("/gisInfo")
    @ApiOperationSupport(order = 3)
    @ApiOperation(value = "查询GIS信息", notes = "查询GIS信息")
    public R<GisInfoVO> getGisInfo(@RequestParam String areaCode) {
        if(StringUtils.isEmpty(areaCode)){
            return R.fail("获取不能为空");
        }
        return R.data(addressService.getGisInfoByAreaCode(areaCode));
    }
}
