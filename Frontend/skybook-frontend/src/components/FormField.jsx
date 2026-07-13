export default function FormField({
  label,
  name,
  type = 'text',
  value,
  onChange,
  options,
  placeholder,
  hint,
  error,
  span2,
  required,
  step,
  disabled,
}) {
  const commonProps = {
    id: name,
    name,
    value: value ?? '',
    onChange: (e) => onChange(name, e.target.value),
    disabled,
    required,
  }

  return (
    <div className={`form-field ${span2 ? 'span-2' : ''}`}>
      <label htmlFor={name}>{label}</label>
      {type === 'select' ? (
        <select {...commonProps}>
          <option value="" disabled>
            {placeholder || 'Select…'}
          </option>
          {options?.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
      ) : type === 'textarea' ? (
        <textarea rows={3} placeholder={placeholder} {...commonProps} />
      ) : (
        <input type={type} placeholder={placeholder} step={step} {...commonProps} />
      )}
      {hint && !error && <span className="hint">{hint}</span>}
      {error && <span className="field-error">{error}</span>}
    </div>
  )
}
