<script setup lang="tsx">
import type { DataTableColumns } from 'naive-ui'
import { NButton, NGi, NGrid, NIcon, NInputNumber, NSelect, NSpace } from 'naive-ui'
import { Add, CheckboxOutline, Save, TrashSharp } from '@vicons/ionicons5'
import { useRoute } from 'vue-router'
import { useBoolean } from '@/hooks'
import { initRulesForm, validateFieldFromErrors } from '@/utils/error'
import { ExportOrderService } from '@/service/api/export-order-service'
import { ProductService } from '@/service/api/product-service'
import { router } from '@/router'
import moment from 'moment'
import { WarehouseService } from '@/service/api/warehouse-service'

const route = useRoute()

const { bool: isModalProduct, setTrue: showModalProduct, setFalse: hidenModalProduct } = useBoolean(false)

const warehouse = ref<Warehouse.Data>({})
const exportOrderId = route.params.exportOrderId
const expectedDate = ref(new Date().getTime())
const exportOrder = ref<ExportOrder.Data>({
  id: '',
  status: 'NEW',
  expectedDate: undefined,
  deliveredDate: undefined,
  totalAmount: 0,
  type: 'EXPORT',
  warehouseId: '',
  customerId: '',
  details: [],
  note: '',
})
const columns = computed(() => {
  const baseColumns: DataTableColumns<ExportOrderDetail.Data> = [
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
      title: 'Location',
      align: 'center',
      key: 'location',
    },
    {
      title: 'Quantity',
      align: 'center',
      key: 'quantity',
      width: 150,
      render: row =>
        exportOrder.value.status === 'NEW'
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
        exportOrder.value.status === 'NEW'
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
        exportOrder.value.status === 'NEW'
          ? (
              <NButton secondary type="error" onClick={() => (row.delete = true)}>
                <NIcon size={18} component={TrashSharp} />
              </NButton>
            )
          : null,
    },
  ] as DataTableColumns<ExportOrderDetail.Data>

  // // Nếu status === CONFIRMED thì thêm cột expand
  // if (exportOrder.value.status === 'CONFIRMED') {
  //   baseColumns.splice(
  //     1,
  //     0,
  //     {
  //       type: 'expand',
  //       title: 'Location',
  //       align: 'center',
  //       width: 100,
  //       renderExpand: (row: any) => (
  //         <div>
  //           <NSpace vertical size="large">
  //             {(row.locations || []).map((l: any, index: number) => (
  //               <NGrid key={index} cols={4} y-gap={12} x-gap={24}>
  //                 <NGi span={1}><NSelect options={optionZones()} placeholder="Zone" v-model:value={l.zoneId} /></NGi>
  //                 <NGi span={1}><NSelect options={optionShelf(l.zoneId)} placeholder="Shelf" v-model:value={l.shelfId} /></NGi>
  //                 <NGi span={1}><NSelect options={optionFloor(l.zoneId, l.shelfId, row.productId!)} placeholder="Floor" v-model:value={l.locationId} /></NGi>
  //                 <NGi span={1}>
  //                   <NButton secondary type="error" onClick={() => removeLocation(row, index)}>
  //                     <NIcon size={18} component={TrashSharp} />
  //                   </NButton>
  //                 </NGi>
  //               </NGrid>
  //             ))}
  //             <NButton secondary type="primary" onClick={() => addLocation(row)}>
  //               <NIcon size={18} component={Add} />
  //             </NButton>
  //           </NSpace>
  //         </div>
  //       ),
  //     } as any,
  //   )
  // }

  return baseColumns
})

const columnsProduct = ref<DataTableColumns<Product.ProductByLocation>>([
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
          {row.productBarcode}
        </NButton>
      )
    },
  },
  {
    title: 'Product Name',
    align: 'center',
    key: 'productName',
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
    title: 'Location',
    align: 'center',
    key: 'location',
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
        <span>{row.productCostPrice!.toLocaleString('vi-VN')}</span>
      )
    },
  },
  {
    title: 'Sale Price',
    align: 'center',
    key: 'salePrice',
    render: (row) => {
      return (
        <span>{row.productSalePrice!.toLocaleString('vi-VN')}</span>
      )
    },
  },
])

// function addLocation(row: any) {
//   if (!Array.isArray(row.locations)) {
//     row.locations = []
//   }
//   row.locations.push({ zoneId: null, shelfId: null, locationId: null, productId: row.productId, purchaseOrderDetailId: row.id })
// }

// function removeLocation(row: any, index: number) {
//   row.locations.splice(index, 1)
// }

const formExportOrderRef = ref()
const errors = ref<Error.ValidationError[]>([])

const exportOrderRules = ref(
  initRulesForm(exportOrder.value, (rule, value, callback, key) => {
    validateFieldFromErrors(errors, key, callback)
  }),
)

const productWarehouse = ref<Warehouse.Data>({})

const referenceData = ref<ReferenceData.ExportOrder>({
  warehouses: [],
  customers: [],
  categories: [],
})

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

const dataRequestBody = ref<Product.FilterProductByLocation>({
  productCategoryId: optionCategories()[0].value || 'ALL',
  zoneId: 'ALL',
  shelfId: 'ALL',
  floorId: 'ALL',
  warehouseId: 'ALL',
})

function optionZones() {
  return [
    { label: 'All', value: 'ALL' },
    ...(productWarehouse.value.zones ?? []).map(item => ({
      label: item.name,
      value: item.id,
    })),
  ]
}

function optionShelfs() {
  const selectedZone = productWarehouse.value.zones?.find(
    z => z.id === dataRequestBody.value.zoneId,
  )

  return [
    { label: 'All', value: 'ALL' },
    ...(selectedZone?.shelves ?? []).map(item => ({
      label: item.name,
      value: item.id,
    })),
  ]
}

function optionFloors() {
  const selectedZone = productWarehouse.value.zones?.find(
    z => z.id === dataRequestBody.value.zoneId,
  )
  const selectedShelf = selectedZone?.shelves?.find(
    s => s.id === dataRequestBody.value.shelfId,
  )

  return [
    { label: 'All', value: 'ALL' },
    ...(selectedShelf?.floors ?? []).map(item => ({
      label: item.floor,
      value: item.id,
    })),
  ]
}

async function getReferenceData() {
  await ExportOrderService.getReferenceData()
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

// function optionZones() {
//   return warehouse.value.zones!.map(item => ({
//     label: item.name,
//     value: item.id,
//   }))
// }

// function optionShelf(zoneId: string) {
//   return warehouse.value.zones?.find(z => z.id === zoneId)?.shelves?.map(item => ({
//     label: item.name,
//     value: item.id,
//   }))
// }

// function optionFloor(zoneId: string, shelfId: string, productId: string) {
//   return warehouse.value.zones?.find(z => z.id === zoneId)?.shelves?.find(s => s.id === shelfId)?.floors?.map(item => ({
//     label: item.floor,
//     value: item.id,
//     disabled: disabledOptionFloor(item.id!, productId),
//   }))
// }

// function disabledOptionFloor(floorId: string, productId: string): boolean {
//   return !!purchaseOrder.value.details
//     ?.find(p => p.productId === productId)
//     ?.locations
//     ?.find(l => l.locationId === floorId)
// }

function optionCustomers() {
  return referenceData.value.customers.map(item => ({
    label: item.name,
    value: item.id,
  }))
}

const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'productBarcode',
  sortDesc: true,
})

const totalRecordsProduct = ref<number>(0)
const listProduct = ref<ExportOrder.Product[]>([])

async function getListProduct() {
  await ProductService.getListProductInventoryByLocation(dataTableRequest.value, dataRequestBody.value)
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
  dataRequestBody.value.floorId = 'ALL'
  dataRequestBody.value.zoneId = 'ALL'
  dataRequestBody.value.shelfId = 'ALL'
  await getListProduct()
}

function openModalProduct() {
  showModalProduct()
}

function activeExportOrderDetails() {
  return (exportOrder.value.details || []).filter(
    item => item.delete === false,
  )
}

function addProduct(product: Product.ProductByLocation) {
  if (!exportOrder.value.details) {
    exportOrder.value.details = []
  }

  const existing = exportOrder.value.details.find(
    item => item.locationId === product.locationId && !item.delete,
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
    exportOrder.value.details.push({
      id: '',
      quantity: 1,
      unitPrice: product.productSalePrice,
      totalAmount: product.productSalePrice,
      status: 'ACTIVE',
      exportOrderId: '',
      productId: product.productId,
      productBarcode: product.productBarcode,
      productName: product.productName,
      productUnit: product.productUnitName,
      location: product.location,
      locationId: product.locationId,
      delete: false,
    })
  }

  hidenModalProduct()
}

async function createOrUpdateExportOrder(status: string) {
  exportOrder.value.expectedDate = moment(expectedDate.value).toDate()
  exportOrder.value.details = exportOrder.value.details!.filter(
    detail => detail.productId && detail.delete !== true,
  )
  await ExportOrderService.createOrUpdateExportOrder({
    ...exportOrder.value,
    status,
  })
    .then((res: any) => {
      if (res.isSuccess) {
        router.push({ name: 'export-order-management.list' })
      }
      else {
        // errors.value = res.data
        // formUserRef.value.validate()
      }
    })
}

async function getExportOrder() {
  if (exportOrderId) {
    await ExportOrderService.getExportOrder(exportOrderId.toString())
      .then((res: any) => {
        exportOrder.value = res.data
        expectedDate.value = moment(res.data.expectedDate).toDate().getTime()
      })
  }
}

async function getWarehouse() {
  await WarehouseService.getWarehouse(exportOrder.value.warehouseId!)
    .then((res: any) => {
      productWarehouse.value = res.data
    })
}

watch(
  () => exportOrder.value.warehouseId,
  (newValue) => {
    if (newValue) {
      getWarehouse()
    }
  },
)

watch(
  () => dataRequestBody.value.zoneId,
  (newZoneId) => {
    if (newZoneId) {
      dataRequestBody.value.shelfId = 'ALL'
      dataRequestBody.value.floorId = 'ALL'
    }
  },
)

watch(
  () => dataRequestBody.value.shelfId,
  (newZoneId) => {
    if (newZoneId) {
      dataRequestBody.value.floorId = 'ALL'
    }
  },
)

onMounted(async () => {
  await getReferenceData()
  await getListProduct()
  if (exportOrderId.toString() !== ':exportOrderId' && exportOrderId.toString() !== 'new') {
    await getExportOrder()
    // await getWarehouse()
  }
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card title="Export Order">
      <template #header-extra>
        <n-tag v-if="exportOrder.status === 'CANCELED'" type="error">
          Canceled
        </n-tag>
        <n-tag v-if="exportOrder.status === 'DELIVERED'" type="success">
          Delivered
        </n-tag>
        <NButton
          v-if="exportOrder.id && exportOrder.status !== 'DELIVERED' && exportOrder.status !== 'CANCELED'"
          style="margin-left: 10px;"
          secondary
          type="error"
          @click="() => {
            createOrUpdateExportOrder('CANCELED')
          }"
        >
          <NIcon size="18" :component="TrashSharp" style="margin-right: 5px;" />
          Cancel
        </NButton>
        <NButton
          v-if="exportOrder.status === 'NEW'"
          secondary
          type="primary"
          style="margin-left: 10px;"
          @click="createOrUpdateExportOrder('NEW')"
        >
          <NIcon size="18" :component="Save" style="margin-right: 5px;" />
          {{ exportOrder.id ? 'Edit' : 'Add' }}
        </NButton>
        <NButton
          v-if="exportOrder.id && exportOrder.status === 'NEW'"
          style="margin-left: 10px;"
          secondary
          type="primary"
          @click="() => {
            createOrUpdateExportOrder('CONFIRMED')
          }"
        >
          <NIcon size="18" :component="CheckboxOutline" style="margin-right: 5px;" />
          Confirm
        </NButton>
        <NButton
          v-if="exportOrder.id && exportOrder.status === 'CONFIRMED'"
          style="margin-left: 10px;"
          secondary
          type="primary"
          @click="() => {
            createOrUpdateExportOrder('DELIVERED')
          }"
        >
          <NIcon size="18" :component="CheckboxOutline" style="margin-right: 5px;" />
          Delivered
        </NButton>
      </template>

      <n-form
        ref="formUserRef"
        inline
        :label-width="80"
        :model="exportOrder"
        :rules="exportOrderRules"
      >
        <NGrid cols="3" y-gap="12" x-gap="24">
          <NGi :span="1">
            <n-form-item label="Customer" path="supplier">
              <NSelect
                v-model:value="exportOrder.customerId"
                placeholder=""
                :options="optionCustomers()"
                :disabled="exportOrder.status !== 'NEW'"
              />
            </n-form-item>
          </NGi>

          <NGi :span="1">
            <n-form-item label="Warehouse" path="warehouse">
              <NSelect
                v-model:value="exportOrder.warehouseId"
                placeholder=""
                :options="optionWarehouses()"
                :disabled="exportOrder.status !== 'NEW'"
              />
            </n-form-item>
          </NGi>

          <NGi :span="1">
            <n-form-item label="Expected Date" path="expectedDate">
              <n-date-picker
                v-model:value="expectedDate"
                value-format="yyyy-MM-dd"
                type="date"
                :disabled="exportOrder.status !== 'NEW'"
              />
            </n-form-item>
          </NGi>

          <NGi :span="3">
            <n-form-item label="Note" path="note">
              <n-input
                v-model:value="exportOrder.note"
                placeholder="Input Note"
                type="textarea"
                :autosize="{
                  minRows: 3,
                  maxRows: 5,
                }"
                :disabled="exportOrder.status !== 'NEW'"
              />
            </n-form-item>
          </NGi>
        </NGrid>
      </n-form>
    </n-card>
    <n-card title="Export Order Detail">
      <template #header-extra>
        <NButton
          v-if="exportOrder.status === 'NEW'"
          secondary
          type="primary"
          @click="async () => {
            if (exportOrder.warehouseId) {
              dataRequestBody.warehouseId = exportOrder.warehouseId
              await getListProduct()
              openModalProduct()
            }
          }"
        >
          <NIcon size="18" :component="Add" style="margin-right: 5px;" />Add
        </NButton>
      </template>
      <NSpace vertical size="large">
        <n-data-table ref="tableRef" :columns="columns" :data="activeExportOrderDetails()" />

        <n-modal
          v-model:show="isModalProduct"
          :mask-closable="false"
          preset="card"
          title="Product List"
          class="w-1200px"
          :segmented="{
            content: true,
            action: true,
          }"
        >
          <NSpace vertical size="large">
            <n-form ref="formRef" :model="dataTableRequest" label-placement="left" inline :show-feedback="false" label-width="80">
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
                  <n-form-item label="Zone" path="filter">
                    <NSelect v-model:value="dataRequestBody.zoneId" placeholder="" :options="optionZones()" />
                  </n-form-item>
                </NGi>
                <NGi :span="1">
                  <n-form-item label="Shelf" path="filter">
                    <NSelect v-model:value="dataRequestBody.shelfId" placeholder="" :options="optionShelfs()" />
                  </n-form-item>
                </NGi>
                <NGi :span="1">
                  <n-form-item label="Floor" path="filter">
                    <NSelect v-model:value="dataRequestBody.floorId" placeholder="" :options="optionFloors()" />
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
