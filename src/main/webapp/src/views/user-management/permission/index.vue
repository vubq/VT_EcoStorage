<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { PermissionGroupService } from '@/service/api/permission-group-service'
import type { DataTableColumns } from 'naive-ui'
import { NButton } from 'naive-ui'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)

const columns: DataTableColumns<PermissionGroup.Table> = [
  {
    title: 'Permission group name',
    // align: 'center',
    key: 'name',
    render: (row) => {
      if (row.new) {
        return (
          <NButton style="width: 100%" strong>
            {{
              icon: () => <icon-park-outline-add-one />,
              default: () => 'Add',
            }}
          </NButton>
        )
      }
      else {
        return (
          <NButton
            style="width: 100%"
            strong
            type={idPermissionGroupSelected.value === row.id ? 'primary' : 'default'}
            onClick={() => {
              idPermissionGroupSelected.value = row.id ?? ''
            }}
          >
            {{
              default: () => row.name,
            }}
          </NButton>
        )
      }
    },
  },
]

const listModule = ref<Module.Data[]>([])
const idPermissionGroupSelected = ref<string>('')
const listPermissionGroup = ref<PermissionGroup.Table[]>([{
  id: '',
  name: '',
  new: true,
}])
const listPermissionSelected = ref<string[]>([])

async function getListModule() {
  startLoading()
  await PermissionGroupService.getListModule()
    .then((res: any) => {
      listModule.value = res.data
    })
  endLoading()
}

async function getListPermissionGroup() {
  startLoading()
  await PermissionGroupService.getListPermissionGroup()
    .then((res: any) => {
      listPermissionGroup.value.push(...res.data)
    })
  endLoading()
}

onMounted(() => {
  getListModule()
  getListPermissionGroup()
})
</script>

<template>
  <NGrid cols="12" x-gap="12">
    <NGi :span="4">
      <n-card>
        <NSpace vertical size="large">
          <n-data-table :columns="columns" :data="listPermissionGroup" :loading="loading" />
        </NSpace>
      </n-card>
    </NGi>
    <NGi :span="8" v-if="idPermissionGroupSelected">
      <n-card>
        <n-checkbox-group v-model:value="listPermissionSelected">
          <NGrid cols="6" y-gap="24">
            <NGi v-for="(module) in listModule" :key="module.id" :span="6">
              <NGrid cols="6" y-gap="12" x-gap="12">
                <NGi :span="6">
                  <n-divider title-placement="left">
                    {{ module.name }}
                  </n-divider>
                </NGi>
                <NGi v-for="permission in module.permissions" :key="permission.id" :span="1">
                  <n-checkbox :value="permission.id" :label="permission.action" />
                </NGi>
              </NGrid>
            </NGi>
          </NGrid>

          <n-space item-style="display: flex;" />
        </n-checkbox-group>
      </n-card>
    </NGi>
  </NGrid>
</template>

<style scoped></style>
