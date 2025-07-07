namespace ExportOrder {
  interface DataTable {
    id?: string
    expectedDate?: Date
    deliveredDate?: Date
    totalAmount?: number
    note?: string
    status?: string
    warehouseName?: string
    customerName?: string
    type?: string
    warehouseToName?: string
  }

  interface Data {
    id?: string
    status?: string
    expectedDate?: Date
    deliveredDate?: Date
    totalAmount?: number
    type?: string
    warehouseId?: string
    customerId?: string
    details?: ExportOrderDetail.Data[]
    note?: string
    warehouseToId?: string
    purchaseOrderId?: string
  }

  interface Product {
    id?: string
    barcode?: string
    name?: string
    productUnitName?: string
    productCategoryName?: string
    inventoryQuantity?: number
    costPrice?: number
    salePrice?: number
  }
}
