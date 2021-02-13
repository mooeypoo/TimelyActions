![Java CI with Maven](https://github.com/mooeypoo/TimelyActions/workflows/Java%20CI%20with%20Maven/badge.svg) ![GitHub last commit](https://img.shields.io/github/last-commit/mooeypoo/TimelyActions) [![Maintainability](https://api.codeclimate.com/v1/badges/17f122d8ffac7ba050c6/maintainability)](https://codeclimate.com/github/mooeypoo/TimelyActions/maintainability) [![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](code_of_conduct.md) [![Donate to the project!](https://img.shields.io/badge/Buy%20me%20a%20coffee!-Donate-ff69b4?style=flat)](https://ko-fi.com/mooeypoo)

<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/github_username/repo_name">
    <img src="https://raw.githubusercontent.com/mooeypoo/TimelyActions/main/assets/TimelyActions-banner.png" alt="TimelyActions Logo">
  </a>

  <h3 align="center">TimelyActions</h3>

  <p align="center">
    A Minecraft plugin that enables admins to run groups of commands for players every set amount of minutes.
    <br />
    <br />
    <a href="https://github.com/mooeypoo/TimelyActions/issues">Report Bug</a>
    ·
    <a href="https://github.com/mooeypoo/TimelyActions/issues">Request Feature</a>
  </p>
</p>

<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-timelyactions">About TimelyActions</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li>
      <a href="#configuration">Configuration</a>
      <ul>
        <li><a href="#configuration-options">Configuration options</a></li>
        <li><a href="#example-configuration">Example configuration</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>

## About TimelyActions

TimelyActions is a Minecraft (Spigot) plugin that enables admins to run groups of commands for players every set minutes.

Users will be eligible for the commands once per defined time period, no matter how many times they rejoin or stay in the server. Optionally base interval actions on user permissions to give different gifts to your users based on their ranks or privileges on the server.

Some use case examples include:

* Give your players a gift every 24h
* Send your users reminder messages every 60 minutes

## Getting Started

To get this plugin working on your server, follow the normal plugin installation method, outlined below.

### Prerequisites

* Spigot server v1.16.3 and up
* Minumum Java version: 8

### Installation

* Download the `jar` file of the latest release.
* Place the `jar` file in your server's `/plugins` directory
* You have two options:
** If you want to create your own config, create the folder `/plugins/TimelyActions` and add `config.yml` file into it. Follow the instructions below on how to create your initial config file with your own interval.
** Restart your server. The plugin will work out of the box with the sample action in an automatically created `config.yml` file. You can then edit this file.

## Configuration

To change and add your own intervals, you should edit the configuration file `/plugins/TimelyActions/config.yml`

### Configuration options

The configuration file allows for several options:

#### `log_everything`
**Default: false**

Enabling this config option will output more information into the console. Use this if you want to verify your plugin operates properly, or if you run into issues, enable this to be able to help with the [bug fix issues](https://github.com/mooeypoo/TimelyActions/issues/new?template=bug_report.md&&labels=bugtitle=%5BBUG%5D+).

#### `intervals`

This is a list (map) of intervals with key names. The named keys will be used for outputs into the console, and in the commands checking whether players have had the interval run for them.

An example interval definition:

```
  iron_every_5:
    every_minutes: 5
    message_to_user: "Every 5 minutes, you get an &7&liron ingot&r, %player%!"
    permission: ''
    commands:
    - give %player% minecraft:iron_ingot
```

The name `iron_every_5` is defined by the admin when writing the definition, and can be any legal yaml key. General guidance is to use lowercase letters with underscores instead of spaces. If you use anything else, please make sure to validate your yaml file to make sure the key is valid. This name will appear in logs and when looking to check player's status within the interval.

**every_minute (default: 60)**

Defines the interval time, in minutes, where players will be eligible to have the actions defined run for them.

For example, if the value is 60, the system will run the given commands for players at minimum every 60 minutes from the last time the command ran for that player, no matter how many times the player has joined or disconnected.

**message_to_user**

Defines the message that is sent to the user when the interval runs for the user. This is a yaml string, and it respects minecraft chat color codes.

**permission**

Defines the permission needed for the player to have this interval apply, regardless of time. If defined, this interval will only apply to users who hold this permissions.

For example, this can be used to give different periodic gifts for your users based on their rank on the server.

**commands**

This defines a list of commands to be run on each relevant user when the interval applies to them. You can define whoever many commands you want, but all commands are run in the console, so they all must be console-commands (do not use in-game commands, and do not use `/`).

You can use placeholders in the command text:

- `%player%` will be replaced by the player's name. Example: `"You're getting an iron ingot every hour, %player%!"`

### Example configuration

An example configuration below results in all players being eligible to receive a gift of iron ingot every hour. The command will run for them if they are online, or whenever they join.

```
log_everything: false
intervals:
  iron_every_hour:
    every_minutes: 60
    message_to_user: "Every hour on the server, you get an &7&liron ingot&r, %player%!"
    permission: ''
    commands:
    - give %player% minecraft:iron_ingot
```

**Please note: Interval commands do not stack;** if a user misses several hours on the server, they will receive **one** iron ingot when they join back again, and then wait for the next another hour. They will not get the missing ingots they would have gotten if they were online for the hours they've missed. This allows admins to make sure the intervals are awarding players every x minutes, rather than stack to give multiple gifts for players who are not on the server.

## Usage

Once the plugin is running, admins have access to several in-game and console actions to manage the intervals and players.

The plugin's main command `/timelyactions [action]` where the actions are:

### `reload`
```
/timelyactions reload
```
Reloads configuration without the need to restart the server.

### `stop` and `start`

```
/timelyactions stop
/timelyactions start
```

Stops or starts the timed action interval. If the stop action is used, the system will completetly stop checking-for and running commands for intervals, even if players are eligible.

### `player`

```
/timelyactions player [player name]
```

Checks the status of a player with intervals that the system is aware of. The command will return the latest 5 intervals with their names and times where the user has recieved the commands specified.

This can help admins find specific command output in the logs, or make sure players have received certain gifts or actions and when.

### `playerinterval`
```
/timelyactions playerinterval [player name] [interval name]
```

Similarly to the `player` command this checks the status of a player with a specific interval that the system is aware of. The command will return the latest 5 results for the given interval and user with the times this has run.

This can help admins find specific command output in the logs, or make sure players have received certain gifts or actions and when.

## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

Distributed under the GPLv3 License. See `LICENSE` for more information.

## Contact
Written and developed by mooeypoo (c) 2020.

Submit a bug report or hit me up on twitter at [@mooeypoo](https://twitter.com/mooeypoo)

**Pull requests are welcome!** :heart_eyes:

## Acknowledgements

* [README heavily adapted from Best README template](https://github.com/othneildrew/Best-README-Template)
* [Plugin inspired by TimeIsMoney](https://github.com/mastercake10/TimeIsMoney/)
* [Config made possible by DazzleConf](https://github.com/A248/DazzleConf)
