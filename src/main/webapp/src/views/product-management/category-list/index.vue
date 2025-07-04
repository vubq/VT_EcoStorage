<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState } from 'naive-ui'
import { NButton, NSpace } from 'naive-ui'
import { Add } from '@vicons/ionicons5'
import { ProductService } from '@/service/api/product-service'
import { useBoolean } from '@/hooks'

const { bool: visible, setTrue: openModal, setFalse: hideModal } = useBoolean(false)
const tableRef = ref()
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'id',
  sortDesc: true,
})
const categoryId = ref<string>('')
const columns = ref<DataTableColumns<Category.Data>>([
  {
    title: 'Id',
    align: 'center',
    key: 'id',
    width: '300',
    render: (row) => {
      return (
        <NButton
          secondary
          type="primary"
          strong
          onClick={() => getCategory(row.id!)}
        >
          {row.id}
        </NButton>
      )
    },
  },
  {
    title: 'Name',
    align: 'center',
    key: 'name',
    width: '300',
  },
  {
    title: 'Description',
    align: 'center',
    key: 'description',
  },
  {
    title: 'Note',
    align: 'center',
    key: 'note',
  },
])
const columnsRef = ref<DataTableColumns<User.Data>>(columns.value)
const listCategory = ref<Category.Data[]>([])
const category = ref<Category.Data>({
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
  await getListCategory()
}

async function getListCategory() {
  await ProductService.getListCategory(dataTableRequest.value)
    .then((res: any) => {
      listCategory.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getListCategory()
}

async function reloadTable() {
  await getListCategory()
}

async function getCategory(id: string) {
  await ProductService.getCategory(id)
    .then((res: any) => {
      category.value = res.data
      openModal()
    })
}

async function createOrUpdateCategory() {
  await ProductService.createOrUpdateCategory(category.value)
    .then((res: any) => {
      if (res.isSuccess) {
        hideModal()
        reloadTableFirst()
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
  getListCategory()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card>
      <n-form ref="formRef" :model="dataTableRequest" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="Search" path="filter">
            <n-input v-model:value="dataTableRequest.filter" placeholder="Keyword" />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" secondary @click="reloadTableFirst()">
              <template #icon>
                <icon-park-outline-search />
              </template>
              Search
            </NButton>
            <NButton strong secondary @click="reloadTableFirst()">
              <template #icon>
                <icon-park-outline-redo />
              </template>
              Reload
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
              category.id = '',
              category.name = '',
              category.status = 'ACTIVE'
              openModal()
            }"
          >
            <NIcon size="18" :component="Add" style="margin-right: 5px;" />Add
          </NButton>
        </div>
        <n-data-table ref="tableRef" :columns="columns" :data="listCategory" @update:sorter="sortTable" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
    <n-modal
      v-model:show="visible"
      :mask-closable="false"
      preset="card"
      :title="category.id ? 'Edit' : 'Add'"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form label-placement="left" :model="category" label-align="left" :label-width="90">
        <n-form-item label="Name" path="name">
          <n-input v-model:value="category.name" />
        </n-form-item>
        <n-form-item label="Description" path="description">
          <n-input v-model:value="category.description" type="textarea" />
        </n-form-item>
        <n-form-item label="Note" path="note">
          <n-input v-model:value="category.note" type="textarea" />
        </n-form-item>
      </n-form>
      <template #action>
        <NSpace justify="center">
          <NButton @click="hideModal()">
            Cancel
          </NButton>
          <NButton type="primary" @click="createOrUpdateCategory()">
            {{ category.id ? 'Edit' : 'Add' }}
          </NButton>
        </NSpace>
      </template>
    </n-modal>
  </NSpace>
</template>
