<script setup lang="tsx">
import type { DataTableColumns, FormRules } from 'naive-ui'
import { NA, NButton, NGi, NGrid, NIcon, NSelect, NSpace, NTag } from 'naive-ui'
import { Add, CheckboxOutline, Save, TrashSharp } from '@vicons/ionicons5'
import { useRoute } from 'vue-router'
import { initRulesForm, validateFieldFromErrors } from '@/utils/error'
import { ProductService } from '@/service/api/product-service'
import { router } from '@/router'
import moment from 'moment'

const route = useRoute()

const referenceData = ref<ReferenceData.Product>({
  productCategories: [],
  productUnits: [],
  productOrigins: [],
})

const productId = route.params.productId
const product = ref<Product.Data>({
  id: '',
  sku: '',
  barcode: '',
  name: '',
  imageUrl: '',
  description: '',
  salePrice: 0,
  costPrice: 0,
  discountPrice: 0,
  taxRate: 0,
  note: '',
  status: 'ACTIVE',
  productCategoryId: '',
  productOriginId: '',
  productUnitId: '',
  inventoryQuantity: 0,
})

const rules: FormRules = {
  name: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  costPrice: [
    {
      required: true,
      validator: (_rule, value) => {
        if (value < 0) {
          return Promise.reject('Giá vốn phải lớn hơn hoặc bằng 0');
        }
        return Promise.resolve();
      },
      trigger: 'blur'
    }
  ],
  salePrice: [
    {
      required: true,
      validator: (_rule, value) => {
        if (value < 0) {
          return Promise.reject('Giá bán phải lớn hơn hoặc bằng 0');
        }
        return Promise.resolve();
      },
      trigger: 'blur'
    }
  ],
  productCategoryId: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  productUnitId: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
  productOriginId: [
    { required: true, message: 'Không được để trống', trigger: 'blur' }
  ],
}

const columnsHistoryInventory = ref<DataTableColumns<ProductInventory.Data>>([
  {
    title: 'Phiếu',
    align: 'center',
    key: 'id',
    render: (row) => {
      const handleClick = (e: MouseEvent) => {
        e.preventDefault()
        if (row.type === 'PURCHASE_ORDER') {
          router.push({
            name: 'purchase-order-management.purchase-order',
            params: { purchaseOrderId: row.purchaseOrderId },
          })
        }
        if (row.type === 'EXPORT_ORDER') {
          router.push({
            name: 'export-order-management.export-order',
            params: { exportOrderId: row.exportOrderId },
          })
        }
      }

      return h(
        NA,
        {
          href: '#',
          onClick: handleClick,
          class: 'underline-on-hover',
          internal: true
        },
        {
          default: () =>
            row.type === 'PURCHASE_ORDER'
              ? row.purchaseOrderId
              : row.type === 'EXPORT_ORDER'
              ? row.exportOrderId
              : ''
        }
      )
    },
  },
  {
    title: 'Số lượng',
    align: 'center',
    key: 'quantity',
  },
  {
    title: 'Loại',
    align: 'center',
    key: 'type',
    render: (row) => {
      return (
        <div>
          { row.type === 'PURCHASE_ORDER'
            ? <NTag type='success'>NHẬP</NTag>
            : '' 
          }
          { row.type === 'EXPORT_ORDER'
            ? <NTag type='error'>XUẤT</NTag>
            : '' 
          }
        </div>
      )
    }
  },
  {
    title: 'Ngày',
    align: 'center',
    key: 'createdAt',
    render: (row) => {
      return (
        <span>{moment(row.createdAt).format('YYYY-MM-DD')}</span>
      )
    },
  },
])
const formProductRef = ref()
const errors = ref<Error.ValidationError[]>([])

const productRules = ref(
  initRulesForm(product.value, (rule, value, callback, key) => {
    validateFieldFromErrors(errors, key, callback)
  }),
)

async function getReferenceData() {
  await ProductService.getReferenceDataProduct()
    .then((res: any) => {
      referenceData.value = res.data
    })
}

function optionOrigins() {
  return referenceData.value.productOrigins.map(item => ({
    label: item.name,
    value: item.id,
  }))
}

function optionUnits() {
  return referenceData.value.productUnits.map(item => ({
    label: item.name,
    value: item.id,
  }))
}

function optionCategoies() {
  return referenceData.value.productCategories.map(item => ({
    label: item.name,
    value: item.id,
  }))
}

const dataTableProductInventoryRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'createdAt',
  sortDesc: true,
})
const totalRecordsProductInventory = ref<number>(0)
const listProductInventory = ref<ProductInventory.Data[]>([])

async function getListProductInventory() {
  await ProductService.getListProductInventory(dataTableProductInventoryRequest.value, productId.toString())
    .then((res: any) => {
      listProductInventory.value = res.data.list
      totalRecordsProductInventory.value = res.data.totalRecords
    })
}

async function getProduct() {
  await ProductService.getProduct(productId.toString())
    .then((res: any) => {
      product.value = res.data
    })
}

async function changePage(page: number, size: number) {
  dataTableProductInventoryRequest.value.currentPage = page
  dataTableProductInventoryRequest.value.perPage = size
  await getListProductInventory()
}

async function createOrUpdateProduct(status: string) {
  formProductRef.value?.validate(async (errors: any) => {
    if (!errors) {
      await ProductService.createOrUpdateProduct({
        ...product.value,
        status,
      })
        .then((res: any) => {
          if (res.isSuccess) {
            router.push({ name: 'product-management.list' })
          }
          else {
            // errors.value = res.data
            // formUserRef.value.validate()
          }
        })
    }
  })
}

onMounted(async () => {
  await getReferenceData()
  
  if (productId.toString() !== ':productId' && productId.toString() !== 'new') {
    await getProduct()
    await getListProductInventory()
  }
})
</script>

<template>
  <NSpace vertical size="large">
    <n-card title="Sản phẩm">
      <template #header-extra>
        <NButton
          secondary
          type="primary"
          style="margin-left: 10px;"
          @click="createOrUpdateProduct('ACTIVE')"
        >
          <NIcon size="18" :component="Save" style="margin-right: 5px;" />
          Lưu
        </NButton>
      </template>

      <n-form
        ref="formProductRef"
        inline
        :label-width="80"
        :model="product"
        :rules="rules"
      >
        <NGrid cols="3" y-gap="12" x-gap="24">
          <NGi NGi :span="1">
            <n-form-item label="Tên" path="name">
              <n-input
                v-model:value="product.name"
                placeholder=""
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Giá nhập" path="costPrice">
              <n-input-number
                style="width: 100%;"
                v-model:value="product.costPrice"
                placeholder=""
                min="0"
              />
            </n-form-item>
          </NGi>

          <NGi NGi :span="1">
            <n-form-item label="Giá bán" path="salePrice">
              <n-input-number
                style="width: 100%;"
                v-model:value="product.salePrice"
                placeholder=""
                min="0"
              />
            </n-form-item>
          </NGi>

          <NGi :span="1">
            <n-form-item label="Danh mục" path="productCategoryId">
              <NSelect
                v-model:value="product.productCategoryId"
                placeholder=""
                :options="optionCategoies()"
              />
            </n-form-item>
          </NGi>

          <NGi :span="1">
            <n-form-item label="Đơn vị tính" path="productUnitId">
              <NSelect
                v-model:value="product.productUnitId"
                placeholder=""
                :options="optionUnits()"
              />
            </n-form-item>
          </NGi>

          <NGi :span="1">
            <n-form-item label="Suất xứ" path="productOriginId">
              <NSelect
                v-model:value="product.productOriginId"
                placeholder=""
                :options="optionOrigins()"
              />
            </n-form-item>
          </NGi>

          <NGi :span="3">
            <n-form-item label="Mô tả" path="description">
              <n-input
                v-model:value="product.description"
                placeholder="Input Description"
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
                v-model:value="product.note"
                placeholder="Input Note"
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
    <n-card title="Lịch sử" v-if="listProductInventory.length > 0">
      <template #header-extra>
        <NButton v-if="listProductInventory.length > 0" secondary type="primary" strong>
          Tồn kho: {{ product.inventoryQuantity }}
        </NButton>
      </template>
      <NSpace vertical size="large">
        <n-data-table ref="tableRef" :columns="columnsHistoryInventory" :data="listProductInventory" />
        <Pagination :count="totalRecordsProductInventory" @change="changePage" />
      </NSpace>
    </n-card>
  </NSpace>
</template>

<style scoped>
.__date-picker-trigger-qa68sz {
  width: 100% !important;
}
</style>
