<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { NButton, NIcon, NInputNumber } from 'naive-ui'
import { Add, CheckboxOutline, LocationSharp, PawOutline, Save, TrashSharp } from '@vicons/ionicons5'
import { useRoute } from 'vue-router'
import { useBoolean } from '@/hooks'
import { initRulesForm, validateFieldFromErrors } from '@/utils/error'
import { PurchaseOrderService } from '@/service/api/purchase-order-service'
import { ProductService } from '@/service/api/product-service'
import { router } from '@/router'
import moment from 'moment'

const route = useRoute()

const { bool: loading, setTrue: loadingStart, setFalse: loadingEnd } = useBoolean(false)
const { bool: isModalProduct, setTrue: showModalProduct, setFalse: hidenModalProduct } = useBoolean(false)

const renderExpandIcon = () => {
  return h(NIcon, null, { default: () => h(LocationSharp) })
}

const columns = ref<DataTableColumns<PurchaseOrderDetail.Data>>([
  {
    title: 'Product Barcode',
    align: 'center',
    key: 'productBarcode',
    render: (row) => {
      return (
        <NButton
          style="width: 100%"
          secondary
          type="primary"
          strong
        >
          {row.productBarcode}
        </NButton>
      )
    },
  },
  {
    type: 'expand',
    title: 'Location',
    align: 'center',
    width: 100,
    renderExpand: (row) => {
      return (
        <div>
          <n-select/>
        </div>
      )
    }
  },
  {
    title: 'Product Name',
    align: 'center',
    key: 'productName',
  },
  {
    title: 'Product Unit',
    align: 'center',
    key: 'productUnit',
  },
  {
    title: 'Quantity',
    align: 'center',
    key: 'quantity',
    width: 150,
    render: (row) => {
      return purchaseOrder.value.status === 'NEW' ? h(NInputNumber, {
        value: row.quantity,
        min: 1,
        placeholder: '',
        onUpdateValue: (val: number | null) => {
          row.quantity = val != null && val >= 1 ? val : 1
          row.totalAmount = row.quantity * row.unitPrice!
        }
      }) : (<span>{row.quantity}</span>)
    },
  },
  {
    title: 'Unit Price',
    align: 'center',
    key: 'unitPrice',
    width: 180,
    render: (row) => {
      return purchaseOrder.value.status === 'NEW' ? h(NInputNumber, {
        value: row.unitPrice,
        min: 1,
        placeholder: '',
        onUpdateValue: (val: number | null) => {
          row.unitPrice = val != null && val >= 1 ? val : 1
          row.totalAmount = row.quantity! * row.unitPrice
        }
      }) : (<span>{row.unitPrice!.toLocaleString('vi-VN')}</span>)
    },
  },
  {
    title: 'Total Amount',
    align: 'center',
    key: 'totalAmount',
    render: (row) => {
      return (
        <span>{row.totalAmount!.toLocaleString('vi-VN')}</span>
      )
    },
  },
  {
    title: '',
    align: 'center',
    key: '',
    render: (row) => {
      return purchaseOrder.value.status === 'NEW' ? (
        <NButton
          secondary
          type="error"
          onClick={() => {
            row.delete = true
          }}
        >
          <NIcon size={18} component={TrashSharp} />
        </NButton>
      ) : null
    },
  },
])

const columnsProduct = ref<DataTableColumns<PurchaseOrder.Product>>([
  {
    title: 'Product Barcode',
    align: 'center',
    key: 'barcode',
    render: (row) => {
      return (
        <NButton
          style="width: 100%"
          secondary
          type="primary"
          strong
          onClick={() => addProduct(row)}
        >
          {row.barcode}
        </NButton>
      )
    },
  },
  {
    title: 'Product Name',
    align: 'center',
    key: 'name',
  },
  {
    title: 'Unit',
    align: 'center',
    key: 'productUnitName',
  },
  {
    title: 'Category',
    align: 'center',
    key: 'productCategoryName',
  },
  {
    title: 'Inventory Quantity',
    align: 'center',
    key: 'inventoryQuantity',
  },
  {
    title: 'Cost Price',
    align: 'center',
    key: 'costPrice',
    render: (row) => {
      return (
        <span>{row.costPrice!.toLocaleString('vi-VN')}</span>
      )
    },
  },
  {
    title: 'Sale Price',
    align: 'center',
    key: 'salePrice',
    render: (row) => {
      return (
        <span>{row.salePrice!.toLocaleString('vi-VN')}</span>
      )
    },
  },
  {
    title: '',
    align: 'center',
    key: 'action',
    render: (row) => {
      return (
        <span>{row.salePrice!.toLocaleString('vi-VN')}</span>
      )
    },
  },
])

const purchaseOrderId = route.params.purchaseOrderId
const expectedDate = ref(new Date().getTime())
const purchaseOrder = ref<PurchaseOrder.Data>({
  id: '',
  status: 'NEW',
  expectedDate: undefined,
  receivedDate: undefined,
  totalAmount: 0,
  type: 'PURCHASE',
  warehouseId: '',
  supplierId: '',
  details: [],
  note: '',
})

const formPurchaseOrderRef = ref()
const errors = ref<Error.ValidationError[]>([])

const purchaseOrderRules = ref(
  initRulesForm(purchaseOrder.value, (rule, value, callback, key) => {
    validateFieldFromErrors(errors, key, callback)
  }),
)

const referenceData = ref<ReferenceData.PurchaseOrder>({
  warehouses: [],
  suppliers: [],
  categories: [],
})

async function getReferenceData() {
  await PurchaseOrderService.getReferenceData()
    .then((res: any) => {
      referenceData.value = res.data
    })
}

function optionWarehouses() {
  return referenceData.value.warehouses.map(item => ({
    label: item.name,
    value: item.id,
  }))
}

function optionCategories() {
  return [
    {
      label: "All",
      value: "ALL", 
    },
    ...referenceData.value.categories.map((item) => ({
      label: item.name,
      value: item.id,
    })),
  ];
}

function optionSuppliers() {
  return referenceData.value.suppliers.map(item => ({
    label: item.name,
    value: item.id,
  }))
}

const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'id',
  sortDesc: true,
})
const dataRequestBody = ref<{
  productCategoryId: string,
  productUnitId: string,
  productOrigin: string,
}>({
  productCategoryId: optionCategories()[0].value || 'ALL',
  productUnitId: '',
  productOrigin: '',
})
const totalRecordsProduct = ref<number>(0)
const listProduct = ref<PurchaseOrder.Product[]>([])

async function getListProduct() {
  await ProductService.getListProduct(dataTableRequest.value, dataRequestBody.value)
    .then((res: any) => {
      listProduct.value = res.data.list
      totalRecordsProduct.value = res.data.totalRecords
    })
}

async function changePage(page: number, size: number) {
  dataTableRequest.value.currentPage = page
  dataTableRequest.value.perPage = size
  await getListProduct()
}

async function reloadTableProductFirst() {
  dataTableRequest.value.currentPage = 1
  await getListProduct()
}

async function reloadFormSearch() {
  dataTableRequest.value.currentPage = 1
  dataTableRequest.value.filter = ''
  dataRequestBody.value.productCategoryId = 'ALL'
  await getListProduct()
}


function openModalProduct() {
  showModalProduct()
}

function activePurchaseOrderDetails() {
  return (purchaseOrder.value.details || []).filter(
    (item) => item.delete === false
  )
}

function addProduct(product: PurchaseOrder.Product) {
  if (!purchaseOrder.value.details) {
    purchaseOrder.value.details = []
  }

  const existing = purchaseOrder.value.details.find(
    (item) => item.productId === product.id && !item.delete
  )

  if (existing) {
    if (existing.delete) {
      existing.quantity = 1
      existing.unitPrice = existing.unitPrice!
      existing.totalAmount = existing.unitPrice!
    }
    existing.quantity! += 1
    existing.totalAmount = existing.quantity! * existing.unitPrice!
  } else {
    purchaseOrder.value.details.push({
      id: '',
      quantity: 1,
      unitPrice: product.costPrice,
      totalAmount: product.costPrice,
      status: 'ACTIVE',
      purchaseOrderId: '',
      productId: product.id,
      productBarcode: product.barcode,
      productName: product.name,
      productUnit: product.productUnitName,
      delete: false
    })
  }

  hidenModalProduct()
}

async function createOrUpdatePurchaseOrder() {
  purchaseOrder.value.expectedDate = moment(expectedDate.value).toDate()
  purchaseOrder.value.details = purchaseOrder.value.details!.filter(
    (detail) => detail.productId && detail.delete !== true
  );
  await PurchaseOrderService.createOrUpdatePurchase(purchaseOrder.value)
    .then((res: any) => {
      if (res.isSuccess) {
        router.push({ name: 'purchase-order-management.list' })
      }
      else {
        // errors.value = res.data
        // formUserRef.value.validate()
      }
    })
}

async function getPurchaseOrder() {
  if (purchaseOrderId) {
    await PurchaseOrderService.getPurchaseOrder(purchaseOrderId.toString())
      .then((res: any) => {
        purchaseOrder.value = res.data
        expectedDate.value = moment(res.data.expectedDate).toDate().getTime()
      })
  }
}

onMounted(async () => {
  await getReferenceData()
  await getListProduct()
  if (purchaseOrderId.toString() !== ':purchaseOrderId' &&  purchaseOrderId.toString() !== 'new') {
    getPurchaseOrder()
  }
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card title="Purchase Order">
      <template #header-extra>
        <NButton
          v-if="purchaseOrder.id && purchaseOrder.status !== 'RECEIVED'"
          style="margin-left: 10px;"
          secondary
          type="error"
          @click="() => {
            purchaseOrder.status = 'CANCELED'
            createOrUpdatePurchaseOrder()
          }"
        >
          <NIcon size="18" :component="TrashSharp" style="margin-right: 5px;" />
          Cancel
        </NButton>
        <NButton
          secondary
          type="primary"
          @click="createOrUpdatePurchaseOrder()"
          style="margin-left: 10px;"
          v-if="purchaseOrder.status === 'NEW'"
        >
          <NIcon size="18" :component="Save" style="margin-right: 5px;" />
          {{ purchaseOrder.id ? 'Edit' : 'Add' }}
        </NButton>
        <NButton
          v-if="purchaseOrder.id && purchaseOrder.status === 'NEW'"
          style="margin-left: 10px;"
          secondary
          type="primary"
          @click="() => {
            purchaseOrder.status = 'CONFIRMED'
            createOrUpdatePurchaseOrder()
          }"
        >
          <NIcon size="18" :component="CheckboxOutline" style="margin-right: 5px;" />
          Confirm
        </NButton>
        <NButton
          v-if="purchaseOrder.id && purchaseOrder.status === 'CONFIRMED'"
          style="margin-left: 10px;"
          secondary
          type="primary"
          @click="() => {
            purchaseOrder.status = 'RECEIVED'
            createOrUpdatePurchaseOrder()
          }"
        >
          <NIcon size="18" :component="CheckboxOutline" style="margin-right: 5px;" />
          Receive
        </NButton>
      </template>

      <n-form
        ref="formUserRef"
        inline
        :label-width="80"
        :model="purchaseOrder"
        :rules="purchaseOrderRules"
      >
        <NGrid cols="3" y-gap="12" x-gap="24">
          <NGi :span="1">
            <n-form-item label="Supplier" path="supplier">
              <n-select
                v-model:value="purchaseOrder.supplierId"
                placeholder=""
                :options="optionSuppliers()"
                :disabled="purchaseOrder.status !== 'NEW'"
              />
            </n-form-item>
          </NGi>

          <NGi :span="1">
            <n-form-item label="Warehouse" path="warehouse">
              <n-select 
                v-model:value="purchaseOrder.warehouseId"
                placeholder=""
                :options="optionWarehouses()"
                :disabled="purchaseOrder.status !== 'NEW'"
              />
            </n-form-item>
          </NGi>

          <NGi :span="1">
            <n-form-item label="Expected Date" path="expectedDate">
              <n-date-picker
                v-model:value="expectedDate"
                value-format="yyyy-MM-dd"
                type="date"
                :disabled="purchaseOrder.status !== 'NEW'"
              />
            </n-form-item>
          </NGi>

          <NGi :span="3">
            <n-form-item label="Note" path="note">
              <n-input
                v-model:value="purchaseOrder.note"
                placeholder="Input Note"
                type="textarea"
                :autosize="{
                  minRows: 3,
                  maxRows: 5,
                }"
                :disabled="purchaseOrder.status !== 'NEW'"
              />
            </n-form-item>
          </NGi>
        </NGrid>
      </n-form>
    </n-card>
    <n-card title="Purchase Order Detail">
      <template #header-extra>
        <NButton secondary type="primary" @click="openModalProduct()" v-if="purchaseOrder.status === 'NEW'">
          <NIcon size="18" :component="Add" style="margin-right: 5px;" />Add
        </NButton>
      </template>
      <NSpace vertical size="large">
        <n-data-table ref="tableRef" :columns="columns" :data="activePurchaseOrderDetails()" />

        <n-modal
          v-model:show="isModalProduct"
          :mask-closable="false"
          preset="card"
          title="Product List"
          class="w-1000px"
          :segmented="{
            content: true,
            action: true,
          }"
        >
          <NSpace vertical size="large">
            <n-form ref="formRef" :model="dataTableRequest" label-placement="left" inline :show-feedback="false">
              <NGrid cols="3" y-gap="12" x-gap="24">
                <NGi :span="1">
                  <n-form-item label="Search" path="filter">
                    <n-input v-model:value="dataTableRequest.filter" placeholder="Product Name or Product Barcode" />
                  </n-form-item>
                </NGi>
                <NGi :span="1">
                  <n-form-item label="Category" path="filter">
                    <n-select v-model:value="dataRequestBody.productCategoryId" placeholder="" :options="optionCategories()" />
                  </n-form-item>
                </NGi>
                <NGi :span="1">
                  <n-flex justify="end">
                    <NButton type="primary" secondary @click="reloadTableProductFirst()">
                      <template #icon>
                        <icon-park-outline-search />
                      </template>
                      Search
                    </NButton>
                    <NButton strong secondary @click="reloadFormSearch()">
                      <template #icon>
                        <icon-park-outline-redo />
                      </template>
                      Reload
                    </NButton>
                  </n-flex>
                </NGi>
              </NGrid>
            </n-form>
            <n-data-table ref="tableProductRef" :columns="columnsProduct" :data="listProduct" :render-expand-icon="renderExpandIcon" />
            <Pagination :count="totalRecordsProduct" v-model:page="dataTableRequest.currentPage" @change="changePage" />
          </NSpace>
        </n-modal>
      </NSpace>
    </n-card>
  </NSpace>
</template>

<style scoped>
.__date-picker-trigger-qa68sz {
  width: 100% !important;
}
</style>
