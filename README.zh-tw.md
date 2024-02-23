# BannerMaker

[![GitHub Actions](https://github.com/jyhsu2000/BannerMaker/actions/workflows/maven.yml/badge.svg)](https://github.com/jyhsu2000/BannerMaker/actions/workflows/maven.yml)
[![license](https://img.shields.io/github/license/jyhsu2000/BannerMaker.svg)](https://github.com/jyhsu2000/BannerMaker/blob/master/LICENSE)
[![Crowdin](https://badges.crowdin.net/bannermaker/localized.svg)](https://crowdin.com/project/bannermaker)
[![bStats Servers](https://img.shields.io/bstats/servers/383?label=bStats%20servers)](https://bstats.org/plugin/bukkit/BannerMaker)
[![bStats Players](https://img.shields.io/bstats/players/383?label=bStats%20players)](https://bstats.org/plugin/bukkit/BannerMaker)

## Link

* [BukkitDev](https://dev.bukkit.org/projects/bannermaker)
* [Spigot Resource](http://www.spigotmc.org/resources/bannermaker.4380/)
* [巴哈小屋](http://home.gamer.com.tw/creationDetail.php?sn=2760067)
* [MCBBS](http://www.mcbbs.net/thread-415289-1-1.html)

覺得旗幟很有趣，但又不知道該如何合成？  
合成公式太複雜，以致於常常出錯？  
你一定要試試這個這個插件

*Read this in other languages: [English](README.md), [正體中文](README.zh-tw.md).*

## 描述

只要一個指令，你將能透過這插件的介面，設計無數種旗幟。  
不需要瞭解旗幟合成方式，只要知道你希望旗幟長怎樣。

## 功能

* 設計並儲存旗幟
* 查詢旗幟合成表
* 支援UUID
* 多國語言（在config.yml設定）
    * 英文(en)
    * 正體中文(zh_TW)
    * 簡體中文(zh_CN)
    * 德文(de)
    * 法文(fr)
    * 俄文(ru)
    * 葡萄牙文(pt_BR)
    * 荷蘭文(nl)
    * 西班牙文(es)
    * 匈牙利文(hu)
    * 波蘭文(pl)
    * 義大利文(it)
* 支援經濟功能（需要Vault）
* 材料估算
* 使用材料合成旗幟

## 使用方法

* 請見最下方圖片

## 指令

| **指令**     | **描述**       | **權限**             |
|------------|--------------|--------------------|
| /bm        | 開啟主要介面       | BannerMaker.use    |
| /bm help   | 指令清單         |                    |
| /bm hand   | 顯示手上持有的旗幟的資訊 | BannerMaker.hand   |
| /bm see    | 顯示看著的旗幟的資訊   | BannerMaker.see    |
| /bm reload | 重新載入設定檔      | BannerMaker.reload |

## 其他權限

| **權限**                              | **描述**                     |
|-------------------------------------|----------------------------|
| BannerMaker.*                       | 完整權限                       |
| BannerMaker.getBanner               | 從介面取得旗幟                    |
| BannerMaker.getBanner.complex-craft | 取得旗幟時，忽略 6 種樣式的限制（於設定檔中啟用） |
| BannerMaker.getBanner.free          | 免費取得旗幟                     |

## 安裝

1. 關閉伺服器
2. 將 .jar 放入 plugins 資料夾
3. 啟動伺服器

## 更新紀錄

*請參閱 [CHANGELOG.zh-tw.md](CHANGELOG.zh-tw.md)*

## 圖片

![Main menu](http://i.imgur.com/rMTTfsE.png)  
![Create banner](http://i.imgur.com/HB6Dhm3.png)  
![Banner info](http://i.imgur.com/Xydmcbj.png)  
![Alphabet & Number](http://i.imgur.com/tGHmakp.png)

## 貢獻

歡迎提交 Pull Request。 對於較大的改動，請先建立 Issue 來讓我們討論一下你想做些什麼。
對於語言包，你也可以使用 [Crowdin](https://crowdin.com/project/bannermaker)
來更新或請求新語言。

### 貢獻者

翻譯者：
[Marrarus](https://github.com/Marrarus) （德文）、
[RedNesto](https://github.com/RedNesto) （法文）、
[sdir01](https://www.spigotmc.org/members/sdir01.238854/) （俄文）、
[Rhander](https://www.spigotmc.org/members/rhander.103119/) （葡萄牙文）、
[DeTrollers](https://www.spigotmc.org/members/detrollers.174265/) （荷蘭文）、
[stevejone1997](https://www.spigotmc.org/members/stevejone1997.432373/) （西班牙文）、
[C4BR3R4](https://www.spigotmc.org/members/c4br3r4.26779/) （西班牙文）、
[montlikadani](https://www.spigotmc.org/members/toldi.251100/) （匈牙利文）、
[ziemniok99](https://www.spigotmc.org/members/ziemniok99.596334/) （波蘭文）、
[Leomixer17](https://www.spigotmc.org/members/leomixer17.140367/) （義大利文）
