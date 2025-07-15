<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState } from 'naive-ui'
import { NA, NButton, NSpace } from 'naive-ui'
import { Add } from '@vicons/ionicons5'
import { CustomerService } from '@/service/api/customer-service'
import { router } from '@/router'

const tableRef = ref()
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'id',
  sortDesc: true,
})
const customerId = ref<string>('')
const columns = ref<DataTableColumns<Customer.Data>>([
  {
    title: 'ID',
    align: 'center',
    key: 'id',
    render: (row) => {
      return h(
        NA,
        {
          href: '#',
          onClick: () => {
            router.push({
              name: 'supplier-and-customer-management.customer.detail',
              params: { customerId: row.id },
            })
          },
          class: 'underline-on-hover',
          internal: true
        },
        {
          default: () => row.id
        }
      )
    },
  },
  {
    title: 'Code',
    align: 'center',
    key: 'code',
  },
  {
    title: 'Tên',
    align: 'center',
    key: 'name',
  },
  {
    title: 'Mã số thuế',
    align: 'center',
    key: 'taxNumber',
  },
  {
    title: 'Số điện thoại',
    align: 'center',
    key: 'phoneNumber',
  },
  {
    title: 'Email',
    align: 'center',
    key: 'email',
  },
  {
    title: 'Địa chỉ',
    align: 'center',
    key: 'address',
  },
  // {
  //   title: 'Mô tả',
  //   align: 'center',
  //   key: 'description',
  // },
  // {
  //   title: 'Ghi chú',
  //   align: 'center',
  //   key: 'note',
  // },
])
const columnsRef = ref<DataTableColumns<Customer.Data>>(columns.value)
const customers = ref<Customer.Data[]>([])
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
  await getCustomers()
}

async function getCustomers() {
  await CustomerService.getCustomers(dataTableRequest.value)
    .then((res: any) => {
      customers.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getCustomers()
}

async function reloadFilter() {
  dataTableRequest.value.currentPage = 1
  dataTableRequest.value.filter = ''
  await getCustomers()
}

async function reloadTable() {
  await getCustomers()
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
  getCustomers()
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
            <NButton strong secondary @click="reloadFilter()">
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
                name: 'supplier-and-customer-management.customer.detail',
                params: { customerId: 'new' },
              })
            }"
          >
            <NIcon size="18" :component="Add" style="margin-right: 5px;" />Thêm
          </NButton>
        </div>
        <n-data-table ref="tableRef" :columns="columns" :data="customers" @update:sorter="sortTable" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
