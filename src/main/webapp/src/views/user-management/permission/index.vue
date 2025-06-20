<script setup lang="tsx">
import { useBoolean } from '@/hooks'
import { PermissionGroupService } from '@/service/api/permission-group-service'
import type { DataTableColumns } from 'naive-ui'
import { NButton, NIcon } from 'naive-ui'
import { Save, TrashSharp, Add } from '@vicons/ionicons5'

const { bool: loading, setTrue: startLoading, setFalse: endLoading } = useBoolean(false)
const { bool: pLoading, setTrue: startPLoading, setFalse: endPLoading } = useBoolean(false)

const columns: DataTableColumns<PermissionGroup.Table> = [
  {
    title: 'Permission group name',
    // align: 'center',
    key: 'name',
    render: (row) => {
      if (row.new) {
        return (
          <NButton
            style="width: 100%"
            type={!permissionGroup.value.id ? 'primary' : 'default'}
            strong onClick={() => {
              permissionGroup.value.id = ''
              permissionGroup.value.name = ''
              listPermissionSelected.value = []
            }}
          >
            {{
              icon: () => (<NIcon size={18}><Add /></NIcon>),
              default: () => 'New',
            }}
          </NButton>
        )
      }
      else {
        return (
          <NButton
            style="width: 100%"
            strong
            type={permissionGroup.value.id === row.id ? 'primary' : 'default'}
            onClick={() => {
              permissionGroup.value.id = row.id!
              getListPermissionByGroup(row.id!)
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
const permissionGroup = ref<PermissionGroup.Data>({
  id: '',
  name: ''
})
const listPermissionGroup = ref<PermissionGroup.Table[]>([])
const listPermissionSelected = ref<string[]>([])

async function getListModule() {
  await PermissionGroupService.getListModule()
    .then((res: any) => {
      listModule.value = res.data
    })
}

async function getListPermissionGroup() {
  await PermissionGroupService.getListPermissionGroup()
    .then((res: any) => {
      listPermissionGroup.value = [{
        id: '',
        name: '',
        new: true,
      }]
      listPermissionGroup.value.push(...res.data)
    })
}

async function getListPermissionByGroup(permissionGroupId: string) {
  startPLoading()
  await PermissionGroupService.getListPermissionByGroup(permissionGroupId)
    .then((res: any) => {
      permissionGroup.value.id = res.data.id
      permissionGroup.value.name = res.data.name
      listPermissionSelected.value = res.data.permissions
    })
    .finally(() => endPLoading())
}

async function createOrUpdatePermissionGroup() {
  endPLoading()
  await PermissionGroupService.createOrUpdatePermissionGroup({
    id: permissionGroup.value.id,
    name: permissionGroup.value.name,
    permissions: listPermissionSelected.value
  }).then(async (res: any) => {
    if (!permissionGroup.value.id) {
      startLoading()
      listPermissionGroup.value.push({
        id: res.data.id,
        name: res.data.name
      })
      endLoading()
    }
    permissionGroup.value.id = res.data.id
  })
  .finally(() => endPLoading())
}

onMounted(async () => {
  startLoading()
  await getListModule()
  await getListPermissionGroup()
  endLoading()
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
    <NGi :span="8">
      <NSpace justify="center" size="large" v-if="pLoading">
        <n-spin size="medium" />
      </NSpace>
      <NSpace vertical size="large" v-else>
        <n-card>
          <n-form label-placement="left" :model="permissionGroup" label-align="left" :label-width="0">
            <n-form-item-grid-item label="" path="name">
              <n-input v-model:value="permissionGroup.name" placeholder="Name" />
              <n-button style="margin-left: 20px;" tertiary @click="createOrUpdatePermissionGroup()">
                <n-icon size="18" :component="Save" style="margin-right: 5px;" />
                {{ permissionGroup.id ? 'Edit' : 'Add' }}
              </n-button>
              <n-button v-if="permissionGroup.id" style="margin-left: 10px;" tertiary @click="createOrUpdatePermissionGroup()">
                <n-icon size="18" :component="TrashSharp" style="margin-right: 5px;" /> Delete
              </n-button>
            </n-form-item-grid-item>
          </n-form>
        </n-card>
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
          </n-checkbox-group>
        </n-card>
      </NSpace>
    </NGi>
  </NGrid>
</template>

<style scoped></style>
