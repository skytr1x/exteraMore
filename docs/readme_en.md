# exteraMore - A plugin that makes using plugins in exteraGram much more enjoyable

exteraGram - A Telegram client that stands out significantly among others due to its unique plugin system, but it has a major issue - security.

The best example of fixing this is [exteraless](https://github.com/exteraless/exteraless), which uses a permission system. exteraMore brings a similar system to exteraGram, but with its own advantages and features.

## How is it better than other exteraless-like plugins?

In fact, it differs only in additional functionality:

- Ability to read the code of plugins made on the Elyx SDK. The plugin creates its own mini-IDE for reading code.
- Native work with install_sheet inside.
- The plugin works much faster due to working with the DEX component.
- Additional optimization of the plugin system.

And all of this comes in just a single `.plugin` file.

## How does reading Elyx plugins work? Aren't they archives?

The plugin reads the contents right in memory before installation. The same applies to decompiling `.pyc` files — they will be decompiled only when opened, nothing more.

Unarchiving anywhere during plugin initialization would take a lot of time. And when the plugin is already checked at an average speed before installation, this would become a bit critical.

## What plugin optimization is there?

exteraMore has a plugin optimization system, specifically:

- Pre-caching of modules.
- Lazy loading of modules.
- Lazy import.

In the future, I see greater potential for optimization, but as of v3.0, the optimization functionality will be limited to this.

## Why not just download exteraless?

The answer is very simple - compatibility.

exteraless has very unstable compatibility with plugins, but in return, it offers a really good security system, which initially attracted me. With exteraMore, it's not like you'll have to compromise between security and functionality.

Also, exteraless lacks one cool feature — reading the code of Elyx plugins (which, I hope, will appear in the very near future).

# Support me

I accept more than just financial support. You can also subscribe to my [Telegram Channel](https://t.me/sktrxdev), but besides that, you can support me financially via [Donation Alerts](https://dalink.to/skytr1x), or via [TON](https://app.tonkeeper.com/transfer/UQAgdyeONxgrlH52aWYsLKiQEZiMhQFgMXDG48Q9SLSppDR_?text=Поддержка+проекта).
