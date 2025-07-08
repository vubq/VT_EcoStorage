namespace Inventory {

  interface Data {
    warehouseId?: string
    warehouseName?: string
    products?: Inventory.Product[]
  }

  interface Product {
    productId?: string
    productName?: string
    productBarcode?: string
    productSKU?: string
    productCategoryId?: string
    productCategoryName?: string
    productUnitId?: string
    productUnitName?: string
    inventoryQuantity?: number
    locations?: Inventory.Location[]
  }

  interface Location {
    locationId?: string
    locationName?: string
    inventoryQuantity?: number
  }
}