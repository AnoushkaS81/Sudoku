# GRASS - Java Swing Sudoku Application

A comprehensive Sudoku game application built with Java Swing as part of CE320 Software Engineering coursework. Features include multiple difficulty levels, interactive gameplay, tutorial system, game solver, and save/load functionality.

---

## Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [Project Structure](#project-structure)
- [Dependencies](#dependencies)
- [Development Practices](#development-practices)
- [Testing](#testing)
- [Troubleshooting](#troubleshooting)
- [Contributors](#contributors)

---

## Features

### Core Gameplay
- **Three Difficulty Levels**: Easy, Medium, and Hard puzzles
- **Interactive Grid**: Click-to-select cells with visual feedback
- **Real-time Validation**: Instant mistake detection and tracking
- **Timer**: Track your solving speed
- **Hint System**: Get help when stuck
- **Auto-save**: Never lose your progress

### Tutorial System
- **Comprehensive "How to Play" Guide**: Learn Sudoku rules with visual demonstrations
- **Full 9×9 Grid Examples**: See complete puzzles with explanations
- **Interactive Navigation**: Smooth transitions between tutorial sections

### Game Management
- **Save/Load Games**: Continue where you left off
- **Multiple Save Slots**: Organize games by difficulty
- **Game Solver**: Auto-solve any valid Sudoku puzzle
- **Custom Puzzles**: Create and play your own puzzles
- **Modern UI**: Clean interface with FlatLaf styling

### Additional Features
- **Collapsible Sidebar**: Access saved games easily
- **Landing Page**: Intuitive starting point
- **Reset Functionality**: Start fresh anytime
- **Visual Feedback**: Cell highlighting and validation indicators

---

## Prerequisites

### Required Software
- **Java Development Kit (JDK)**: Version 21 or higher
  - Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
  - Verify installation: `java -version`

### Recommended IDE
- **IntelliJ IDEA** (Community or Ultimate Edition)
- Alternatively: Eclipse, NetBeans, or VS Code with Java extensions

---

## Installation & Setup

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Group_Project
```

### 2. Library Setup

The project requires the following external library:

#### **FlatLaf** (Modern Look and Feel)
- **Version**: 3.2.5
- **Purpose**: Provides modern UI styling for Swing components
- **Download**: [FlatLaf Releases](https://github.com/JFormDesigner/FlatLaf/releases)

**Installation Steps:**

1. Download `flatlaf-3.2.5.jar` from the link above
2. Create a `lib/` folder in the project root (if it doesn't exist):
   ```
   Group_Project/
   ├── src/
   ├── lib/
   │   └── flatlaf-3.2.5.jar
   ├── saves/
   └── README.md
   ```
3. Place `flatlaf-3.2.5.jar` in the `lib/` folder

#### **JUnit** (Testing Framework - Already Included)
- **Version**: 4.10
- **Purpose**: Unit testing framework
- **Location**: Already in `lib/junit-4.10.jar`

### 3. Configure IDE

#### **IntelliJ IDEA:**

1. Open the project in IntelliJ IDEA
2. Go to **File → Project Structure → Libraries**
3. Click **+** → **Java**
4. Navigate to `lib/flatlaf-3.2.5.jar` and add it
5. Click **Apply** and **OK**

#### **Eclipse:**

1. Right-click project → **Build Path → Configure Build Path**
2. Go to **Libraries** tab
3. Click **Add External JARs**
4. Select `lib/flatlaf-3.2.5.jar`
5. Click **Apply and Close**

#### **Command Line (javac/java):**

The classpath is automatically set when using the run commands below.

---

## Running the Application

### Option 1: Using IntelliJ IDEA

1. Open the project in IntelliJ
2. Navigate to `src/pages/Main.java`
3. Right-click and select **Run 'Main.main()'**
4. Or click the green play button next to the `main` method

### Option 2: Using Command Line

#### **Windows:**

```bash
# Compile
javac -cp "lib\*" -d out src\pages\*.java src\components\*.java src\db\*.java

# Run
java -cp "out;lib\*" pages.Main
```

#### **macOS/Linux:**

```bash
# Compile
javac -cp "lib/*" -d out src/pages/*.java src/components/*.java src/db/*.java

# Run
java -cp "out:lib/*" pages.Main
```

### Option 3: Create Run Configuration in IntelliJ

1. Go to **Run → Edit Configurations...**
2. Click **+** → **Application**
3. Set **Name**: `Sudoku Application`
4. Set **Main class**: `pages.Main`
5. Set **Module**: Your project module
6. Click **Apply** and **OK**
7. Run using the configuration dropdown

---

## Project Structure

```
Group_Project/
│
├── src/                              # Source code
│   ├── components/                   # Reusable UI components
│   │   ├── HintDialog.java                  # Hint dialog component
│   │   ├── PuzzleDifficultyRater.java       # Difficulty assessment
│   │   ├── SolveHandler.java               # Solver logic handler
│   │   ├── Solver.java                     # Sudoku puzzle solver
│   │   ├── SudokuBoard9By9.java            # Main 9×9 Sudoku grid
│   │   └── VictoryDialog.java              # Victory popup dialog
│   │
│   ├── pages/                        # Application pages/screens
│   │   ├── HomePageTest.java               # Home page tests
│   │   ├── HowToPlayPage.java              # Tutorial system
│   │   ├── InteractiveSudokuController.java # Game controller logic
│   │   ├── LandingPage.java                # Home screen
│   │   ├── Main.java                       # Application entry point
│   │   └── SideBarPanel.java               # Sidebar navigation
│   │
│   ├── db/                           # Data management
│   │   └── GameSaveManager.java            # Save/load functionality
│   │
│   ├── images/                       # Application images/assets
│   │
│   └── utils/                        # Utility classes
│       └── CommonConstants.java            # Shared constants
│
├── test/                             # Unit tests
│   ├── GameSaveManagerTest.java           # Save manager tests
│   ├── HintDialogTest.java                # Hint dialog tests
│   ├── PageTester.java                    # Page testing utilities
│   ├── SolverTest.java                    # Solver algorithm tests
│   ├── SudokuBoard9By9Test.java           # Board validation tests
│   └── TestGenerator.java                 # Test data generator
│
├── lib/                              # External libraries
│   ├── flatlaf-3.2.5.jar                  # FlatLaf UI library
│   └── junit-4.10.jar                     # Testing framework
│
├── saves/                            # Saved games directory
│   ├── easy_games.json
│   ├── medium_games.json
│   └── hard_games.json
│
├── Group_Project.iml                 # IntelliJ project file
└── README.md                         # This file
```

---

## Dependencies

### Runtime Dependencies

| Library | Version | Purpose | License |
|---------|---------|---------|---------|
| **FlatLaf** | 3.2.5 | Modern Swing Look and Feel | Apache 2.0 |

### Development Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| **JUnit** | 4.10 | Unit testing framework |
| **JDK** | 21+ | Java Development Kit |

### Adding FlatLaf to Classpath

The FlatLaf library provides a modern, flat design for Swing applications. It must be added to your classpath:

**Maven (if migrating to Maven):**
```xml
<dependency>
    <groupId>com.formdev</groupId>
    <artifactId>flatlaf</artifactId>
    <version>3.2.5</version>
</dependency>
```

**Gradle (if migrating to Gradle):**
```gradle
implementation 'com.formdev:flatlaf:3.2.5'
```

---

## Development Practices

This project follows professional software engineering practices as part of CE320 coursework:

### Test-Driven Development (TDD)
- Write tests before implementation
- Comprehensive test coverage for game logic
- Located in `test/` directory

### Version Control
- **Git**: Small, focused commits
- **Tuleap Integration**: Commits linked to user stories
- **Branching**: Feature branches for new development

### Code Quality Standards
- **Method Length**: Keep methods under 30 lines
- **Naming**: Descriptive variable names (no cryptic abbreviations)
- **Constants**: Replace magic numbers with named constants
- **DRY Principle**: Eliminate code duplication through refactoring

### Pair Programming
- Driver/Navigator methodology
- Regular role switching
- Collaborative problem-solving

---

## Testing

### Running Unit Tests

#### **IntelliJ IDEA:**
1. Right-click `test/` folder
2. Select **Run 'All Tests'**

#### **Command Line:**

```bash
# Compile tests
javac -cp "lib\*;out" -d out test\components\*.java

# Run tests
java -cp "out;lib\*" org.junit.runner.JUnitCore components.SudokuBoard9By9Test
```
---

## Troubleshooting

### Common Issues

#### **Error: Could not find or load main class pages.Main**

**Solution:**
1. Rebuild the project: **Build → Rebuild Project**
2. Verify `Main.java` has `package pages;` at the top
3. Check that compiled `.class` files exist in `out/` directory
4. Ensure classpath includes both `out/` and `lib/*`

#### **FlatLaf Not Found / NoClassDefFoundError**

**Solution:**
1. Verify `flatlaf-3.2.5.jar` exists in `lib/` folder
2. Re-add library to project structure (see Installation steps)
3. Rebuild the project

#### **Saved Games Not Appearing**

**Solution:**
- Saves are stored in: `C:\Users\[YourUsername]\AppData\Local\SudokuGame\saves\` (Windows)
- Or in the project's `saves/` folder if using project-relative saves
- Check console output for save location on startup

#### **Application Not Starting / GUI Issues**

**Solution:**
1. Verify Java version: `java -version` (should be 21+)
2. Check FlatLaf is properly loaded in `Main.java`
3. Look for error messages in console output

---

## Contributors

This project was developed as part of CE320 Extreme Programming and Large Scale Software Systems coursework.

### Development Team
- **Team Members**: Constantinos Patatas, Dithusan Sathiyarooban, Aadit Sachdeva, Sanjev Ravi, Kieren Moore, Michalis Philippides, Anoushka Singh
- **Project Name**: GRASS Sudoku
- **Course**: CE320 Extreme Programming and Large Scale Software Systems
- **Institution**: University of Essex
- **Academic Year**: 2025/2026

---

## License

This project is developed for academic purposes as part of university coursework.

**Happy Sudoku Solving! 🎮**