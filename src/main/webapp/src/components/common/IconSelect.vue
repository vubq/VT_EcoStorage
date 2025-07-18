<script setup lang="ts">
import { mapEntries } from 'radash'

interface Props {
  disabled?: boolean
}

const {
  disabled = false,
} = defineProps<Props>()

interface IconList {
  prefix: string
  icons: string[]
  title: string
  total: number
  categories: Record<string, string[]>
}
const value = defineModel('value', { type: String })

// 包含的图标库系列名
const nameList = ['icon-park-outline', 'carbon']

// 获取单个图标库数据
async function fetchIconList(name: string): Promise<IconList> {
  return await fetch(`https://api.iconify.design/collection?prefix=${name}`).then(res => res.json())
}

// 获取所有图标库数据
async function fetchIconAllList(nameList: string[]) {
  const namePromises = nameList.map(name => fetchIconList(name))
  const targets = await Promise.all(namePromises)

  return targets.map((i) => {
    i.icons = Object.entries(i.categories).reduce((prev, next) => {
      const [_key, value] = next
      return prev.concat(value)
    }, [] as string[])
    return i
  })
}
// 获取svg文件名
function getSvgName(path: string) {
  const regex = /\/([^/]+)\.svg$/
  const match = path.match(regex)
  if (match) {
    const fileName = match[1]
    return fileName
  }
  return path
}

// 获取所有本地图标
function generateLocalIconList() {
  const localSvgList = import.meta.glob('@/assets/svg-icons/*.svg', {
    query: '?raw',
    import: 'default',
    eager: true,
  })

  return mapEntries(localSvgList, (key, value) => {
    return [getSvgName(key), value]
  })
}

const iconList = shallowRef<IconList[]>([])
const LocalIconList = shallowRef({})

onMounted(async () => {
  iconList.value = await fetchIconAllList(nameList)
  LocalIconList.value = generateLocalIconList()
})

// 当前tab
const currentTab = shallowRef(0)
// 当前tag
const currentTag = shallowRef('')

// 切换tab
function handleChangeTab(index: number) {
  currentTab.value = index
  currentTag.value = ''
}
// 搜索图标输入框值
const searchValue = ref('')

// 当前页数
const currentPage = shallowRef(1)

// 选择分类tag
function handleSelectIconTag(icon: string) {
  currentTag.value = currentTag.value === icon ? '' : icon
  currentPage.value = 1
}

// 包含当前分类或所有图标列表
const icons = computed(() => {
  if (!iconList.value[currentTab.value])
    return []
  const hasTag = !!currentTag.value
  return hasTag
    ? iconList.value[currentTab.value]?.categories[currentTag.value] || []
    : iconList.value[currentTab.value].icons || []
})

// 符合搜索条件的图标列表
const filteredIcons = computed(() => {
  return icons.value?.filter(i => i.includes(searchValue.value)) || []
})

// 当前页显示的图标
const visibleIcons = computed(() => {
  return filteredIcons.value.slice((currentPage.value - 1) * 200, currentPage.value * 200)
})

const showModal = ref(false)

// 选择图标
function handleSelectIcon(icon: string) {
  value.value = icon
  showModal.value = false
}

// 清除图标
function clearIcon() {
  value.value = ''
  showModal.value = false
}
</script>

<template>
  <n-input-group disabled>
    <n-button v-if="value" :disabled="disabled" type="primary">
      <template #icon>
        <nova-icon :icon="value" />
      </template>
    </n-button>
    <n-input :value="value" readonly :placeholder="$t('components.iconSelector.inputPlaceholder')" />
    <n-button type="primary" ghost :disabled="disabled" @click="showModal = true">
      {{ $t('common.choose') }}
    </n-button>
  </n-input-group>
  <n-modal
    v-model:show="showModal" preset="card" :title="$t('components.iconSelector.selectorTitle')" size="small" class="w-800px" :bordered="false"
  >
    <template #header-extra>
      <n-button type="warning" size="small" ghost @click="clearIcon">
        {{ $t('components.iconSelector.clearIcon') }}
      </n-button>
    </template>

    <n-tabs :value="currentTab" type="line" animated placement="left" @update:value="handleChangeTab">
      <n-tab-pane name="local" tab="local">
        <n-flex :size="2">
          <n-el
            v-for="(_icon, key) in LocalIconList" :key="key"
            class="hover:(text-[var(--primary-color)] ring-1) ring-[var(--primary-color)] p-1 rounded flex-center"
            :title="`local:${key}`"
            @click="handleSelectIcon(`local:${key}`)"
          >
            <nova-icon :icon="`local:${key}`" :size="24" />
          </n-el>
        </n-flex>
      </n-tab-pane>
      <n-tab-pane v-for="(list, index) in iconList" :key="list.prefix" :name="index" :tab="list.title">
        <n-flex vertical>
          <n-flex size="small">
            <n-tag
              v-for="(_v, k) in list.categories" :key="k"
              :checked="currentTag === k" round checkable size="small"
              @update:checked="handleSelectIconTag(k)"
            >
              {{ k }}
            </n-tag>
          </n-flex>

          <n-input
            v-model:value="searchValue" type="text" clearable
            :placeholder="$t('components.iconSelector.searchPlaceholder')"
          />

          <div>
            <n-flex :size="2">
              <n-el
                v-for="(icon) in visibleIcons" :key="icon"
                class="hover:(text-[var(--primary-color)] ring-1) ring-[var(--primary-color)] p-1 rounded flex-center"
                :title="`${list.prefix}:${icon}`"
                @click="handleSelectIcon(`${list.prefix}:${icon}`)"
              >
                <nova-icon :icon="`${list.prefix}:${icon}`" :size="24" />
              </n-el>
              <n-empty v-if="visibleIcons.length === 0" class="w-full" />
            </n-flex>
          </div>

          <n-flex justify="center">
            <n-pagination
              v-model:page="currentPage"
              :item-count="filteredIcons.length"
              :page-size="200"
            />
          </n-flex>
        </n-flex>
      </n-tab-pane>
    </n-tabs>
  </n-modal>
</template>
