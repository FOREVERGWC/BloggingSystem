<template>
  <div>
    <el-row>
      <el-col :span="24">
        <el-card>
          <el-row :gutter="20">
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-input v-model="queryParams.title" clearable placeholder="请输入标题"/>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-select v-model='queryParams.categoryId' clearable filterable placeholder='请选择类别'>
                <el-option v-for='item in categoryList' :key='item.id' :label='item.name' :value='item.id'/>
              </el-select>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-input v-model="queryParams.content" clearable placeholder="请输入内容"/>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-select v-model='queryParams.userId' clearable filterable placeholder='请选择作者'>
                <el-option v-for='item in userList' :key='item.id' :label='item.name || item.username'
                           :value='item.id'/>
              </el-select>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-select v-model='queryParams.top' clearable placeholder='请选择是否置顶'>
                <el-option v-for='item in topList' :key='item.value' :label='item.label' :value='item.value'/>
              </el-select>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-select v-model='queryParams.visible' clearable placeholder='请选择可见性'>
                <el-option v-for='item in visibleList' :key='item.value' :label='item.label' :value='item.value'/>
              </el-select>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-select v-model='queryParams.commentable' clearable placeholder='请选择是否允许评论'>
                <el-option v-for='item in commentableList' :key='item.value' :label='item.label' :value='item.value'/>
              </el-select>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-select v-model='queryParams.status' clearable placeholder='请选择状态'>
                <el-option v-for='item in statusList' :key='item.value' :label='item.label' :value='item.value'/>
              </el-select>
            </el-col>
            <el-col :lg="4" :md="4" :sm="12" :xl="4" :xs="12">
              <el-date-picker v-model='queryParams.releaseTime' clearable
                              placeholder='请选择发布时间' type='date'
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
              <vue3-json-excel :fields="articleFields" :json-data="articleList" name='文章列表.xls'>
                <el-button icon="Download" plain>导出</el-button>
              </vue3-json-excel>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <el-table v-loading="loading" :cell-style="{ textAlign: 'center' }" :data="articleList"
                :header-cell-style="{ textAlign: 'center' }" stripe
                @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55"/>
        <el-table-column label="序号" type="index" width="70"/>
        <el-table-column label="标题">
          <template v-slot="{ row }">
            <el-link :href="`/detail/${row.id}`" :underline="false" target="_blank">
              <span>{{ row.title }}</span>
            </el-link>
          </template>
        </el-table-column>
        <el-table-column label="类别" prop="category.name" sortable/>
        <el-table-column label="作者">
          <template v-slot="{ row }">
            {{ row.user.name || row.user.username }}
          </template>
        </el-table-column>
        <el-table-column label="浏览量" prop="viewCount" sortable/>
        <el-table-column label="点赞量" prop="likeCount" sortable/>
        <el-table-column label="点踩量" prop="dislikeCount" sortable/>
        <el-table-column label="评论量" prop="commentCount" sortable/>
        <el-table-column label="收藏量" prop="collectionCount" sortable/>
        <el-table-column label="置顶">
          <template v-slot="{ row }">
            <el-switch v-model="row.top" @change="() => handleTop(row.id)"/>
          </template>
        </el-table-column>
        <el-table-column label="可见性" prop="visibleText"/>
        <el-table-column label="允许评论">
          <template v-slot="{ row }">
            <el-switch v-model="row.commentable" @change="() => handleComment(row.id)"/>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="statusText"/>
        <el-table-column label="发布时间" prop="releaseTime"/>
        <el-table-column label="操作" width="180">
          <template v-slot="{ row }">
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

    <el-dialog v-model="form.visible" :title="form.title" destroy-on-close width="80%">
      <el-form ref="formRef" :model="form.data" :rules="rules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.data.title" autocomplete="new"/>
        </el-form-item>
        <el-form-item label="类别" prop="categoryId">
          <el-select v-model='form.data.categoryId' clearable filterable placeholder='请选择类别'>
            <el-option v-for='item in categoryList' :key='item.id' :label='item.name' :value='item.id'/>
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <v-md-editor v-model="form.data.content" height="600px"/>
        </el-form-item>
        <!-- TODO 添加鉴权功能，只有管理员才可以修改四个数量 -->
        <el-form-item v-if="form.data.id" label="浏览量" prop="viewCount">
          <el-input v-model="form.data.viewCount" autocomplete="new"/>
        </el-form-item>
        <el-form-item v-if="form.data.id" label="点赞量" prop="likeCount">
          <el-input v-model="form.data.likeCount" autocomplete="new"/>
        </el-form-item>
        <el-form-item v-if="form.data.id" label="点踩量" prop="dislikeCount">
          <el-input v-model="form.data.dislikeCount" autocomplete="new"/>
        </el-form-item>
        <el-form-item v-if="form.data.id" label="评论量" prop="commentCount">
          <el-input v-model="form.data.commentCount" autocomplete="new"/>
        </el-form-item>
        <el-form-item v-if="form.data.id" label="收藏量" prop="collectionCount">
          <el-input v-model="form.data.collectionCount" autocomplete="new"/>
        </el-form-item>
        <el-form-item label="置顶" prop="top">
          <el-select v-model='form.data.top' clearable placeholder='请选择是否置顶'>
            <el-option v-for='item in topList' :key='item.value' :label='item.label' :value='item.value'/>
          </el-select>
        </el-form-item>
        <el-form-item label="可见性" prop="visible">
          <el-select v-model='form.data.visible' clearable placeholder='请选择可见性'>
            <el-option v-for='item in visibleList' :key='item.value' :label='item.label' :value='item.value'/>
          </el-select>
        </el-form-item>
        <el-form-item label="允许评论" prop="commentable">
          <el-select v-model='form.data.commentable' clearable placeholder='请选择是否允许评论'>
            <el-option v-for='item in commentableList' :key='item.value' :label='item.label' :value='item.value'/>
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.data.id" label='状态' prop='status'>
          <el-select v-model='form.data.status' clearable placeholder='请选择状态'>
            <el-option v-for='item in statusList' :key='item.value' :label='item.label' :value='item.value'/>
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间" prop="releaseTime" v-if="form.data.id">
          <el-date-picker v-model='form.data.releaseTime' placeholder='请选择发布时间'
                          type='date'
                          value-format='YYYY-MM-DD HH:mm:ss'/>
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
  </div>
</template>

<script setup>
import {computed, nextTick, onMounted, reactive, ref, toRaw} from 'vue'
import {
  getArticleOne,
  getArticlePage,
  handleCommentArticle,
  handleTopArticle,
  removeArticleBatchByIds,
  saveArticle
} from '@/api/article'
import {getArticleCategoryList} from '@/api/articleCategory'
import {getUserList} from '@/api/user'
import {ElMessage} from "element-plus"

const loading = ref(true)
const queryParams = reactive({
  pageNo: 1,
  pageSize: 20,
  title: '',
  categoryId: null,
  content: '',
  userId: null,
  viewCount: null,
  likeCount: null,
  dislikeCount: null,
  commentCount: null,
  collectionCount: null,
  top: null,
  visible: '',
  commentable: null,
  status: '',
  releaseTime: ''
})
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const categoryList = ref([])
const userList = ref([])
const articleList = ref([])
const total = ref(0)
const articleFields = {
  '序号': {
    field: 'id',
    callback: (id) => {
      const index = articleList.value.findIndex(item => item.id === id)
      return index + 1
    }
  },
  '主键ID': 'id',
  '标题': 'title',
  '类别ID': 'categoryId',
  '内容': 'content',
  '作者ID': 'userId',
  '浏览量': 'viewCount',
  '点赞量': 'likeCount',
  '点踩量': 'dislikeCount',
  '评论量': 'commentCount',
  '收藏量': 'collectionCount',
  '置顶': 'top',
  '可见性': 'visible',
  '允许评论': 'commentable',
  '状态': 'status',
  '发布时间': 'releaseTime'
}
const topList = [
  {label: '是', value: true},
  {label: '否', value: false}
]
const visibleList = [
  {label: '私有', value: '0'},
  {label: '公开', value: '1'}
]
const commentableList = [
  {label: '是', value: true},
  {label: '否', value: false}
]
const statusList = [
  {label: '未发布', value: '0'},
  {label: '已发布', value: '1'},
  {label: '定时发布', value: '2'}
]
const form = ref({
  visible: false,
  title: '',
  data: {}
})
const formRef = ref(null)
const rules = {
  title: [{required: true, message: '请输入标题', trigger: 'blur'}],
  categoryId: [{required: true, message: '请选择类别', trigger: 'change'}],
  content: [{required: true, message: '请输入内容', trigger: 'blur'}],
  userId: [{required: true, message: '请选择作者', trigger: 'change'}],
  viewCount: [{required: true, message: '请输入浏览量', trigger: 'blur'}],
  likeCount: [{required: true, message: '请输入点赞量', trigger: 'blur'}],
  dislikeCount: [{required: true, message: '请输入点踩量', trigger: 'blur'}],
  commentCount: [{required: true, message: '请输入评论量', trigger: 'blur'}],
  collectionCount: [{required: true, message: '请输入收藏量', trigger: 'blur'}],
  top: [{required: true, message: '请选择是否置顶', trigger: 'change'}],
  visible: [{required: true, message: '请选择可见性', trigger: 'change'}],
  commentable: [{required: true, message: '请选择是否允许评论', trigger: 'change'}],
  status: [{required: true, message: '请选择状态', trigger: 'change'}],
  releaseTime: [{required: true, message: '请选择发布时间', trigger: 'change'}]
}

const getPage = () => {
  loading.value = true
  getArticleCategoryList({}).then(res => {
    categoryList.value = res.data || []
  })
  getUserList({}).then(res => {
    userList.value = res.data || []
  })
  getArticlePage(queryParams).then(res => {
    articleList.value = res.data?.records || []
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
    title: '添加文章',
    data: {
      title: '',
      categoryId: null,
      content: '',
      userId: null,
      viewCount: null,
      likeCount: null,
      dislikeCount: null,
      commentCount: null,
      collectionCount: null,
      top: false,
      visible: '1',
      commentable: true,
      status: '',
      releaseTime: '',
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
  getArticleOne(params).then(res => {
    if (res.code !== 200) return
    form.value = {
      visible: true,
      title: '编辑文章',
      data: {
        ...res.data
      }
    }
  })
}

const handleSave = () => {
  formRef.value.validate(valid => {
    if (!valid) return
    saveArticle(form.value.data).then(res => {
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
  removeArticleBatchByIds(params).then(res => {
    if (res.code !== 200) {
      ElMessage.error(res.msg)
      return
    }
    ElMessage.success('删除成功！')
  }).finally(() => {
    getPage()
  })
}

const handleTop = (id) => {
  handleTopArticle(id).then(res => {
    if (res.code !== 200) {
      ElMessage.error(res.msg)
    } else {
      ElMessage.success('操作成功！')
      getPage()
    }
  })
}

const handleComment = (id) => {
  handleCommentArticle(id).then(res => {
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
  queryParams.title = ''
  queryParams.categoryId = null
  queryParams.content = ''
  queryParams.userId = null
  queryParams.viewCount = null
  queryParams.likeCount = null
  queryParams.dislikeCount = null
  queryParams.commentCount = null
  queryParams.collectionCount = null
  queryParams.top = null
  queryParams.visible = ''
  queryParams.commentable = null
  queryParams.status = ''
  getPage()
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

.el-pagination {
  margin-top: 8px;
}
</style>
