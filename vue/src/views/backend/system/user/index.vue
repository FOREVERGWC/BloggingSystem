<template>
  <div>
    <el-row>
      <el-col :span="24">
        <el-card>
          <el-row :gutter="20">
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-input v-model="queryParams.username" clearable placeholder="请输入用户名"/>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-input v-model="queryParams.name" clearable placeholder="请输入姓名"/>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-date-picker v-model='queryParams.birthday' clearable
                              placeholder='请选择生日' type='date'
                              value-format='YYYY-MM-DD HH:mm:ss'/>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-select v-model='queryParams.status' clearable filterable placeholder="请选择状态">
                <el-option v-for='item in statusList' :key='item.value' :label='item.label' :value='item.value'/>
              </el-select>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-select v-model='queryParams.role' clearable filterable placeholder="请选择角色">
                <el-option v-for='item in roleList' :key='item.id' :label='item.name' :value='item.id'/>
              </el-select>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-input v-model="queryParams.phone" clearable placeholder="请输入电话"/>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-input v-model="queryParams.email" clearable placeholder="请输入邮箱"/>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-input v-model="queryParams.loginIp" clearable placeholder="请输入最后登录IP"/>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-date-picker v-model='queryParams.loginTime' clearable
                              placeholder='请选择最后登录时间' type='date'
                              value-format='YYYY-MM-DD HH:mm:ss'/>
            </el-col>
            <el-col :lg="2" :md="2" :sm="12" :xl="2" :xs="12">
              <el-button icon="Search" plain type="info" @click="getPage">查询</el-button>
            </el-col>
            <el-col :lg="2" :md="2" :sm="12" :xl="2" :xs="12">
              <el-button icon="Refresh" plain type="warning" @click="resetQuery">
                重置
              </el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-row>
      <el-col :span="24">
        <el-card>
          <el-row :gutter="20">
            <el-col :lg="2" :md="2" :sm="12" :xl="2" :xs="12">
              <el-button icon="Plus" plain type="primary" @click="showAdd">新增</el-button>
            </el-col>
            <el-col :lg="2" :md="2" :sm="12" :xl="2" :xs="12">
              <el-button :disabled="single" icon="Edit" plain type="success" @click="showEdit">
                修改
              </el-button>
            </el-col>
            <el-col :lg="2" :md="2" :sm="12" :xl="2" :xs="12">
              <el-popconfirm title="确认删除选中数据吗？" @confirm="handleDelete(null)">
                <template #reference>
                  <el-button :disabled="multiple" icon="Delete" plain type="danger">
                    删除
                  </el-button>
                </template>
              </el-popconfirm>
            </el-col>
            <el-col :lg="2" :md="2" :sm="12" :xl="2" :xs="12">
              <el-button icon="Download" plain @click="handleExport">导出</el-button>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <el-table v-loading="loading" :cell-style="{ textAlign: 'center' }" :data="userList"
                :header-cell-style="{ textAlign: 'center' }" stripe
                @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"/>
        <el-table-column type="expand">
          <template v-slot="{ row }">
            <el-descriptions :column="3" border>
              <el-descriptions-item>
                <template #label>
                  <span>
                    <el-icon>
                      <male/>
                    </el-icon>
                    性别
                  </span>
                </template>
                <span>{{ row.genderText }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <span>
                    <el-icon>
                      <key/>
                    </el-icon>
                    微信小程序开放ID
                  </span>
                </template>
                <span>{{ row.openId }}</span>
              </el-descriptions-item>
              <el-descriptions-item>
                <template #label>
                  <span>
                    <el-icon>
                      <wallet/>
                    </el-icon>
                    余额
                  </span>
                </template>
                <span>{{ row.balance }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </template>
        </el-table-column>
        <el-table-column label="序号" type="index" width="70"/>
        <el-table-column label="用户名" prop="username"/>
        <el-table-column label="昵称" prop="nickname"/>
        <el-table-column label="姓名" prop="name"/>
        <el-table-column label="头像">
          <template v-slot="{ row }">
            <div style="display: flex; align-items: center; justify-content: center">
              <el-image v-if="row.avatar" :preview-src-list="[getUrl(row.avatar)]" :src="getUrl(row.avatar)"
                        preview-teleported>
                <template #error>
                  <img alt="" src="../../../../assets/imgs/profile.png"/>
                </template>
              </el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="生日" width="90">
          <template v-slot="{ row }">
            {{ formatDate(row.birthday) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template v-slot="{ row }">
            <el-switch v-model="row.status" active-value="1" inactive-value="0" @change="() => handleStatus(row.id)"/>
          </template>
        </el-table-column>
        <el-table-column label="角色">
          <template v-slot="{ row }">
            <el-tag type="info" v-for="item in row.roleList">
              {{ item.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="电话" prop="phone"/>
        <el-table-column label="邮箱" prop="email"/>
        <el-table-column label="最后登录IP" prop="loginIp"/>
        <el-table-column label="最后登录时间" prop="loginTime" width="150"/>
        <el-table-column label="操作" width="280">
          <template v-slot="{ row }">
            <el-button icon="EditPen" @click="showAssign(row)">分配</el-button>
            <el-button icon="Edit" plain type="primary" @click="showEdit(row)">编辑</el-button>
            <el-popconfirm title="确认删除该行吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button icon="Delete" plain style="margin-left: 10px" type="danger">
                  删除
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          :current-page="queryParams.pageNo"
          :page-size="queryParams.pageSize"
          :page-sizes="[20, 30, 40, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange">
      </el-pagination>
    </el-card>

    <el-dialog :title="form.title" v-model="form.visible" destroy-on-close width="40%">
      <el-form ref="formRef" :model="form.data" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.data.username" autocomplete="new"/>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.data.password" autocomplete="new"/>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.data.name" autocomplete="new"/>
        </el-form-item>
        <el-form-item label="头像">
          <AvatarUpload v-model="form.data.avatar"/>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.data.gender" clearable filterable placeholder="请选择性别">
            <el-option v-for="item in genderList" :key="item.value" :label='item.label' :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker v-model='form.data.birthday' placeholder='请选择生日'
                          type='date'
                          value-format='YYYY-MM-DD HH:mm:ss'/>
        </el-form-item>
        <el-form-item v-if="form.data.id" label="状态" prop="status">
          <el-select v-model="form.data.status" clearable filterable placeholder="请选择状态">
            <el-option v-for="item in statusList" :key="item.value" :label='item.label' :value="item.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.data.phone" autocomplete="new"/>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.data.email" autocomplete="new"/>
        </el-form-item>
        <el-form-item label="微信小程序开放ID">
          <el-input v-model="form.data.openId" autocomplete="new"/>
        </el-form-item>
        <el-form-item label="余额" prop="balance">
          <el-input v-model="form.data.balance" autocomplete="new"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.data.remark" :rows="5" autocomplete="new" type="textarea"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="form.visible = false">取 消</el-button>
        <el-button type="primary" @click="handleSave">确 定</el-button>
      </template>
    </el-dialog>

    <RoleAssign :id="assignForm.userId" :visible="assignForm.visible" @update:visible="assignForm.visible = $event"
                @refresh="getPage"/>
  </div>
</template>

<script setup>
import RoleAssign from './components/RoleAssign.vue';
import {computed, nextTick, onMounted, reactive, ref, toRaw} from 'vue'
import {getUserOne, getUserPage, handleStatusUser, removeUserBatchByIds, saveUser} from '@/api/user.js'
import {ElMessage} from "element-plus"
import {downloadFile, formatDate, genderList, statusList} from "@/utils/common.js";
import useRoleStore from "@/store/modules/role.js";

const roleStore = useRoleStore();

const loading = ref(true)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  username: '',
  name: '',
  gender: '',
  birthday: '',
  status: '',
  role: '',
  phone: '',
  email: '',
  openId: '',
  balance: null,
  loginIp: '',
  loginTime: ''
})
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const roleList = ref(roleStore.roleList)
const userList = ref([])
const total = ref(0)
const form = ref({
  visible: false,
  title: '',
  data: {}
})
const assignForm = ref({
  userId: '',
  visible: false
})
const formRef = ref(null)
const rules = {
  username: [{required: true, message: '请输入用户名', trigger: 'blur'}],
  password: [{required: true, message: '请输入密码', trigger: 'blur'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}],
  phone: [{required: true, message: '请输入电话', trigger: 'blur'}],
  email: [{required: true, message: '请输入邮箱', trigger: 'blur'}],
  balance: [{required: true, message: '请输入余额', trigger: 'blur'}]
}

const getPage = () => {
  loading.value = true
  getUserPage(queryParams).then(res => {
    userList.value = res.data?.records || []
    total.value = res.data?.total || 0
    loading.value = false
  })
}

const showAdd = () => {
  nextTick(() => {
    if (!formRef.value) return
    formRef.value.resetFields()
  })
  form.value = {
    visible: true,
    title: '添加用户信息',
    data: {
      username: '',
      password: '',
      name: '',
      avatar: '',
      birthday: '',
      status: '',
      phone: '',
      email: '',
      openId: '',
      balance: null,
      remark: ''
    }
  }
}

const showEdit = (row) => {
  nextTick(() => {
    if (!formRef.value) return
    formRef.value.resetFields()
  })
  const params = {id: row.id || ids.value[0]}
  getUserOne(params).then(res => {
    if (res.code !== 200) return
    form.value = {
      visible: true,
      title: '编辑用户信息',
      data: {
        ...res.data
      }
    }
  })
}

const showAssign = (row) => {
  assignForm.value.userId = row.id;
  assignForm.value.visible = true;
};

const handleSave = () => {
  formRef.value.validate(valid => {
    if (!valid) return
    saveUser(form.value.data).then(res => {
      if (res.code !== 200) {
        ElMessage.error(res.msg)
        return
      }
      ElMessage.success('保存成功！')
      form.value.visible = false
    }).finally(() => {
      getPage()
    })
  })
}

const handleDelete = (id) => {
  const params = id || ids.value
  removeUserBatchByIds(params).then(res => {
    if (res.code !== 200) {
      ElMessage.error(res.msg)
      return
    }
    ElMessage.success('删除成功！')
  }).finally(() => {
    getPage()
  })
}

const handleStatus = (id) => {
  handleStatusUser(id).then(res => {
    if (res.code !== 200) {
      ElMessage.error(res.msg)
    } else {
      ElMessage.success('操作成功！')
      getPage()
    }
  })
}

const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => toRaw(item).id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

const resetQuery = () => {
  queryParams.pageNo = 1
  queryParams.pageSize = 20
  queryParams.username = ''
  queryParams.name = ''
  queryParams.status = ''
  queryParams.role = ''
  queryParams.phone = ''
  queryParams.email = ''
  queryParams.openId = ''
  queryParams.balance = null
  queryParams.loginIp = ''
  queryParams.loginTime = ''
  getPage()
}

const handleExport = () => {
  downloadFile('/user/export', queryParams)
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getPage()
}

const handleCurrentChange = (val) => {
  queryParams.pageNo = val
  getPage()
}

onMounted(() => {
  getPage()
})

const getUrl = computed(() => (path) => {
  return import.meta.env.VITE_APP_BASE_API + path
})
</script>

<style lang="scss" scoped>
.el-select, :deep(.el-date-editor) {
  width: 100%;
}

.el-col {
  margin-bottom: 8px;

  .el-button {
    width: 100%;
  }
}

.el-image, .el-image img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.el-pagination {
  margin-top: 8px;
}
</style>
