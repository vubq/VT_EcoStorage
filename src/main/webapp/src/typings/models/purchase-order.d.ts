namespace PurchaseOrder {
  interface DataTable {
    id?: string
    expectedDate?: Date
    receivedDate?: Date
    totalAmount?: number
    note?: string
    status?: string
    warehouseName?: string
    supplierName?: string
    type?: string
    warehouseFromName?: string
  }

  interface Data {
    id?: string
    status?: string
    expectedDate?: Date
    receivedDate?: Date
    totalAmount?: number
    type?: string
    warehouseId?: string
    supplierId?: string
    details?: PurchaseOrderDetail.Data[]
    note?: string
    warehouseFromId?: string
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
