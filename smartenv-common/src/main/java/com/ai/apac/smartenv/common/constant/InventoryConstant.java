package com.ai.apac.smartenv.common.constant;

public interface InventoryConstant {
    public static String SPLIT_COMMA = ",";//分隔符
    interface ExceptionMsg {
        String CODE = "exception_message";
        String KEY_RESTYPE_NAME = "inventory000001";//资源类型已经存在
        String KEY_RESSPEC_NAME = "inventory000002";//资源规格已经存在
        String KEY_RESTYPE_DEL = "inventory000003";//资源类型下有绑定的规格，请先删除对应的规格
        String KEY_RESSPEC_DEL = "inventory000004";//该规格有绑定的物品资源，不能删除
        String KEY_RESINFO_ADD = "inventory000005";//添加物品列表不能为空
        String KEY_RECORD_MODIFY = "inventory000006";//要修改的记录不存在
        String KEY_RESINFO_AMOUNT = "inventory000007";//修改的库存数量与原数量相同
        String KEY_RESAPPLY_DTL = "inventory000008";//物资申请列表不能为空
        String KEY_RESAPPLY_ERROR = "inventory000009";//物资申请审批流程启动失败，请重新提交
        String KEY_RESAPPLY_DELIEVERY = "inventory000010";//申请单审批后才能领取
        String KEY_INVENTORY_AMOUNT = "inventory000011";//库存数量不足
        String KEY_INVENTORY_ORDER = "inventory000012";//你查询的订单不存在
        String KEY_INVENTORY_CANCEL = "inventory000013";//当前订单不允许取消

    }
    //物资管理状态
    interface ResManageStatus {
        String CODE = "ResManageStatus";
        String NORMAL = "1";//正常状态
    }
    //物资管理业务类型
    interface ResBusinessType {
        String CODE = "ResBusinessType";
        String PUTIN_STORAGE = "100001";//物资入库
        String MODIFY_STORAGE = "100002";//库存数量修改
        String RES_APPLY = "100003";//物资申请
        String RES_DELIEVERY = "100004";//物资申请
    }
    //物资仓库
    interface StorageName {
        String CODE = "StorageName";
        String LOCAL_STORAGE = "1";//本地仓库
        String OTHER_STORAGE = "2";//其它仓库
    }

    //订单状态
    interface Order_Status {
        public static String CODE = "Order_Status";
        public static int CANCEL = 0;//撤单
        public static int SUBMIT = 1;//提交
        public static int APPROVE = 2;//待审批
        public static int APPROVE_AGREE = 3;//审批通过
        public static int APPROVE_REFUSED = 4;//审批拒绝
        public static int RECEIVE = 5;//待领取
        public static int FINISH = 6;//已完成
    }
    //流程实例
    interface Flow_Key {
        public static String RES_APPLY_FLOW = "ResApplyFlow";//物资申请流程
        public static String RES_FLOW_START = "Application";//物流申请提交
        public static String RES_FLOW_APPLY = "Apply";//物流申请审批节点
        public static String RES_FLOW_DELIVERY = "Delivery";//仓库提货节点
        public static String RES_APPLY_APPLYRESULT = "applyResult";//审批结果
        public static String RES_APPLY_APPLYREMARK = "applyRemark";//审批结果
        public static String RES_APPLY_ORDERID = "orderId";//申请订单号
        public static String RES_APPLY_ASSIGNID = "assignmentId";//处理人id
        public static String RES_APPLY_ASSIGNNAME = "assignmentName";//处理人名称
    }

    // 物资入库导入模板
    interface ResInventoryExcelImportIndex {
        int RESOURCE_SOURECE = 0;
        int PURCHASING_AGENT = 1;
        int PURCHASING_DATE = 2;
        int STORAGE_NAME = 3;
        int RES_SPEC_NAME = 4;
        int AMOUNT = 5;
        int UNIT_PRICE = 6;
        int REMARK = 7;
    }
    //物资来源
    interface ResourceSource {
        public static String CODE = "resource_source";
    }

    String OSS_BUCKET_NAME = "smartenv";
    String DICT_IMPORT_EXCEL_MODEL = "import_excel_model";
    String DICT_IMPORT_EXCEL_MODEL_INVENTORY = "7";
    
}
