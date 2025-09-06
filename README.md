# Random Respawn On Death

A comprehensive Minecraft survival enhancement mod that transforms the death experience with hardcore mode mechanics, achievement systems, and immersive visual changes.

## 🎯 Core Features

### 🏆 Achievement System
- **Dedicated advancement tab** with custom mod achievements
- **Progressive challenges** that unlock as you play
- **Visual rewards** including custom Jack-o'-lantern icons
- **Death tracking** across all achievements

### ⚰️ Temporary Hardcore Mode
- **First death triggers hardcore transformation** of survival worlds
- **Authentic hardcore experience** until redemption
- **World difficulty automatically locks** to Hard during cursed phase
- **Commands blocked** during hardcore mode for true challenge
- **Automatic restoration** when health is fully recovered

### 💖 Health Curse & Redemption System
- **Health reduced to 50%** (5 hearts) after first death
- **Golden apple redemption** - each golden apple restores 1 heart
- **Visual feedback** with authentic hardcore heart display
- **Progressive restoration** requiring 5 golden apples for full recovery

### 🎨 Immersive Visual Effects
- **Hardcore heart icons** during cursed phase using advanced rendering
- **Actionbar notifications** for status updates
- **Particle effects** for visual feedback
- **Chat messages** explaining system state changes
- **Real-time health tracking** with restoration progress

### 🗺️ Enhanced Respawn System
- **Random respawn locations** when dying without spawn points
- **Configurable distance range** (100-10,000 blocks)
- **Smart surface detection** with safety validation
- **Proper End dimension handling**
- **Respects bed spawn points** and vanilla mechanics

## 🎮 How It Works

### The Death Experience
1. **First Death**: Triggers the "First Death" achievement and hardcore mode
2. **Cursed Phase**: 
   - Health reduced to 5 hearts with hardcore heart display
   - World difficulty locked to Hard
   - Commands disabled
   - Visual and audio feedback provided
3. **Redemption Journey**: Collect and eat 5 golden apples to restore health
4. **Liberation**: Full health restoration automatically exits hardcore mode

### Achievement Progression
- 🎃 **First Death** - Your first death activates the curse
- 🛡️ **Curse Breaker** - Escape hardcore mode by restoring full health  
- 💀 **Death Collector** - Accumulate multiple deaths (5+)
- 👑 **Master of Fate** - Complete all achievements to master death itself

## 📦 Installation

1. Install **NeoForge for Minecraft 1.21.1**
2. Download from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/random-respawn-on-death-neoforge-forge-quilt) or [Modrinth](https://modrinth.com/mod/random-respawn-on-death)
3. Place the .jar file in your `mods` folder
4. Launch and experience enhanced survival!

## ⚙️ Configuration

Configure the mod in-game:
1. Main menu or pause menu → **Mods**
2. Find "Random Respawn On Death" → **Configure**
3. Adjust respawn distance (100-10,000 blocks)

## 🔧 Technical Details

### Advanced Features
- **Client-server packet synchronization** for hardcore mode state
- **Advanced reflection system** for world modification
- **Sophisticated GUI rendering** for authentic hardcore hearts
- **Comprehensive field detection** for cross-version compatibility
- **Event-driven architecture** with proper cleanup and restoration

### Compatibility
- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.206+
- **Client-Server**: Full multiplayer support
- **Performance**: Optimized with minimal overhead

## Installation

1. Install NeoForge for Minecraft 1.21.1
2. Download the latest release from [CurseForge](https://www.curseforge.com/minecraft/mc-mods/random-respawn-on-death-neoforge-forge-quilt) or [Modrinth](https://modrinth.com/mod/random-respawn-on-death)
3. Place the .jar file in your mods folder

## Bug Reports & Feature Requests

Found a bug or have a suggestion? Please report it on our [GitHub Issues page](https://github.com/TheeMrCheeky/random-respawn-on-death/issues)!

When reporting bugs, please include:
- Minecraft version
- NeoForge version
- Mod version
- Description of the issue
- Steps to reproduce
- Any relevant crash reports or logs
- List of other mods installed (if applicable)

## Configuration

The mod can be configured in-game:

1. From the main menu or pause menu, go to Mods
2. Find "Random Respawn On Death" and click the Configure button
3. Adjust the respawn distance (100-10,000 blocks)

## License

All Rights Reserved

## For Developers

### Building from Source

1. Clone the repository
2. Open a terminal in the project directory
3. Run `./gradlew build`
4. Find the built jar in `build/libs`

### Java requirement

This project requires Java 21 to build and run the NeoForge toolchain. If your system's `JAVA_HOME` points to an older JDK (for example, Java 17), Gradle will still work but the toolchain needs a JDK 21 installation available. You can install Eclipse Adoptium Temurin 21 on Windows via winget:

```powershell
winget install -e --id EclipseAdoptium.Temurin.21.JDK
```

Or download and install from: https://adoptium.net/

### Dependencies

- Minecraft 1.21.1
- NeoForge 21.1.206+
