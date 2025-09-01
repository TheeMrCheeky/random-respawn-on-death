# Random Respawn On Death Changelog

## v1.1.0 - Hardcore Mode & Achievement System 🎯
### 🎊 Major Features Added
- **Complete Achievement System**: Dedicated mod advancement tab with progressive challenges
- **Temporary Hardcore Mode**: First death transforms survival worlds into hardcore until redemption
- **Authentic Hardcore Hearts**: Visual hardcore heart display during cursed phase using advanced rendering
- **Health Curse System**: First death reduces max health to 50% (5 hearts) with visual feedback
- **Golden Apple Redemption**: Restore health and exit hardcore mode by eating golden apples
- **Command Restriction**: Commands blocked during hardcore mode for authentic challenge
- **World Difficulty Locking**: Automatically locks difficulty to Hard during cursed phase
- **Immersive Visual Effects**: Actionbar messages, particle effects, and chat notifications
- **Death Counter System**: Track deaths across achievements and progression

### 🎯 Achievements Added
- **First Death** - Die for the first time (Jack-o'-lantern icon)
- **Curse Breaker** - Escape hardcore mode by restoring full health
- **Death Collector** - Die multiple times (5+ deaths)
- **Master of Fate** - Complete all other achievements

### 🛠️ Technical Improvements
- **Advanced Client-Side Rendering**: Sophisticated hardcore heart display using reflection
- **Network Packet System**: Custom packets for client-server hardcore mode synchronization
- **Enhanced Reflection System**: Comprehensive field detection for world modification
- **Clean Code Architecture**: Modular design with proper event handling and state management

### 🎮 How It Works
1. **First Death**: Triggers achievement and activates temporary hardcore mode
2. **Cursed Phase**: Health reduced, hardcore hearts visible, commands blocked, difficulty locked
3. **Golden Apple Redemption**: Each golden apple restores 1 heart (need 5 to fully restore)
4. **Mode Restoration**: Full health triggers automatic return to normal survival mode

---

## v1.0.2 - Enhanced Surface Safety
### 🛡️ Improvements
- **Enhanced surface detection**: Now uses sky visibility checks to ensure true surface spawning
- **Expanded safety validation**: 3 blocks of air space instead of 2 for better clearance
- **Danger zone detection**: Scans for lava, fire, and magma blocks in spawn area
- **Improved search algorithm**: Expanding ring search (10x10 area) for better safe location finding
- **Better fallback systems**: Multiple fallback options including world spawn and sea level+10

### 🐛 Bug Fixes
- Fixed issue where players could spawn in caves or under overhangs
- Fixed insufficient air space causing suffocation on spawn
- Improved surface level detection accuracy

---

## v1.0.0 - Initial Release
### Release Highlights
- Initial release for NeoForge 1.21.1
- Fully configurable random respawn system
- Seamless integration with Minecraft's respawn mechanics

### Features
- Random respawn on death within configurable distance
- Configurable minimum and maximum spawn distance
- Respects bed spawn points (optional)
- Safe spawn location detection
- Dimension-specific spawn settings
- Config screen with tooltips for easy configuration

## Technical Details
- Compatible with Minecraft 1.21.1
- Built for NeoForge 21.1.206
- Clean, performant implementation
- No known conflicts with other mods

## Configuration Options
- Minimum spawn distance from death point
- Maximum spawn distance from death point
- Option to respect bed spawn points
- Dimension-specific settings
- Safe spawn location parameters

## Installation
1. Install NeoForge 21.1.206 or later
2. Drop the mod file into your mods folder
3. Launch Minecraft and configure settings as desired

## Known Issues
- None reported

## Planned Features
- Additional dimension-specific configurations
- More spawn point customization options
- Integration with other mods' spawn systems
