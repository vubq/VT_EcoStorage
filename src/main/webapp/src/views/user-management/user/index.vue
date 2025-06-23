<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState } from 'naive-ui'
import { NButton, NSpace } from 'naive-ui'
import { UserService } from '@/service/api/user'
import { router } from '@/router'
import { Add } from '@vicons/ionicons5'

const tableRef = ref()
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'username',
  sortDesc: true,
})
const userId = ref<string>('')
const columns = ref<DataTableColumns<User.Data>>([
  {
    title: 'Username',
    align: 'center',
    key: 'username',
    sorter: true,
    sortOrder: sortDefault('username'),
    render: (row) => {
      return (
        <NButton
          style="width: 100%"
          secondary
          type="primary"
          strong
          onClick={() => getUser(row.id!)}
        >
          {row.username}
        </NButton>
      )
    },
  },
  {
    title: 'First name',
    align: 'center',
    key: 'firstName',
    sorter: true,
    sortOrder: sortDefault('firstName'),
  },
  {
    title: 'Last name',
    align: 'center',
    key: 'lastName',
    sorter: true,
    sortOrder: sortDefault('lastName'),
  },
  {
    title: 'Email',
    align: 'center',
    key: 'email',
    sorter: true,
    sortOrder: sortDefault('email'),
  },
  {
    title: 'Phone number',
    align: 'center',
    key: 'phoneNumber',
    sorter: true,
    sortOrder: sortDefault('phoneNumber'),
  },
])
const columnsRef = ref<DataTableColumns<User.Data>>(columns.value)
const listUser = ref<User.Data[]>([])
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
  await getListUser()
}

async function getListUser() {
  await UserService.getListUser(dataTableRequest.value)
    .then((res: any) => {
      listUser.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getListUser()
}

async function reloadTable() {
  await getListUser()
}

async function getUser(id: string) {
  userId.value = id
  await router.push({
    name: 'user-management.user-info',
    params: { userId: id },
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
  getListUser()
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
          <NButton strong type="primary" secondary class="ml-a" @click="getUser('new')">
            <NIcon size="18" :component="Add" style="margin-right: 5px;" />Add
          </NButton>
        </div>
        <n-data-table ref="tableRef" :columns="columns" :data="listUser" @update:sorter="sortTable" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
