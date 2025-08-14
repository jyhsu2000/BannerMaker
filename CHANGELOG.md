# Change Logs

## Unreleased Version

- None

## v2.5.1 (for v1.21.x)

- Optimize code
- Add Ukrainian(uk) translation
- Improve precise block targeting of `/bm see` command

## v2.5.0 (for v1.21.x)

- Update Spigot API to 1.21
- Copy share command to clipboard directly instead of showing it in chat input box

## v2.4.0 (for v1.20.x)

- Add permission node for bypassing 6-patterns limit when getting banners (Enabled in config)
- Update Spigot API to 1.20.1
- Update Java compile target version to 17
- Stop providing technical support for legacy Spigot (prior to Spigot 1.20)
- Show `/bm help` info when providing invalid arguments to `/bm`
- [Experimental] Add banner showing feature to banner info page
- [Experimental] Add `/bm view` command for banner sharing (via text, in social media other than Minecraft)
    - Example: `/bm view rO0ABXQADjE0O2NyOjE7Y3JlOjEx`

*Note: Some messages of **Experimental features** cannot be translated yet.*

## v2.3.2 (for 1.17.x)

- Update to 1.17.1
- Support loom recipes (for non-craftable patterns)

## v2.3.1 (for 1.16.x)

- Fix delete button missing bug in 1.16.x

## v2.3.0 (for 1.16.x)

- Update to 1.16.1

## v2.2.2 (for 1.14.x)

- Add Italian(it) translation (Thanks [Leomixer17](https://www.spigotmc.org/members/leomixer17.140367/))

## v2.2.1 (for 1.14.x)

- Add Polish(pl) translation (Thanks [ziemniok99](https://www.spigotmc.org/members/ziemniok99.596334/))

## v2.2.0 (for 1.14.x)

- Update to 1.14.4
- Fix dye materials in recipes
- Update language mechanism
    - Auto detect environment language if set language to "auto"
    - Change filename syntax of language files from "zh-tw" to "zh_TW"

## v2.1.1 (for 1.13.x)

- Fix bug of missing dye materials when crafting banners

## v2.1.0 (for 1.13.x)

- Add simple preview mode (Toggleable in "Create Banner" menu)
    - https://imgur.com/a/ydF8frS

## v2.0.1 (for 1.13.x)

- Fix sorting of materials
- Cleanup code

## v2.0.0 (for 1.13.x)

- Update to 1.13.2
- No longer support 1.8.x ~ 1.12.x

## v1.9.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)

- Optimize code
- Now only support Java 8
- Update Spanish(es) translation (Thanks [C4BR3R4](https://www.spigotmc.org/members/c4br3r4.26779/))

## v1.8.1 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)

- Optimize code
- Finish new flexible command system

## v1.8.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)

- Now player can craft a banner by using materials
- If player has permission `BannerMaker.getBanner.free`, he cna only see `Get banner for free` button, buy and craft
  button will not shown
- Update inventory menu system (now most button only accept left click)

## v1.7.2 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)

- Add Hungarian(hu) translation (Thanks [montlikadani](https://www.spigotmc.org/members/toldi.251100/))

## v1.7.1 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)

- Add Spanish(es) translation (Thanks [stevejone1997](https://www.spigotmc.org/members/stevejone1997.432373/))

## v1.7.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)

- Update to 1.12
- Fix bug that need to reload twice to apply new language

## v1.6.3 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)

- Add Dutch(nl) translation (Thanks [DeTrollers](https://www.spigotmc.org/members/detrollers.174265/))

## v1.6.2 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)

- Add Portuguese(pt-br) translation (Thanks [Rhander](https://www.spigotmc.org/members/rhander.103119/))

## v1.6.1 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)

- Identify gui button in a more exact way
- Optimize inventory menu transition

## v1.6.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)

- New Commands
    - /bm help: command list
    - /bm hand: Show banner info of the banner in hand
    - /bm see: Show banner info of the banner you're looking at
- Rewrite command system and add tab completion.
- Remove banner data update functions which for update from v1.0 or v1.1

## v1.5.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)

- Make AlphabetAndNumberBanner toggleable
- Improve price setting
- Optimize code
- Split README and CHANGELOG
- Use [bStats](https://bstats.org/plugin/bukkit/BannerMaker) instead of MCStats

## v1.4.6 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)

- Update to 1.11
- Add Russian(ru) translation (Thanks [sdir01](https://www.spigotmc.org/members/sdir01.238854/))

## v1.4.5 (for 1.8.x, 1.9.x)

- Fix "Title cannot be longer than 32 characters" exception

## v1.4.4 (for 1.8.x, 1.9.x)

- Optimize code
- Prevent "Title cannot be longer than 32 characters" exception
- Add Plugin Metrics

## v1.4.3 (for 1.8.x, 1.9.x)

- Add French(fr) translation (Thanks [RedNesto](https://github.com/RedNesto))
- Update recipe of bordered alphabet D and R
- Update checking of language files
- Check if materials enough and show in banner info

## v1.4.2 (for 1.8.x, 1.9.x)

- Update recipe of bordered alphabet S
- Add German(de) Translation (Thanks [Marrarus](https://github.com/Marrarus))
- Optimize code

## v1.4.1 (for 1.8.x, 1.9.x)

- Add menu of Alphabet & Number
- Add craft material estimates
- Make GUI title prefix editable
- Fix wrong wool color of black banner
- Optimize performance

## v1.4 (for 1.8.x, 1.9.x)

- Update to 1.9.2
- Remove old preview mode (banner icons will never disappear)
- Make message prefix editable
- Add "Clone & Edit" button

## v1.3.2 (for 1.8.x)

- Remove unnecessary debug messages
- Drop banner on the ground if get banner when inventory is full
- Tried to fix IndexOutOfBoundsException
- Create a toggle button preview mode in create-banner-menu

## v1.3.1 (for 1.8)

- Fixed some bug

## v1.3 (for 1.8)

- Add Economic support (need Vault)

## v1.2 (for 1.8)

- Support UUID
- Multi language support  
  (Setting in config.yml. Now support English(en),Traditional Chinese(zh-tw), Simplified Chinese(zh-cn))

## v1.1 (for 1.8)

- Move banner data to folder "banner"
- More clear and smaller data format  
  (Move all &lt;player&gt;.yml to folder "banner" and it will be auto update to new data format)

## v1.0 (for 1.8)

- First Release
