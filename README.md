<div style="margin-left:0px; transform: scale(1.0); transform-origin: left;">
  <a href="https://oliviermarteaux.dev">
    <img
      src="public/readme_banner.jpg"
      alt="oliviermarteaux.dev"
    />
  </a>
</div>

<br>

# 🧱 BrickBreaker – Jetpack Compose Arcade Game

A modern implementation of the classic **Brick Breaker** arcade game built entirely with **Jetpack Compose Canvas**.

This project demonstrates real-time rendering, state management, collision detection, and custom drawing in Compose — without using any external game engine.

---

## 🎮 Features

* 🧱 Dynamic brick grid (configurable rows & columns)
* 🟢 Smooth paddle movement
* 🔴 Ball physics with wall & paddle collision
* 💥 Brick destruction on impact
* 🎨 Premium UI rendering:

  * Rounded corners
  * Gradient shading
  * Drop shadows
  * Glow effects
* ⚡ Pure Compose Canvas rendering (no XML)

---

## 🛠 Built With

* Kotlin
* Jetpack Compose
* Canvas API
* Mutable state (`remember`, `mutableStateOf`)
* Functional grid generation (`flatMap`, `map`, `toSet`)

---

## 🧠 Architecture Overview

### Game Elements

| Component | Responsibility                          |
| --------- | --------------------------------------- |
| `Brick`   | Data class storing row & column         |
| Paddle    | Player-controlled horizontal movement   |
| Ball      | Position, velocity, and collision logic |
| Canvas    | Rendering & drawing of all objects      |

---

### Grid Generation

Bricks are dynamically generated using:

```kotlin
val bricks = (0 until brickColumns).flatMap { col ->
    (0 until brickRows).map { row ->
        Brick(col, row)
    }
}.toSet()
```

This ensures:

* Scalable grid size
* Clean immutable state updates
* Efficient redraws

---

## 🎨 Rendering Approach

All visuals are drawn using:

* `drawRoundRect`
* `drawCircle`
* `Brush.verticalGradient`
* `Brush.radialGradient`

Shadows and highlights are layered manually to create depth.

Example:

```kotlin
drawRoundRect(
    brush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4FC3F7),
            Color(0xFF1976D2)
        )
    ),
    cornerRadius = CornerRadius(18f, 18f)
)
```

---

## 📐 Game Logic

### Collision Handling

* Ball ↔ Walls
* Ball ↔ Paddle
* Ball ↔ Bricks

On brick collision:

* Brick is removed from state
* Ball direction is inverted

---

## ⚙️ Configuration

You can easily adjust the grid:

```kotlin
val brickColumns = 5
val brickRows = 5
```

The layout auto-scales based on screen width.

---

## 🚀 How To Run

1. Clone the repository
2. Open in Android Studio
3. Run on emulator or device
4. Enjoy breaking bricks 🎮

---

## 📈 Possible Improvements

* Score system
* Multiple levels
* Sound effects
* Particle explosions
* Power-ups
* Game over / restart screen
* Increasing difficulty
* High score persistence

---

## 🎯 Learning Goals

This project is ideal for learning:

* Canvas drawing in Compose
* Real-time UI updates
* State-driven game loops
* Functional grid generation
* Manual physics implementation

---

## 📜 License

Free to use for learning and experimentation.

---
