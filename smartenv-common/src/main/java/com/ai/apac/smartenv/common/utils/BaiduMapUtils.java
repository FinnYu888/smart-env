package com.ai.apac.smartenv.common.utils;

import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.ai.apac.smartenv.common.dto.AmapDrvierResult;
import com.ai.apac.smartenv.common.dto.BaiduMapGeocovResult;
import com.ai.apac.smartenv.common.dto.BaiduMapReverseGeoCodingResult;
import com.ai.apac.smartenv.common.dto.Coords;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springblade.core.tool.utils.BeanUtil;
import org.springblade.core.tool.utils.CollectionUtil;
import org.springblade.core.tool.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: BaiduMapUtils
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/2/20
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/2/20  20:08    panfeng          v1.0.0             修改原因
 */
@Component
@Slf4j
public class BaiduMapUtils {

    private static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient();
    }

    private static String BD_AK;

    @Value("${mapapi.config.bdak}")
    public void setBD_AK(String BD_AK) {
        BaiduMapUtils.BD_AK = BD_AK;
    }

    private static String BD_JAVASCRIPT_AK;

    @Value("${mapapi.config.bdJavascriptAK}")
    public void setBD_JAVASCRIPT_AK(String BD_JAVASCRIPT_AK) {
        BaiduMapUtils.BD_JAVASCRIPT_AK = BD_JAVASCRIPT_AK;
    }

    private static String BD_SK;

    @Value("${mapapi.config.bdsk}")
    public void setBD_SK(String BD_SK) {
        BaiduMapUtils.BD_SK = BD_SK;
    }

    private static String GD_KEY;

    @Value("${mapapi.config.gdkey}")
    public void setGD_KEY(String GD_KEY) {
        BaiduMapUtils.GD_KEY = GD_KEY;
    }

    private static String GD_SIG;

    @Value("${mapapi.config.gdsig}")
    public void setGD_SIG(String GD_SIG) {
        BaiduMapUtils.GD_SIG = GD_SIG;
    }

    private static String GAODE_BASE_URL;

    @Value("${mapapi.config.gaoDeBaseUrl}")
    public void setGAODE_BASE_URL(String GAODE_BASE_URL) {
        BaiduMapUtils.GAODE_BASE_URL = GAODE_BASE_URL;
    }

    //百度坐标转换URL
    private static String BD_GEO_COV_URL;

    @Value("${mapapi.config.baiduGeoCovUrl}")
    public void setBD_GEO_COV_URL(String BD_GEO_COV_URL) {
        BaiduMapUtils.BD_GEO_COV_URL = BD_GEO_COV_URL;
    }

    //百度全球逆地理编码URL
    private static String BD_REVERSE_GEOCODINGS_URL;

    @Value("${mapapi.config.baiduReverserGeocodingsUrl}")
    public void setBD_REVERSE_GEOCODINGS_URL(String BD_REVERSE_GEOCODINGS_URL) {
        BaiduMapUtils.BD_REVERSE_GEOCODINGS_URL = BD_REVERSE_GEOCODINGS_URL;
    }

    //百度全球逆地理编码URL
    private static String BD_STATIC_IMAGE_URL;

    @Value("${mapapi.config.baiduStaticImageUrl}")
    public void setBD_STATIC_IMAGE_URL(String BD_STATIC_IMAGE_URL) {
        BaiduMapUtils.BD_STATIC_IMAGE_URL = BD_STATIC_IMAGE_URL;
    }

    //高德坐标转换URL
    private static String GD_GEO_COV_URL = "/v3/assistant/coordinate/convert";
    //高德驾车规划URL
    private static String GD_DRIVER_URL = "/v3/direction/driving";
    //高德地理编码URL
    private static String GD_GEO_URL = "/v3/geocode/geo";

    @Value("${mapapi.config.gaodeGeoCovUrl}")
    public void setGD_GEO_COV_URL(String GD_GEO_COV_URL) {
        BaiduMapUtils.GD_GEO_COV_URL = GD_GEO_COV_URL;
    }

    private static String BAIDU_BASE_URL;

    @Value("${mapapi.config.baiduBaseUrl}")
    public void setBaiduBaseUrl(String BAIDU_BASE_URL) {
        BaiduMapUtils.BAIDU_BASE_URL = BAIDU_BASE_URL;
    }

    //百度地址转坐标URL
    private static String BD_PLACE_TO_COORD_URL;

    @Value("${mapapi.config.baiduPlaceToCoordinatesUrl}")
    public void setBdPlaceToCoordUrl(String BD_PLACE_TO_COORD_URL) {
        BaiduMapUtils.BD_PLACE_TO_COORD_URL = BD_PLACE_TO_COORD_URL;
    }

    /**
     * 坐标系
     */
    public enum CoordsSystem {
        /**
         * GPS设备获取的角度坐标，WGS84坐标;
         */
        WGS84(1),
        /**
         * GPS获取的米制坐标、sogou地图所用坐标;
         */
        WGS84_MC(2),
        /**
         * google地图、soso地图、aliyun地图、mapabc地图和amap地图所用坐标，国测局（GCJ02）坐标;
         */
        GC02(3),
        /**
         * 国测局GC-02坐标的米制坐标
         */
        GC02_MC(4),
        /**
         * 百度地图采用的经纬度坐标;
         */
        BD09LL(5),
        /**
         * 百度地图采用的米制坐标;
         */
        BD09_MC(6),
        /**
         * mapbar地图坐标;
         */
        MAPBAR(7),
        /**
         * 51地图坐标
         */
        MAP_51(8);
        public Integer value;

        CoordsSystem(Integer value) {
            this.value = value;
        }

        public static CoordsSystem getCoordsSystem(Integer value) {

            CoordsSystem[] values = CoordsSystem.values();
            for (CoordsSystem coords : values) {
                if (coords.value.equals(value)) {
                    return coords;
                }
            }
            return null;
        }
    }


    /**
     * 采用非官方方式将其他坐标系转换为百度坐标系，只支持wgs84,gc02
     *
     * @param from
     * @param coords
     * @return
     * @throws IOException
     */
    private List<Coords> coordsToBaiduMapUnofficial(CoordsSystem from, List<Coords> coords) throws IOException {
        List<Coords> coordsList = new ArrayList<>();
        coords.forEach(coord -> {
            if (StringUtil.isBlank(coord.getLatitude()) || StringUtil.isBlank(coord.getLongitude())) {
                coordsList.add(coord);
                return;
            }
            try {
                if (from.equals(CoordsSystem.GC02)) {
                    String latitude = coord.getLatitude();
                    String longitude = coord.getLongitude();
                    double lat = Double.parseDouble(latitude);
                    double lng = Double.parseDouble(longitude);
                    double[] doubles = GPSUtil.gcj02_To_Bd09(lat, lng);
                    lat = doubles[0];
                    lng = doubles[1];
                    Coords copy = BeanUtil.copy(coord, Coords.class);
                    copy.setLatitude(String.valueOf(lat));
                    copy.setLongitude(String.valueOf(lng));
                    coordsList.add(copy);
                } else if (from.equals(CoordsSystem.WGS84)) {
                    String latitude = coord.getLatitude();
                    String longitude = coord.getLongitude();
                    double lat = Double.parseDouble(latitude);
                    double lng = Double.parseDouble(longitude);
                    double[] doubles = GPSUtil.gps84_To_bd09(lat, lng);
                    lat = doubles[0];
                    lng = doubles[1];
                    Coords copy = BeanUtil.copy(coord, Coords.class);
                    copy.setLatitude(String.valueOf(lat));
                    copy.setLongitude(String.valueOf(lng));
                    coordsList.add(copy);
                } else if (from.equals(CoordsSystem.BD09LL)) {
                    coordsList.add(coord);
                }
            } catch (Exception e) {
                coordsList.add(coord);
            }
        });

        return coordsList;

    }

    /**
     * 将指定坐标系的坐标转换为百度坐标系。
     *
     * @param from
     * @param coords
     * @return
     * @throws IOException
     */
    public List<Coords> coordsToBaiduMapllAll(CoordsSystem from, List<Coords> coords) throws IOException {
        //如果支持，采用非官方转换方式
        if (CoordsSystem.GC02.equals(from) || CoordsSystem.BD09LL.equals(from) || CoordsSystem.WGS84.equals(from)) {
            return coordsToBaiduMapUnofficial(from, coords);
        }

        List<Coords> result = new ArrayList<>();
        Queue<Coords> coordsQueue = new LinkedList<>();
        coordsQueue.addAll(coords);
        List<Coords> temp = new ArrayList<>();
        while (true) {
            Coords poll = coordsQueue.poll();
            if (poll != null)
                temp.add(poll);
            if (poll == null || temp.size() == 100) {
                List<Coords> coords1 = coordsToBaiduMapLL(from, temp);
                temp.clear();
                if (CollectionUtil.isNotEmpty(coords1)) {
                    result.addAll(coords1);
                }
            }
            if (poll == null) {
                break;
            }
        }

        result.forEach(coo -> {
            coo.setLongitude(coo.getX());
            coo.setLatitude(coo.getY());
        });

        return result;
    }


    /**
     * 将坐标转换为百度坐标系，list的数量不能大于100
     *
     * @param from
     * @param coords
     * @return
     * @throws IOException
     */
    private List<Coords> coordsToBaiduMapLL(CoordsSystem from, List<Coords> coords) throws IOException {
        if (CollectionUtil.isEmpty(coords)) {
            return null;
        }
        Map<String, String> param = new LinkedHashMap<>();
        String coordsStr = null;
        if (coords.size() == 1) {
            param.put("coords", coords.get(0).getLongitude().concat(",").concat(coords.get(0).getLatitude()));
        } else {
            // 根据百度地图的要求，多个坐标点用;隔开
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < coords.size(); i++) {
                stringBuffer.append(coords.get(i).getLongitude()).append(",").append(coords.get(i).getLatitude());
                if (i != coords.size() - 1) {
                    stringBuffer.append(";");
                }
            }
            param.put("coords", stringBuffer.toString());
        }
        param.put("ak", BD_AK);
        param.put("from", from.value.toString());
        param.put("to", CoordsSystem.BD09LL.value.toString());
        param.put("output", "json");
        //SN必须最后一个放入到map
        param.put("sn", SN(BD_GEO_COV_URL, param));
        String paramStr = HttpUtil.toParams(param);
        Request request = new Request.Builder()
                .get()
                .url(BAIDU_BASE_URL + BD_GEO_COV_URL + "?" + paramStr)
                .build();
        Call call = okHttpClient.newCall(request);
        Response execute = call.execute();
        String string = execute.body().string();
        BaiduMapGeocovResult baiduMapResult = JSONUtil.toBean(string, BaiduMapGeocovResult.class);
        return baiduMapResult.getResult();
    }


    public List<Coords> baiduMapllToGC02All(List<Coords> coords) {
        return coordsToGC02All("baidu", coords);
    }

    public List<Coords> wgs84ToGC02All(List<Coords> coords) {
        return coordsToGC02All("gps", coords);
    }

    /**
     * 将百度坐标系的坐标转换为国测坐标系。list大小无限制
     *
     * @param coords
     * @return
     * @throws IOException
     */
    public List<Coords> coordsToGC02All(String coordType, List<Coords> coords) {
        try {
            List<Coords> result = new ArrayList<>();
            Queue<Coords> coordsQueue = new LinkedList<>();
            coordsQueue.addAll(coords);

            List<Coords> temp = new ArrayList<>();
            while (true) {
                Coords poll = coordsQueue.poll();
                if (poll != null)
                    temp.add(poll);
                if (poll == null || temp.size() == 40) {
                    List<Coords> coords1 = coordsToGC02(coordType, temp);
                    temp.clear();
                    if (CollectionUtil.isNotEmpty(coords1)) {
                        result.addAll(coords1);
                    }
                }
                if (poll == null) {
                    break;
                }
            }
            return result;
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * 百度经纬度坐标系到国测02坐标系,list的大小不能大于40
     *
     * @param coords
     * @return
     */
    private List<Coords> coordsToGC02(String coordType, List<Coords> coords) throws IOException {
        List<Coords> result = new ArrayList<>();
        if (CollectionUtil.isEmpty(coords)) {
            return null;
        }
        Map<String, String> param = new LinkedHashMap<>();
        String coordsStr = null;
        if (coords.size() == 1) {
            param.put("locations", coords.get(0).getLongitude().concat(",").concat(coords.get(0).getLatitude()));
        } else {
            // 根据百度地图的要求，多个坐标点用;隔开
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < coords.size(); i++) {
                stringBuffer.append(coords.get(i).getLongitude()).append(",").append(coords.get(i).getLatitude());
                if (i != coords.size() - 1) {
                    stringBuffer.append("|");
                }
            }
            param.put("locations", stringBuffer.toString());
        }
        param.put("key", GD_KEY);
        param.put("coordsys", coordType);
        param.put("output", "JSON");
        param.put("sig", GD_SIG);
        String paramStr = HttpUtil.toParams(param);
        Request request = new Request.Builder()
                .get()
                .url(GAODE_BASE_URL + GD_GEO_COV_URL + "?" + paramStr)
                .build();
        Call call = okHttpClient.newCall(request);
        Response execute = call.execute();
        String string = execute.body().string();
        JSONObject jsonObject = JSONUtil.parseObj(string);
        String status = jsonObject.getStr("status");
        if (!"1".equals(status)) {
            return null;
        }
        String locations = jsonObject.getStr("locations");
        String[] split = locations.split(";");
        for (String coord : split) {
            String[] coordxy = coord.split(",");
            String x = coordxy[0];
            String y = coordxy[1];
            Coords resu = new Coords();
            resu.setLatitude(y);
            resu.setLongitude(x);
            result.add(resu);
        }
        return result;
    }

    /**
     * 进行逆地理编码。
     *
     * @param coords
     * @return
     * @throws IOException
     */
    public static BaiduMapReverseGeoCodingResult getReverseGeoCoding(@NotNull Coords coords, CoordsSystem coordsSystem) throws IOException {
        String key = coords.getLatitude() + "," + coords.getLongitude();
        BaiduMapReverseGeoCodingResult result = null;
        String coordsName = "gcj02ll";
        if (CoordsSystem.BD09LL.equals(coordsSystem)) {
            coordsName = "bd09ll";
        }

        Map<String, String> param = new LinkedHashMap<>();
        param.put("ak", BD_AK);
        param.put("output", "json");
        param.put("coordtype", coordsName);// 百度大地坐标系
        param.put("location", key);
        //SN必须最后一个放入到map
        param.put("sn", SN(BD_REVERSE_GEOCODINGS_URL, param));
        String paramStr = HttpUtil.toParams(param);
        Request request = new Request.Builder()
                .get()
                .url(BAIDU_BASE_URL + BD_REVERSE_GEOCODINGS_URL + "?" + paramStr)
                .build();
        Call call = okHttpClient.newCall(request);
        Response execute = call.execute();
        String string = execute.body().string();
        result = JSONUtil.toBean(string, BaiduMapReverseGeoCodingResult.class);
        return result;
    }


    /**
     * 采用环卫最佳策略规划一条路线
     *
     * @param start
     * @param end
     */
    public static AmapDrvierResult directionDriving(List<Coords> start, Coords end) {

        try {
            Map<String, Object> param = new HashMap<>();

            String coordsStr = null;
            if (start.size() == 1) {
                param.put("origin", start.get(0).getLongitude().concat(",").concat(start.get(0).getLatitude()));
            } else {
                // 根据百度地图的要求，多个坐标点用;隔开
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < start.size(); i++) {
                    stringBuffer.append(start.get(i).getLongitude()).append(",").append(start.get(i).getLatitude());
                    if (i != start.size() - 1) {
                        stringBuffer.append("|");
                    }
                }
                param.put("origin", stringBuffer.toString()); //出发点
            }
            param.put("destination", end.getLongitude().concat(",").concat(end.getLatitude())); //目标点
            param.put("strategy", 13); //环卫导航策略采用不走高速策略
            param.put("ferry", 1); //环卫导航不能使用轮渡
            param.put("key", GD_KEY);
            param.put("output", "JSON");
            param.put("sig", GD_SIG);
            String paramStr = HttpUtil.toParams(param);
            Request request = new Request.Builder()
                    .get()
                    .url(GAODE_BASE_URL + GD_DRIVER_URL + "?" + paramStr)
                    .build();
            Call call = okHttpClient.newCall(request);
            Response response = call.execute();
            String string = response.body().string();
            AmapDrvierResult amapDrvierResult = JSONUtil.toBean(string, AmapDrvierResult.class);
            amapDrvierResult.formatResult();
            return amapDrvierResult;
        } catch (Exception e) {
            log.error("从高德地图中获取路线规划失败", e);
        }

        return null;
    }

    public static Coords amapGeo(String address, String city) {
        if (city == null) {
            city = "100000";
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("address", address);
            param.put("city", city);
            param.put("key", GD_KEY);
            param.put("output", "JSON");
            param.put("sig", GD_SIG);

            String paramStr = HttpUtil.toParams(param);
            Request request = new Request.Builder()
                    .get()
                    .url(GAODE_BASE_URL + GD_GEO_URL + "?" + paramStr)
                    .build();
            Call call = okHttpClient.newCall(request);
            Response response = null;
            response = call.execute();
            String string = response.body().string();
            JSONObject jsonObject = JSONUtil.parseObj(string);
            String status = jsonObject.getStr("status");
            if (!"1".equals(status)) {
                return null;
            }
            JSONArray geocodes = jsonObject.getJSONArray("geocodes");
            if (CollectionUtil.isEmpty(geocodes)){
                return null;
            }
            JSONObject geoCode = (JSONObject) geocodes.get(0);
            String location = geoCode.getStr("location");
            if (StringUtil.isNotBlank(location)){
                String[] split = location.split(",");
                Coords coords=new Coords();
                coords.setLongitude(split[0]);
                coords.setLatitude(split[1]);
                return coords;
            }
        } catch (Exception e) {
            log.error("高德地理编码失败",e);
        }
        return null;

    }


    /**
     * 进行逆地理编码。
     *
     * @param coords
     * @return
     * @throws IOException
     */
    public static BaiduMapReverseGeoCodingResult getReverseGeoCoding(@NotNull Coords coords) throws IOException {

        return getReverseGeoCoding(coords, CoordsSystem.GC02);
//        String key=coords.getLatitude()+","+coords.getLongitude();
//        BaiduMapReverseGeoCodingResult result = null;
//
//        Map<String,String> param=new LinkedHashMap<>();
//        param.put("ak", BD_AK);
//        param.put("output","json");
//        param.put("coordtype","gcj02ll");// 百度大地坐标系
//        param.put("location",key);
//        //SN必须最后一个放入到map
//        param.put("sn",SN(BD_REVERSE_GEOCODINGS_URL,param));
//        String paramStr = HttpUtil.toParams(param);
//        Request request=new Request.Builder()
//                .get()
//                .url(BAIDU_BASE_URL + BD_REVERSE_GEOCODINGS_URL +"?"+paramStr)
//                .build();
//        Call call = okHttpClient.newCall(request);
//        Response execute = call.execute();
//        String string = execute.body().string();
//        result=JSONUtil.toBean(string,BaiduMapReverseGeoCodingResult.class);
//        return result;
    }


    /**
     * 获取带有一根线的百度地图静态图片
     *
     * @param coords
     * @return
     * @throws IOException
     */
    public InputStream getLineBaiduStaticImage(List<Coords> coords) throws IOException {


        List<Coords> result = filterByLevel(coords);

        List<List<Coords>> coordListArray = new ArrayList<>();
        coordListArray.add(result);
        return getLinesBaiduStaticImage(coordListArray);

    }

    /**
     * 获取一张带有多跟线的地图图片
     *
     * @param coords
     * @return
     * @throws IOException
     */
    private InputStream getLinesBaiduStaticImage(List<List<Coords>> coords) throws IOException {
        List<Coords> coordsList = new ArrayList<>();
        for (List<Coords> list : coords) {
            coordsList.addAll(list);
        }
        List<Coords> bboxByCoords = getBboxByCoords(coordsList);
        if (bboxByCoords == null || bboxByCoords.size() < 2) {
            throw new RuntimeException("无法确定地图边界");
        }

        String minLatitude = bboxByCoords.get(0).getLatitude();
        String minLongitude = bboxByCoords.get(0).getLongitude();
        String maxLatitude = bboxByCoords.get(1).getLatitude();
        String maxLongitude = bboxByCoords.get(1).getLongitude();
        //地图上所有的线，多段线用| 隔开，每个点用 ;隔开
        String paths = "";
        //地图上所有的点，多个点用| 隔开
        String markers = "";

        String labels = "";
        String labelStyles = "";

        for (int i = 0; i < coords.size(); i++) {
            List<Coords> list = coords.get(i);
            String tempPath = "";
            for (int j = 0; j < list.size(); j++) {
                Coords coords1 = list.get(j);
                tempPath = tempPath.concat(coords1.getLongitude()).concat(",").concat(coords1.getLatitude());
                if (j != list.size() - 1) {
                    tempPath = tempPath.concat(";");
                }
            }
            if (i != coords.size() - 1) {
                tempPath = tempPath.concat("|");
            }
            paths = paths.concat(tempPath);
            if (list.size() > 1) {
                if (StringUtil.isNotBlank(labels)) {
                    labels = labels.concat("|");
                }

                if (StringUtil.isNotBlank(labelStyles)) {
                    labelStyles = labelStyles.concat("|");
                }

                labels = labels.concat(list.get(0).getLongitude().concat(",").concat(list.get(0).getLatitude()));
                labelStyles = labelStyles.concat("起点,1,15,0xffffff,0x990000,1");
                labels = labels.concat("|");
                labelStyles = labelStyles.concat("|");
                labels = labels.concat(list.get(list.size() - 1).getLongitude().concat(",").concat(list.get(list.size() - 1).getLatitude()));
                labelStyles = labelStyles.concat("终点,1,15,0xffffff,0x990000,1");

            }

        }
        for (int i = 0; i < coordsList.size(); i++) {
            Coords coords1 = coordsList.get(i);
            markers = markers.concat(coords1.getLongitude()).concat(",").concat(coords1.getLatitude());
            if (i != coordsList.size() - 1) {
                markers = markers.concat("|");
            }
        }

        Map<String, String> param = new LinkedHashMap<>();
        param.put("ak", BD_JAVASCRIPT_AK);
        param.put("width", "1024");// 图片的宽
        param.put("height", "1024");// 图片的高
//        param.put("center","118.756855,31.976915"); 指定中心点，根据业务需求来判断。是需要
        param.put("zoom", "");// zoom 和 bbox 只能传1个。如果两个都传，bbox会失效。如果要使bbox生效，需要将zoom设置为空
        param.put("copyright", "1");//版权样式
        param.put("dpiType", "ph"); //使用高分辨率地图
        param.put("coordtype", "bd09ll");// 百度大地坐标系
        param.put("bbox", minLongitude.concat(",").concat(minLatitude).concat(";").concat(maxLongitude).concat(maxLatitude));//地图边界，指示图片中地图的范围，分别是最低经度，最低纬度，最高经度，最高纬度。得到到方形就是整个地图的范围。注意，如果使用这种方式来确定地图边界，zoom参数需要设置为空，center参数会无效
        param.put("paths", paths);//地图中的线，每个点之间以; 进行分割。每根线之间用|进行分割
        param.put("pathStyles", "0x14a184,3,1");//线的样式
//        param.put("markers",markers);// 地图中的点，每个点之间用| 进行分割
//        param.put("markerStyles","s,A,0x76777a");//点的样式


        param.put("labels", labels);//点的样式
        param.put("labelStyles", labelStyles);//点的样式
        String paramStr = HttpUtil.toParams(param);
        Request request = new Request.Builder()
                .get()
                .url(BAIDU_BASE_URL + BD_STATIC_IMAGE_URL + "?" + paramStr)
                .build();
        Call call = okHttpClient.newCall(request);
        Response execute = call.execute();
        return new ByteArrayInputStream(execute.body().bytes());
    }

    /**
     * 通过坐标列表计算图片的地图边界，返回一个list，list里面固定为两个坐标，分别为图片最西南边角和东北边角的经纬度
     *
     * @param coordsList
     * @return
     */
    private List<Coords> getBboxByCoords(List<Coords> coordsList) {
        // 计算思路：取最小X坐标，最小Y坐标，最大X坐标，最大Y坐标
        if (CollectionUtil.isEmpty(coordsList)) {
            throw new RuntimeException("无法确定地图边界");
        }

        Double minX = null;
        Double minY = null;
        Double maxX = null;
        Double maxY = null;
        List<Coords> list = new ArrayList<>();
        for (Coords coords : coordsList) {
            // 如果为第一次进来，先确定一个初始的边界值，以第一个坐标点为中心 东西 82米，南北115米
            if (minX == null & minY == null & maxX == null & maxY == null) {
                minX = Double.parseDouble(coords.getLongitude()) - 0.002d;
                minY = Double.parseDouble(coords.getLatitude()) - 0.002d;
                maxX = Double.parseDouble(coords.getLongitude()) + 0.002d;
                maxY = Double.parseDouble(coords.getLatitude()) + 0.002d;
                continue;
            }
            //不断寻找最低纬度，最低经度，最高纬度，最高经度
            double lng = Double.parseDouble(coords.getLongitude());
            double lat = Double.parseDouble(coords.getLatitude());
            if (lng < minX) {
                minX = lng;
            }
            if (lng > maxX) {
                maxX = lng;
            }
            if (lat < minY) {
                minY = lat;
            }
            if (lat > maxY) {
                maxY = lat;
            }
        }
        //minX 和 minY 能确定地图最西南的点，将这个点向外再次扩大。防止因为边界太小导致地图标记被切割。maxX和maxY 为地图最东北的点，也向外扩大。
        minX -= 0.004d;
        minY -= 0.004d;
        maxX += 0.004d;
        maxY += 0.004d;
        Coords southwest = new Coords();
        Coords northeast = new Coords();
        //有了地图最西南和最东北的点，地图就可以确定一个边界了
        southwest.setLongitude(minX.toString());
        southwest.setLatitude(minY.toString());
        northeast.setLongitude(maxX.toString());
        northeast.setLatitude(maxY.toString());


        list.add(southwest);
        list.add(northeast);
        return list;
    }


    // 对Map内所有value作utf8编码，拼接返回结果，用于SN计算
    private static String toQueryString(Map<?, ?> data)
            throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Map.Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append(URLEncoder.encode((String) pair.getValue(),
                    "UTF-8") + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }

    /**
     * 计算百度地图的SN值
     *
     * @param urlPrefx
     * @param param
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String SN(String urlPrefx, Map<String, String> param) throws UnsupportedEncodingException {
        //计算SN
        String queryString = toQueryString(param);
        String whoStr = new String(urlPrefx + "?" + queryString + BD_SK);
        String tempStr = URLEncoder.encode(whoStr, "UTF-8");
        return MD5.create().digestHex(tempStr);
    }


    /**
     * 判断点是否在多边形内，如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
     *
     * @param point 检测点
     * @param pts   多边形的顶点
     * @return 点在多边形内返回true, 否则返回false
     */
    public static boolean IsPtInPoly(Point2D.Double point, List<Point2D.Double> pts) {

        int N = pts.size();
        boolean boundOrVertex = true; //如果点位于多边形的顶点或边上，也算做点在多边形内，直接返回true
        int intersectCount = 0;//cross points count of x
        double precision = 2e-10; //浮点类型计算时候与0比较时候的容差
        Point2D.Double p1, p2;//neighbour bound vertices
        Point2D.Double p = point; //当前点

        p1 = pts.get(0);//left vertex
        for (int i = 1; i <= N; ++i) {//check all rays
            if (p.equals(p1)) {
                return boundOrVertex;//p is an vertex
            }

            p2 = pts.get(i % N);
            if (p.x < Math.min(p1.x, p2.x) || p.x > Math.max(p1.x, p2.x)) {
                p1 = p2;
                continue;
            }

            if (p.x > Math.min(p1.x, p2.x) && p.x < Math.max(p1.x, p2.x)) {
                if (p.y <= Math.max(p1.y, p2.y)) {
                    if (p1.x == p2.x && p.y >= Math.min(p1.y, p2.y)) {
                        return boundOrVertex;
                    }

                    if (p1.y == p2.y) {
                        if (p1.y == p.y) {
                            return boundOrVertex;
                        } else {//before ray
                            ++intersectCount;
                        }
                    } else {
                        double xinters = (p.x - p1.x) * (p2.y - p1.y) / (p2.x - p1.x) + p1.y;
                        if (Math.abs(p.y - xinters) < precision) {
                            return boundOrVertex;
                        }

                        if (p.y < xinters) {
                            ++intersectCount;
                        }
                    }
                }
            } else {
                if (p.x == p2.x && p.y <= p2.y) {
                    Point2D.Double p3 = pts.get((i + 1) % N);
                    if (p.x >= Math.min(p1.x, p3.x) && p.x <= Math.max(p1.x, p3.x)) {
                        ++intersectCount;
                    } else {
                        intersectCount += 2;
                    }
                }
            }
            p1 = p2;
        }

        if (intersectCount % 2 == 0) {//偶数在多边形外
            return false;
        } else { //奇数在多边形内
            return true;
        }
    }


    /**
     *
     */
    public static Map<String, Double> getLatAndLngByAddress(String addr) {
        Map<String, String> param = new LinkedHashMap<>();
        param.put("ak", "jZN3TRHr54DWNhRU8BqhFWZE");
        param.put("query", addr);
        param.put("region", "全国");
        param.put("output", "json");
        String paramStr = HttpUtil.toParams(param);

        String url = BAIDU_BASE_URL + BD_PLACE_TO_COORD_URL + "?" + paramStr;
        URL myURL = null;
        URLConnection httpsConn = null;
        //进行转码
        try {
            myURL = new URL(url);
        } catch (MalformedURLException e) {

        }
        StringBuffer sb = new StringBuffer();
        try {
            httpsConn = (URLConnection) myURL.openConnection();
            if (httpsConn != null) {
                InputStreamReader insr = new InputStreamReader(
                        httpsConn.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(insr);
                String data = null;
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                ;
                while ((data = br.readLine()) != null) {
                    sb.append(data);
                }
                insr.close();
            }
        } catch (IOException e) {

        }
        Map<String, Double> map = new HashMap<String, Double>();
        JSONObject resultJson = JSONUtil.parseObj(sb.toString());
        //resultJson  {"message":"ok","results":[{"uid":"30e1d0bb0c0014f8b6147fe6","name":"攀枝花市","location":{"lng":101.725544,"lat":26.588034}}],"status":0}
        JSONArray jsonArray = (JSONArray) resultJson.get("results");
        JSONObject results0Obj = (JSONObject) jsonArray.get(0);
        JSONObject locationObj = (JSONObject) results0Obj.get("location");
        //纬度
        Double lat = (Double) locationObj.get("lat");
        //经度
        Double lng = (Double) locationObj.get("lng");
        map.put("lat", lat);
        map.put("lng", lng);
        return map;
    }

    /**
     * 判断点是否在指定区域内，true-在区域内。false-不在区域内
     * 需要注意的是 传入的 list 这个点集必须排好序，如果这个点集是乱序的话，那么绘制出来的多边形也是乱的！ 切记！
     *
     * @param point
     * @param polygon
     * @return
     */
    public static boolean pointInArea(Point2D.Double point, List<Point2D.Double> polygon) {

        if (null == polygon || polygon.isEmpty()) { // 不存在区域坐标点，无法绘制区域
            return false;
        } else if (polygon.size() < 3) { // 区域坐标点数小于3， 无法绘制区域
            return false;
        } else {
            GeneralPath p = new GeneralPath();
            // 获取坐标区域的第一个点
            Point2D.Double first = polygon.get(0);
            // 绘图 GeneralPath 路径的开始
            p.moveTo(first.x, first.y);
            // 第一个点已经绘制完成，remove掉
            polygon.remove(0);

            for (Point2D.Double d : polygon) {
                // 按坐标连线
                p.lineTo(d.x, d.y);
            }
            // 将区域闭合
            p.lineTo(first.x, first.y);
            // 绘图结束
            p.closePath();
            // 判断 该点是否在绘制的图形内
            return p.contains(point);
        }
    }

//public static void main(String[] args){
////    System.out.println(getLatAndLngByAddress("江西省抚州市黎川县").toString());
//    Point2D.Double point = new Point2D.Double();
//    point.x=118.764711542024;
//    point.y=31.962623288906;
//    List<Point2D.Double> pointList = new ArrayList<>();
//    Point2D.Double point1 = new Point2D.Double();
//    point1.x=118.759742202547;
//    point1.y=31.964890582325;
//    Point2D.Double point2 = new Point2D.Double();
//    point2.x=118.759882236254;
//    point2.y=31.958761368909;
//    Point2D.Double point3 = new Point2D.Double();
//    point3.x=118.771292076456;
//    point3.y=31.958557218624;
//    Point2D.Double point4 = new Point2D.Double();
//    point4.x=118.771440344224;
//    point4.y=31.964681407487;
//    pointList.add(point1);
//    pointList.add(point2);
//    pointList.add(point3);
//    pointList.add(point4);
//    System.out.println(pointInArea(point,pointList));
//}

    /*************************************************************静态图筛点算法  start ****************************************************************/


    /**
     * 将传入的静态点进行筛选，筛选到不到170个。计算方法：
     * 1.遍历每一个点（跳过第一个和最后一个点），先判断遍历到的点距离上一个有效的坐标点是否大于100米，如果大于100米，则当前点为有效点，否则遍历到的点无效。（如果筛选结果小于170，则直接当作最终结果）
     * 2.将上一步筛选的结果遍历（跳过第一个和最后一个点），判断被遍历到的点，到前一个<span>有效点</span>和后一个点形成的夹角是否大于175°。如果小于，则此点为有效点，否则置为无效点（如果筛选结果小于170，则直接当作最终结果）
     * 3.如果步骤1 和步骤2 的结果都大于170.则重复开始第一步，不过要将判断的有效坐标点最小距离加100米加到200米，夹角大小从175度减去5 到170°。以此类推，最终有效坐标点最小距离大于3000米的时候并且夹角小于100°的时候，视为筛选失败！！
     * <p>
     * 4.如果上面的步骤筛选失败，进行下面步骤
     * <p>
     * 5.遍历每一个点（跳过第一个和最后一个点），先判断遍历到的点距离上一个有效的坐标点是否大于100米，如果大于100米，则当前点为有效点，否则遍历到的点无效。（如果筛选结果小于170，则直接当作最终结果）
     * <span> 注意下面一步和上面第二步的区别，下面这步判断的是”上一个点“。上面第二部判断的是”上一个有效点“。下面这种失真会比较严重 </span>
     * 6.将上一步筛选的结果遍历（跳过第一个和最后一个点），判断被遍历到的点，到前一个<span>点</span>和后一个点形成的夹角是否大于175°。如果小于，则此点为有效点，否则置为无效点（如果筛选结果小于170，则直接当作最终结果）
     * 7.如果步骤1 和步骤2 的结果都大于170.则重复开始第一步，不过要将判断的有效坐标点最小距离加100米加到200米，夹角大小从175度减去5 到170°。以此类推，最终有效坐标点最小距离大于3000米的时候并且夹角小于100°的时候，视为筛选失败！！
     * <p>
     * 8.如果上面步骤都筛选失败，则对集合进行平均取点，平均取170个点！
     *
     * @param coords
     * @return
     */
    private static List<Coords> filterByLevel(List<Coords> coords) {
        final Integer maxPointCnt = 170;

        double minLine = 100.0;
        double maxangle = 175;


        double minangle = 100;
        double maxline = 3000.0;

        while (true) {
            //线最多到3000米，大于3000米以上的点均认为是有效点。点最低到110度。低于110度的点均认为是有效点（可以自己拿个量角器看看110度的角有多明显）
            if (minLine > maxline && minangle < maxangle) {
                break;
            }
            // 筛选各个点到前一个点的距离，小于minLine的都认为是无效点。返回值是筛选后的结果
            List<Coords> result = filterLineByDistance(coords, minLine);
            if (result.size() <= maxPointCnt) {
                return result;
            }
            // 筛选各个点到前一个”有效点“的距离，大于maxangle 的都认为是无效点。返回值是筛选后的结果
            result = filterLineByMaxAngleByLast(result, maxangle);

            if (result.size() <= maxPointCnt) {
                return result;
            }
            // 如果筛选不掉，则minLine 加100米。最高角度减去5
            minLine = minLine + 100.0;
            maxangle = maxangle - 5;
        }

        minLine = 0.0;
        maxangle = 175;
        minangle = 100;
        maxline = 5000.0;


        while (true) {
            //线最多到3000米，大于3000米以上的点均认为是有效点。点最低到110度。低于110度的点均认为是有效点（可以自己拿个量角器看看110度的角有多明显）
            if (minLine > maxline && minangle < maxangle) {
                break;
            }
            // 筛选各个点到前一个点的距离，小于minLine的都认为是无效点。返回值是筛选后的结果
            List<Coords> result = filterLineByDistance(coords, minLine);
            if (result.size() <= maxPointCnt) {
                return result;
            }
            // 筛选各个点到前一个”点“的距离，大于maxangle 的都认为是无效点。返回值是筛选后的结果
            result = filterLineByMaxAngleByNear(coords, maxangle);

            if (result.size() <= maxPointCnt) {
                return result;
            }
            // 如果筛选不掉，则minLine 加100米。最高角度减去5

            minLine = minLine + 100.0;
            maxangle = maxangle - 5;
        }

        // 下面是平均取点的逻辑


        Integer setBy = coords.size() / (maxPointCnt - 2);

        int curIndex = setBy;

        List<Coords> result = new ArrayList<>();
        result.add(coords.get(0));
        while (curIndex <= coords.size()) {
            result.add(coords.get(curIndex));
            curIndex += setBy;
        }
        result.add(coords.get(coords.size() - 1));
        return result;
    }

    /**
     * 通过有效长度来筛选点（即：如果两个点距离太近的话，认为他们是同一个点）
     *
     * @param coords
     * @param minLine
     * @return
     */
    public static List<Coords> filterLineByDistance(List<Coords> coords, double minLine) {

        List<Coords> resu = new ArrayList<>();
        //最后一个有效点
        Coords last = null;
        for (int i = 0; i < coords.size(); i++) {
            //当前点
            Coords coords1 = coords.get(i);
            if (last == null) {
                last = coords1;
                resu.add(coords1);
                continue;
            }

            String latitude = coords1.getLatitude();
            String longitude = coords1.getLongitude();
            String latitude1 = last.getLatitude();
            String longitude1 = last.getLongitude();
            // 判断最后一个有效点到当前点的距离 单位：米
            double distance = CommonUtil.getDistance(Double.parseDouble(longitude), Double.parseDouble(latitude), Double.parseDouble(longitude1), Double.parseDouble(latitude1));
            if (distance > minLine) {
                resu.add(coords1);//有效的数据才能放入集合
                last = coords1;
            }
        }

        return resu;
    }


    /**
     * 通过上一个有效点的有效角度来筛选点（即：如果某个点和前后两个有效点形成的夹角太大，则认为他本身就是直线）
     * 180度的角是直线。接近180度的点当作180度来处理
     *
     * @param coords   坐标点集合
     * @param maxAngle 最大角度，如果大于这个角度，则当作180度来处理
     * @return
     */
    public static List<Coords> filterLineByMaxAngleByLast(List<Coords> coords, double maxAngle) {
        List<Coords> resu = new ArrayList<>();

        //最后一个有效点
        Coords c1 = null;

        for (int i = 0; i < coords.size(); i++) {
            //当前点
            Coords cur = coords.get(i);
            if (i == 0 || i == (coords.size() - 1)) {
                resu.add(cur);
                c1 = cur;
                continue;
            }
//            Coords coords1 = coords.get(i + 1);
            // 下一个点
            Coords coords3 = coords.get(i - 1);


            double latitude = Double.parseDouble(cur.getLatitude());
            double longitude = Double.parseDouble(cur.getLongitude());
            double latitude1 = Double.parseDouble(c1.getLatitude());
            double longitude1 = Double.parseDouble(c1.getLongitude());
            double latitude3 = Double.parseDouble(coords3.getLatitude());
            double longitude3 = Double.parseDouble(coords3.getLongitude());
            // 计算当前点和前后点形成的三角形当前点所在的角的角度
            double angle = CommonUtil.get_angle(longitude, latitude, longitude1, latitude1, longitude3, latitude3);
            if (angle < maxAngle) {
                resu.add(cur);
                c1 = cur;
            }
        }
        return resu;
    }

    /**
     * 通过上一个点的有效角度来筛选点（即：如果某个点和前后两个有效点形成的夹角太大，则认为他本身就是直线）
     * 180度的角是直线。接近180度的点当作180度来处理
     *
     * @param coords   坐标点集合
     * @param maxAngle 最大角度，如果大于这个角度，则当作180度来处理
     * @param coords
     * @param maxAngle
     * @return
     */
    public static List<Coords> filterLineByMaxAngleByNear(List<Coords> coords, double maxAngle) {
        List<Coords> resu = new ArrayList<>();

        for (int i = 0; i < coords.size(); i++) {
            //当前点
            Coords cur = coords.get(i);
            //不判断第一个和最后一个点
            if (i == 0 || i == (coords.size() - 1)) {
                resu.add(cur);

                continue;
            }
            //上一个点
            Coords coords1 = coords.get(i + 1);
            //下一个点
            Coords coords3 = coords.get(i - 1);


            double latitude = Double.parseDouble(cur.getLatitude());
            double longitude = Double.parseDouble(cur.getLongitude());
            double latitude1 = Double.parseDouble(coords1.getLatitude());
            double longitude1 = Double.parseDouble(coords1.getLongitude());
            double latitude3 = Double.parseDouble(coords3.getLatitude());
            double longitude3 = Double.parseDouble(coords3.getLongitude());
            // 计算当前点和前后点形成的三角形当前点所在的角的角度
            double angle = CommonUtil.get_angle(longitude, latitude, longitude1, latitude1, longitude3, latitude3);
            if (angle <= maxAngle) {
                resu.add(cur);
            }
        }
        return resu;
    }

/*************************************************************静态图筛点算法  end ****************************************************************/


}
