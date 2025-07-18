<script setup lang="tsx">
import { PermissionGroupService } from '@/service/api/permission-group-service'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { NButton, NIcon } from 'naive-ui'
import { Add, Save, TrashSharp } from '@vicons/ionicons5'

const permissionGroup = ref<PermissionGroup.Data>({
  id: '',
  name: '',
})
const listPermissionSelected = ref<string[]>([])
const columns: DataTableColumns<PermissionGroup.Table> = [
  {
    title: 'Nhóm quyền',
    // align: 'center',
    key: 'name',
    render: (row) => {
      if (row.new) {
        return (
          <NButton
            style="width: 100%"
            type={!permissionGroup.value.id ? 'primary' : 'default'}
            strong
            secondary
            onClick={() => {
              permissionGroup.value.id = ''
              permissionGroup.value.name = ''
              listPermissionSelected.value = []
            }}
          >
            {{
              icon: () => (<NIcon size={18}><Add /></NIcon>),
              default: () => 'Thêm',
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
            secondary
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

const formRef = ref<FormInst | null>(null)

const rules: FormRules = {
  name: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
}

const listModule = ref<Module.Data[]>([])
const listPermissionGroup = ref<PermissionGroup.Table[]>([])

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
  await PermissionGroupService.getListPermissionByGroup(permissionGroupId)
    .then((res: any) => {
      permissionGroup.value.id = res.data.id
      permissionGroup.value.name = res.data.name
      listPermissionSelected.value = res.data.permissions
    })
}

async function createOrUpdatePermissionGroup() {
  await PermissionGroupService.createOrUpdatePermissionGroup({
    id: permissionGroup.value.id,
    name: permissionGroup.value.name,
    permissions: listPermissionSelected.value,
  }).then(async (res: any) => {
    if (!permissionGroup.value.id) {
      listPermissionGroup.value.push({
        id: res.data.id,
        name: res.data.name,
      })
    } else {
      listPermissionGroup.value.find(p => p.id === res.data.id)!.name = res.data.name
    }
    permissionGroup.value.id = res.data.id
  })
}

onMounted(async () => {
  await getListModule()
  await getListPermissionGroup()
})
</script>

<template>
  <NGrid cols="12" x-gap="12">
    <NGi :span="4">
      <n-card>
        <NSpace vertical size="large">
          <n-data-table :columns="columns" :data="listPermissionGroup" />
        </NSpace>
      </n-card>
    </NGi>
    <NGi :span="8">
      <!-- <NSpace v-if="pLoading" justify="center" size="large">
        <n-spin size="medium" />
      </NSpace> -->
      <NSpace vertical size="large">
        <n-card>
          <n-form ref="formRef" label-placement="left" :model="permissionGroup" :rules="rules" label-align="left" :label-width="0">
            <n-form-item-grid-item label="" path="name">
              <n-input v-model:value="permissionGroup.name" placeholder="" />
              <NButton
                style="margin-left: 20px;"
                type="primary"
                secondary
                @click="createOrUpdatePermissionGroup()"
              >
                <NIcon size="18" :component="Save" style="margin-right: 5px;" />
                Lưu
              </NButton>
              <NButton v-if="permissionGroup.id" style="margin-left: 10px;" tertiary @click="createOrUpdatePermissionGroup()">
                <NIcon size="18" :component="TrashSharp" style="margin-right: 5px;" /> Xóa
              </NButton>
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
