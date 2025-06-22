namespace PurchaseOrderDetail {

  interface Data {
    id?: string
    quantity?: number
    unitPrice?: number
    totalAmount?: number
    status?: string
    purchaseOrderId?: string
    productId?: string
    productBarcode?: string
    productUnit?: string
    delete?: boolean
    locations?: ProductInventoryLocation.Data[]
    productName?: string
  }
}
