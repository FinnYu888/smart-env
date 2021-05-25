package com.ai.apac.smartenv.common.constant;

/**
 * Copyright: Copyright (c) 2020 Asiainfo
 *
 * @ClassName: AddressConstant
 * @Description:
 * @version: v1.0.0
 * @author: panfeng
 * @date: 2020/3/4
 * Modification History:
 * Date               Author          Version            Description
 * --------------------------------------------------------------------*
 * 2020/3/4  10:30    panfeng          v1.0.0             修改原因
 */
public interface AddressConstant {

    String BUCKET = "smartenv";

    interface ExportStatus {

        Integer EXPORTING = 1;
        Integer EXPORTED = 2;
        Integer EXPORT_FIELD = 3;


    }

    interface MongoDBTable {
        String GIS_INFO = "gis_info";
    }

}
