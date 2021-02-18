# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Added ability to control the number of results shown for log commands

### Changed
- Written proper README
- Upgrade spigot-api 1.16.5-R0.1-SNAPSHOT

## [v0.9.0]
### Added
- Commands: reload, start, stop, player and playerinterval
- Changed to H2 storage to remove unnecessary 7mb size from jar
- Add dedicated SQLite action log
- Added SQLite storage for player->interval state
- First working prototype

[Unreleased]: https://github.com/mooeypoo/TimelyActions/compare/v0.9.0...HEAD
[v0.9.0]: https://github.com/mooeypoo/TimelyActions/releases/tag/v0.9.0