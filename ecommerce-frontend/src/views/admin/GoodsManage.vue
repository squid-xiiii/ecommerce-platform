<template>
  <div class="goods-manage">
    <div class="page-header">
      <h2 class="page-title">商品管理</h2>
      <el-button type="primary" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 添加商品
      </el-button>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索商品名称或规格"
        style="width: 300px"
        clearable
        @clear="loadGoods"
        @keyup.enter="loadGoods"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>

      <el-radio-group v-model="filterCategory" @change="loadGoods">
        <el-radio-button value="">全部</el-radio-button>
        <el-radio-button value="笔记本电脑">笔记本电脑</el-radio-button>
        <el-radio-button value="手机">手机</el-radio-button>
        <el-radio-button value="配件">配件</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 商品列表 -->
    <el-table :data="filteredGoodsList" stripe style="width: 100%">
      <el-table-column prop="goodsId" label="商品编号" width="120" />
      <el-table-column prop="goodsInfo" label="商品名称" min-width="250" show-overflow-tooltip />
      <el-table-column prop="category" label="分类" width="120" />
      <el-table-column prop="price" label="价格" width="120">
        <template #default="{ row }">¥{{ formatPrice(row.price) }}</template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="100" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="openEditDialog(row)">编辑</el-button>
          <el-button type="danger" link @click="deleteGoods(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑弹窗（保持不变） -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="商品编号" prop="goodsId">
          <el-input v-model="form.goodsId" :disabled="!!editId" />
        </el-form-item>
        <el-form-item label="商品名称" prop="goodsInfo">
          <el-input v-model="form.goodsInfo" type="textarea" rows="2" />
        </el-form-item>
        <el-form-item label="规格信息" prop="specificationsInfo">
          <el-input v-model="form.specificationsInfo" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" style="width: 100%">
            <el-option label="笔记本电脑" value="笔记本电脑" />
            <el-option label="手机" value="手机" />
            <el-option label="配件" value="配件" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="广告信息" prop="adInfo">
          <el-input v-model="form.adInfo" placeholder="可选" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveGoods">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import { adminGoodsApi } from '@/api'

const goodsList = ref([])
const filterCategory = ref('')
const searchKeyword = ref('')
const dialogVisible = ref(false)
const dialogTitle = ref('')
const editId = ref(null)
const formRef = ref()

// 过滤后的商品列表（先按分类，再按关键词搜索）
const filteredGoodsList = computed(() => {
  let result = goodsList.value

  // 按分类筛选
  if (filterCategory.value) {
    result = result.filter(g => g.category === filterCategory.value)
  }

  // 按关键词搜索
  if (searchKeyword.value && searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.trim().toLowerCase()
    result = result.filter(g =>
      g.goodsInfo.toLowerCase().includes(keyword) ||
      g.specificationsInfo.toLowerCase().includes(keyword)
    )
  }

  return result
})

const form = ref({
  goodsId: null,
  goodsInfo: '',
  specificationsInfo: '',
  category: '',
  price: 0,
  stock: 0,
  adInfo: ''
})

const rules = {
  goodsId: [{ required: true, message: '请输入商品编号', trigger: 'blur' }],
  goodsInfo: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

const formatPrice = (price) => {
  return (price / 100).toFixed(2)
}

const loadGoods = async () => {
  try {
    goodsList.value = await adminGoodsApi.getList()
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

const openAddDialog = () => {
  editId.value = null
  dialogTitle.value = '添加商品'
  form.value = {
    goodsId: null,
    goodsInfo: '',
    specificationsInfo: '',
    category: '',
    price: 0,
    stock: 0,
    adInfo: ''
  }
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  editId.value = row.id
  dialogTitle.value = '编辑商品'
  form.value = {
    ...row,
    goodsId: row.goodsId,
    price: row.price / 100
  }
  dialogVisible.value = true
}

const saveGoods = async () => {
  await formRef.value.validate()

  const data = {
    ...form.value,
    price: Math.round(form.value.price * 100)
  }

  try {
    if (editId.value) {
      await adminGoodsApi.update(editId.value, data)
      ElMessage.success('更新成功')
    } else {
      await adminGoodsApi.create(data)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadGoods()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteGoods = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除商品 "${row.goodsInfo}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await adminGoodsApi.delete(row.id)
    ElMessage.success('删除成功')
    loadGoods()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadGoods()
})
</script>

<style scoped>
.goods-manage {
  background: white;
  border-radius: 12px;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  margin: 0;
  color: #333;
}

.search-bar {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
  align-items: center;
  flex-wrap: wrap;
}
</style>
