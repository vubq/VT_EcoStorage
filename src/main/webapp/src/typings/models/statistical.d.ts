namespace Statistical {

  interface Data {
    warehouseId?: string
    warehouseName?: string
    products?: Statistical.Product[]
  }

  interface Product {
    productId?: string
    productName?: string
    productBarcode?: string
    productSKU?: string
    totalImportQuantity?: number
    totalImportAmount?: number
    totalExportQuantity?: number
    totalExportAmount?: number
  }
}