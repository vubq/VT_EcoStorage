<script setup lang="tsx">
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { NButton, NGi, NGrid, NIcon, NSelect, NSpace, NTag } from 'naive-ui'
import { Save } from '@vicons/ionicons5'
import { useRoute } from 'vue-router'
import { initRulesForm, validateFieldFromErrors } from '@/utils/error'
import { ProductService } from '@/service/api/product-service'
import { router } from '@/router'
import moment from 'moment'
import { CustomerService } from '@/service/api/customer-service'

const route = useRoute()

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

const customerId = route.params.customerId
const customer = ref<Customer.Data>({
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

const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'id',
  sortDesc: true,
})

const statusTypeMap: Record<string, any> = {
  NEW: 'info',
  CONFIRMED: 'info',
  DELIVERED: 'success',
  CANCELED: 'error',
}
const columns = ref<DataTableColumns<ExportOrder.DataTable>>([
  {
    title: 'ID',
    align: 'center',
    key: 'id',
    render: (row) => {
      return (
        <NButton
          style="width: 100%"
          secondary
          type="primary"
          strong
          onClick={() => {
            router.push({
              name: 'export-order-management.export-order',
              params: { exportOrderId: row.id },
            })
          }}
        >
          {row.id}
        </NButton>
      )
    },
  },
  {
    title: 'Ngày dự kiến',
    align: 'center',
    key: 'expectedDate',
    render: (row) => {
      return (
        <span>{moment(row.expectedDate).format('YYYY-MM-DD')}</span>
      )
    },
  },
  {
    title: 'Ngày giao hàng',
    align: 'center',
    key: 'deliveredDate',
    render: (row) => {
      return (
        <span>{ row.deliveredDate ? moment(row.deliveredDate).format('YYYY-MM-DD') : ''}</span>
      )
    },
  },
  {
    title: 'Tổng tiền',
    align: 'center',
    key: 'totalAmount',
    render: (row) => {
      return (
        <span>{row.totalAmount!.toLocaleString('vi-VN')}</span>
      )
    },
  },
  {
    title: 'Trạng thái',
    align: 'center',
    key: 'status',
    render: (row) => {
      return (
        <NTag type={statusTypeMap[row.status!] || 'default'}>
          {row.status === 'NEW' && <span>THÊM MỚI</span>}
          {row.status === 'CONFIRMED' && <span>ĐÃ XÁC NHẬN</span>}
          {row.status === 'DELIVERED' && <span>ĐÃ XUẤT HÀNG</span>}
          {row.status === 'CANCELED' && <span>ĐÃ HỦY</span>}
        </NTag>
      )
    },
  },
])

const exportOrders = ref<ExportOrder.DataTable[]>([])
const totalRecords = ref<number>(0)

async function getCustomer() {
  await CustomerService.getCustomer(customerId.toString())
    .then((res: any) => {
      customer.value = res.data
    })
}

async function createOrUpdateCustomer() {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      await CustomerService.createOrUpdateCustomer(customer.value)
        .then((res: any) => {
          if (res.isSuccess) {
            router.push({ name: 'supplier-and-customer-management.customer.list' })
          }
        })
    }
  })
}

async function changePage(page: number, size: number) {
  dataTableRequest.value.currentPage = page
  dataTableRequest.value.perPage = size
  await getExportOrders()
}

async function getExportOrders() {
  await CustomerService.getExportOrders(dataTableRequest.value, customerId.toString())
    .then((res: any) => {
      exportOrders.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

onMounted(async () => {
  if (customerId.toString() !== ':customerId' && customerId.toString() !== 'new') {
    await getCustomer()
    await getExportOrders()
  }
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card title="Khách hàng">
      <template #header-extra>
        <NButton
          secondary
          type="primary"
          style="margin-left: 10px;"
          @click="createOrUpdateCustomer()"
        >
          <NIcon size="18" :component="Save" style="margin-right: 5px;" />
          Lưu
        </NButton>
      </template>

      <n-form
        ref="formRef"
        inline
        :label-width="80"
        :model="customer"
        :rules="rules"
      >
        <NGrid cols="3" y-gap="12" x-gap="24">
          <NGi NGi :span="1">
            <n-form-item label="Tên" path="name">
              <n-input
                v-model:value="customer.name"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Code" path="code">
              <n-input
                v-model:value="customer.code"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Mã số thuế" path="taxNumber">
              <n-input
                v-model:value="customer.taxNumber"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Số điện thoại" path="phoneNumber">
              <n-input
                v-model:value="customer.phoneNumber"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Email" path="email">
              <n-input
                v-model:value="customer.email"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Địa chỉ" path="address">
              <n-input
                v-model:value="customer.address"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi :span="3">
            <n-form-item label="Mô tả" path="description">
              <n-input
                v-model:value="customer.description"
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
                v-model:value="customer.note"
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
    <n-card title="Hóa đơn khách hàng" v-if="exportOrders.length > 0">
      <NSpace vertical size="large">
        <n-data-table ref="tableRef" :columns="columns" :data="exportOrders" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>

<style scoped>
</style>
