# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.2.2] - 2025-09-04

### Changed

- Modpack version check is now less strict: extras will show a warning if used standalone or with mismatched versions,
  but will still load instead of failing.
- Refactored Goreye entity logic; it now correctly despawns during daylight.

### Removed

- Custom recipes are now included in the main modpack to keep extras independent.
- Lead recipe (backport from Minecraft 1.21.6) is now managed by the main modpack.
- Custom menu music audio files have been moved to the main modpack for a more standalone extras experience.

### Fixed

- Resolved a startup crash on Fabric caused by sounds being registered after items.

### Notes

- Goreye is a work-in-progress entity. It will not spawn naturally, but can be summoned manually using its spawn egg or
  the `/summon` command.

## [0.2.1] - 2025-08-16

### Added

- Initial public release.

[0.2.2]: https://github.com/TotallyNotCursed/extras/compare/0.2.1...0.2.2

[0.2.1]: https://github.com/TotallyNotCursed/extras/releases/tag/0.2.1
