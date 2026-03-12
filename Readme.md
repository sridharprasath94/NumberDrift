

# Number Drift

**Number Drift** is a modern Android puzzle game inspired by the classic 2048-style gameplay. The project focuses on clean architecture, maintainable code structure, and scalable feature development.

The game introduces a unique **drift mechanic**, where tiles can automatically shift over time depending on the selected mode, adding an additional layer of challenge beyond traditional merge puzzles.

---

## Game Modes

**Classic Mode**  
A traditional puzzle experience with no time pressure. Focus on strategy and tile merging.

**Drift Mode (2s)**  
The board automatically drifts every 2 seconds, requiring faster decision making.

**Drift Mode (1s)**  
A high‑difficulty mode where the board drifts every second.

---

## Features

- Multiple gameplay modes
- Resume unfinished games
- Persistent high scores per mode
- Custom wooden themed UI
- Sound effects and background music
- Vibration feedback
- Dark mode support
- Unit testing
- CI pipeline using GitHub Actions

---

## Architecture

The project follows a **Clean Architecture + MVVM** approach to keep the codebase modular and easy to maintain.

The structure separates the application into three primary layers:

```
presentation
├── UI
├── ViewModels
└── Rendering & Input handling


domain
├── Models
├── UseCases
└── Repository interfaces


data
└── Repository implementations
```

This separation ensures that core game logic remains independent of the Android framework and can be easily tested.

---

## Tech Stack

- Kotlin
- Coroutines
- StateFlow
- MVVM
- Clean Architecture
- Material Design
- JUnit
- GitHub Actions (CI)

---

## Future Improvements

Planned enhancements include:

- Daily challenge mode
- Tutorial screen
- Achievements and leaderboards
- Gameplay animations
- Tablet optimization

---

## Author

**Sridhar Prasath**
