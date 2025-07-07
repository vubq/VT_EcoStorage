<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState } from 'naive-ui'
import { NButton, NSpace, NTag } from 'naive-ui'
import { router } from '@/router'
import moment from 'moment'
import { Add } from '@vicons/ionicons5'
import { ExportOrderService } from '@/service/api/export-order-service'

const tableRef = ref()
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'expectedDate',
  sortDesc: true,
})
const statusTypeMap: Record<string, any> = {
  NEW: 'info',
  CONFIRMED: 'info',
  DELIVERED: 'success',
  CANCELED: 'error',
}
const columns = ref<DataTableColumns<ExportOrder.DataTable>>([
  {
    title: 'ID',
    align: 'center',
    key: 'id',
    sorter: true,
    sortOrder: sortDefault('id'),
    render: (row) => {
      return (
        <NButton
          style="width: 100%"
          secondary
          type="primary"
          strong
          onClick={() => {
            router.push({
              name: 'export-order-management.export-order',
              params: { exportOrderId: row.id },
            })
          }}
        >
          {row.id}
        </NButton>
      )
    },
  },
  {
    title: 'Kho',
    align: 'center',
    key: 'warehouseName',
  },
  {
    title: 'Loại',
    align: 'center',
    key: 'type',
  },
  {
    title: 'Khách hàng',
    align: 'center',
    key: 'customerName',
    render: (row) => {
      return (
        <div>
          <span v-if={row.type === 'EXPORT'}>{ row.customerName }</span>
          <span v-if={row.type === 'INTERNAL'}>{ row.warehouseToName }</span>
        </div>
      )
    },
  },
  {
    title: 'Ngày dự kiến',
    align: 'center',
    key: 'expectedDate',
    sorter: true,
    sortOrder: sortDefault('expectedDate'),
    render: (row) => {
      return (
        <span>{moment(row.expectedDate).format('YYYY-MM-DD')}</span>
      )
    },
  },
  {
    title: 'Ngày giao hàng',
    align: 'center',
    key: 'deliveredDate',
    sorter: true,
    sortOrder: sortDefault('deliveredDate'),
    render: (row) => {
      return (
        <span>{ row.deliveredDate ? moment(row.deliveredDate).format('YYYY-MM-DD') : ''}</span>
      )
    },
  },
  {
    title: 'Tổng tiền',
    align: 'center',
    key: 'totalAmount',
    sorter: true,
    sortOrder: sortDefault('totalAmount'),
    render: (row) => {
      return (
        <span>{row.totalAmount!.toLocaleString('vi-VN')}</span>
      )
    },
  },
  {
    title: 'Trạng thái',
    align: 'center',
    key: 'status',
    render: (row) => {
      return (
        <NTag type={statusTypeMap[row.status!] || 'default'}>
          {row.status}
        </NTag>
      )
    },
  },
])
const columnsRef = ref<DataTableColumns<User.Data>>(columns.value)
const listExportOrder = ref<ExportOrder.DataTable[]>([])
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
  await getListExportOrder()
}

async function getListExportOrder() {
  await ExportOrderService.getListExportOrder(dataTableRequest.value)
    .then((res: any) => {
      listExportOrder.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getListExportOrder()
}

async function reloadTable() {
  await getListExportOrder()
}

async function reloadSearch() {
  dataTableRequest.value.filter = ''
  await reloadTableFirst()
}

// async function getUser(id: string) {
//   userId.value = id
//   loading1Start()
//   await router.push({
//     name: 'user-management.user-info',
//     params: { id },
//   })
//   loading1End()
// }

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
  reloadTableFirst()
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
            <NButton type="primary" secondary @click="reloadTable()">
              <template #icon>
                <icon-park-outline-search />
              </template>
              Tìm kiếm
            </NButton>
            <NButton strong secondary @click="reloadSearch()">
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
              router.push({
                name: 'export-order-management.export-order',
                params: { exportOrderId: 'new' },
              })
            }"
          >
            <template #icon>
              <NIcon size="18" :component="Add" />
            </template>
            Thêm
          </NButton>
        </div>
        <n-data-table ref="tableRef" :columns="columns" :data="listExportOrder" @update:sorter="sortTable" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
