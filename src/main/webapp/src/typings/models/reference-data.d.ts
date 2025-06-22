namespace ReferenceData {
  interface PurchaseOrder {
    warehouses: Warehouse.Data[]
    suppliers: Supplier.Data[]
    categories: Category.Data[]
  }
}
