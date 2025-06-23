namespace ReferenceData {
  interface PurchaseOrder {
    warehouses: Warehouse.Data[]
    suppliers: Supplier.Data[]
    categories: Category.Data[]
  }

  interface ExportOrder {
    warehouses: Warehouse.Data[]
    customers: Customer.Data[]
    categories: Category.Data[]
  }
}
