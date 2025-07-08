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

  interface Product {
    productCategories: Category.Data[]
    productUnits: Unit.Data[]
    productOrigins: Origin.Data[]
  }

  interface Statistical {
    warehouses: Warehouse.Data[]
  }

  interface Inventory {
    warehouses: Warehouse.Data[]
    categories: Category.Data[]
  }
}
