# MadokraftMagica

A Minecraft Forge mod project targeting Minecraft 1.19.2, built with Gradle and managed with MCreator.

This repository contains:
- A Gradle-based Forge mod project (Java 17).
- An MCreator workspace (`madokraftmagica.mcreator`) and `elements/` folder for generated content.
- Forge MDK 43.5.0 integration for Minecraft 1.19.2.

If you’d like to help, contributions are very welcome! See “Contributing” below for how to get started.

---

## Requirements

- Java Development Kit (JDK) 17
- Git
- An IDE (IntelliJ IDEA recommended; VS Code and Eclipse also work)
- Minecraft Forge toolchain (provided via Gradle wrapper)
- Optional: MCreator (if you plan to contribute content via the visual editor)
  - Open the included `madokraftmagica.mcreator` workspace file

Target versions:
- Minecraft: 1.19.2
- Forge: 43.5.0

---

## Getting Started

1. Clone the repository
   - `git clone https://github.com/dontloseyourheadsu/MadokraftMagica.git`
   - `cd MadokraftMagica`

2. Configure JDK 17 in your IDE or environment

3. First build (downloads dependencies)
   - macOS/Linux: `./gradlew build`
   - Windows: `gradlew.bat build`

4. Open the project in your IDE as a Gradle project

---

## Running in Development

- Run the client:
  - macOS/Linux: `./gradlew runClient`
  - Windows: `gradlew.bat runClient`

- Run the server (optional):
  - macOS/Linux: `./gradlew runServer`
  - Windows: `gradlew.bat runServer`

- Build a distributable JAR:
  - macOS/Linux: `./gradlew build`
  - Windows: `gradlew.bat build`
  - Output is in `build/libs/`

Note: If you’re using IntelliJ IDEA with Forge, you usually don’t need to run extra “gen runs” tasks in modern MDK setups—the provided Gradle tasks should work out of the box.

---

## Project Structure

- `src/main/java` — Java source code for the mod
- `src/main/resources` — Resources (assets, data packs, `mods.toml`, etc.)
- `elements/` — MCreator element definitions
- `.mcreator/` — MCreator internal configuration
- `madokraftmagica.mcreator` — MCreator workspace file
- `build.gradle`, `settings.gradle`, `gradle.properties` — Gradle configuration
- `gradlew`, `gradlew.bat`, `gradle/` — Gradle wrapper

MCreator notes:
- Content defined in `elements/` and the MCreator workspace generates code/resources under `src/`.
- Direct edits to generated files may be overwritten by MCreator regeneration. Prefer adding/editing elements via MCreator when applicable, then regenerate.

---

## Contributing

There are multiple ways to contribute:
- Report bugs and crashes
- Suggest features or improvements
- Create textures, models, sounds, or localization files
- Implement new gameplay content (items/blocks/entities/procedures) via MCreator
- Write or refactor Java code, fix issues, improve performance
- Improve documentation

Before you start:
- Check existing issues: https://github.com/dontloseyourheadsu/MadokraftMagica/issues
- If your change is significant, please open an issue to discuss your approach first.

### Development Setup

- For code-centric changes:
  1. Set up JDK 17 and your IDE
  2. Build once
