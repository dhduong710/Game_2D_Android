# GameCombat

**GameCombat** is a 2D fighting game for Android, heavily inspired by classic anime fighting games like *Bleach vs. Naruto*.  
This project is built with the **LibGDX** framework using **Kotlin**, focusing on a clean entity system and a flexible combat mechanism based on resources (mana/cooldown).

Players can choose their favorite character, select an arena, and battle against an AI opponent.

---

## Features

- **Character Selection:** Choose from a roster of fighters (currently includes **Naruto** and **Luffy**).
- **Arena Selection:** Pick your battlefield (*Konoha*, *Thousand Sunny*).
- **Full Game Flow:** From **Main Menu → Character Select → Arena Select → Game Screen → Result → Back to Menu**.
- **Detailed Combat System:**
  - **HP/MP Management:** Both characters have health and mana bars.
  - **4 Attack Buttons:** 1 normal attack + 3 special skills.
  - **Mana-based Skills:** Skills consume MP, which regenerates over time.
  - **Strategic Defense:** The *Down* button blocks incoming damage but consumes 50% of the current MP — creating a tactical trade-off.

- **Gameplay Balance:**
  - **Cooldown for Player:** Prevents spamming normal attacks.
  - **Advanced AI:** The enemy has no cooldown, providing an extra challenge.

- **AI Opponent:** Automatically moves, tracks the player, and randomly selects one of four attack types when in range.
- **Time Limit:** Each battle lasts **60 seconds**. The fighter with more HP when time runs out wins.
- **On-screen Controls:** Fully functional D-pad and action buttons designed for mobile play.

---

## Technologies Used

| Component | Technology |
|------------|-------------|
| Game Engine | LibGDX |
| Language | Kotlin |
| Platform | Android |
| UI Framework | LibGDX Scene2D |
| IDE | Android Studio |

---

## Project Structure

```
core/src/main/kotlin/com/hust/gamecombat/
├── entities/
│   ├── BaseCharacter.kt       # Abstract class for common logic (HP, MP, physics, states...)
│   ├── CharacterID.kt         # Enum for character assets and names
│   ├── Player.kt              # Player class (handles cooldown for normal attacks)
│   └── Enemy.kt               # Enemy class (AI logic)
│
├── MenuScreen.kt              # Main Menu screen (Start/Exit)
├── CharacterSelectScreen.kt   # Character selection screen (P1/P2)
├── ArenaSelectScreen.kt       # Arena selection screen
└── GameScreen.kt              # Main combat screen (handles input, combat, HUD...)

assets/
├── arenas/                    # Arena backgrounds (konoha.png, thousandsunny.png)
├── backgrounds/               # Menu backgrounds
├── characters/                # Character sprites (naruto, luffy)
├── fonts/                     # Game fonts
└── ui/                        # UI elements (buttons, HUD, banners...)
```

---

## How to Run the Project

1. Clone this repository.

2. Open the project using **Android Studio**.

3. Wait for **Gradle** to sync and download all required dependencies.

4. Connect a real Android device **or** launch an **Android Emulator**.

5. Run the **"android"** configuration in Android Studio.

---

## License
This project is developed for learning and research purposes — not intended for commercial use.
