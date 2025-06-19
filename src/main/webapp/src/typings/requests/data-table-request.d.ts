/// <reference path="../global.d.ts"/>

namespace DataTableRequest {

  interface Request {
    id?: number
    isRoot?: 0 | 1
    code: string
    label: string
    value?: number
  }
}
