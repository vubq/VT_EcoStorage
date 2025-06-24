namespace ProductInventory {
  interface Data {
    id?: string
    quantity?: number
    type?: string
    transactionType?: string
    productId?: string
    purchaseOrderId?: string
    exportOrderId?: string
    createdAt?: Date
  }
}
