namespace ProductInventoryLocation {

  interface Data {
    id?: string
    status?: string
    productId?: string
    locationId?: string
    purchaseOrderDetailId?: string
    quantity?: number

    zoneId?: string
    shelf?: string
  }
}
