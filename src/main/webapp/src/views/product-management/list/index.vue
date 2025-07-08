<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState } from 'naive-ui'
import { NButton, NSpace } from 'naive-ui'
import { router } from '@/router'
import { Add } from '@vicons/ionicons5'
import { ProductService } from '@/service/api/product-service'

const tableRef = ref()
const referenceData = ref<ReferenceData.Product>({
  productCategories: [],
  productUnits: [],
  productOrigins: [],
})
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'barcode',
  sortDesc: true,
})
const dataRequestBody = ref<Product.Filter>({
  productCategoryId: 'ALL',
  productOriginId: 'ALL',
  productUnitId: 'ALL'
})
const columns = ref<DataTableColumns<Product.DataTable>>([
  {
    title: 'Barcode',
    align: 'center',
    key: 'barcode',
    sorter: true,
    sortOrder: sortDefault('barcode'),
    render: (row) => {
      return (
        <NButton
          style="width: 100%"
          secondary
          type="primary"
          strong
          onClick={() => {
            router.push({
              name: 'product-management.product',
              params: { productId: row.id },
            })
          }}
        >
          {row.barcode}
        </NButton>
      )
    },
  },
  {
    title: 'Tên',
    align: 'center',
    key: 'name',
    sorter: true,
    sortOrder: sortDefault('name'),
  },
  {
    title: 'Danh mục',
    align: 'center',
    key: 'productCategoryName',
  },
  {
    title: 'SKU',
    align: 'center',
    key: 'sku',
    sorter: true,
    sortOrder: sortDefault('sku'),
  },
  {
    title: 'Tồn kho',
    align: 'center',
    key: 'inventoryQuantity',
    sorter: true,
    sortOrder: sortDefault('inventoryQuantity'),
  },
])
const columnsRef = ref<DataTableColumns<User.Data>>(columns.value)
const listProduct = ref<Product.DataTable[]>([])
const totalRecords = ref<number>(0)

function sortDefault(columnKey: string) {
  if (dataTableRequest.value.sortBy !== columnKey) {
    return false
  }
  return dataTableRequest.value.sortDesc ? 'descend' : 'ascend'
}

async function changePage(page: number, size: number) {
  dataTableRequest.value.currentPage = page
  dataTableRequest.value.perPage = size
  await getListProduct()
}

async function getListProduct() {
  await ProductService.getListProduct(dataTableRequest.value, dataRequestBody.value)
    .then((res: any) => {
      listProduct.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

async function getReferenceDataProduct() {
  await ProductService.getReferenceDataProduct()
    .then((res: any) => {
      referenceData.value = res.data
    })
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getListProduct()
}

async function reloadTable() {
  await getListProduct()
}

async function reloadSearch() {
  dataTableRequest.value.filter = ''
  dataRequestBody.value = {
    productCategoryId: 'ALL',
    productOriginId: 'ALL',
    productUnitId: 'ALL',
  }
  dataTableRequest.value.currentPage = 1
  await reloadTableFirst()
}

function sortTable(sorter: DataTableSortState) {
  columnsRef.value.forEach((column: any) => {
    if (column.key === sorter.columnKey) {
      column.sortOrder = sorter.order
    }
    else {
      column.sortOrder = false
    }
  })
  sortData(sorter)
}

function sortData(sorter: DataTableSortState) {
  if (!sorter.order) {
    dataTableRequest.value.sortBy = ''
    dataTableRequest.value.sortDesc = false
  }
  else {
    dataTableRequest.value.sortBy = sorter.columnKey.toString()
    dataTableRequest.value.sortDesc = sorter.order === 'descend'
  }
  reloadTable()
}

function optionCategories() {
  return [
    {
      label: 'Tất cả',
      value: 'ALL',
    },
    ...referenceData.value.productCategories.map(item => ({
      label: item.name,
      value: item.id,
    })),
  ]
}

onMounted(async () => {
  await getReferenceDataProduct()
  await reloadTableFirst()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card>
      <n-form :model="dataTableRequest" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="Danh mục" path="filter">
            <NSelect style="width: 150px;" v-model:value="dataRequestBody.productCategoryId" placeholder="" :options="optionCategories()" />
          </n-form-item>
          <n-form-item label="Tìm kiếm" path="filter">
            <n-input v-model:value="dataTableRequest.filter" placeholder="Từ khóa..." />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" secondary @click="reloadTableFirst()">
              <template #icon>
                <icon-park-outline-search />
              </template>
              Tìm kiếm
            </NButton>
            <NButton strong secondary @click="reloadSearch()">
              <template #icon>
                <icon-park-outline-redo />
              </template>
            </NButton>
          </n-flex>
        </n-flex>
      </n-form>
    </n-card>
    <n-card>
      <NSpace vertical size="large">
        <div class="flex gap-4">
          <NButton
            strong
            type="primary"
            secondary
            class="ml-a"
            @click="() => {
              router.push({
                name: 'product-management.product',
                params: { productId: 'new' },
              })
            }"
          >
            <template #icon>
              <NIcon size="18" :component="Add" />
            </template>
            Thêm
          </NButton>
        </div>
        <n-data-table ref="tableRef" :columns="columns" :data="listProduct" @update:sorter="sortTable" />
        <Pagination :count="totalRecords" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>
