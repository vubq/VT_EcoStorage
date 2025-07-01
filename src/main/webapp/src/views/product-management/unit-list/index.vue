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
const unitId = ref<string>('')
const columns = ref<DataTableColumns<Unit.Data>>([
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
          onClick={() => getUnit(row.id!)}
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
  await ProductService.createOrUpdateUnit(unit.value)
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
  getListUnit()
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
              unit.id = '',
              unit.name = '',
              unit.status = 'ACTIVE'
              openModal()
            }"
          >
            <NIcon size="18" :component="Add" style="margin-right: 5px;" />Add
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
      :title="unit.id ? 'Edit' : 'Add'"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form label-placement="left" :model="unit" label-align="left" :label-width="80">
        <n-form-item label="Name" path="name">
          <n-input v-model:value="unit.name" />
        </n-form-item>
        <n-form-item label="Description" path="description">
          <n-input v-model:value="unit.description" type="textarea" />
        </n-form-item>
        <n-form-item label="Note" path="note">
          <n-input v-model:value="unit.note" type="textarea" />
        </n-form-item>
      </n-form>
      <template #action>
        <NSpace justify="center">
          <NButton @click="hideModal()">
            Cancel
          </NButton>
          <NButton type="primary" @click="createOrUpdateUnit()">
            {{ unit.id ? 'Edit' : 'Add' }}
          </NButton>
        </NSpace>
      </template>
    </n-modal>
  </NSpace>
</template>
