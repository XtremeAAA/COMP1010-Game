# Masters of MQ

A text-based RPG combat game set in Macquarie University where players defend the campus from dark forces.

## What problems does your application solve?

My application solves the problem of making a traditional RPG system playable and engaging in a text-based format. It simplifies complex mechanics like health, stamina, and turn-based combat, while automatically handling calculations and random outcomes to keep the game exciting. The story-driven battles, where players fight through three progressive stages to save the university, give clear goals and a sense of progression.

## A description of the structure of your program.

### Core Features

- **Team Building**: Select 2 characters from 4 unique classes (Warrior, Mage, Rogue, Cleric)
- **Combat System**: Turn-based battles with stamina management and strategic choices
- **Skill System**: Each character has unique abilities with cooldowns and prerequisites
- **Progressive Difficulty**: Three increasingly challenging battles across campus locations

### Game Flow

1. Create your team with a custom name
2. Battle through three locations:
   - Library (vs Dark Warrior & Shadow Mage)
   - Courtyard (vs Corrupted Rogue & Dark Priest)
   - 4RPD (Final Boss: Overlord)

### Strategic Elements

- Resource Management (HP & Stamina)
- Combat Actions:
  - Basic Attack (10 stamina)
  - Defensive Stance (50% damage reduction)
  - Special Skills (varied costs)
- Team Synergy between different character classes

## How to Run the Game

### Prerequisites

* Java Development Kit (JDK) installed on your system
* Required libraries (included in lib/ folder):
  - junit-4.13.2.jar
  - org.json

### How to run

If the build script doesn't work:

1. Run the `build.sh` file for MacOS or `build.bat` file for WindowsOS

## Game Controls

* Enter numbers to select menu options
* Type 'info `<number>`' to see character details
* During battle:
  - Type '1' for basic attack
  - Type '2' to defend
  - Type '3' or '3.1', '3.2' etc. for skills
  - Follow on-screen prompts to select targets

## Troubleshooting

* If you see "Failed to load game data":

  - Verify that the data/ folder contains characters.json and opponents.json
  - Check that lib/ contains required JAR files
* If you get "ClassNotFoundException":

  - Make sure you're running from the project root directory
  - Verify the classpath includes both source and lib folders
* For Windows users:

  - Run the `build.bat` file
* If the code doesn't run:

  - For MacOS users
    - Compile the code and run `java -cp "source:lib/*" mastersofmq.MastersOfMQ`
  - For WindowsOS users
    - Complie the code and run
    - ```
      mkdir -p source
      javac -d source -cp "lib/*" $(find source -name "*.java")
      java -cp "source:lib/*" mastersofmq.MastersOfMQ
      ```
