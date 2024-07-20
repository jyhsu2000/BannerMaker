# BannerMaker

[![GitHub Actions](https://github.com/jyhsu2000/BannerMaker/actions/workflows/maven.yml/badge.svg)](https://github.com/jyhsu2000/BannerMaker/actions/workflows/maven.yml)
[![license](https://img.shields.io/github/license/jyhsu2000/BannerMaker.svg)](https://github.com/jyhsu2000/BannerMaker/blob/master/LICENSE)
[![Crowdin](https://badges.crowdin.net/bannermaker/localized.svg)](https://crowdin.com/project/bannermaker)

[![bStats Servers](https://img.shields.io/bstats/servers/383?label=bStats%20servers)](https://bstats.org/plugin/bukkit/BannerMaker)
[![bStats Players](https://img.shields.io/bstats/players/383?label=bStats%20players)](https://bstats.org/plugin/bukkit/BannerMaker)
[![Spiget Downloads](https://img.shields.io/spiget/downloads/4380?label=Spiget%20downloads)](https://www.spigotmc.org/resources/bannermaker.4380/)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/89852?label=CurseForge%20downloads)](https://dev.bukkit.org/projects/bannermaker)

Docs Links:
[![changelog](https://img.shields.io/badge/changelog-en-green)](CHANGELOG.md)
[![contributing](https://img.shields.io/badge/contributing-guide-green)](CONTRIBUTING.md)
[![contributors](https://img.shields.io/badge/contributors-5d5d5d)](CONTRIBUTORS.md)

External Links:
[[BukkitDev]](https://dev.bukkit.org/projects/bannermaker)
[[Spigot Resource]](http://www.spigotmc.org/resources/bannermaker.4380/)
[[巴哈小屋]](http://home.gamer.com.tw/creationDetail.php?sn=2760067)

*Read this in other languages: [English](README.md), [正體中文](README.zh-tw.md).*

Feel banner is fun, but you don't know how to craft?  
It's too hard to craft, so you make wrong usually?  
You must try this plugin.

## Description

Using just one command, you can use GUI of this plugin to design any kind of banner.  
You don't need to know how to craft. All you need to know is how it will look like.

## Features

- Design and save banners
- Look up recipe of banners
- Multi-language support (Setting in `config.yml`)
    - English (`en`)
    - Traditional Chinese (`zh_TW`)
    - Simplified Chinese (`zh_CN`)
    - German (`de`)
    - French (`fr`)
    - Russian (`ru`)
    - Portuguese (`pt_BR`)
    - Dutch (`nl`)
    - Spanish (`es`)
    - Hungarian (`hu`)
    - Polish (`pl`)
    - Italian (`it`)
- Economic support (require Vault installed)
- Material estimates
- Craft banner by using materials
- Show/share your banners to others

## How to use

1. Run command `/bm` to open GUI
2. Enjoy it

## Commands

| **Command**    | **Description**                                  | **Permission**       |
|----------------|--------------------------------------------------|----------------------|
| `/bm`          | Open GUI                                         | `BannerMaker.use`    |
| `/bm help`     | Command list                                     |                      |
| `/bm hand`     | View banner info of the banner in hand           | `BannerMaker.hand`   |
| `/bm see`      | View banner info of the banner you're looking at | `BannerMaker.see`    |
| `/bm view ...` | View banner info of the banner command           | `BannerMaker.view`   |
| `/bm reload`   | Reload config                                    | `BannerMaker.reload` |

## Major Permission Sets

| **Permission**       | **Description**                | **Default** |
|----------------------|--------------------------------|-------------|
| `BannerMaker.player` | Permissions for normal players | True        |
| `BannerMaker.admin`  | Whole permission               | OP          |
| `BannerMaker.show`   | Show banner info to players    | OP          |

## Other Permissions

| **Permission**                        | **Description**                                                 | **Default** |
|---------------------------------------|-----------------------------------------------------------------|-------------|
| `BannerMaker.getBanner`               | Get banners from GUI                                            | OP          |
| `BannerMaker.getBanner.complex-craft` | Bypass 6-patterns limit when getting banner (Enabled in config) | OP          |
| `BannerMaker.getBanner.free`          | Get banners for free                                            | OP          |

*All detailed permissions can be found in [plugin.yml](src/main/resources/plugin.yml)*

## Pictures

![Main menu](http://i.imgur.com/rMTTfsE.png)  
![Create banner](http://i.imgur.com/HB6Dhm3.png)  
![Banner info](http://i.imgur.com/Xydmcbj.png)  
![Alphabet & Number](http://i.imgur.com/tGHmakp.png)

[![bStats](https://bstats.org/signatures/bukkit/BannerMaker.svg)](https://bstats.org/plugin/bukkit/BannerMaker)
