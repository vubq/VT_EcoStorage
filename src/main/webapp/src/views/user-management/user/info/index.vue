<script setup lang="tsx">
import { PermissionGroupService } from '@/service/api/permission-group-service'
import { NButton, NIcon } from 'naive-ui'
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

const formUserRef = ref()
const errors = ref<Error.ValidationError[]>([])

const userRules = ref(
  initRulesForm(user.value, (rule, value, callback, key) => {
    validateFieldFromErrors(errors, key, callback)
  }),
)

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

onMounted(async () => {
  await getListModule()
  if (userId !== 'new') {
    await getUser()
  }
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card title="Info">
      <template #header-extra>
        <NButton secondary type="primary" @click="createOrUpdateUser()">
          <NIcon size="18" :component="Save" style="margin-right: 5px;" />
          {{ user.id ? 'Edit' : 'Add' }}
        </NButton>
      </template>

      <n-form
        ref="formUserRef"
        inline
        :label-width="80"
        :model="user"
        :rules="userRules"
      >
        <NGrid cols="3" y-gap="12" x-gap="24">
          <NGi :span="1">
            <n-form-item label="Username" path="username">
              <n-input v-model:value="user.username" placeholder="Input Username" :disabled="!!user.id" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="Password" path="password">
              <n-input v-model:value="user.password" placeholder="Input Password" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="Email" path="email">
              <n-input v-model:value="user.email" placeholder="Input Email" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="First name" path="firstName">
              <n-input v-model:value="user.firstName" placeholder="Input First name" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="Last name" path="lastName">
              <n-input v-model:value="user.lastName" placeholder="Input Last name" />
            </n-form-item>
          </NGi>
          <NGi :span="1">
            <n-form-item label="Phone number" path="phoneNumber">
              <n-input v-model:value="user.phoneNumber" placeholder="Input Phone number" />
            </n-form-item>
          </NGi>
          <NGi :span="3">
            <n-form-item label="Note" path="note">
              <n-input
                v-model:value="user.note"
                placeholder="Input Note"
                type="textarea"
                :autosize="{
                  minRows: 3,
                  maxRows: 5,
                }"
              />
            </n-form-item>
          </NGi>
        </NGrid>
      </n-form>
    </n-card>
    <n-card title="Permission">
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
    <n-card title="Permission Group" />
  </NSpace>
</template>

<style scoped>
.n-divider:not(.n-divider--vertical) {
  margin: 0;
}
</style>
