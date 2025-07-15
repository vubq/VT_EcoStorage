<script setup lang="ts">
import { useAuthStore } from '@/store'
import { renderIcon } from '@/utils/icon'
import IconBookOpen from '~icons/icon-park-outline/book-open'
import IconGithub from '~icons/icon-park-outline/github'
import IconLogout from '~icons/icon-park-outline/logout'
import IconUser from '~icons/icon-park-outline/user'

const { t } = useI18n()

const { userInfo, logout } = useAuthStore()
const router = useRouter()

const options = computed(() => {
  return [
    {
      label: 'Thông tin',
      key: 'userCenter',
      icon: () => h(IconUser),
    },
    {
      type: 'divider',
      key: 'd1',
    },
    // {
    //   label: 'Github',
    //   key: 'guthub',
    //   icon: () => h(IconGithub),
    // },
    // {
    //   label: 'Gitee',
    //   key: 'gitee',
    //   icon: renderIcon('simple-icons:gitee'),
    // },
    // {
    //   label: 'Docs',
    //   key: 'docs',
    //   icon: () => h(IconBookOpen),
    // },
    // {
    //   type: 'divider',
    //   key: 'd1',
    // },
    {
      label: 'Đăng xuất',
      key: 'loginOut',
      icon: () => h(IconLogout),
    },
  ]
})
function handleSelect(key: string | number) {
  if (key === 'loginOut') {
    window.$dialog?.info({
      title: 'Đăng xuất',
      content: 'Xác nhận đăng xuất khỏi tài khoản hiện tại?',
      positiveText: 'Xác nhận',
      negativeText: 'Hủy',
      onPositiveClick: () => {
        logout()
      },
    })
  }
  if (key === 'userCenter')
    router.push('/userCenter')

  if (key === 'guthub')
    window.open('https://github.com/chansee97/nova-admin')

  if (key === 'gitee')
    window.open('https://gitee.com/chansee97/nova-admin')

  if (key === 'docs')
    window.open('https://nova-admin-docs.pages.dev/')
}
</script>

<template>
  <n-dropdown
    trigger="click"
    :options="options"
    @select="handleSelect"
  >
    <n-avatar
      round
      class="cursor-pointer"
      :src="userInfo?.avatar"
    >
      <template #fallback>
        <div class="wh-full flex-center">
          <icon-park-outline-user />
        </div>
      </template>
    </n-avatar>
  </n-dropdown>
</template>

<style scoped></style>
