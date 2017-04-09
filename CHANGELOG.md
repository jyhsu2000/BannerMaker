# Change Logs
## Unreleased Version
- Nothing

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
