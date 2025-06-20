<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'

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
    render: (row) => {
      return (
          <NButton
            style="width: 100%"
            tertiary
            type="primary"
            strong onClick={() => getUser(row.id!)}
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
  sortDesc: true
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
    params: { userId: userId }
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
            <NButton type="primary" @click="reloadTableFirst()">
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
          <NButton strong type="primary" class="ml-a" @click="getUser('new')">
            <template #icon>
              <icon-park-outline-add-one />
            </template>
            Add
          </NButton>
        </div>
        <n-data-table :columns="columns" :data="listUser" :loading="loading" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
