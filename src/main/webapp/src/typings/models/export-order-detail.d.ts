namespace ExportOrderDetail {

  interface Data {
    id?: string
    quantity?: number
    unitPrice?: number
    totalAmount?: number
    status?: string
    exportOrderId?: string
    productId?: string
    productBarcode?: string
    productUnit?: string
    delete?: boolean
    productName?: string
  }
}
