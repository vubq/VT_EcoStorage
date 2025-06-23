<script setup lang="tsx">
import type { DataTableColumns, DataTableSortState } from 'naive-ui'
import { Add, ChevronForward } from '@vicons/ionicons5'

import { useBoolean } from '@/hooks'
import { NButton, NSpace } from 'naive-ui'
import { UserService } from '@/service/api/user'
import { router } from '@/router'
import { WarehouseService } from '@/service/api/warehouse-service'

const { bool: loading, setTrue: loadingStart, setFalse: loadingEnd } = useBoolean(false)
const { bool: loading1, setTrue: loading1Start, setFalse: loading1End } = useBoolean(false)
const { bool: isModalAddZone, setTrue: showModalAddZone, setFalse: hidenModalAddZone } = useBoolean(false)
const { bool: isModalAddShelf, setTrue: showModalAddShelf, setFalse: hidenModalAddShelf } = useBoolean(false)
const { bool: isModalAddFloor, setTrue: showModalAddFloor, setFalse: hidenModalAddFloor } = useBoolean(false)

const tableRef = ref()
const dataTableRequest = ref<DataTable.Request>({
  currentPage: 1,
  perPage: 10,
  filter: '',
  sortBy: 'username',
  sortDesc: true,
})
const userId = ref<string>('')
const columns = ref<DataTableColumns<User.Data>>([
  {
    title: 'Username',
    align: 'center',
    key: 'username',
    sorter: true,
    sortOrder: sortDefault('username'),
    render: (row) => {
      return (
        <NButton
          style="width: 100%"
          secondary
          type="primary"
          strong
          loading={loading1.value && userId.value === row.id}
          onClick={() => getUser(row.id!)}
        >
          {row.username}
        </NButton>
      )
    },
  },
  {
    title: 'First name',
    align: 'center',
    key: 'firstName',
    sorter: true,
    sortOrder: sortDefault('firstName'),
  },
  {
    title: 'Last name',
    align: 'center',
    key: 'lastName',
    sorter: true,
    sortOrder: sortDefault('lastName'),
  },
  {
    title: 'Email',
    align: 'center',
    key: 'email',
    sorter: true,
    sortOrder: sortDefault('email'),
  },
  {
    title: 'Phone number',
    align: 'center',
    key: 'phoneNumber',
    sorter: true,
    sortOrder: sortDefault('phoneNumber'),
  },
])
const columnsRef = ref<DataTableColumns<User.Data>>(columns.value)
const listUser = ref<User.Data[]>([])
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
  await getListUser()
}

async function getListUser() {
  loadingStart()
  await UserService.getListUser(dataTableRequest.value)
    .then((res: any) => {
      listUser.value = res.data.list
      totalRecords.value = res.data.totalRecords
    })
    .finally(() => loadingEnd())
}

async function reloadTableFirst() {
  dataTableRequest.value.currentPage = 1
  await getListUser()
}

async function reloadTable() {
  await getListUser()
}

async function getUser(id: string) {
  userId.value = id
  loading1Start()
  await router.push({
    name: 'user-management.user-info',
    params: { id },
  })
  loading1End()
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

const expanded = ref(false)
const toggle = () => (expanded.value = !expanded.value)

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
  zoneId?: string
): Zone.Data | undefined {
  return warehouse.zones?.find(z => z.id === zoneId)
}

function findShelf(
  warehouse: Warehouse.Data,
  zoneId?: string,
  shelfId?: string
): Shelf.Data | undefined {
  const zone = findZone(warehouse, zoneId)
  return zone?.shelves?.find(s => s.id === shelfId)
}

// Lấy danh sách shelves của zone
function getShelves(
  warehouse: Warehouse.Data,
  zoneId?: string
): Shelf.Data[] {
  return findZone(warehouse, zoneId)?.shelves || []
}

// Lấy danh sách floors của shelf
function getFloors(
  warehouse: Warehouse.Data,
  zoneId?: string,
  shelfId?: string
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
  floorId: string
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
  if (shelf) shelf.floorId = floorId
}

async function createOrUpdateZone() {
  await WarehouseService.createOrUpdateZone(zoneNew.value)
    .then((res: any) => {
      if (res.isSuccess) {
        warehouseList.value.find(w => w.id = zoneNew.value.warehouseId)?.zones!.push(res.data)
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
        warehouseList.value.find(w => w.id = shelfNew.value.warehouseId)?.zones?.find(z => z.id === shelfNew.value.zoneId)?.shelves?.push(res.data)
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
        warehouseList.value.find(w => w.id = floorNew.value.warehouseId)?.zones?.find(z => z.id === floorNew.value.zoneId)?.shelves?.find(s => s.id === floorNew.value.shelfId)?.floors!.push(...res.data)
        hidenModalAddFloor()
        floorNew.value = {
          status: 'ACTIVE',
          quantity: 1,
        }
      }
    })
}

onMounted(async () => {
  getListUser()
  await getListWarehouse()
})
</script>

<template>
  <NSpace vertical size="large">
    <NGrid cols="2" y-gap="12" x-gap="12">
      <NGi v-for="w in warehouseList" :span="1">
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
                  @click="setZoneId(w, z.id!)"
                  :type="isSelectedZone(w, z.id!) ? 'primary' : 'default'"
                  strong secondary
                >
                  {{ z.name }}
                </NButton>
                <NButton
                  :type="'default'"
                  strong secondary
                  @click="() => {
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
                  @click="setShelfId(w, w.zoneId!, s.id!)"
                  :type="isSelectedShelf(w, w.zoneId!, s.id!) ? 'primary' : 'default'"
                  strong secondary
                >
                  {{ s.name }}
                </NButton>
                <NButton
                  :type="'default'"
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
                  @click="setFloorId(w, w.zoneId!, f.id!)"
                  :type="isSelectedFloor(w, w.zoneId!, f.id!) ? 'primary' : 'default'"
                  strong secondary
                >
                  {{ f.floor }}
                </NButton>
                <NButton
                  :type="'default'"
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
        <n-space justify="end">
          <n-button type="primary" secondary @click="createOrUpdateZone()">
            Add
          </n-button>
        </n-space>
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
        <n-space justify="end">
          <n-button type="primary" secondary @click="createOrUpdateShelf()">
            Add
          </n-button>
        </n-space>
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
        <n-space justify="end">
          <n-button type="primary" secondary @click="createOrUpdateFloor()">
            Add
          </n-button>
        </n-space>
      </template>
    </n-modal>
  </NSpace>
</template>

<style scoped>
.arrow {
  transition: transform 0.3s ease;
  display: inline-block;
}
.arrow.expanded {
  transform: rotate(90deg);
}
.floor {
  margin-left: 30px;
}
.shelf, .floor {
  margin-left: 15px;
  margin-top: 5px;
}
.zone, .shelf, .floor {
  font-weight: 500;
  cursor: pointer;
}
.zone:hover, .shelf:hover, .floor:hover {
  text-decoration: underline;
}
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
