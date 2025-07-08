<script setup lang="tsx">
import { NSpace } from 'naive-ui'
import { StatisticalService } from '@/service/api/statistical-service'
import moment from 'moment'
import { useAppStore } from '@/store'

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
                  <td>{{ product.productBarcode }}</td>
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
