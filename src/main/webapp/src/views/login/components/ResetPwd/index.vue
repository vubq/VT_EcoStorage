<script setup lang="ts">
import type { FormInst } from 'naive-ui'

const emit = defineEmits(['update:modelValue'])
function toLogin() {
  emit('update:modelValue', 'login')
}
const { t } = useI18n()

const rules = computed(() => {
  return {
    account: {
      required: true,
      trigger: 'blur',
      message: t('login.resetPasswordRuleTip'),
    },
  }
})
const formValue = ref({
  account: '',
})
const formRef = ref<FormInst | null>(null)
function handleRegister() {
  formRef.value?.validate()
}
</script>

<template>
  <div>
    <n-h2 depth="3" class="text-center">
      Đặt lại mật khẩu
    </n-h2>
    <n-form
      ref="formRef"
      :rules="rules"
      :model="formValue"
      :show-label="false"
      size="large"
    >
      <n-form-item path="account">
        <n-input
          v-model:value="formValue.account"
          clearable
          :placeholder="'Tài khoản'"
        />
      </n-form-item>
      <n-form-item>
        <n-space
          vertical
          :size="20"
          class="w-full"
        >
          <n-button
            block
            type="primary"
            @click="handleRegister"
          >
            Đặt lại
          </n-button>
          <n-flex justify="center">
            <n-text>Bạn đã có tài khoản?</n-text>
            <n-button
              text
              type="primary"
              @click="toLogin"
            >
              Đăng nhập
            </n-button>
          </n-flex>
        </n-space>
      </n-form-item>
    </n-form>
  </div>
</template>

<style scoped></style>
