<template>
  <div class="invoice-box"  ref="invoiceContent">
    <!-- Thông tin công ty -->
    <div class="company-info">
      <div class="company-logo">
        <img src="/vt_ecostorage_logo.png" alt="Logo" />
      </div>
      <div class="company-details">
        <strong>CÔNG TY TNHH VT-ECOSTORAGE</strong><br />
        Địa chỉ: {{ warehouse()?.address }}<br />
        Mã số thuế: {{ referenceData.company.taxNumber }}<br />
        SĐT: {{ referenceData.company.phoneNumber }} | Email: {{ referenceData.company.email }}
      </div>
    </div>

    <!-- Tiêu đề hóa đơn -->
    <div class="invoice-title">HÓA ĐƠN NHẬP HÀNG {{ order.type === 'INTERNAL' ? '(NỘI BỘ)' : '' }}</div>

    <!-- Thông tin khách hàng -->
    <table>
      <tbody>
        <tr>
          <td><strong>Ngày:</strong> {{ moment().format('DD/MM/YYYY') }}</td>
          <td style="text-align:right"><strong>Mã hóa đơn:</strong> {{ order.id }}</td>
        </tr>
        <tr>
          <td colspan="2"><strong>Nhà cung cấp:</strong> {{ supplier()?.name }}</td>
        </tr>
        <tr v-if="order.type === 'PURCHASE'">
          <td colspan="2"><strong>SĐT:</strong> {{ supplier()?.phoneNumber }}</td>
        </tr>
        <tr>
          <td colspan="2"><strong>Địa chỉ:</strong> {{ supplier()?.address }}</td>
        </tr>
      </tbody>
    </table>

    <br />

    <!-- Bảng sản phẩm -->
    <table>
      <thead>
        <tr class="heading">
          <td>STT</td>
          <td>Mã SP</td>
          <td>Tên sản phẩm</td>
          <td>ĐVT</td>
          <td>Số lượng</td>
          <td>Đơn giá</td>
          <td>Thành tiền</td>
        </tr>
      </thead>
      <tbody>
        <tr
          class="item"
          v-for="(item, index) in order.details"
          :key="index"
        >
          <td>{{ index + 1 }}</td>
          <td>{{ item.productBarcode }}</td>
          <td>{{ item.productName }}</td>
          <td>{{ item.productUnit }}</td>
          <td>{{ item.quantity }}</td>
          <td>{{ formatCurrency(item.unitPrice!) }}</td>
          <td>{{ formatCurrency(item.totalAmount!) }}</td>
        </tr>
        <tr class="total">
          <td colspan="6" style="text-align:right">Tổng cộng:</td>
          <td>{{ formatCurrency(order.totalAmount!) }}</td>
        </tr>
      </tbody>
    </table>

    <br /><br />

    <!-- Chữ ký -->
    <table class="signature">
      <tbody>
        <tr>
          <td><strong>Người lập hóa đơn</strong><br /><br /><br />(Ký, ghi rõ họ tên)</td>
          <td><strong>Khách hàng</strong><br /><br /><br />(Ký, ghi rõ họ tên)</td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script setup lang="tsx">
import moment from 'moment';

const invoiceContent = ref(null)
defineExpose({ invoiceContent })

const props = defineProps<{
  order: PurchaseOrder.Data,
  referenceData: ReferenceData.PurchaseOrder
}>()

function formatCurrency(value: number) {
  return value?.toLocaleString('vi-VN')
}

function warehouse() {
  return props.referenceData.warehouses.find(w => w.id === props.order.warehouseId)
}

function supplier() {
  if (props.order.type === 'PURCHASE') {
    const customer = props.referenceData.suppliers.find(s => s.id === props.order.supplierId)
    return {
      name: customer?.name,
      phoneNumber: customer?.phoneNumber,
      address: customer?.address
    }
  }
  if (props.order.type === 'INTERNAL') {
    const warehouse = props.referenceData.warehouses.find(w => w.id === props.order.warehouseId)
    return {
      name: warehouse?.name,
      phoneNumber: '',
      address: warehouse?.address
    }
  }
}
</script>

<style scoped>
@page {
  size: A4;
  margin: 20mm;
}
.invoice-box {
  max-width: 800px;
  margin: auto;
  padding: 30px;
  font-family: "DejaVu Sans", Arial, sans-serif;
  font-size: 14px;
  line-height: 1.5;
  color: #333;
}
.company-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.company-info img {
  height: 80px;
}
.company-details {
  text-align: right;
  font-size: 13px;
}
.invoice-title {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  margin: 20px 0;
}
table {
  width: 100%;
  border-collapse: collapse;
  text-align: left;
}
table td {
  padding: 5px;
  vertical-align: top;
}
table tr.heading td {
  background: #eee;
  border-bottom: 1px solid #ddd;
  font-weight: bold;
}
table tr.item td {
  border-bottom: 1px solid #eee;
}
table tr.total td {
  font-weight: bold;
  border-top: 2px solid #eee;
}
.signature {
  text-align: center;
  margin-top: 10px;
}
.signature td {
  width: 50%;
}
</style>
