<template>
  <basic-container>
    <avue-crud :option="option"
               :table-loading="loading"
               :data="data"
               :page="page"
               :permission="permissionList"
               :before-open="beforeOpen"
               v-model="form"
               ref="crud"
               @row-update="rowUpdate"
               @row-save="rowSave"
               @row-del="rowDel"
               @search-change="searchChange"
               @search-reset="searchReset"
               @selection-change="selectionChange"
               @current-change="currentChange"
               @size-change="sizeChange"
               @on-load="onLoad">
      <template slot="menuLeft">
        <el-button type="danger"
                   size="small"
                   icon="el-icon-delete"
                   plain
                   v-if="permission.facilityext_delete"
                   @click="handleDelete">删 除
        </el-button>
      </template>
    </avue-crud>
  </basic-container>
</template>

<script>
  import {getList, getDetail, add, update, remove} from "@/api/facility/facilityext";
  import {mapGetters} from "vuex";

  export default {
    data() {
      return {
        form: {},
        query: {},
        loading: true,
        page: {
          pageSize: 10,
          currentPage: 1,
          total: 0
        },
        selectionList: [],
        option: {
          height:'auto',
          calcHeight: 350,
          tip: false,
          border: true,
          index: true,
          viewBtn: true,
          selection: true,
          column: [
            {
              label: "设施扩展属性id",
              prop: "facilityExtId",
              rules: [{
                required: true,
                message: "请输入设施扩展属性id",
                trigger: "blur"
              }]
            },
            {
              label: "设施id",
              prop: "facilityId",
              rules: [{
                required: true,
                message: "请输入设施id",
                trigger: "blur"
              }]
            },
            {
              label: "车,人,设施,物资,设备",
              prop: "attrId",
              rules: [{
                required: true,
                message: "请输入车,人,设施,物资,设备",
                trigger: "blur"
              }]
            },
            {
              label: "属性名称",
              prop: "attrName",
              rules: [{
                required: true,
                message: "请输入属性名称",
                trigger: "blur"
              }]
            },
            {
              label: "属性值ID",
              prop: "attrValueId",
              rules: [{
                required: true,
                message: "请输入属性值ID",
                trigger: "blur"
              }]
            },
            {
              label: "扩展属性值序列",
              prop: "attrValueSeq",
              rules: [{
                required: true,
                message: "请输入扩展属性值序列",
                trigger: "blur"
              }]
            },
            {
              label: "属性值",
              prop: "attrValue",
              rules: [{
                required: true,
                message: "请输入属性值",
                trigger: "blur"
              }]
            },
            {
              label: "属性显示值",
              prop: "attrDisplayValue",
              rules: [{
                required: true,
                message: "请输入属性显示值",
                trigger: "blur"
              }]
            },
            {
              label: "创建信息组织",
              prop: "createOrgId",
              rules: [{
                required: true,
                message: "请输入创建信息组织",
                trigger: "blur"
              }]
            },
            {
              label: "创建信息操作员",
              prop: "createOpId",
              rules: [{
                required: true,
                message: "请输入创建信息操作员",
                trigger: "blur"
              }]
            },
            {
              label: "创建/修改信息组织",
              prop: "orgId",
              rules: [{
                required: true,
                message: "请输入创建/修改信息组织",
                trigger: "blur"
              }]
            },
            {
              label: "创建/修改信息操作员",
              prop: "opId",
              rules: [{
                required: true,
                message: "请输入创建/修改信息操作员",
                trigger: "blur"
              }]
            },
            {
              label: "创建时间",
              prop: "createDate",
              rules: [{
                required: true,
                message: "请输入创建时间",
                trigger: "blur"
              }]
            },
            {
              label: "创建/修改时间",
              prop: "doneDate",
              rules: [{
                required: true,
                message: "请输入创建/修改时间",
                trigger: "blur"
              }]
            },
            {
              label: "状态",
              prop: "state",
              rules: [{
                required: true,
                message: "请输入状态",
                trigger: "blur"
              }]
            },
          ]
        },
        data: []
      };
    },
    computed: {
      ...mapGetters(["permission"]),
      permissionList() {
        return {
          addBtn: this.vaildData(this.permission.facilityext_add, false),
          viewBtn: this.vaildData(this.permission.facilityext_view, false),
          delBtn: this.vaildData(this.permission.facilityext_delete, false),
          editBtn: this.vaildData(this.permission.facilityext_edit, false)
        };
      },
      ids() {
        let ids = [];
        this.selectionList.forEach(ele => {
          ids.push(ele.id);
        });
        return ids.join(",");
      }
    },
    methods: {
      rowSave(row, loading, done) {
        add(row).then(() => {
          loading();
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!"
          });
        }, error => {
          done();
          console.log(error);
        });
      },
      rowUpdate(row, index, loading, done) {
        update(row).then(() => {
          loading();
          this.onLoad(this.page);
          this.$message({
            type: "success",
            message: "操作成功!"
          });
        }, error => {
          done();
          console.log(error);
        });
      },
      rowDel(row) {
        this.$confirm("确定将选择数据删除?", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            return remove(row.id);
          })
          .then(() => {
            this.onLoad(this.page);
            this.$message({
              type: "success",
              message: "操作成功!"
            });
          });
      },
      handleDelete() {
        if (this.selectionList.length === 0) {
          this.$message.warning("请选择至少一条数据");
          return;
        }
        this.$confirm("确定将选择数据删除?", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            return remove(this.ids);
          })
          .then(() => {
            this.onLoad(this.page);
            this.$message({
              type: "success",
              message: "操作成功!"
            });
            this.$refs.crud.toggleSelection();
          });
      },
      beforeOpen(done, type) {
        if (["edit", "view"].includes(type)) {
          getDetail(this.form.id).then(res => {
            this.form = res.data.data;
          });
        }
        done();
      },
      searchReset() {
        this.query = {};
        this.onLoad(this.page);
      },
      searchChange(params) {
        this.query = params;
        this.onLoad(this.page, params);
      },
      selectionChange(list) {
        this.selectionList = list;
      },
      selectionClear() {
        this.selectionList = [];
        this.$refs.crud.toggleSelection();
      },
      currentChange(currentPage){
        this.page.currentPage = currentPage;
      },
      sizeChange(pageSize){
        this.page.pageSize = pageSize;
      },
      onLoad(page, params = {}) {
        this.loading = true;
        getList(page.currentPage, page.pageSize, Object.assign(params, this.query)).then(res => {
          const data = res.data.data;
          this.page.total = data.total;
          this.data = data.records;
          this.loading = false;
          this.selectionClear();
        });
      }
    }
  };
</script>

<style>
</style>
