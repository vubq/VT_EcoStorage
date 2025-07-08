<script setup lang="tsx">
import { NButton, NSpace } from 'naive-ui'
import { InventoryService } from '@/service/api/inventory-service'
import { useAppStore } from '@/store'
import { router } from '@/router'
import ExcelJS from 'exceljs'
import { saveAs } from 'file-saver'
import moment from 'moment'

const appStore = useAppStore()

const inventoryList = ref<Inventory.Data[]>([])
const referenceData = ref<ReferenceData.Inventory>({
  warehouses: [],
  categories: [],
})
const warehouseId = ref<string>('ALL')
const productCategoryId = ref<string>('ALL')
const keyword = ref<string>('')

async function getReferenceData() {
  appStore.showProgress && window.$loadingBar?.start()
  await InventoryService.getReferenceData()
    .then((res: any) => {
      referenceData.value = res.data
    })
    .finally(() => appStore.showProgress && window.$loadingBar?.finish())
}

async function getInventory() {
  await InventoryService.getInventory(warehouseId.value, productCategoryId.value, keyword.value)
    .then((res: any) => {
      if (res.isSuccess) {
        inventoryList.value = res.data
      }
    })
}

function getWarehouseRowspan(warehouse: Inventory.Data): number {
  if (!warehouse.products)
    return 1
  return warehouse.products.reduce((sum, product) => {
    return sum + (product.locations?.length || 1)
  }, 0)
}

function optionWarehouses() {
  return [
    {
      label: 'Tất cả',
      value: 'ALL',
    },
    ...referenceData.value.warehouses.map(item => ({
      label: item.name,
      value: item.id,
    })),
  ]
}

function optionProductCategories() {
  return [
    {
      label: 'Tất cả',
      value: 'ALL',
    },
    ...referenceData.value.categories.map(item => ({
      label: item.name,
      value: item.id,
    })),
  ]
}

function reloadFilter() {
  keyword.value = ''
  warehouseId.value = 'ALL'
  productCategoryId.value = 'ALL'
  getInventory()
}

async function exportToExcel() {
  const workbook = new ExcelJS.Workbook()
  const sheet = workbook.addWorksheet('Tồn kho')

  // Header
  const headers = [
    'Kho',
    'Sản phẩm',
    'Barcode',
    'SKU',
    'Danh mục',
    'Đơn vị tính',
    'Tồn kho',
    'Vị trí',
    'Tồn kho theo vị trí',
  ]
  sheet.addRow(headers)
  const headerRow = sheet.getRow(1)
  headerRow.eachCell((cell) => {
    cell.font = { bold: true }
    cell.fill = { type: 'pattern', pattern: 'solid', fgColor: { argb: 'FFE0E0E0' } }
    cell.alignment = { horizontal: 'center', vertical: 'middle' }
    cell.border = { top: { style: 'thin' }, bottom: { style: 'thin' }, left: { style: 'thin' }, right: { style: 'thin' } }
  })

  // Dữ liệu
  let rowIndex = 2
  inventoryList.value.forEach((warehouse) => {
    const warehouseStart = rowIndex

    warehouse.products?.forEach((product) => {
      const productStart = rowIndex

      product.locations?.forEach((location) => {
        const row = sheet.addRow([
          warehouse.warehouseName,
          product.productName,
          product.productBarcode,
          product.productSKU,
          product.productCategoryName,
          product.productUnitName,
          product.inventoryQuantity,
          location.locationName,
          location.inventoryQuantity,
        ])
        row.eachCell((cell) => {
          cell.alignment = { vertical: 'top' }
          cell.border = { top: { style: 'thin' }, bottom: { style: 'thin' }, left: { style: 'thin' }, right: { style: 'thin' } }
        })
        rowIndex++
      })

      // Merge sản phẩm
      if ((product.locations?.length || 0) > 1) {
        for (let col = 2; col <= 7; col++) {
          sheet.mergeCells(productStart, col, rowIndex - 1, col)
        }
      }
    })

    // Merge kho
    if (rowIndex - warehouseStart > 1) {
      sheet.mergeCells(warehouseStart, 1, rowIndex - 1, 1)
    }
  })

  // Auto width
  sheet.columns.forEach((col) => {
    let max = 0
    col.eachCell?.({ includeEmpty: true }, (c) => {
      max = Math.max(max, String(c.value || '').length)
    })
    col.width = max + 2
  })

  const buffer = await workbook.xlsx.writeBuffer()
  const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
  saveAs(blob, `TonKho_${moment().format('YYYYMMDD_HHmmss')}.xlsx`)
}

onMounted(async () => {
  await getReferenceData()
  await getInventory()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card>
      <n-form label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="Kho">
            <n-select
              v-model:value="warehouseId"
              placeholder=""
              :options="optionWarehouses()"
              style="width: 150px;"
            />
          </n-form-item>
          <n-form-item label="Danh mục">
            <n-select
              v-model:value="productCategoryId"
              placeholder=""
              :options="optionProductCategories()"
              style="width: 150px;"
            />
          </n-form-item>
          <n-form-item label="Tìm kiếm" path="filter">
            <n-input v-model:value="keyword" placeholder="Từ khóa..." />
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" secondary @click="getInventory()">
              <template #icon>
                <icon-park-outline-search />
              </template>
              Lọc
            </NButton>
            <NButton strong secondary @click="reloadFilter()">
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
          <NButton type="primary" strong secondary class="ml-a" @click="exportToExcel()">
            <template #icon>
              <icon-park-outline-download />
            </template>
            Xuất Excel
          </NButton>
        </div>
        <n-table :single-line="false" cellspacing="0" cellpadding="5">
          <thead>
            <tr>
              <th>Kho</th>
              <th>Sản phẩm</th>
              <th>Barcode</th>
              <th>SKU</th>
              <th>Danh mục</th>
              <th>Đơn vị tính</th>
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
                    <td
                      v-if="pIndex === 0 && lIndex === 0"
                      :rowspan="getWarehouseRowspan(warehouse)"
                    >
                      {{ warehouse.warehouseName }}
                    </td>

                    <!-- Product info rowspan -->
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      {{ product.productName }}
                    </td>
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      <n-a
                        href="#"
                        class="underline-on-hover"
                        internal="true"
                        @click.prevent="() => {
                          router.push({
                            name: 'product-management.product',
                            params: { productId: product.productId },
                          })
                        }"
                      >
                        {{ product.productBarcode }}
                      </n-a>
                    </td>
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      {{ product.productSKU }}
                    </td>
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      {{ product.productCategoryName }}
                    </td>
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      {{ product.productUnitName }}
                    </td>
                    <td v-if="lIndex === 0" :rowspan="product.locations?.length || 1">
                      {{ product.inventoryQuantity }}
                    </td>

                    <!-- Location -->
                    <td>
                      <n-a
                        href="#"
                        class="underline-on-hover"
                        internal="true"
                        @click.prevent="() => {
                          router.push({ name: 'warehouse-management', query: { locationId: location.locationId } })
                        }"
                      >
                        {{ location.locationName }}
                      </n-a>
                    </td>
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
