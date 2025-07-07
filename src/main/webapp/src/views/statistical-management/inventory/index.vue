<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState } from 'naive-ui'
import { NButton, NSpace } from 'naive-ui'
import { Add } from '@vicons/ionicons5'
import { ProductService } from '@/service/api/product-service'
import { useBoolean } from '@/hooks'
import { InventoryService } from '@/service/api/inventory-service'

const { bool: visible, setTrue: openModal, setFalse: hideModal } = useBoolean(false)
const tableRef = ref()
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'id',
  sortDesc: true,
})
const categoryId = ref<string>('')
const columns = ref<DataTableColumns<Category.Data>>([
  {
    title: 'Id',
    align: 'center',
    key: 'id',
    render: (row) => {
      return (
        <NButton
          secondary
          type="primary"
          strong
          onClick={() => getCategory(row.id!)}
        >
          {row.id}
        </NButton>
      )
    },
  },
  {
    title: 'Name',
    align: 'center',
    key: 'name',
  },
])
const columnsRef = ref<DataTableColumns<User.Data>>(columns.value)
const listCategory = ref<Category.Data[]>([])
const category = ref<Category.Data>({
  name: '',
  status: 'ACTIVE',
})
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
  await getListCategory()
}

async function getListCategory() {
  await ProductService.getListCategory(dataTableRequest.value)
    .then((res: any) => {
      listCategory.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getListCategory()
}

async function reloadTable() {
  await getListCategory()
}

async function getCategory(id: string) {
  await ProductService.getCategory(id)
    .then((res: any) => {
      category.value = res.data
      openModal()
    })
}

async function createOrUpdateCategory() {
  await ProductService.createOrUpdateCategory(category.value)
    .then((res: any) => {
      if (res.isSuccess) {
        hideModal()
        reloadTableFirst()
      }
    })
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

const inventoryList = ref<Inventory.Data[]>([])

async function getInventory() {
  await InventoryService.getInventory()
    .then((res: any) => {
      if (res.isSuccess) {
        inventoryList.value = res.data
        console.log(res)
      }
    })
}

function getWarehouseRowspan(warehouse: Inventory.Data): number {
  if (!warehouse.products) return 1
  return warehouse.products.reduce((sum, product) => {
    return sum + (product.locations?.length || 1)
  }, 0)
}

onMounted(() => {
  getInventory()
  getListCategory()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card>
      <n-form ref="formRef" :model="dataTableRequest" label-placement="left" inline :show-feedback="false">
        <n-flex>
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
            <NButton strong secondary @click="reloadTableFirst()">
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
        <n-table :single-line="false" cellspacing="0" cellpadding="5">
          <thead>
            <tr>
              <th>Kho</th>
              <th>Dản phẩm</th>
              <th>Barcode</th>
              <th>SKU</th>
              <th>Tồn kho</th>
              <th>Vị trí</th>
              <th>Tồn kho theo vị trí</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="(warehouse, wIndex) in inventoryList" :key="warehouse.warehouseId">
              <template v-for="(product, pIndex) in warehouse.products || []" :key="product.productId">
                <template v-for="(location, lIndex) in product.locations || []" :key="location.locationId">
                  <tr>
                    <!-- Warehouse rowspan -->
                    <td v-if="pIndex === 0 && lIndex === 0"
                        :rowspan="getWarehouseRowspan(warehouse)">
                      {{ warehouse.warehouseName }}
                    </td>

                    <!-- Product info rowspan -->
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      {{ product.productName }}
                    </td>
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      {{ product.productBarcode }}
                    </td>
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      {{ product.productSKU }}
                    </td>
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      {{ product.inventoryQuantity }}
                    </td>

                    <!-- Location -->
                    <td>{{ location.locationName }}</td>
                    <td>{{ location.inventoryQuantity }}</td>
                  </tr>
                </template>
              </template>
            </template>
          </tbody>
        </n-table>
      </NSpace>
    </n-card>
  </NSpace>
</template>
