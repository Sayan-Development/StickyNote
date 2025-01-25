> [!WARNING]  
> **StickyNote is not as documented as it should be. Use it only if you're willing to dive into the source code yourself.**
>
> Example of a plugin made using StickyNote: https://github.com/Syrent/SayanVanish

# What is StickyNote?

StickyNote is a toolkit and framework for Kotlin that simplifies creating Minecraft plugins. It provides a collection of libraries, dynamically loads them at runtime, and offers a variety of utilities to help you focus on what you want to achieve rather than writing boilerplate code.

# Why is it a Framework?

StickyNote is more than just a collection of libraries. It groups and customizes them to work seamlessly together without requiring additional setup. It changes how you write code and manage dependencies, offering a Gradle plugin to handle much of the heavy lifting for you.

# What Does the StickyNote Gradle Plugin Do?

1. **Module Management**  
   The plugin lets you select the modules you need for your project. For example, if you're developing a Bukkit plugin, you can add the required modules to your Gradle build file like this:

   ```kotlin
   stickynote {
       modules(StickyNoteModules.BUKKIT)
   }
   ```

   With this, you'll have everything most plugins need, such as Adventure for message handling, Cloud for command management, and more.  

   Want to include NMS (net.minecraft.server) utilities in your plugin? Simply add the `BUKKIT_NMS` module:

   ```kotlin
   stickynote {
       modules(StickyNoteModules.BUKKIT, StickyNoteModules.BUKKIT_NMS)
   }
   ```

   Now you'll have access to utilities built with Takenaka, enabling features like packet NPCs, player info packets, and more.

2. **Dependency Management**  
   The plugin automatically adds all the required libraries to your dependencies, allowing you to access their Javadocs and source code within your IDE without issues.  
   Relocations are handled at compile time (via ShadowJar), ensuring the methods you use in your IDE remain non-relocated.

3. **Special `stickyload` Extension**  
   StickyNote includes a `stickyload` extension for runtime dependencies. You can use `stickyload.implementation()` inside the `dependencies` block of your Gradle build script to effortlessly add runtime dependencies.

   ```kotlin
   dependencies {
       stickyload.implementation("com.example:library:1.0.0")
   }
   ```
