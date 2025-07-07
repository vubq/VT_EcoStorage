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
}
