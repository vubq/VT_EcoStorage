<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { NButton, NGi, NGrid, NIcon, NInputNumber, NSelect, NSpace } from 'naive-ui'
import { Add, CheckboxOutline, Save, TrashSharp } from '@vicons/ionicons5'
import { useRoute } from 'vue-router'
import { useBoolean } from '@/hooks'
import { initRulesForm, validateFieldFromErrors } from '@/utils/error'
import { PurchaseOrderService } from '@/service/api/purchase-order-service'
import { ProductService } from '@/service/api/product-service'
import { router } from '@/router'
import moment from 'moment'
import { WarehouseService } from '@/service/api/warehouse-service'

const route = useRoute()

const { bool: isModalProduct, setTrue: showModalProduct, setFalse: hidenModalProduct } = useBoolean(false)

const warehouse = ref<Warehouse.Data>({})
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
const columns = computed(() => {
  const baseColumns: DataTableColumns<PurchaseOrderDetail.Data> = [
    {
      title: 'Product Barcode',
      align: 'center',
      key: 'productBarcode',
      render: row => (
        <NButton style="width: 100%" secondary type="primary" strong>
          {row.productBarcode}
        </NButton>
      ),
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
      render: row =>
        purchaseOrder.value.status === 'NEW'
          ? h(NInputNumber, {
              value: row.quantity,
              min: 1,
              onUpdateValue: (val: number | null) => {
                row.quantity = val != null && val >= 1 ? val : 1
                row.totalAmount = row.quantity * row.unitPrice!
              },
            })
          : <span>{row.quantity}</span>,
    },
    {
      title: 'Unit Price',
      align: 'center',
      key: 'unitPrice',
      width: 180,
      render: row =>
        purchaseOrder.value.status === 'NEW'
          ? h(NInputNumber, {
              value: row.unitPrice,
              min: 1,
              onUpdateValue: (val: number | null) => {
                row.unitPrice = val != null && val >= 1 ? val : 1
                row.totalAmount = row.quantity! * row.unitPrice
              },
            })
          : <span>{row.unitPrice!.toLocaleString('vi-VN')}</span>,
    },
    {
      title: 'Total Amount',
      align: 'center',
      key: 'totalAmount',
      render: row => <span>{row.totalAmount!.toLocaleString('vi-VN')}</span>,
    },
    {
      title: '',
      align: 'center',
      key: '',
      render: row =>
        purchaseOrder.value.status === 'NEW'
          ? (
              <NButton secondary type="error" onClick={() => (row.delete = true)}>
                <NIcon size={18} component={TrashSharp} />
              </NButton>
            )
          : null,
    },
  ] as DataTableColumns<PurchaseOrderDetail.Data>

  // Nếu status === CONFIRMED thì thêm cột expand
  if (purchaseOrder.value.status === 'CONFIRMED') {
    baseColumns.splice(
      1,
      0,
      {
        type: 'expand',
        title: 'Location',
        align: 'center',
        width: 100,
        renderExpand: (row: any) => (
          <div>
            <NSpace vertical size="large">
              {(row.locations || []).map((l: any, index: number) => (
                <NGrid key={index} cols={5} y-gap={12} x-gap={24}>
                  <NGi span={1}><NSelect options={optionZones()} placeholder="Zone" v-model:value={l.zoneId} /></NGi>
                  <NGi span={1}><NSelect options={optionShelf(l.zoneId)} placeholder="Shelf" v-model:value={l.shelfId} /></NGi>
                  <NGi span={1}><NSelect options={optionFloor(l.zoneId, l.shelfId, row.productId!)} placeholder="Floor" v-model:value={l.locationId} /></NGi>
                  <NGi span={1}><NInputNumber v-model:value={l.quantity} placeholder="Quantity" /></NGi>
                  <NGi span={1}>
                    <NButton secondary type="error" onClick={() => removeLocation(row, index)}>
                      <NIcon size={18} component={TrashSharp} />
                    </NButton>
                  </NGi>
                </NGrid>
              ))}
              <NButton secondary type="primary" onClick={() => addLocation(row)}>
                <NIcon size={18} component={Add} />
              </NButton>
            </NSpace>
          </div>
        ),
      } as any,
    )
  }

  return baseColumns
})

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
])

function addLocation(row: any) {
  if (!Array.isArray(row.locations)) {
    row.locations = []
  }
  row.locations.push({ zoneId: null, shelfId: null, locationId: null, productId: row.productId, purchaseOrderDetailId: row.id, quantity: 0 })
}

function removeLocation(row: any, index: number) {
  row.locations.splice(index, 1)
}

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

function optionZones() {
  return warehouse.value.zones!.map(item => ({
    label: item.name,
    value: item.id,
  }))
}

function optionShelf(zoneId: string) {
  return warehouse.value.zones?.find(z => z.id === zoneId)?.shelves?.map(item => ({
    label: item.name,
    value: item.id,
  }))
}

function optionFloor(zoneId: string, shelfId: string, productId: string) {
  return warehouse.value.zones?.find(z => z.id === zoneId)?.shelves?.find(s => s.id === shelfId)?.floors?.map(item => ({
    label: item.floor,
    value: item.id,
    disabled: disabledOptionFloor(item.id!, productId),
  }))
}

function disabledOptionFloor(floorId: string, productId: string): boolean {
  return !!purchaseOrder.value.details
    ?.find(p => p.productId === productId)
    ?.locations
    ?.find(l => l.locationId === floorId)
}

function optionCategories() {
  return [
    {
      label: 'All',
      value: 'ALL',
    },
    ...referenceData.value.categories.map(item => ({
      label: item.name,
      value: item.id,
    })),
  ]
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
  productCategoryId: string
  productUnitId: string
  productOrigin: string
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
    item => item.delete === false,
  )
}

function addProduct(product: PurchaseOrder.Product) {
  if (!purchaseOrder.value.details) {
    purchaseOrder.value.details = []
  }

  const existing = purchaseOrder.value.details.find(
    item => item.productId === product.id && !item.delete,
  )

  if (existing) {
    if (existing.delete) {
      existing.quantity = 1
      existing.unitPrice = existing.unitPrice!
      existing.totalAmount = existing.unitPrice!
    }
    existing.quantity! += 1
    existing.totalAmount = existing.quantity! * existing.unitPrice!
  }
  else {
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
      delete: false,
    })
  }

  hidenModalProduct()
}

async function createOrUpdatePurchaseOrder(status: string) {
  purchaseOrder.value.expectedDate = moment(expectedDate.value).toDate()
  purchaseOrder.value.details = purchaseOrder.value.details!.filter(
    detail => detail.productId && detail.delete !== true,
  )
  await PurchaseOrderService.createOrUpdatePurchase({
    ...purchaseOrder.value,
    status,
  })
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

async function getWarehouse() {
  await WarehouseService.getWarehouse(purchaseOrder.value.warehouseId!)
    .then((res: any) => {
      warehouse.value = res.data
    })
}

onMounted(async () => {
  await getReferenceData()
  await getListProduct()
  if (purchaseOrderId.toString() !== ':purchaseOrderId' && purchaseOrderId.toString() !== 'new') {
    await getPurchaseOrder()
    await getWarehouse()
  }
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card title="Purchase Order">
      <template #header-extra>
        <n-tag v-if="purchaseOrder.status === 'CANCELED'" type="error">
          Canceled
        </n-tag>
        <n-tag v-if="purchaseOrder.status === 'RECEIVED'" type="success">
          Received
        </n-tag>
        <NButton
          v-if="purchaseOrder.id && purchaseOrder.status !== 'RECEIVED' && purchaseOrder.status !== 'CANCELED'"
          style="margin-left: 10px;"
          secondary
          type="error"
          @click="() => {
            createOrUpdatePurchaseOrder('CANCELED')
          }"
        >
          <NIcon size="18" :component="TrashSharp" style="margin-right: 5px;" />
          Cancel
        </NButton>
        <NButton
          v-if="purchaseOrder.status === 'NEW'"
          secondary
          type="primary"
          style="margin-left: 10px;"
          @click="createOrUpdatePurchaseOrder('NEW')"
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
            createOrUpdatePurchaseOrder('CONFIRMED')
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
            createOrUpdatePurchaseOrder('RECEIVED')
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
              <NSelect
                v-model:value="purchaseOrder.supplierId"
                placeholder=""
                :options="optionSuppliers()"
                :disabled="purchaseOrder.status !== 'NEW'"
              />
            </n-form-item>
          </NGi>

          <NGi :span="1">
            <n-form-item label="Warehouse" path="warehouse">
              <NSelect
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
        <NButton v-if="purchaseOrder.status === 'NEW'" secondary type="primary" @click="openModalProduct()">
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
                    <NSelect v-model:value="dataRequestBody.productCategoryId" placeholder="" :options="optionCategories()" />
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
            <n-data-table ref="tableProductRef" :columns="columnsProduct" :data="listProduct" :default-expand-all="true" />
            <Pagination v-model:page="dataTableRequest.currentPage" :count="totalRecordsProduct" @change="changePage" />
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
