<script setup lang="tsx">
import { NSpace } from 'naive-ui'
import { StatisticalService } from '@/service/api/statistical-service'
import moment from 'moment'

const statisticalList = ref<Statistical.Data[]>([])

async function getStatistical() {
  await StatisticalService.getStatistical(
    moment(range.value[0]).format("YYYY-MM-DD"),
    moment(range.value[1]).format("YYYY-MM-DD")
  )
    .then((res: any) => {
      if (res.isSuccess) {
        statisticalList.value = res.data
        console.log(res)
      }
    })
}

const range = ref<[number, number]>([Date.now(), Date.now()])

onMounted(() => {
  getStatistical()
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card>
      <n-form ref="formRef" label-placement="left" inline :show-feedback="false">
        <n-flex>
          <n-form-item label="Thời gian" path="date">
            <n-date-picker v-model:value="range" type="daterange" @change="getStatistical()" />
          </n-form-item>
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
