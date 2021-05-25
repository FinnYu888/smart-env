/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2020/3/2 下午2:24:59                           */
/*==============================================================*/


drop table if exists ai_kpi_catalog;

drop table if exists ai_kpi_def;

drop table if exists ai_kpi_target;

drop table if exists ai_kpi_target_detail;

drop table if exists ai_kpi_tpl_band;

drop table if exists ai_kpi_tpl_catalog;

drop table if exists ai_kpi_tpl_def;

drop table if exists ai_kpi_tpl_detail;

drop table if exists ai_staff_kpi_ins;

drop table if exists ai_staff_kpi_ins_detail;

/*==============================================================*/
/* Table: ai_kpi_catalog                                        */
/*==============================================================*/
create table ai_kpi_catalog
(
   id                   bigint(20) not null,
   catalog_name         varchar(64) not null comment '考核指标名称',
   remark               varchar(255) comment 'kpi描述',
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-正常
            2-禁用',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID',
   primary key (id)
);

alter table ai_kpi_catalog comment '考核指标分类';

/*==============================================================*/
/* Table: ai_kpi_def                                            */
/*==============================================================*/
create table ai_kpi_def
(
   id                   bigint(20) not null,
   kpi_catalog          bigint(20) not null comment '指标分类',
   kpi_name             varchar(20) not null comment '考核指标名称',
   kpi_remark           varchar(255) comment 'kpi描述',
   appraisal_criteria   varchar(255) comment '评分标准',
   weighting            decimal(2,2) comment '参考权重，单位是百分比',
   score_type           int comment '1-人工考核
            2-自动考核',
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-正常
            2-禁用',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID',
   primary key (id)
);

alter table ai_kpi_def comment '考核指标定义表';

/*==============================================================*/
/* Table: ai_kpi_target                                         */
/*==============================================================*/
create table ai_kpi_target
(
   id                   bigint(20) not null,
   target_name          varchar(255) not null comment '考核指标名称',
   kpi_tpl_id           bigint(20) not null,
   station_id           varchar(255),
   start_time           datetime,
   end_time             datetime,
   grader_id            bigint(64),
   remark               varchar(255) comment 'kpi描述',
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-正常
            2-禁用',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID',
   primary key (id)
);

/*==============================================================*/
/* Index: idx_kpi_tpl_id                                        */
/*==============================================================*/
create index idx_kpi_tpl_id on ai_kpi_target
(
   kpi_tpl_id
);

/*==============================================================*/
/* Table: ai_kpi_target_detail                                  */
/*==============================================================*/
create table ai_kpi_target_detail
(
   id                   bigint(20) not null,
   target_id            bigint(20) not null comment '考核指标名称',
   staff_id             bigint(20) not null,
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-正常
            2-禁用',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID',
   primary key (id)
);

/*==============================================================*/
/* Table: ai_kpi_tpl_band                                       */
/*==============================================================*/
create table ai_kpi_tpl_band
(
   id                   bigint(20) not null,
   kpi_tpl_id           varchar(20) not null comment '考核指标名称',
   band                 varchar(10),
   min_score            int,
   max_score            int,
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-正常',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID',
   primary key (id)
);

/*==============================================================*/
/* Index: idx_band_tpl_id                                       */
/*==============================================================*/
create index idx_band_tpl_id on ai_kpi_tpl_band
(
   kpi_tpl_id
);

/*==============================================================*/
/* Table: ai_kpi_tpl_catalog                                    */
/*==============================================================*/
create table ai_kpi_tpl_catalog
(
   id                   bigint(20) not null,
   catalog_name         varchar(64) not null comment '考核指标名称',
   remark               varchar(255) comment 'kpi描述',
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-正常
            2-禁用',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID',
   primary key (id)
);

alter table ai_kpi_tpl_catalog comment '考核模板分类';

/*==============================================================*/
/* Table: ai_kpi_tpl_def                                        */
/*==============================================================*/
create table ai_kpi_tpl_def
(
   id                   bigint(20) not null,
   kpi_tpl_name         varchar(20) not null comment '考核指标名称',
   kpi_tpl_catalog_id   bigint(20),
   score_type           int comment '1-百分制
            2-十分制
            3-5分制',
   remark               varchar(255) comment '描述',
   total_score          int,
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-正常',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID',
   primary key (id)
);

alter table ai_kpi_tpl_def comment '考核模板定义表';

/*==============================================================*/
/* Index: idx_catalogAndTplName                                 */
/*==============================================================*/
create index idx_catalogAndTplName on ai_kpi_tpl_def
(
   kpi_tpl_name,
   kpi_tpl_catalog_id
);

/*==============================================================*/
/* Index: idx_kpi_tpl_catalog_id                                */
/*==============================================================*/
create index idx_kpi_tpl_catalog_id on ai_kpi_tpl_def
(
   kpi_tpl_catalog_id
);

/*==============================================================*/
/* Table: ai_kpi_tpl_detail                                     */
/*==============================================================*/
create table ai_kpi_tpl_detail
(
   id                   bigint(20) not null,
   kpi_tpl_id           bigint(20) not null comment '考核指标名称',
   kpi_id               bigint(20) not null comment '考核指标名称',
   kpi_remark           varchar(255) comment 'kpi描述',
   appraisal_criteria   varchar(255) comment '评分标准',
   weighting            decimal(2,2) comment '参考权重，单位是百分比',
   sort                 int comment '排序',
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-正常',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID',
   primary key (id)
);

alter table ai_kpi_tpl_detail comment '考核模板明细';

/*==============================================================*/
/* Index: idx_kpiTplIdAndKpiId                                  */
/*==============================================================*/
create index idx_kpiTplIdAndKpiId on ai_kpi_tpl_detail
(
   kpi_tpl_id,
   kpi_id
);

/*==============================================================*/
/* Table: ai_staff_kpi_ins                                      */
/*==============================================================*/
create table ai_staff_kpi_ins
(
   id                   bigint(20) not null,
   kpi_target_id        bigint(20) not null,
   staff_id             bigint(20) not null comment '员工ID',
   start_time           datetime not null comment '考核开始时间',
   end_time             datetime not null comment '考核开始时间',
   score_time           datetime comment '评分时间',
   total_score          int comment '得分',
   scorer               bigint(20),
   grade                varchar(20),
   staff_remark         varchar(255),
   manager_remark       varchar(255),
   kpi_result           varchar(10),
   dead_line            datetime not null comment '生成实例的时候就填写，从系统中读取配置,考核结束时间+N天',
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-待考核
            2-已评分
            3-已发布
            4-已结束
            ',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID',
   primary key (id)
);

alter table ai_staff_kpi_ins comment '考核实例表，存放每个人的kpi';

/*==============================================================*/
/* Index: idx_kpi_target_id                                     */
/*==============================================================*/
create index idx_kpi_target_id on ai_staff_kpi_ins
(
   kpi_target_id
);

/*==============================================================*/
/* Index: idx_staff_id                                          */
/*==============================================================*/
create index idx_staff_id on ai_staff_kpi_ins
(
   staff_id
);

/*==============================================================*/
/* Table: ai_staff_kpi_ins_detail                               */
/*==============================================================*/
create table ai_staff_kpi_ins_detail
(
   id                   bigint(20) not null,
   kpi_ins_id           bigint(20) not null comment '考核实例ID',
   kpi_tpl_detail_id    bigint(20) not null,
   score                int comment '参考权重，单位是百分比',
   staff_remark         varchar(255),
   manager_remark       varchar(255),
   create_user          bigint(20) comment '创建操作员',
   create_time          datetime comment '创建时间',
   update_user          bigint(20) comment '更新操作员',
   update_time          datetime comment '更新时间',
   status               int comment '1-正常',
   is_deleted           int comment '0-未删除
            1-已删除',
   tenant_id            varchar(12) comment '租户ID'
);

alter table ai_staff_kpi_ins_detail comment '员工考核实例明细';

/*==============================================================*/
/* Index: idx_kpi_ins_id                                        */
/*==============================================================*/
create index idx_kpi_ins_id on ai_staff_kpi_ins_detail
(
   kpi_ins_id
);

