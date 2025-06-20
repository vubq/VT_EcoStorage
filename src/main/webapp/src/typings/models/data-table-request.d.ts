namespace DataTable {
  export interface Request {
    currentPage: number
    perPage: number
    filter: string
    sortBy: string
    sortDesc: boolean
    [key: string]: any
  }
}