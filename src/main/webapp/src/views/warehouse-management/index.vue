<script setup lang="tsx">
import { Add, TrashSharp } from '@vicons/ionicons5'

import { useBoolean } from '@/hooks'
import type { DataTableColumns, FormInst, FormRules } from 'naive-ui'
import { NA, NButton, NSpace } from 'naive-ui'
import { WarehouseService } from '@/service/api/warehouse-service'
import { ProductService } from '@/service/api/product-service'
import { router } from '@/router'

const { bool: isModalAddZone, setTrue: showModalAddZone, setFalse: hidenModalAddZone } = useBoolean(false)
const { bool: isModalAddShelf, setTrue: showModalAddShelf, setFalse: hidenModalAddShelf } = useBoolean(false)
const { bool: isModalAddFloor, setTrue: showModalAddFloor, setFalse: hidenModalAddFloor } = useBoolean(false)
const { bool: isModalAddWarehouse, setTrue: showModalAddWarehouse, setFalse: hidenModalAddWarehouse } = useBoolean(false)
const { bool: isModalLocation, setTrue: showModalLocation, setFalse: hidenModalLocation } = useBoolean(false)

const route = useRoute()
const locationId = route.query.locationId as string

const moveLocation = ref<{
  productId: string
  productBarcode: string
  productName: string
  productUnitName: string
  inventoryQuantity: number
  location: string
  locationId: string
  warehouseId: string
  warehouseName: string
  locationsNew: {
    locationId?: string | null
    quantity: number
    zoneId?: string | null
    shelfId?: string | null
    productId: string
  }[]
}>({
  productId: '',
  productBarcode: '',
  productName: '',
  productUnitName: '',
  inventoryQuantity: 0,
  location: '',
  locationId: '',
  warehouseId: '',
  warehouseName: '',
  locationsNew: [],
})

const columnsProduct = ref<DataTableColumns<Product.ProductByLocation>>([
  {
    title: 'Barcode',
    align: 'center',
    key: 'barcode',
    render: (row) => {
      return (
        h(
          NA,
          {
            href: '#',
            class: 'underline-on-hover',
            internal: true,
            onClick: (e: MouseEvent) => {
              e.preventDefault()
              router.push({
                name: 'product-management.product',
                params: { productId: row.productId },
              })
            },
          },
          { default: () => row.productBarcode },
        )
      )
    },
  },
  {
    title: 'Sản phẩm',
    align: 'center',
    key: 'productName',
  },
  {
    title: 'Đơn vị tính',
    align: 'center',
    key: 'productUnitName',
  },
  {
    title: 'Tồn kho',
    align: 'center',
    key: 'inventoryQuantity',
  },
  {
    title: '',
    align: 'center',
    key: 'barcode',
    render: (row) => {
      return (
        h(
          NA,
          {
            href: '#',
            class: 'underline-on-hover',
            internal: true,
            onClick: (e: MouseEvent) => {
              e.preventDefault()
              moveLocation.value.productName = row.productName!
              moveLocation.value.productId = row.productId!
              moveLocation.value.productBarcode = row.productBarcode!
              moveLocation.value.productUnitName = row.productUnitName!
              moveLocation.value.location = row.location!
              moveLocation.value.locationId = row.floorId!
              moveLocation.value.inventoryQuantity = row.inventoryQuantity!
              moveLocation.value.warehouseId = row.warehouseId!
              moveLocation.value.warehouseName = row.warehouseName!
              showModalLocation()
            },
          },
          { default: () => 'Di chuyển vị trí' },
        )
      )
    },
  },
])

const formWarehouseRef = ref<FormInst | null>(null)
const formZoneRef = ref<FormInst | null>(null)
const formShelfRef = ref<FormInst | null>(null)
const formFloorRef = ref<FormInst | null>(null)

const rules: FormRules = {
  name: [
    { required: true, message: 'Không được để trống', trigger: 'blur' },
  ],
  address: [
    { required: true, message: 'Không được để trống', trigger: 'blur' },
  ],
  quantity: [
    {
      type: 'number',
      min: 1,
      message: 'Số lượng phải từ 1 trở lên',
      trigger: 'blur',
    },
  ],
}

const warehouseList = ref<Warehouse.Data[]>([])
const zoneNew = ref<Zone.Data>({
  status: 'ACTIVE',
})
const shelfNew = ref<Shelf.Data>({
  status: 'ACTIVE',
})
const floorNew = ref<Floor.Data>({
  status: 'ACTIVE',
  quantity: 1,
})
const warehouseNew = ref<Warehouse.Data>({
  status: 'ACTIVE',
})

async function getListWarehouse() {
  await WarehouseService.getListWarehouse()
    .then((res: any) => {
      warehouseList.value = res.data
    })
}

function findZone(
  warehouse: Warehouse.Data,
  zoneId?: string,
): Zone.Data | undefined {
  return warehouse.zones?.find(z => z.id === zoneId)
}

function findShelf(
  warehouse: Warehouse.Data,
  zoneId?: string,
  shelfId?: string,
): Shelf.Data | undefined {
  const zone = findZone(warehouse, zoneId)
  return zone?.shelves?.find(s => s.id === shelfId)
}

// Lấy danh sách shelves của zone
function getShelves(
  warehouse: Warehouse.Data,
  zoneId?: string,
): Shelf.Data[] {
  return findZone(warehouse, zoneId)?.shelves || []
}

// Lấy danh sách floors của shelf
function getFloors(
  warehouse: Warehouse.Data,
  zoneId?: string,
  shelfId?: string,
): Floor.Data[] {
  return findShelf(warehouse, zoneId, shelfId)?.floors || []
}

// Check id đang chọn
function isSelectedZone(warehouse: Warehouse.Data, zoneId: string): boolean {
  return warehouse.zoneId === zoneId
}
function isSelectedShelf(warehouse: Warehouse.Data, zoneId: string, shelfId: string): boolean {
  return findZone(warehouse, zoneId)?.shelfId === shelfId
}
function isSelectedFloor(
  warehouse: Warehouse.Data,
  zoneId: string,
  floorId: string,
): boolean {
  const zone = findZone(warehouse, zoneId)
  const shelf = zone?.shelves?.find(s => s.id === zone.shelfId)
  return shelf?.floorId === floorId
}

// Set giá trị id
function setZoneId(warehouse: Warehouse.Data, zoneId: string) {
  // Nếu zoneId mới giống với zoneId hiện tại -> reset
  if (warehouse.zoneId === zoneId) {
    const currentZone = findZone(warehouse, zoneId)
    if (currentZone) {
      const currentShelf = currentZone.shelves?.find(s => s.id === currentZone.shelfId)
      if (currentShelf) {
        currentShelf.floorId = undefined
      }
      currentZone.shelfId = undefined
    }
    warehouse.zoneId = undefined
    warehouse.products = undefined
    return
  }

  // Nếu khác -> cập nhật zoneId mới và reset shelfId + floorId + products
  warehouse.zoneId = zoneId
  const zone = findZone(warehouse, zoneId)
  if (zone) {
    const shelf = zone.shelves?.find(s => s.id === zone.shelfId)
    if (shelf) {
      shelf.floorId = undefined
    }
    zone.shelfId = undefined
  }
  warehouse.products = undefined
}
function setShelfId(warehouse: Warehouse.Data, zoneId: string, shelfId: string) {
  const zone = findZone(warehouse, zoneId)
  if (!zone)
    return

  // Nếu shelfId trùng với shelfId hiện tại, đặt lại là null và reset floorId + products
  if (zone.shelfId === shelfId) {
    zone.shelfId = undefined
    const currentShelf = findShelf(warehouse, zoneId, shelfId)
    if (currentShelf) {
      currentShelf.floorId = undefined
    }
    warehouse.products = undefined
    return
  }

  // Nếu shelfId mới khác, cập nhật shelfId mới và reset
  zone.shelfId = shelfId
  const newShelf = findShelf(warehouse, zoneId, shelfId)
  if (newShelf) {
    newShelf.floorId = undefined
  }
  warehouse.products = undefined
}
async function setFloorId(warehouse: Warehouse.Data, zoneId: string, floorId: string) {
  const zone = findZone(warehouse, zoneId)
  const shelf = zone?.shelves?.find(s => s.id === zone.shelfId)

  if (!shelf)
    return

  // Nếu floorId mới trùng với floorId hiện tại, gán null và không gọi API
  if (shelf.floorId === floorId) {
    shelf.floorId = undefined
    warehouse.products = []
    return
  }

  shelf.floorId = floorId
  await ProductService.getListProductInventoryByLocationId(floorId)
    .then((res: any) => {
      warehouse.products = res.data
    })
}

function findWarehouseByLocationId(warehouses: Warehouse.Data[], locationId: string) {
  for (const warehouse of warehouses) {
    for (const zone of warehouse.zones || []) {
      for (const shelf of zone.shelves || []) {
        for (const floor of shelf.floors || []) {
          if (floor.id === locationId) {
            return {
              warehouse,
              zoneId: zone.id,
              shelfId: shelf.id,
              floorId: floor.id,
            }
          }
        }
      }
    }
  }
  return null
}

async function createOrUpdateZone() {
  formZoneRef.value?.validate(async (errors) => {
    if (!errors) {
      await WarehouseService.createOrUpdateZone(zoneNew.value)
        .then((res: any) => {
          if (res.isSuccess) {
            const warehouse = warehouseList.value.find(
              w => w.id === zoneNew.value.warehouseId,
            )
            if (warehouse) {
              if (Array.isArray(warehouse.zones)) {
                warehouse.zones.push(res.data)
              }
              else {
                warehouse.zones = [res.data]
              }
            }
            hidenModalAddZone()
            zoneNew.value = {
              status: 'ACTIVE',
            }
          }
        })
    }
  })
}

async function createOrUpdateShelf() {
  formShelfRef.value?.validate(async (errors) => {
    if (!errors) {
      await WarehouseService.createOrUpdateShelf(shelfNew.value)
        .then((res: any) => {
          if (res.isSuccess) {
            const warehouse = warehouseList.value.find(
              w => w.id === shelfNew.value.warehouseId,
            )
            const zone = warehouse?.zones?.find(z => z.id === shelfNew.value.zoneId)

            if (zone) {
              if (Array.isArray(zone.shelves)) {
                zone.shelves.push(res.data)
              }
              else {
                zone.shelves = [res.data]
              }
            }
            hidenModalAddShelf()
            shelfNew.value = {
              status: 'ACTIVE',
            }
          }
        })
    }
  })
}

async function createOrUpdateFloor() {
  formFloorRef.value?.validate(async (errors) => {
    if (!errors) {
      await WarehouseService.createOrUpdateFloor(floorNew.value)
        .then((res: any) => {
          if (res.isSuccess) {
            const warehouse = warehouseList.value.find(
              w => w.id === floorNew.value.warehouseId,
            )
            const zone = warehouse?.zones?.find(z => z.id === floorNew.value.zoneId)
            const shelf = zone?.shelves?.find(s => s.id === floorNew.value.shelfId)

            if (shelf) {
              if (Array.isArray(shelf.floors)) {
                shelf.floors.push(...res.data)
              }
              else {
                shelf.floors = [...res.data]
              }
            }
            hidenModalAddFloor()
            floorNew.value = {
              status: 'ACTIVE',
              quantity: 1,
            }
          }
        })
    }
  })
}

async function createOrUpdateWarehouse() {
  formWarehouseRef.value?.validate(async (errors) => {
    if (!errors) {
      await WarehouseService.createOrUpdateWarehouse(warehouseNew.value)
        .then((res: any) => {
          if (res.isSuccess) {
            warehouseList.value.push(res.data)
            hidenModalAddWarehouse()
            warehouseNew.value = {
              status: 'ACTIVE',
            }
          }
        })
    }
  })
}

function optionZones() {
  return warehouseList.value.find(w => w.id === moveLocation.value.warehouseId)?.zones?.map(zone => ({
    label: zone.name,
    value: zone.id,
  })) || []
}

function optionShelf(zoneId: string) {
  return warehouseList.value.find(w => w.id === moveLocation.value.warehouseId)?.zones?.find(z => z.id === zoneId)?.shelves?.map(s => ({
    label: s.name,
    value: s.id,
  })) || []
}

function optionFloor(zoneId: string, shelfId: string) {
  return warehouseList.value.find(w => w.id === moveLocation.value.warehouseId)?.zones?.find(z => z.id === zoneId)?.shelves?.find(s => s.id === shelfId)?.floors?.map(f => ({
    label: f.floor,
    value: f.id,
    disabled: disabledOptionFloor(f.id!),
  })) || []
}

function disabledOptionFloor(floorId: string): boolean {
  return moveLocation.value.locationsNew.some(loc => loc.locationId === floorId)
}

function addLocation() {
  moveLocation.value.locationsNew.push({ zoneId: undefined, shelfId: undefined, locationId: undefined, quantity: 1, productId: moveLocation.value.productId })
}

function removeLocation(index: number) {
  moveLocation.value.locationsNew.splice(index, 1)
}

async function moveLocationProduct() {
  await WarehouseService.moveLocation(moveLocation.value)
    .then(async (res: any) => {
      if (res.isSuccess) {
        await getListWarehouse()
        if (locationId && warehouseList.value.length > 0) {
          const path = findWarehouseByLocationId(warehouseList.value, locationId)
          if (path) {
            const { warehouse, zoneId, shelfId, floorId } = path
            setZoneId(warehouse, zoneId!)
            setShelfId(warehouse, zoneId!, shelfId!)
            await setFloorId(warehouse, zoneId!, floorId!) // gọi API để load product
          }
        }
      }
    })
}

onMounted(async () => {
  await getListWarehouse()
  if (locationId && warehouseList.value.length > 0) {
    const path = findWarehouseByLocationId(warehouseList.value, locationId)
    if (path) {
      const { warehouse, zoneId, shelfId, floorId } = path
      setZoneId(warehouse, zoneId!)
      setShelfId(warehouse, zoneId!, shelfId!)
      await setFloorId(warehouse, zoneId!, floorId!) // gọi API để load product
    }
  }
})
</script>

<template>
  <NSpace vertical size="large">
    <NGrid cols="2" y-gap="12" x-gap="12">
      <NGi v-for="w in warehouseList" :key="w.id" :span="1">
        <n-card :title="`Kho: ${w.name} [${w.address}]`">
          <NSpace vertical size="large">
            <n-divider />
            <!-- Zone list -->
            <div>
              <n-grid :cols="24" x-gap="12" style="align-items: center;">
                <n-gi :span="3">
                  <div>
                    Khu vực
                  </div>
                </n-gi>
                <n-gi :span="21">
                  <NSpace size="large" style="width: 100%;">
                    <NButton
                      v-for="z in w.zones"
                      :key="z.id"
                      :type="isSelectedZone(w, z.id!) ? 'primary' : 'default'"
                      strong
                      secondary @click="setZoneId(w, z.id!)"
                    >
                      {{ z.name }}
                    </NButton>
                    <NButton
                      type="default"
                      strong secondary
                      @click="() => {
                        console.log(w.id)
                        showModalAddZone()
                        zoneNew.warehouseId = w.id
                        zoneNew.warehouseName = w.name
                      }"
                    >
                      <NIcon size="18" :component="Add" />
                    </NButton>
                  </NSpace>
                </n-gi>
              </n-grid>
            </div>
            <n-divider v-if="w.zoneId" />
            <!-- Shelf list -->
            <div v-if="w.zoneId">
              <n-grid :cols="24" x-gap="12" style="align-items: center;">
                <n-gi :span="3">
                  <div>Kệ</div>
                </n-gi>
                <n-gi :span="21">
                  <NSpace size="large" style="width: 100%;">
                    <NButton
                      v-for="s in getShelves(w, w.zoneId)"
                      :key="s.id"
                      :type="isSelectedShelf(w, w.zoneId!, s.id!) ? 'primary' : 'default'"
                      strong
                      secondary @click="setShelfId(w, w.zoneId!, s.id!)"
                    >
                      {{ s.name }}
                    </NButton>
                    <NButton
                      type="default"
                      strong secondary
                      @click="() => {
                        showModalAddShelf()
                        shelfNew.zoneId = w.zoneId
                        shelfNew.warehouseId = w.id
                        shelfNew.warehouseName = w.name
                        shelfNew.zoneName = findZone(w, w.zoneId)?.name
                      }"
                    >
                      <NIcon size="18" :component="Add" />
                    </NButton>
                  </NSpace>
                </n-gi>
              </n-grid>
            </div>
            <n-divider v-if="w.zoneId && findZone(w, w.zoneId)?.shelfId" />
            <!-- Floor list -->
            <div v-if="w.zoneId && findZone(w, w.zoneId)?.shelfId">
              <n-grid :cols="24" x-gap="12" style="align-items: center;">
                <n-gi :span="3">
                  <div>Tầng</div>
                </n-gi>
                <n-gi :span="21">
                  <NSpace size="large" style="max-width: 100%;">
                    <NButton
                      v-for="f in getFloors(w, w.zoneId, findZone(w, w.zoneId)?.shelfId)"
                      :key="f.id"
                      :type="isSelectedFloor(w, w.zoneId!, f.id!) ? 'primary' : 'default'"
                      strong
                      secondary @click="setFloorId(w, w.zoneId!, f.id!)"
                    >
                      {{ f.floor }}
                    </NButton>
                    <NButton
                      type="default"
                      strong secondary
                      @click="() => {
                        showModalAddFloor()
                        floorNew.shelfId = findZone(w, w.zoneId)?.shelfId
                        floorNew.warehouseId = w.id
                        floorNew.warehouseName = w.name
                        floorNew.zoneId = findZone(w, w.zoneId)?.id
                        floorNew.zoneName = findZone(w, w.zoneId)?.name
                        floorNew.shelfId = findShelf(w, w.zoneId, findZone(w, w.zoneId)?.shelfId)?.id
                        floorNew.shelfName = findShelf(w, w.zoneId, findZone(w, w.zoneId)?.shelfId)?.name
                      }"
                    >
                      <NIcon size="18" :component="Add" />
                    </NButton>
                  </NSpace>
                </n-gi>
              </n-grid>
            </div>
            <n-divider v-if="w.products && w.products!.length > 0" />
            <n-data-table v-if="w.products && w.products!.length > 0" ref="tableRef" :columns="columnsProduct" :data="w.products" />
          </NSpace>
        </n-card>
      </NGi>
      <NGi :span="1">
        <n-card>
          <NSpace vertical size="large">
            <NButton
              type="default"
              strong secondary
              @click="() => {
                showModalAddWarehouse()
              }"
            >
              <NIcon size="18" :component="Add" />Thêm kho
            </NButton>
          </NSpace>
        </n-card>
      </NGi>
    </NGrid>
    <!-- Modal add zone -->
    <n-modal
      v-model:show="isModalAddZone"
      :mask-closable="false"
      preset="card"
      title="Thêm khu vực"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form
        ref="formZoneRef"
        inline
        :model="zoneNew"
        :rules="rules"
      >
        <n-grid :cols="24">
          <n-form-item-grid-item :span="24" label="Kho:" style="width: 100%;">
            <n-input v-model:value="zoneNew.warehouseName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Tên khu vực:" path="name" style="width: 100%;">
            <n-input v-model:value="zoneNew.name" placeholder="" />
          </n-form-item-grid-item>
        </n-grid>
      </n-form>
      <template #action>
        <NSpace justify="end">
          <NButton type="primary" secondary @click="createOrUpdateZone()">
            Lưu
          </NButton>
        </NSpace>
      </template>
    </n-modal>
    <!-- Modal add shelf -->
    <n-modal
      v-model:show="isModalAddShelf"
      :mask-closable="false"
      preset="card"
      title="Thêm kệ"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form
        ref="formShelfRef"
        inline
        :model="shelfNew"
        :rules="rules"
      >
        <n-grid :cols="24">
          <n-form-item-grid-item :span="24" label="Kho:" style="width: 100%;">
            <n-input v-model:value="shelfNew.warehouseName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Khu vực:" style="width: 100%;">
            <n-input v-model:value="shelfNew.zoneName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Tên kệ:" path="name" style="width: 100%;">
            <n-input v-model:value="shelfNew.name" placeholder="" style="width: 100%;" />
          </n-form-item-grid-item>
        </n-grid>
      </n-form>
      <template #action>
        <NSpace justify="end">
          <NButton type="primary" secondary @click="createOrUpdateShelf()">
            Lưu
          </NButton>
        </NSpace>
      </template>
    </n-modal>
    <!-- Modal floor -->
    <n-modal
      v-model:show="isModalAddFloor"
      :mask-closable="false"
      preset="card"
      title="Thêm tầng"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form
        ref="formFloorRef"
        inline
        :model="floorNew"
        :rules="rules"
      >
        <n-grid :cols="24">
          <n-form-item-grid-item :span="24" label="Kho:" style="width: 100%;">
            <n-input v-model:value="floorNew.warehouseName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Khu vực:" style="width: 100%;">
            <n-input v-model:value="floorNew.zoneName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Kệ:" style="width: 100%;">
            <n-input v-model:value="floorNew.shelfName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Số tầng:" path="quantity" style="width: 100% !important;">
            <n-input-number v-model:value="floorNew.quantity" :min="1" placeholder="" />
          </n-form-item-grid-item>
        </n-grid>
      </n-form>
      <template #action>
        <NSpace justify="end">
          <NButton type="primary" secondary @click="createOrUpdateFloor()">
            Lưu
          </NButton>
        </NSpace>
      </template>
    </n-modal>
    <!-- Modal warehouse -->
    <n-modal
      v-model:show="isModalAddWarehouse"
      :mask-closable="false"
      preset="card"
      title="Thêm kho"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form
        ref="formWarehouseRef"
        inline
        :model="warehouseNew"
        :rules="rules"
      >
        <n-grid :cols="24">
          <n-form-item-grid-item :span="24" label="Tên kho:" path="name" style="width: 100%;">
            <n-input v-model:value="warehouseNew.name" placeholder="" />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Địa chỉ:" path="address" style="width: 100%;">
            <n-input v-model:value="warehouseNew.address" placeholder="" type="textarea" />
          </n-form-item-grid-item>
        </n-grid>
      </n-form>
      <template #action>
        <NSpace justify="end">
          <NButton type="primary" secondary @click="createOrUpdateWarehouse()">
            Lưu
          </NButton>
        </NSpace>
      </template>
    </n-modal>
    <!-- Di chuyển vị trí -->
    <n-modal
      v-model:show="isModalLocation"
      :mask-closable="false"
      preset="card"
      title="Di chuyển vị trí"
      class="w-800px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form
        ref="formWarehouseRef"
        inline
        :model="warehouseNew"
        :rules="rules"
      >
        <n-grid :cols="36">
          <n-form-item-grid-item label="Sản phẩm:" :span="16" style="width: 100%;">
            {{ moveLocation.productName }} - {{ moveLocation.productBarcode }}
          </n-form-item-grid-item>
          <n-form-item-grid-item label="Vị trí:" :span="14" style="width: 100%;">
            {{ moveLocation.warehouseName }} - {{ moveLocation.location }}
          </n-form-item-grid-item>
          <n-form-item-grid-item label="Tồn kho:" :span="6" style="width: 100%;">
            {{ moveLocation.inventoryQuantity }}
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="36" :label="`Chuyển đến: ${moveLocation.warehouseName}`" style="width: 100%;">
            <NSpace vertical size="large">
              <NGrid v-for="(l, i) in moveLocation.locationsNew" :key="i" :cols="5" :y-gap="12" :x-gap="24">
                <NGi span="{1}">
                  <NSelect v-model:value="l.zoneId" :options="optionZones()" placeholder="Khu vực" @change="() => { l.shelfId = null; l.locationId = null }" />
                </NGi>
                <NGi span="{1}">
                  <NSelect
                    v-model:value="l.shelfId"
                    :options="optionShelf(l.zoneId!)"
                    placeholder="Kệ"
                    @change="() => l.locationId = null"
                  />
                </NGi>
                <NGi span="{1}">
                  <NSelect
                    v-model:value="l.locationId"
                    :options="optionFloor(l.zoneId!, l.shelfId!)"
                    placeholder="Tầng"
                  />
                </NGi>
                <NGi span="{1}">
                  <NInputNumber v-model:value="l.quantity" placeholder="Số lượng" min="{1}" />
                </NGi>
                <NGi span="{1}">
                  <NButton secondary type="error" @click="() => removeLocation(i)">
                    <NIcon size="18" :component="TrashSharp" />
                  </NButton>
                </NGi>
              </NGrid>
              <NButton secondary type="primary" @click="() => addLocation()">
                <NIcon size="18" :component="Add" />
              </NButton>
            </NSpace>
          </n-form-item-grid-item>
        </n-grid>
      </n-form>
      <template #action>
        <NSpace justify="end">
          <NButton type="primary" secondary @click="moveLocationProduct()">
            Lưu
          </NButton>
        </NSpace>
      </template>
    </n-modal>
  </NSpace>
</template>

<style scoped>
.n-form-item.n-form-item--left-labelled .n-form-item-label .n-form-item-label__text {
  text-align: start;
}
.n-divider:not(.n-divider--vertical) {
  margin-top: 5px;
  margin-bottom: 5px;
}
.n-input-number {
  width: 100%;
}
</style>
