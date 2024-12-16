# Dungeon Chef

A 2D arcade-style game where you collect ingredients while avoiding enemies to create magical recipes. Navigate through three unique boards - Fire, Ice and Earth - each with their own challenges and ingredients.

## Game Features

- Three unique levels: Fire, Ice and Earth boards
- Collect ingredients to complete recipes
- Avoid static and moving enemies
- Special bonus ingredients with higher point values
- Score tracking and timer
- Sound effects and background music

## Prerequisites

- Java 17 or higher
- Apache Maven 3.6 or higher

## Building and Running

1. Clone the repository:
```bash
git clone [repository-url]
cd CMPT276-Group-22
```

2. Build and run the game:
```bash
mvn clean compile exec:java
```

## How to Play

1. Use arrow keys to move your character
2. Collect required ingredients:
   - Fire Board: Broth, Chili, Meat
   - Ice Board: Banana, Ice Cream, Cherry, Whip Cream
   - Earth Board: Dal, Potato, Carrot, Onion
3. Watch for bonus ingredients that appear temporarily
4. Avoid enemies - static enemies reduce score, moving enemies end the game
5. Press 'P' to pause

## Testing

Run all tests*:
```bash
mvn clean test
```

Run specific test file:
```bash
mvn test -Dtest=TestNameTest -e
```
Example test names:
- FireBoardTest
- IceBoardTest 
- EarthBoardTest
- GameBoardTest
- BoardFactoryTest

*NOTE: During MainMenuTest, when instructions game panel pops up, must press "OK" to continue testing

## Using Artifacts

JAR file located in Dungeon-Chef/artifacts

Run JAR file:
```
java -jar target/Dungeon-Chef-1.0-SNAPSHOT.jar
```

Javadocs located in Dungeon-Chef/artifacts/apidocs (open index.html to view in browser)

OR

Create the JAR on your own*:
```
mvn clean package
```
*NOTE: Running this command runs all tests. During MainMenuTest, when instructions game panel pops up, must press "OK" to continue testing

Create the Javadocs on your own:
```
mvn javadoc:javadoc
```

## Game Controls

- Arrow Keys: Move character
- P: Pause game
- ESC: Access menu

## Points System

Fire Board:
- Broth: +10
- Chili: +5  
- Meat: +7
- Ice Tea Bonus: +20
- Enemy Hit: -10

Ice Board:
- Banana: +5
- Ice Cream: +10
- Cherry: +7
- Whip Cream: +8
- Milkshake Bonus: +20
- Enemy Hit: -10

Earth Board:
- Dal: +7
- Potato: +5
- Carrot: +5
- Onion: +5
- Rice Bonus: +20
- Enemy Hit: -10
