export function initRulesForm(
  model: Record<string, any>,
  getError: (
    rule: any,
    value: any,
    callback: (err?: Error) => void,
    key: string
  ) => void,
) {
  const rules: Record<string, any> = {}

  for (const key in model) {
    if (Object.prototype.hasOwnProperty.call(model, key)) {
      rules[key] = [
        {
          validator: (rule: any, value: any, callback: any) =>
            getError(rule, value, callback, key),
          trigger: ['input', 'blur'],
        },
      ]
    }
  }

  return rules
}

export function validateFieldFromErrors(
  errors: Ref<Error.ValidationError[]>,
  key: string,
  callback: (err?: Error) => void,
) {
  const found = errors.value.find(
    e => e.field === key && e.messages.length !== 0,
  )
  callback(found ? new Error(found.messages[0]) : undefined)
}
