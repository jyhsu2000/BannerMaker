# BannerMaker
[![Build Status](https://ci.kid7.club/job/BannerMaker/badge/icon)](https://ci.kid7.club/job/BannerMaker/)
[![bStats](https://img.shields.io/badge/bStats-1.1-brightgreen.svg)](https://bstats.org/plugin/bukkit/BannerMaker)
## Link
* [BukkitDev](https://dev.bukkit.org/projects/bannermaker)
* [Spigot Resource](http://www.spigotmc.org/resources/bannermaker.4380/)
* [巴哈小屋](http://home.gamer.com.tw/creationDetail.php?sn=2760067)
* [MCBBS](http://www.mcbbs.net/thread-415289-1-1.html)

Feel banner is fun but you don't know how to craft?  
It's too hard to craft so you make wrong usually?  
You must try this plugin.

*Read this in other languages: [English](README.md), [正體中文](README.zh-tw.md).*

## Description
Using just one command, you can use GUI of this plugin to design any kind of banner.  
You don't need to know how to craft. All you need to know is how it will looks like.

## Features
* Design and save banner
* Look up recipe of banner
* Support UUID
* Multi language support (Setting in config.yml)  
  * English(en)
  * Traditional Chinese(zh-tw)
  * Simplified Chinese(zh-cn)
  * German(de) (Thanks [Marrarus](https://github.com/Marrarus))
  * French(fr) (Thanks [RedNesto](https://github.com/RedNesto))
  * Russian(ru) (Thanks [sdir01](https://www.spigotmc.org/members/sdir01.238854/))
* Economic support (need Vault)
* Material estimates

## How to use
* Look at pictures at bottom of page

## Commands
|**Command**|**Description**|**Permission**|
|---|---|---|
|/bm|Open main gui|BannerMaker.use|
|/bm help|Command list||
|/bm hand|Show banner info of the banner in hand|BannerMaker.hand|
|/bm see|Show banner info of the banner you're looking at|BannerMaker.see|
|/bm reload|Reload config|BannerMaker.reload|

## Other Permissions
|**Permission**|**Description**|
|---|---|
|BannerMaker.*|Whole permission|
|BannerMaker.getBanner|Get banners from GUI|
|BannerMaker.getBanner.free|Get banners for free|

## Installation
1. Shutdown the server
2. Put the .jar into the plugins folder
3. Start the server

## Change Logs
v1.6.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)
- New Command
  - /bm help: command list
  - /bm hand: Show banner info of the banner in hand
  - /bm see: Show banner info of the banner you're looking at
- Rewrite command system and add tab completion.
- Remove banner data update functions which for update from v1.0 or v1.1

*Read more in [CHANGELOG.md](CHANGELOG.md)*

## Pictures
![Main menu](http://i.imgur.com/rMTTfsE.png)  
![Create banner](http://i.imgur.com/HB6Dhm3.png)  
![Banner info](http://i.imgur.com/Xydmcbj.png)  
![Alphabet & Number](http://i.imgur.com/tGHmakp.png)
