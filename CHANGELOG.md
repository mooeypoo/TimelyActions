# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [v1.0.0]
### Added
- Added ability to control the number of results shown for log commands

### Changed
- Upgrade to Java 17 to support minecraft 1.18
- Upgrade spigot-api 1.18-R0.1-SNAPSHOT
- Written proper README

## [v0.9.0]
### Added
- Commands: reload, start, stop, player and playerinterval
- Changed to H2 storage to remove unnecessary 7mb size from jar
- Add dedicated SQLite action log
- Added SQLite storage for player->interval state
- First working prototype

[Unreleased]: https://github.com/mooeypoo/TimelyActions/compare/v1.0.0...HEAD
[v1.0.0]: https://github.com/mooeypoo/TimelyActions/compare/v1.0.0...v0.9.0
[v0.9.0]: https://github.com/mooeypoo/TimelyActions/releases/tag/v0.9.0