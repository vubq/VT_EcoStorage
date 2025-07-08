<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState, FormRules } from 'naive-ui'
import { NButton, NSpace } from 'naive-ui'
import { Add } from '@vicons/ionicons5'
import { ProductService } from '@/service/api/product-service'
import { useBoolean } from '@/hooks'

const formRef = ref()
const rules: FormRules = {
  name: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
}
const { bool: visible, setTrue: openModal, setFalse: hideModal } = useBoolean(false)
const tableRef = ref()
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'id',
  sortDesc: true,
})
const unitId = ref<string>('')
const columns = ref<DataTableColumns<Unit.Data>>([
  {
    title: 'ID',
    align: 'center',
    key: 'id',
    width: '300',
    render: (row) => {
      return (
        <NButton
          secondary
          type="primary"
          strong
          onClick={() => getUnit(row.id!)}
        >
          {row.id}
        </NButton>
      )
    },
  },
  {
    title: 'Tên',
    align: 'center',
    key: 'name',
    width: '300',
  },
  {
    title: 'Mô tả',
    align: 'center',
    key: 'description',
  },
  {
    title: 'Ghi chú',
    align: 'center',
    key: 'note',
  },
])
const columnsRef = ref<DataTableColumns<User.Data>>(columns.value)
const listUnit = ref<Unit.Data[]>([])
const unit = ref<Unit.Data>({
  name: '',
  status: 'ACTIVE',
})
const totalRecords = ref<number>(0)

function sortDefault(columnKey: string) {
  if (dataTableRequest.value.sortBy !== columnKey) {
    return false
  }
  return dataTableRequest.value.sortDesc ? 'descend' : 'ascend'
}

async function changePage(page: number, size: number) {
  dataTableRequest.value.currentPage = page
  dataTableRequest.value.perPage = size
  await getListUnit()
}

async function getListUnit() {
  await ProductService.getListUnit(dataTableRequest.value)
    .then((res: any) => {
      listUnit.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getListUnit()
}

async function reloadTable() {
  await getListUnit()
}

async function getUnit(id: string) {
  await ProductService.getUnit(id)
    .then((res: any) => {
      unit.value = res.data
      openModal()
    })
}

async function createOrUpdateUnit() {
  formRef.value?.validate(async (errors: any) => {
    if (!errors) {
      await ProductService.createOrUpdateUnit(unit.value)
        .then((res: any) => {
          if (res.isSuccess) {
            hideModal()
            reloadTableFirst()
          }
        })
    }
  })
}

function sortTable(sorter: DataTableSortState) {
  columnsRef.value.forEach((column: any) => {
    if (column.key === sorter.columnKey) {
      column.sortOrder = sorter.order
    }
    else {
      column.sortOrder = false
    }
  })
  sortData(sorter)
}

function sortData(sorter: DataTableSortState) {
  if (!sorter.order) {
    dataTableRequest.value.sortBy = ''
    dataTableRequest.value.sortDesc = false
  }
  else {
    dataTableRequest.value.sortBy = sorter.columnKey.toString()
    dataTableRequest.value.sortDesc = sorter.order === 'descend'
  }
  reloadTable()
}

onMounted(() => {
  getListUnit()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card>
      <n-form ref="formRef" :model="dataTableRequest" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="Tìm kiếm" path="filter">
            <n-input v-model:value="dataTableRequest.filter" placeholder="Từ khóa..." />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" secondary @click="reloadTableFirst()">
              <template #icon>
                <icon-park-outline-search />
              </template>
              Tìm kiếm
            </NButton>
            <NButton strong secondary @click="reloadTableFirst()">
              <template #icon>
                <icon-park-outline-redo />
              </template>
            </NButton>
          </n-flex>
        </n-flex>
      </n-form>
    </n-card>
    <n-card>
      <NSpace vertical size="large">
        <div class="flex gap-4">
          <NButton
            strong
            type="primary"
            secondary
            class="ml-a"
            @click="() => {
              unit.id = '',
              unit.name = '',
              unit.status = 'ACTIVE'
              openModal()
            }"
          >
            <NIcon size="18" :component="Add" style="margin-right: 5px;" />Thêm
          </NButton>
        </div>
        <n-data-table ref="tableRef" :columns="columns" :data="listUnit" @update:sorter="sortTable" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
    <n-modal
      v-model:show="visible"
      :mask-closable="false"
      preset="card"
      :title="unit.id ? 'Sửa' : 'Thêm'"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form label-placement="left" :model="unit" label-align="left" :label-width="80">
        <n-form-item label="Tên" path="name">
          <n-input v-model:value="unit.name" placeholder="" />
        </n-form-item>
        <n-form-item label="Mô tả" path="description">
          <n-input v-model:value="unit.description" type="textarea" placeholder="" />
        </n-form-item>
        <n-form-item label="Ghi chú" path="note">
          <n-input v-model:value="unit.note" type="textarea" placeholder="" />
        </n-form-item>
      </n-form>
      <template #action>
        <NSpace justify="center">
          <NButton @click="hideModal()">
            Hủy
          </NButton>
          <NButton type="primary" @click="createOrUpdateUnit()">
            Lưu
          </NButton>
        </NSpace>
      </template>
    </n-modal>
  </NSpace>
</template>
