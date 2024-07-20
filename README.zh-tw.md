# BannerMaker

[![GitHub Actions](https://github.com/jyhsu2000/BannerMaker/actions/workflows/maven.yml/badge.svg)](https://github.com/jyhsu2000/BannerMaker/actions/workflows/maven.yml)
[![license](https://img.shields.io/github/license/jyhsu2000/BannerMaker.svg)](https://github.com/jyhsu2000/BannerMaker/blob/master/LICENSE)
[![Crowdin](https://badges.crowdin.net/bannermaker/localized.svg)](https://crowdin.com/project/bannermaker)

[![bStats Servers](https://img.shields.io/bstats/servers/383?label=bStats%20servers)](https://bstats.org/plugin/bukkit/BannerMaker)
[![bStats Players](https://img.shields.io/bstats/players/383?label=bStats%20players)](https://bstats.org/plugin/bukkit/BannerMaker)
[![Spiget Downloads](https://img.shields.io/spiget/downloads/4380?label=Spiget%20downloads)](https://www.spigotmc.org/resources/bannermaker.4380/)
[![CurseForge Downloads](https://img.shields.io/curseforge/dt/89852?label=CurseForge%20downloads)](https://dev.bukkit.org/projects/bannermaker)

文件連結：
[![changelog](https://img.shields.io/badge/changelog-zh--tw-green)](CHANGELOG.zh-tw.md)
[![contributing](https://img.shields.io/badge/contributing-guide-green)](CONTRIBUTING.md)
[![contributors](https://img.shields.io/badge/contributors-5d5d5d)](CONTRIBUTORS.md)

外部連結：
[[BukkitDev]](https://dev.bukkit.org/projects/bannermaker)
[[Spigot Resource]](http://www.spigotmc.org/resources/bannermaker.4380/)
[[巴哈小屋]](http://home.gamer.com.tw/creationDetail.php?sn=2760067)

*Read this in other languages: [English](README.md), [正體中文](README.zh-tw.md).*

覺得旗幟很有趣，但又不知道該如何合成？  
合成公式太複雜，以致於常常出錯？  
你一定要試試這個這個插件

## 描述

只要一個指令，你將能透過這插件的介面，設計無數種旗幟。  
不需要瞭解旗幟合成方式，只要知道你希望旗幟長怎樣。

## 功能

- 設計並儲存旗幟
- 查詢旗幟合成表
- 多國語言（在 `config.yml` 設定）
    - 英文（`en`）
    - 正體中文（`zh_TW`）
    - 簡體中文（`zh_CN`）
    - 德文（`de`）
    - 法文（`fr`）
    - 俄文（`ru`）
    - 葡萄牙文（`pt_BR`）
    - 荷蘭文（`nl`）
    - 西班牙文（`es`）
    - 匈牙利文（`hu`）
    - 波蘭文（`pl`）
    - 義大利文（`it`）
- 支援經濟功能（需安裝 Vault）
- 材料估算
- 使用材料合成旗幟
- 向其他人展示／分享你的旗幟

## 使用方法

1. 使用指令 `/bm` 來開啟界面
2. 享受其中吧

## 指令

| **指令**         | **描述**       | **權限**               |
|----------------|--------------|----------------------|
| `/bm`          | 開啟主要介面       | `BannerMaker.use`    |
| `/bm help`     | 指令清單         |                      |
| `/bm hand`     | 檢視手上持有的旗幟的資訊 | `BannerMaker.hand`   |
| `/bm see`      | 檢視看著的旗幟的資訊   | `BannerMaker.see`    |
| `/bm view ...` | 檢視指令附帶的旗幟的資訊 | `BannerMaker.view`   |
| `/bm reload`   | 重新載入設定檔      | `BannerMaker.reload` |

## 主要權限集合

| **權限**               | **描述**    | **預設值** |
|----------------------|-----------|---------|
| `BannerMaker.player` | 一般玩家的權限   | True    |
| `BannerMaker.admin`  | 完整權限      | OP      |
| `BannerMaker.show`   | 向其他玩家展示旗幟 | OP      |

## 其他權限

| **Permission**                        | **Description**            | **Default** |
|---------------------------------------|----------------------------|-------------|
| `BannerMaker.getBanner`               | 從介面取得旗幟                    | OP          |
| `BannerMaker.getBanner.complex-craft` | 取得旗幟時，忽略 6 種樣式的限制（於設定檔中啟用） | OP          |
| `BannerMaker.getBanner.free`          | 免費取得旗幟                     | OP          |

*您可以在 [plugin.yml](src/main/resources/plugin.yml) 中找到所有細部權限*

## 圖片

![Main menu](http://i.imgur.com/rMTTfsE.png)  
![Create banner](http://i.imgur.com/HB6Dhm3.png)  
![Banner info](http://i.imgur.com/Xydmcbj.png)  
![Alphabet & Number](http://i.imgur.com/tGHmakp.png)

[![bStats](https://bstats.org/signatures/bukkit/BannerMaker.svg)](https://bstats.org/plugin/bukkit/BannerMaker)
