namespace Product {
  interface DataTable {
    id?: string
    name?: string
    barcode?: string
    sku?: string
    inventoryQuantity?: number
    productCategoryName?: string
  }

  interface Filter {
    productCategoryId?: string
    productOriginId?: string
    productUnitId?: string
  }

  interface Data {
    id?: string
    sku?: string
    barcode?: string
    name?: string
    imageUrl?: string
    description?: string
    salePrice?: number
    costPrice?: number
    discountPrice?: number
    taxRate?: number
    note?: string
    status?: string
    
    productCategoryId?: string
    productOriginId?: string
    productUnitId?: string

    inventoryQuantity?: number

    historyInventories?: ProductInventory.Data[]
  }
}
