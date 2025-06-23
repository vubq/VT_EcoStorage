namespace Shelf {
  interface Data {
    id?: string
    name?: string
    status?: string
    zoneId?: string
    warehouseId?: string
    
    warehouseName?: string
    zoneName?: string

    floors?: Floor.Data[]
    floorId?: string
  }
}
