namespace User {
  interface Data {
    id?: string
    username?: string
    password?: string
    email?: string
    phoneNumber?: string
    firstName?: string
    lastName?: string
    note?: string
    status?: string

    permissions?: string[]
    permissionGroups?: string[]
  }
}
