<script setup lang="tsx">
import type { FormInst, FormRules } from 'naive-ui'
import { NButton, NGi, NGrid, NIcon, NSpace } from 'naive-ui'
import { Save } from '@vicons/ionicons5'
import { CompanyService } from '@/service/api/company-service'

const formRef = ref<FormInst | null>(null)

const rules: FormRules = {
  name: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  code: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  taxNumber: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  phoneNumber: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  email: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  address: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  contactPerson: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ]
}

const company = ref<Company.Data>({
  id: '',
  name: '',
  status: 'ACTIVE',
  code: '',
  taxNumber: '',
  phoneNumber: '',
  email: '',
  address: '',
  contactPerson: '',
  description: '',
  note: '',
})


async function getCompany() {
  await CompanyService.getCompany()
    .then((res: any) => {
      company.value = res.data
    })
}

async function createOrUpdateSupplier() {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      await CompanyService.createOrUpdateCompany(company.value)
        .then((res: any) => {
          if (res.isSuccess) {
            // router.push({ name: 'supplier-and-customer-management.supplier.list' })
          }
        })
    }
  })
}

onMounted(async () => {
  getCompany()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card title="Thông tin công ty">
      <template #header-extra>
        <NButton
          secondary
          type="primary"
          style="margin-left: 10px;"
          @click="createOrUpdateSupplier()"
        >
          <NIcon size="18" :component="Save" style="margin-right: 5px;" />
          Lưu
        </NButton>
      </template>

      <n-form
        ref="formRef"
        inline
        :label-width="80"
        :model="company"
        :rules="rules"
      >
        <NGrid cols="3" y-gap="12" x-gap="24">
          <NGi NGi :span="1">
            <n-form-item label="Tên" path="name">
              <n-input
                v-model:value="company.name"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Code" path="code">
              <n-input
                v-model:value="company.code"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Mã số thuế" path="taxNumber">
              <n-input
                v-model:value="company.taxNumber"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Số điện thoại" path="phoneNumber">
              <n-input
                v-model:value="company.phoneNumber"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Email" path="email">
              <n-input
                v-model:value="company.email"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Địa chỉ" path="address">
              <n-input
                v-model:value="company.address"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi :span="3">
            <n-form-item label="Mô tả" path="description">
              <n-input
                v-model:value="company.description"
                type="textarea"
                placeholder=""
                :autosize="{
                  minRows: 3,
                  maxRows: 5,
                }"
              />
            </n-form-item>
          </NGi>

          <NGi :span="3">
            <n-form-item label="Ghi chú" path="note">
              <n-input
                v-model:value="company.note"
                type="textarea"
                placeholder=""
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
  </NSpace>
</template>

<style scoped>
</style>
