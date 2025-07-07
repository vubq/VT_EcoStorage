<script setup lang="tsx">
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { NButton, NGi, NGrid, NIcon, NSpace, NTag } from 'naive-ui'
import { Save } from '@vicons/ionicons5'
import { useRoute } from 'vue-router'
import { router } from '@/router'
import moment from 'moment'
import { SupplierService } from '@/service/api/supplier-service'

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

const supplierId = route.params.supplierId
const supplier = ref<Supplier.Data>({
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
  RECEIVED: 'success',
  CANCELED: 'error',
}
const columns = ref<DataTableColumns<PurchaseOrder.DataTable>>([
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
              name: 'purchase-order-management.purchase-order',
              params: { purchaseOrderId: row.id },
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
        <span>{ row.receivedDate ? moment(row.receivedDate).format('YYYY-MM-DD') : ''}</span>
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
          {row.status}
        </NTag>
      )
    },
  },
])

const purchaseOrders = ref<PurchaseOrder.DataTable[]>([])
const totalRecords = ref<number>(0)

async function getSupplier() {
  await SupplierService.getSupplier(supplierId.toString())
    .then((res: any) => {
      supplier.value = res.data
    })
}

async function createOrUpdateSupplier() {
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      await SupplierService.createOrUpdateSupplier(supplier.value)
        .then((res: any) => {
          if (res.isSuccess) {
            router.push({ name: 'supplier-and-customer-management.supplier.list' })
          }
        })
    }
  })
}

async function changePage(page: number, size: number) {
  dataTableRequest.value.currentPage = page
  dataTableRequest.value.perPage = size
  await getPurchaseOrders()
}

async function getPurchaseOrders() {
  await SupplierService.getPurchaseOrders(dataTableRequest.value, supplierId.toString())
    .then((res: any) => {
      purchaseOrders.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

onMounted(async () => {
  if (supplierId.toString() !== ':supplierId' && supplierId.toString() !== 'new') {
    await getSupplier()
    await getPurchaseOrders()
  }
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card title="Nhà cung cấp">
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
        :model="supplier"
        :rules="rules"
      >
        <NGrid cols="3" y-gap="12" x-gap="24">
          <NGi NGi :span="1">
            <n-form-item label="Tên" path="name">
              <n-input
                v-model:value="supplier.name"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Code" path="code">
              <n-input
                v-model:value="supplier.code"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Mã số thuế" path="taxNumber">
              <n-input
                v-model:value="supplier.taxNumber"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Số điện thoại" path="phoneNumber">
              <n-input
                v-model:value="supplier.phoneNumber"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Email" path="email">
              <n-input
                v-model:value="supplier.email"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Địa chỉ" path="address">
              <n-input
                v-model:value="supplier.address"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi :span="3">
            <n-form-item label="Mô tả" path="description">
              <n-input
                v-model:value="supplier.description"
                type="textarea"
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
                v-model:value="supplier.note"
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
    <n-card title="Hóa đơn nhà cung cấp" v-if="purchaseOrders.length > 0">
      <NSpace vertical size="large">
        <n-data-table ref="tableRef" :columns="columns" :data="purchaseOrders" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>

<style scoped>
</style>
