<script setup lang="tsx">
import { NSpace } from 'naive-ui'
import { StatisticalService } from '@/service/api/statistical-service'
import moment from 'moment'
import { useAppStore } from '@/store'
import { router } from '@/router'
import * as XLSX from 'xlsx'
import { saveAs } from 'file-saver'

const statisticalList = ref<Statistical.Data[]>([])

const appStore = useAppStore()

async function getStatistical() {
  await StatisticalService.getStatistical(
    moment(range.value[0]).format("YYYY-MM-DD"),
    moment(range.value[1]).format("YYYY-MM-DD"),
    warehouseId.value,
    keyword.value,
    onlyWithTransaction.value
  )
    .then((res: any) => {
      if (res.isSuccess) {
        statisticalList.value = res.data
        console.log(res)
      }
    })
}

const range = ref<[number, number]>([
  new Date(new Date().setMonth(new Date().getMonth() - 1)).getTime(),
  Date.now()
])
const onlyWithTransaction = ref<boolean>(false)
const warehouseId = ref<string>('ALL')
const keyword = ref<string>('')

const referenceData = ref<ReferenceData.Statistical>({
  warehouses: [],
})

async function getReferenceData() {
  appStore.showProgress && window.$loadingBar?.start()
  await StatisticalService.getReferenceData()
    .then((res: any) => {
      referenceData.value = res.data
    })
    .finally(() => appStore.showProgress && window.$loadingBar?.finish())
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
    }))
  ]
}

function reloadFilter() {
  range.value = [
    new Date(new Date().setMonth(new Date().getMonth() - 1)).getTime(),
    Date.now()
  ]
  warehouseId.value = 'ALL'
  keyword.value = ''
  onlyWithTransaction.value = false
  getStatistical()
}

function exportToExcel() {
  const aoa: any[][] = []

  // Header
  aoa.push([
    'Kho',
    'Sản phẩm',
    'Barcode',
    'SKU',
    'Tổng nhập (SL)',
    'Tổng nhập (Tiền)',
    'Tổng xuất (SL)',
    'Tổng xuất (Tiền)',
  ])

  const merges: XLSX.Range[] = []
  let currentRow = 1

  statisticalList.value.forEach((warehouse) => {
    const productList = warehouse.products || []

    productList.forEach((product) => {
      aoa.push([
        warehouse.warehouseName,
        product.productName,
        product.productBarcode,
        product.productSKU,
        (product.totalImportQuantity ?? 0).toLocaleString('vi-VN'),
        (product.totalImportAmount ?? 0).toLocaleString('vi-VN'),
        (product.totalExportQuantity ?? 0).toLocaleString('vi-VN'),
        (product.totalExportAmount ?? 0).toLocaleString('vi-VN'),
      ])
    })

    if (productList.length > 1) {
      merges.push({
        s: { r: currentRow, c: 0 },
        e: { r: currentRow + productList.length - 1, c: 0 },
      })
    }

    currentRow += productList.length
  })

  const worksheet = XLSX.utils.aoa_to_sheet(aoa)

  // Merge ô theo kho
  worksheet['!merges'] = merges

  // Set độ rộng cột (dãn khoảng cách)
  worksheet['!cols'] = [
    { wch: 20 }, // Kho
    { wch: 25 }, // Sản phẩm
    { wch: 20 }, // Barcode
    { wch: 20 }, // SKU
    { wch: 15 }, // Tổng nhập SL
    { wch: 18 }, // Tổng nhập Tiền
    { wch: 15 }, // Tổng xuất SL
    { wch: 18 }, // Tổng xuất Tiền
  ]

  // Thêm border cho từng ô
  const borderAll = {
    top: { style: 'thin' },
    bottom: { style: 'thin' },
    left: { style: 'thin' },
    right: { style: 'thin' }
  }

  const range = XLSX.utils.decode_range(worksheet['!ref']!)
  for (let R = range.s.r; R <= range.e.r; ++R) {
    for (let C = range.s.c; C <= range.e.c; ++C) {
      const cell_address = { c: C, r: R }
      const cell_ref = XLSX.utils.encode_cell(cell_address)
      if (!worksheet[cell_ref]) continue

      if (!worksheet[cell_ref].s) worksheet[cell_ref].s = {}
      worksheet[cell_ref].s.border = borderAll
    }
  }

  // Tạo workbook và export
  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, 'Thống kê')

  const excelBuffer = XLSX.write(workbook, {
    bookType: 'xlsx',
    type: 'array',
    cellStyles: true // quan trọng để border áp dụng
  })

  const blob = new Blob([excelBuffer], { type: 'application/octet-stream' })
  saveAs(blob, `ThongKeKho_${moment().format('YYYYMMDD_HHmmss')}.xlsx`)
}

onMounted(async () => {
  await getReferenceData()
  await getStatistical()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card>
      <n-form label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="Thời gian">
            <n-date-picker v-model:value="range" type="daterange" />
          </n-form-item>
          <n-form-item label="Kho">
            <n-select
              v-model:value="warehouseId"
              placeholder=""
              :options="optionWarehouses()"
              style="width: 150px;"
            />
          </n-form-item>
          <n-form-item label="Tìm kiếm" path="filter">
            <n-input v-model:value="keyword" placeholder="Từ khóa..." />
          </n-form-item>
          <n-form-item label="Chỉ nhập/xuất">
            <n-switch v-model:value="onlyWithTransaction">
              <!-- <template #checked>
                Có nhập/xuất
              </template>
              <template #unchecked>
                Tất cả
              </template> -->
            </n-switch>
          </n-form-item>
          <n-flex class="ml-auto">
            <NButton type="primary" secondary @click="getStatistical()">
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
        <NButton type="success" ghost @click="exportToExcel()">
          <template #icon>
            <icon-park-outline-download />
          </template>
          Xuất Excel
        </NButton>
        <n-table :single-line="false" cellspacing="0" cellpadding="5">
          <thead>
            <tr>
              <th>Kho</th>
              <th>Sản phẩm</th>
              <th>Barcode</th>
              <th>SKU</th>
              <th>Tổng nhập (SL)</th>
              <th>Tổng nhập (Tiền)</th>
              <th>Tổng xuất (SL)</th>
              <th>Tổng xuất (Tiền)</th>
            </tr>
          </thead>
          <tbody>
            <template v-for="(warehouse, wIndex) in statisticalList" :key="warehouse.warehouseId">
              <template v-for="(product, pIndex) in warehouse.products || []" :key="product.productId">
                <tr>
                  <!-- Chỉ hiển thị tên kho 1 lần -->
                  <td v-if="pIndex === 0" :rowspan="warehouse.products?.length || 1">
                    {{ warehouse.warehouseName }}
                  </td>
                  <td>{{ product.productName }}</td>
                  <td>
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
                  <td>{{ product.productSKU }}</td>
                  <td>{{ product.totalImportQuantity!.toLocaleString('vi-VN') }}</td>
                  <td>{{ product.totalImportAmount!.toLocaleString('vi-VN') }}</td>
                  <td>{{ product.totalExportQuantity!.toLocaleString('vi-VN') }}</td>
                  <td>{{ product.totalExportAmount!.toLocaleString('vi-VN') }}</td>
                </tr>
              </template>
            </template>
          </tbody>
        </n-table>
      </NSpace>
    </n-card>
  </NSpace>
</template>
