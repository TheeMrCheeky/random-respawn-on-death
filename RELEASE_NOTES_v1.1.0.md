# Random Respawn On Death v1.1.0 - Release Notes 🎯

## 🎊 Major Release: Hardcore Mode & Achievement System

This massive update transforms Random Respawn On Death from a simple respawn mod into a comprehensive survival enhancement experience with hardcore mechanics, achievement systems, and immersive visual changes.

## 🚀 What's New in v1.1.0

### 🏆 Complete Achievement System
- **Dedicated mod advancement tab** with custom achievements
- **Progressive unlocking system** that tracks your death journey
- **Custom icons** including Jack-o'-lantern for first death
- **Four main achievements** with meaningful progression

### ⚰️ Temporary Hardcore Mode
- **Survival worlds transform into hardcore** after first death
- **Authentic hardcore experience** until redemption
- **World automatically locked to Hard difficulty** during curse
- **Commands completely disabled** for true hardcore challenge
- **Seamless restoration** when health is recovered

### 💖 Health Curse & Golden Apple Redemption
- **Health reduced to 50%** (5 hearts) after first death
- **Golden apples restore 1 heart each** (need 5 total for full recovery)
- **Visual progression tracking** with real-time feedback
- **Authentic hardcore heart display** during cursed phase

### 🎨 Advanced Visual System
- **Hardcore hearts with skull overlay** using sophisticated client-side rendering
- **Actionbar notifications** for status updates and progress
- **Particle effects** for visual feedback during key moments
- **Chat integration** with clear explanations of system changes
- **Immersive sound and visual cues** throughout the experience

## 🎮 How the New System Works

### The Death Journey
1. **First Death** 💀
   - Unlocks "First Death" achievement with Jack-o'-lantern icon
   - Instantly transforms survival world into temporary hardcore mode
   - Health reduced to 5 hearts with hardcore heart display
   - Commands disabled, difficulty locked to Hard

2. **The Cursed Phase** 🏴‍☠️
   - Hardcore hearts visible with authentic skull overlay
   - Death becomes permanent until redemption
   - Difficulty cannot be changed
   - Commands remain blocked
   - Regular reminders of hardcore status

3. **Golden Apple Redemption** 🍎✨
   - Each golden apple consumed restores 1 heart
   - Progress tracked and displayed in actionbar
   - Visual and audio feedback for each restoration
   - Need 5 golden apples total for complete recovery

4. **Liberation** 🎉
   - Full health restoration automatically exits hardcore mode
   - Hearts return to normal appearance
   - Commands re-enabled
   - Difficulty restored to original setting
   - "Curse Breaker" achievement unlocked

### Achievement Progression
- 🎃 **First Death** - Your journey into darkness begins
- 🛡️ **Curse Breaker** - Escape the hardcore curse through golden apple redemption
- 💀 **Death Collector** - Accumulate 5+ deaths across your adventures  
- 👑 **Master of Fate** - Complete all achievements to master death itself

## 🔧 Technical Achievements

### Advanced Client-Side Rendering
- **Sophisticated hardcore heart display** using reflection-based level data modification
- **RenderGuiLayerEvent integration** for perfect timing and compatibility
- **Cross-version field detection** for robust hardcore mode activation
- **Clean rendering pipeline** with proper state restoration

### Network Architecture  
- **Custom packet system** for client-server hardcore mode synchronization
- **Persistent data management** across world reloads and server restarts
- **Event-driven state management** with proper cleanup and restoration
- **Multiplayer-ready design** with per-player state tracking

### World Modification System
- **Enhanced reflection system** with comprehensive field detection
- **Dynamic difficulty locking** using setDifficultyLocked() API
- **Server tick maintenance** for persistent world state
- **Automatic restoration** when conditions are met

## 📊 Compatibility & Requirements

- **Minecraft**: 1.21.1
- **NeoForge**: 21.1.206 or newer
- **Client-Server**: Full multiplayer support
- **Performance**: Optimized with minimal overhead
- **Compatibility**: Works with most other mods

## 🎯 For Players

This update completely transforms the survival experience:
- **Death has consequences** but redemption is always possible
- **Golden apples become precious** redemption tools
- **Achievements provide clear goals** and progression tracking
- **Visual feedback keeps you informed** of your status
- **Hardcore mode adds genuine challenge** without permanent world loss

## 🛠 For Server Administrators

- **Per-player state tracking** - each player has their own curse status
- **No world corruption risk** - all changes are safely reversible
- **Configurable respawn distance** through in-game menu
- **Clean mod architecture** with proper event handling
- **Comprehensive logging** for debugging if needed

## 📋 Upgrade Notes

- **Existing worlds compatible** - no world regeneration required
- **Configuration preserved** - respawn distance settings maintained  
- **Achievement progress starts fresh** - new achievement system begins with v1.1.0
- **No data loss** - all existing functionality preserved and enhanced

## 🚀 Get v1.1.0 Now

Download from your preferred platform:
- **CurseForge**: [Random Respawn On Death](https://www.curseforge.com/minecraft/mc-mods/random-respawn-on-death-neoforge-forge-quilt)
- **Modrinth**: [Random Respawn On Death](https://modrinth.com/mod/random-respawn-on-death)
- **GitHub**: [Latest Release](https://github.com/TheeMrCheeky/random-respawn-on-death/releases)

## 🎉 Thank You

This massive update represents a complete evolution of the Random Respawn On Death experience. The combination of temporary hardcore mode, achievement systems, and immersive visual changes creates a unique survival challenge that maintains the ability to recover and continue your world.

**Experience death like never before - where every death matters, but redemption is always within reach! 🎯**
