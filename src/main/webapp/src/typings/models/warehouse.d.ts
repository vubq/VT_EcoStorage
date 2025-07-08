namespace Warehouse {
  interface Data {
    id?: string
    name?: string
    status?: string
    address?: string

    zones?: Zone.Data[]
    zoneId?: string

    products?: Product.ProductByLocation[]
  }

  interface MoveLocation {
    productId: string
    productBarcode: string
    productName: string
    productUnitName: string
    inventoryQuantity: number
    location: string
    locationId: string
    warehouseId: string
    warehouseName: string
    locationsNew: {
      locationId?: string | null
      quantity: number
      zoneId?: string | null
      shelfId?: string | null
    }[]
  }
}
