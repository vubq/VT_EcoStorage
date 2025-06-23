namespace Warehouse {
  interface Data {
    id?: string
    name?: string
    status?: string

    zones?: Zone.Data[]
    zoneId?: string
  }
}
