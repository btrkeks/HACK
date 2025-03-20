import type React from "react"

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  fullWidth?: boolean
}

export default function Input({ className = "", fullWidth = false, ...props }: InputProps) {
  return (
    <input
      className={`px-4 py-2 border border-primary rounded-md focus:outline-none focus:ring-2 focus:ring-primary focus:ring-offset-2 ${fullWidth ? "w-full" : ""} ${className}`}
      {...props}
    />
  )
}

