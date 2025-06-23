<script setup lang="tsx">
import { Add } from '@vicons/ionicons5'

import { useBoolean } from '@/hooks'
import { NButton, NSpace } from 'naive-ui'
import { WarehouseService } from '@/service/api/warehouse-service'

const { bool: isModalAddZone, setTrue: showModalAddZone, setFalse: hidenModalAddZone } = useBoolean(false)
const { bool: isModalAddShelf, setTrue: showModalAddShelf, setFalse: hidenModalAddShelf } = useBoolean(false)
const { bool: isModalAddFloor, setTrue: showModalAddFloor, setFalse: hidenModalAddFloor } = useBoolean(false)

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
  warehouse.zoneId = zoneId
}
function setShelfId(warehouse: Warehouse.Data, zoneId: string, shelfId: string) {
  findZone(warehouse, zoneId)!.shelfId = shelfId
}
function setFloorId(warehouse: Warehouse.Data, zoneId: string, floorId: string) {
  const zone = findZone(warehouse, zoneId)
  const shelf = zone?.shelves?.find(s => s.id === zone.shelfId)
  if (shelf)
    shelf.floorId = floorId
}

async function createOrUpdateZone() {
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

async function createOrUpdateShelf() {
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

async function createOrUpdateFloor() {
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

onMounted(async () => {
  await getListWarehouse()
})
</script>

<template>
  <NSpace vertical size="large">
    <NGrid cols="2" y-gap="12" x-gap="12">
      <NGi v-for="w in warehouseList" :key="w.id" :span="1">
        <n-card :title="`Warehouse: ${w.name}`">
          <NSpace vertical size="large">
            <n-divider />
            <!-- Zone list -->
            <div style="display: flex; align-items: center;">
              <span style="margin-right: 20px;">Zone</span>
              <NSpace size="large" style="max-width: 82%;">
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
            </div>
            <n-divider v-if="w.zoneId" />
            <!-- Shelf list -->
            <div v-if="w.zoneId" style="display: flex; align-items: center;">
              <span style="margin-right: 20px;">Shelf</span>
              <NSpace size="large" style="max-width: 82%;">
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
            </div>
            <n-divider v-if="w.zoneId && findZone(w, w.zoneId)?.shelfId" />
            <!-- Floor list -->
            <div v-if="w.zoneId && findZone(w, w.zoneId)?.shelfId" style="display: flex; align-items: center;">
              <span style="margin-right: 20px;">Floor</span>
              <NSpace size="large" style="max-width: 82%;">
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
            </div>
          </NSpace>
        </n-card>
      </NGi>
    </NGrid>
    <!-- Modal add zone -->
    <n-modal
      v-model:show="isModalAddZone"
      :mask-closable="false"
      preset="card"
      title="Add Zone"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form
        ref=""
        inline
        :model="zoneNew"
      >
        <n-grid :cols="24">
          <n-form-item-grid-item :span="24" label="Warehouse:" style="width: 100%;">
            <n-input v-model:value="zoneNew.warehouseName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Zone name:" path="name" style="width: 100%;">
            <n-input v-model:value="zoneNew.name" placeholder="Input Zone name" />
          </n-form-item-grid-item>
        </n-grid>
      </n-form>
      <template #action>
        <NSpace justify="end">
          <NButton type="primary" secondary @click="createOrUpdateZone()">
            Add
          </NButton>
        </NSpace>
      </template>
    </n-modal>
    <!-- Modal add shelf -->
    <n-modal
      v-model:show="isModalAddShelf"
      :mask-closable="false"
      preset="card"
      title="Add Shelf"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form
        ref=""
        inline
        :model="shelfNew"
      >
        <n-grid :cols="24">
          <n-form-item-grid-item :span="24" label="Warehouse:" style="width: 100%;">
            <n-input v-model:value="shelfNew.warehouseName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Zone:" style="width: 100%;">
            <n-input v-model:value="shelfNew.zoneName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Shelf name:" path="name" style="width: 100%;">
            <n-input v-model:value="shelfNew.name" placeholder="Input Shelf name" style="width: 100%;" />
          </n-form-item-grid-item>
        </n-grid>
      </n-form>
      <template #action>
        <NSpace justify="end">
          <NButton type="primary" secondary @click="createOrUpdateShelf()">
            Add
          </NButton>
        </NSpace>
      </template>
    </n-modal>
    <!-- Modal floor -->
    <n-modal
      v-model:show="isModalAddFloor"
      :mask-closable="false"
      preset="card"
      title="Add Floor"
      class="w-400px"
      :segmented="{
        content: true,
        action: true,
      }"
    >
      <n-form
        ref=""
        inline
        :model="floorNew"
      >
        <n-grid :cols="24">
          <n-form-item-grid-item :span="24" label="Warehouse:" style="width: 100%;">
            <n-input v-model:value="floorNew.warehouseName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Zone:" style="width: 100%;">
            <n-input v-model:value="floorNew.zoneName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Shelf:" style="width: 100%;">
            <n-input v-model:value="floorNew.shelfName" placeholder="" disabled />
          </n-form-item-grid-item>
          <n-form-item-grid-item :span="24" label="Number of floors:" path="name" style="width: 100% !important;">
            <n-input-number v-model:value="floorNew.quantity" :min="1" placeholder="Input Number of floors" />
          </n-form-item-grid-item>
        </n-grid>
      </n-form>
      <template #action>
        <NSpace justify="end">
          <NButton type="primary" secondary @click="createOrUpdateFloor()">
            Add
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
