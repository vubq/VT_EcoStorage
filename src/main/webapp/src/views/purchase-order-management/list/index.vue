<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState } from 'naive-ui'
import { NButton, NSpace, NTag } from 'naive-ui'
import { router } from '@/router'
import { PurchaseOrderService } from '@/service/api/purchase-order-service'
import moment from 'moment'
import { Add } from '@vicons/ionicons5'

const tableRef = ref()
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'expectedDate',
  sortDesc: true,
})
const userId = ref<string>('')
const statusTypeMap: Record<string, any> = {
  NEW: 'info',
  CONFIRMED: 'info',
  RECEIVED: 'success',
  CANCELED: 'error',
}
const columns = ref<DataTableColumns<PurchaseOrder.DataTable>>([
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
              name: 'purchase-order-management.purchase-order',
              params: { purchaseOrderId: row.id },
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
    render: (row) => {
      return (
        <div>
          {row.type === 'PURCHASE' && <span>THƯỜNG</span>}
          {row.type === 'INTERNAL' && <span>NỘI BỘ</span>}
        </div>
      )
    },
  },
  {
    title: 'Nhà cung cấp',
    align: 'center',
    key: 'supplierName',
    render: (row) => {
      return (
        <div>
          {row.type === 'PURCHASE' && <span>{ row.supplierName }</span>}
          {row.type === 'INTERNAL' && <span>{ row.warehouseFromName }</span>}
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
    title: 'Ngày nhận hàng',
    align: 'center',
    key: 'receivedDate',
    sorter: true,
    sortOrder: sortDefault('receivedDate'),
    render: (row) => {
      return (
        <span>{ row.receivedDate ? moment(row.receivedDate).format('YYYY-MM-DD') : ''}</span>
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
          {row.status === 'NEW' && <span>THÊM MỚI</span>}
          {row.status === 'CONFIRMED' && <span>ĐÃ XÁC NHẬN</span>}
          {row.status === 'RECEIVED' && <span>ĐÃ NHẬN HÀNG</span>}
          {row.status === 'CANCELED' && <span>ĐÃ HỦY</span>}
        </NTag>
      )
    },
  },
])
const columnsRef = ref<DataTableColumns<User.Data>>(columns.value)
const listPurchaseOrder = ref<PurchaseOrder.DataTable[]>([])
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
  await getListPurchaseOrder()
}

async function getListPurchaseOrder() {
  dataTableRequest.value.warehouseId = warehouseId.value
  dataTableRequest.value.type = type.value
  dataTableRequest.value.status = status.value
  await PurchaseOrderService.getListPurchaseOrder(dataTableRequest.value)
    .then((res: any) => {
      listPurchaseOrder.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getListPurchaseOrder()
}

async function reloadTable() {
  await getListPurchaseOrder()
}

async function reloadSearch() {
  dataTableRequest.value.filter = ''
  type.value = 'ALL'
  status.value = 'ALL'
  warehouseId.value = 'ALL'
  dataTableRequest.value.currentPage = 1
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

async function getReferenceData() {
  await PurchaseOrderService.getReferenceData()
    .then((res: any) => {
      referenceData.value = res.data
    })
}

const warehouseId = ref<string>('ALL')

const referenceData = ref<ReferenceData.PurchaseOrder>({
  warehouses: [],
  suppliers: [],
  categories: [],
})

function optionWarehouses() {
  return [
    {
      label: 'Tất cả',
      value: 'ALL',
    },
    ...referenceData.value.warehouses.map(item => ({
      label: item.name,
      value: item.id,
    }))
  ]
}

const type = ref<string>('ALL')

const exportTypeOptions = [
  { label: 'Tất cả', value: 'ALL' },
  { label: 'THƯỜNG', value: 'PURCHASE' },
  { label: 'NỘI BỘ', value: 'INTERNAL' },
]

const status = ref<string>('ALL')

const statusOptions = [
  { label: 'Tất cả', value: 'ALL' },
  { label: 'THÊM MỚI', value: 'NEW' },
  { label: 'ĐÃ XÁC NHẬN', value: 'CONFIRMED' },
  { label: 'ĐÃ NHẬN HÀNG', value: 'RECEIVED' },
  { label: 'ĐÃ HỦY', value: 'CANCELED' },
]

onMounted(async () => {
  await getReferenceData()
  await reloadTableFirst()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card>
      <n-form :model="dataTableRequest" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="Kho">
            <n-select
              v-model:value="warehouseId"
              placeholder=""
              :options="optionWarehouses()"
              style="width: 150px;"
            />
          </n-form-item>
          <n-form-item label="Loại">
            <n-select
              v-model:value="type"
              placeholder=""
              :options="exportTypeOptions"
              style="width: 150px;"
            />
          </n-form-item>
          <n-form-item label="Trạng thái">
            <n-select
              v-model:value="status"
              placeholder=""
              :options="statusOptions"
              style="width: 150px;"
            />
          </n-form-item>
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
                name: 'purchase-order-management.purchase-order',
                params: { purchaseOrderId: 'new' },
              })
            }"
          >
            <template #icon>
              <NIcon size="18" :component="Add" />
            </template>
            Thêm
          </NButton>
        </div>
        <n-data-table ref="tableRef" :columns="columns" :data="listPurchaseOrder" @update:sorter="sortTable" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
