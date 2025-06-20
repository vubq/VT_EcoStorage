<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState } from 'naive-ui'

import { useBoolean } from '@/hooks'
import { NButton, NSpace } from 'naive-ui'
import { UserService } from '@/service/api/user'
import { router } from '@/router'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const columns: DataTableColumns<User.Data> = [
  {
    title: 'Username',
    align: 'center',
    key: 'username',
    sorter: true,
    render: (row) => {
      return (
        <NButton
          style="width: 100%"
          secondary
          type="primary"
          strong
          onClick={() => getUser(row.id!)}
        >
          {{
            default: () => row.username,
          }}
        </NButton>
      )
    },
  },
  {
    title: 'First name',
    align: 'center',
    key: 'firstName',
  },
  {
    title: 'Last name',
    align: 'center',
    key: 'lastName',
  },
  {
    title: 'Email',
    align: 'center',
    key: 'email',
  },
  {
    title: 'Phone number',
    align: 'center',
    key: 'phoneNumber',
  },
]

const listUser = ref<User.Data[]>([])
const totalRecords = ref<number>(0)
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'id',
  sortDesc: true,
})

async function changePage(page: number, size: number) {
  dataTableRequest.value.currentPage = page
  dataTableRequest.value.perPage = size
  await getListUser()
}

async function getListUser() {
  startLoading()
  await UserService.getListUser(dataTableRequest.value)
    .then((res: any) => {
      listUser.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
    .finally(() => endLoading())
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getListUser()
}

async function reloadTable() {
  await getListUser()
}

function getUser(userId: string) {
  router.push({
    name: 'user-management.user-info',
    params: { userId },
  })
}

function sorter(sorterInfo: DataTableSortState | null) {
  if (!sorterInfo)
    return
  const { columnKey, order } = sorterInfo
  console.log('Column Key:', columnKey)
  console.log('Order:', order)

  if (!order)
    return

  data.value.sort((a, b) => {
    let result = 0
    if (columnKey === 'name') {
      result = a.name.localeCompare(b.name)
    }
    else if (columnKey === 'age') {
      result = a.age - b.age
    }
    return order === 'ascend' ? result : -result
  })
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
            <template #icon>
              <icon-park-outline-add-one />
            </template>
            Add
          </NButton>
        </div>
        <n-data-table :columns="columns" :data="listUser" :loading="loading" :sorter="sorter" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
