# Interfaces

A declarative inventory GUI builder API for [Paper](https://papermc.io/) plugins.
Define inventory layouts using character-based patterns _similar to shaped crafting recipes_
and bind items, renderers, and click actions to each character.

> [!Note]
> **Early Stage Project:** The API may change as things evolve.
> Feedback, bug reports, and contributions are very welcome!

## Features

- **Pattern-based layouts** – design your inventory visually with characters
- **Dynamic rendering** – render items based on slot, row, column, or index
- **Click actions** – handle clicks with conditions like permissions or click types
- **Pagination** – built-in support for paginated interfaces
- **JSON definitions** – define GUIs in JSON files using the `InterfaceReader`
- **Arithmetic expressions** – use expressions like `row*2` or `index+1` for dynamic item amounts
- **Event hooks** – `onOpen` and `onClose` callbacks with session and reason context

## Installation

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven("https://repo.thenextlvl.net/releases")
}

dependencies {
    implementation("net.thenextlvl:interfaces:VERSION")
}
```

You can find the latest version on the [releases page](https://repo.thenextlvl.net/#/releases/net/thenextlvl/interfaces/).

## Usage

### Builder API

Build interfaces programmatically with the fluent `Interface.Builder`:

```java
var gui = Interface.builder()
        .title(Component.text("My Shop"))
        .rows(3)
        .layout(Layout.builder()
                .pattern("# # # # #",
                         "  a b c  ",
                         "# # x # #")
                .mask('#', ItemStack.of(Material.GRAY_STAINED_GLASS_PANE))
                .mask('a', ctx -> ItemStack.of(Material.DIAMOND, ctx.index() + 1))
                .mask('b', ctx -> ItemStack.of(Material.EMERALD, ctx.row()))
                .mask('c', ctx -> ItemStack.of(Material.GOLD_INGOT, ctx.column()))
                .build())
        .slot('x', ItemStack.of(Material.BARRIER), ClickAction.of(player -> {
            player.sendMessage(Component.text("Closing!"));
            player.closeInventory();
        }))
        .onOpen(session -> session.player().sendMessage(Component.text("Welcome!")))
        .onClose((session, reason) -> session.player().sendMessage(Component.text("Goodbye!")))
        .build();

gui.open(player);
```

### JSON Definitions with InterfaceReader

Define your GUIs in JSON files and load them at runtime:

```java
var gui = InterfaceReader.reader()
        .readResource("my-gui.json")
        .build();

gui.open(player);
```

```json
{
  "title": "My GUI",
  "pattern": [
    "# # # # #",
    "  a b c  ",
    "# # # # #"
  ],
  "#": {
    "item": "minecraft:gray_stained_glass_pane",
    "hide_tooltip": true
  },
  "a": {
    "item": "minecraft:diamond",
    "amount": "row*2",
    "click_actions": [
      {
        "permission": "shop.diamond",
        "run_console_command": "give <player> diamond"
      },
      {
        "no_permission": "shop.diamond",
        "send_message": "<red>You don't have permission!</red>"
      }
    ]
  },
  "b": {
    "item": "minecraft:emerald",
    "name": "<green>Emerald</green>",
    "click_actions": [
      {
        "click_type": "left",
        "play_sound": { "sound": "minecraft:ui.button.click" }
      }
    ]
  },
  "c": {
    "item": "minecraft:gold_ingot",
    "amount": "index",
    "click_actions": [
      { "run_command": "say Hello from <player>!" }
    ]
  }
}
```

### Paginated Interfaces

```java
var gui = PaginatedInterface.<MyItem>builder(
        InterfaceReader.reader().readResource("paginated.json")
    )
    .content(myItemList)
    .transformer(item -> new ActionItem(
            ctx -> item.toItemStack(),
            ClickAction.of(player -> player.sendMessage(item.name()))
    ))
    .build();

gui.open(player);
```

## JSON Schema

A JSON schema is available at [`interface.schema.json`](https://raw.githubusercontent.com/TheNextLvl-net/interfaces/refs/heads/main/src/main/resources/interface.schema.json)
for editor autocompletion and validation of your GUI definitions.

## Contributing

This is an early project and contributions are highly appreciated!
If you find a bug, have a feature request, or want to improve something, feel free to
[open an issue](https://github.com/TheNextLvl-net/interfaces/issues) or submit a pull request.
