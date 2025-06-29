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

  interface FilterProductByLocation {
    productCategoryId?: string
    zoneId?: string
    shelfId?: string
    floorId?: string
    warehouseId?: string
  }

  interface ProductByLocation {
    locationId?: string
    productId?: string
    productBarcode?: string
    productName?: string
    productCostPrice?: number
    productSalePrice?: number
    productStatus?: string
    productCategoryId?: string
    productCategoryName?: string
    productUnitName?: string
    zoneId?: string
    zoneName?: string
    shelfId?: string
    shelfName?: string
    floorId?: string
    floor?: number
    inventoryQuantity?: number
    location?: string
  }
}
