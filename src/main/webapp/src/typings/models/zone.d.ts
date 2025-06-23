namespace Zone {
  interface Data {
    id?: string
    name?: string
    status?: string
    warehouseId?: string
    warehouseName?: string

    shelves?: Shelf.Data[]
    shelfId?: string
  }
}
