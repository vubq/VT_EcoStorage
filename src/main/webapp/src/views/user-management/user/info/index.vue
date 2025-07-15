<script setup lang="tsx">
import { PermissionGroupService } from '@/service/api/permission-group-service'
import { FormRules, NButton, NIcon } from 'naive-ui'
import { Save } from '@vicons/ionicons5'
import { useRoute } from 'vue-router'
import { UserService } from '@/service/api/user'
import { router } from '@/router'
import { initRulesForm, validateFieldFromErrors } from '@/utils/error'

const route = useRoute()

const userId = route.params.userId
const user = ref<User.Data>({
  id: '',
  username: '',
  password: '',
  email: '',
  firstName: '',
  lastName: '',
  note: '',
  status: 'ACTIVE',
  permissions: [],
})

const statusOptions = [
  { label: 'Hoạt động', value: 'ACTIVE' },
  { label: 'Khóa', value: 'INACTIVE' },
]

const formUserRef = ref()

const rules: FormRules = {
  username: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  password: [
    {
      required: true,
      validator: (rule, value) => {
        if (userId === 'new' && !value) {
          return Promise.reject('Không được để trống')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  email: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  firstName: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  lastName: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  phoneNumber: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
}

const errors = ref<Error.ValidationError[]>([])

const listPermissionGroup = ref<PermissionGroup.Data[]>([])

const userRules = ref(
  initRulesForm(user.value, (rule, value, callback, key) => {
    validateFieldFromErrors(errors, key, callback)
  }),
)

const optionsPermission = [
  { label: 'Mục 1', value: '1' },
  { label: 'Mục 2', value: '2' },
  { label: 'Mục 3', value: '3' }
]

const listModule = ref<Module.Data[]>([])

async function getListModule() {
  await PermissionGroupService.getListModule()
    .then((res: any) => {
      listModule.value = res.data
    })
}

async function getUser() {
  if (userId) {
    await UserService.getUser(userId.toString())
      .then((res: any) => {
        user.value = res.data
      })
  }
}

async function createOrUpdateUser() {
  formUserRef.value?.validate(async (errors: any) => {
    if (!errors) {
      await UserService.createOrUpdateUser(user.value)
        .then((res: any) => {
          if (res.isSuccess) {
            router.push({ name: 'user-management.user' })
          }
          else {
            errors.value = res.data
            formUserRef.value.validate()
          }
        })
    }
  })
}

async function getListPermissionGroup() {
  await PermissionGroupService.getListPermissionGroup()
    .then((res: any) => {
      listPermissionGroup.value.push(...res.data)
    })
}

function optionPermissionGroups() {
  return listPermissionGroup.value.map(item => ({
    label: item.name,
    value: item.id,
  }))
}

onMounted(async () => {
  await getListModule()
  if (userId !== 'new') {
    await getUser()
  }
  await getListPermissionGroup()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card title="Thông tin">
      <template #header-extra>
        <NButton secondary type="primary" @click="createOrUpdateUser()">
          <NIcon size="18" :component="Save" style="margin-right: 5px;" />
          Lưu
        </NButton>
      </template>

      <n-form
        ref="formUserRef"
        inline
        :label-width="80"
        :model="user"
        :rules="rules"
      >
        <NGrid cols="3" y-gap="12" x-gap="24">
          <NGi :span="1">
            <n-form-item label="Tài khoản" path="username">
              <n-input v-model:value="user.username" placeholder="" :disabled="!!user.id" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="Mật khẩu" path="password">
              <n-input type="password" v-model:value="user.password" placeholder="" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="Email" path="email">
              <n-input v-model:value="user.email" placeholder="" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="Họ" path="firstName">
              <n-input v-model:value="user.firstName" placeholder="" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="Tên" path="lastName">
              <n-input v-model:value="user.lastName" placeholder="" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="Số điện thoại" path="phoneNumber">
              <n-input v-model:value="user.phoneNumber" placeholder="" />
            </n-form-item>
          </NGi>
          <NGi :span="3">
            <n-form-item label="Ghi chú" path="note">
              <n-input
                v-model:value="user.note"
                placeholder=""
                type="textarea"
                :autosize="{
                  minRows: 3,
                  maxRows: 5,
                }"
              />
            </n-form-item>
          </NGi>
          <NGi :span="3" v-if="userId !== 'new'">
            <n-form-item label="Trạng thái">
              <n-select
                v-model:value="user.status"
                placeholder=""
                :options="statusOptions"
              />
            </n-form-item>
          </NGi>
        </NGrid>
      </n-form>
    </n-card>
    <n-card title="Quyền">
      <NSpace vertical size="large">
        <n-checkbox-group v-model:value="user.permissions">
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
      </NSpace>
    </n-card>
    <n-divider dashed>
      Or
    </n-divider>
    <n-card title="Nhóm quyền">
      <n-select v-model:value="user.permissionGroups" multiple :options="optionPermissionGroups()" placeholder="Chọn nhóm quyền" />
    </n-card>
  </NSpace>
</template>

<style scoped>
.n-divider:not(.n-divider--vertical) {
  margin: 0;
}
</style>
