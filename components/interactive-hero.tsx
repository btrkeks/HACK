"use client"

import { useEffect, useRef, useState } from "react"

export default function InteractiveHero() {
  const containerRef = useRef<HTMLDivElement>(null)
  const [mousePosition, setMousePosition] = useState({ x: 0, y: 0 })
  const [isHovering, setIsHovering] = useState(false)

  useEffect(() => {
    const container = containerRef.current
    if (!container) return

    const handleMouseMove = (e: MouseEvent) => {
      const rect = container.getBoundingClientRect()
      const x = e.clientX - rect.left
      const y = e.clientY - rect.top
      setMousePosition({ x, y })
    }

    const handleMouseEnter = () => setIsHovering(true)
    const handleMouseLeave = () => setIsHovering(false)

    container.addEventListener("mousemove", handleMouseMove)
    container.addEventListener("mouseenter", handleMouseEnter)
    container.addEventListener("mouseleave", handleMouseLeave)

    return () => {
      container.removeEventListener("mousemove", handleMouseMove)
      container.removeEventListener("mouseenter", handleMouseEnter)
      container.removeEventListener("mouseleave", handleMouseLeave)
    }
  }, [])

  // Generate random positions for the floating elements
  const generateElements = () => {
    const elements = []
    const shapes = ["circle", "square", "triangle", "hexagon"]

    for (let i = 0; i < 15; i++) {
      const shape = shapes[Math.floor(Math.random() * shapes.length)]
      const size = Math.random() * 60 + 20
      const x = Math.random() * 100
      const y = Math.random() * 100
      const delay = Math.random() * 5
      const duration = Math.random() * 10 + 10

      elements.push({ shape, size, x, y, delay, duration })
    }

    return elements
  }

  const elements = generateElements()

  return (
    <div
      ref={containerRef}
      className="absolute inset-0 overflow-hidden bg-gradient-to-br from-gray-50 to-gray-100 -z-10"
    >
      {/* Animated background elements */}
      {elements.map((el, index) => (
        <div
          key={index}
          className={`absolute opacity-20 ${
            el.shape === "circle"
              ? "rounded-full bg-primary"
              : el.shape === "square"
                ? "bg-primary"
                : el.shape === "triangle"
                  ? "triangle bg-primary"
                  : "hexagon bg-primary"
          }`}
          style={{
            width: `${el.size}px`,
            height: `${el.size}px`,
            left: `${el.x}%`,
            top: `${el.y}%`,
            animation: `float ${el.duration}s ease-in-out ${el.delay}s infinite alternate`,
            transform: `rotate(${Math.random() * 360}deg)`,
          }}
        />
      ))}

      {/* Interactive glow effect that follows the mouse */}
      <div
        className="absolute w-[500px] h-[500px] rounded-full bg-primary opacity-10 blur-[100px] transition-all duration-300"
        style={{
          left: `${mousePosition.x - 250}px`,
          top: `${mousePosition.y - 250}px`,
          opacity: isHovering ? 0.2 : 0,
        }}
      />

      {/* Radial gradient overlay */}
      <div className="absolute inset-0 bg-radial-gradient from-transparent to-white opacity-70" />

      {/* Interactive grid pattern */}
      <div className="absolute inset-0 grid-pattern opacity-10" />
    </div>
  )
}

