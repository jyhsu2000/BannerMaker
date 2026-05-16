# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 專案概觀

BannerMaker 是一個 Spigot/Paper Minecraft 插件，讓玩家透過 GUI 設計與管理旗幟。本專案已完成現代化遷移（Adventure、ACF、InventoryFramework、XSeries），目標是維護一個高效能、模組化且易於擴展的程式碼庫。

## 技術堆疊

- **語言**：Java 21
- **平台**：Spigot / Paper 1.21.0+
- **建置**：Maven（含 `minimizeJar` 與 shade 重定位）
- **關鍵函式庫**：
    - **Adventure**：`adventure-text-minimessage 4.17.0` + `adventure-platform-bukkit 4.4.1`，文字與訊息處理核心
    - **ACF (Paper)**：`0.5.1-SNAPSHOT`，指令管理系統
    - **InventoryFramework**：`0.10.14`，GUI 介面框架
    - **XSeries**：`11.3.0`，跨小版本材質與聲音相容
    - **MockBukkit**：`4.41.1`（針對 1.21.x，測試端搭配 paper-api 1.21.4），單元測試框架

## 建置、測試與執行

```powershell
# 跑全部測試（MockBukkit 套件，約 10 秒）
& "C:\Users\jyhsu\AppData\Local\Programs\IntelliJ IDEA Ultimate\plugins\maven\lib\maven3\bin\mvn.cmd" test

# 產出 shaded jar（輸出至 target/BannerMaker.jar）
& "C:\Users\jyhsu\AppData\Local\Programs\IntelliJ IDEA Ultimate\plugins\maven\lib\maven3\bin\mvn.cmd" package

# 跑單一測試類別
& "..\mvn.cmd" test -Dtest=LanguageTest
```

本機透過 IDEA 內建 Maven 執行，因系統 `PATH` 上沒有獨立的 `mvn`；CI 在 `ubuntu-latest` 上直接使用 `mvn`。

## 架構規範

### 設定與資料

- **必須**使用 `club.kid7.bannermaker.configuration.ConfigManager` 進行所有 YAML 檔案存取。
- **禁止**直接實例化 `YamlConfiguration` 或使用 Bukkit API 的預設 config 方法（除非在 `ConfigManager` 內部）。
- **單元測試**：`tearDown` **必須**呼叫 `ConfigManager.reset()` 以清除靜態狀態、防止測試間污染。
- `Language.java` 中翻譯鍵值若在執行時缺漏，**不會**再觸發同步磁碟寫入（避免效能瓶頸）。

### 訊息處理

- **必須**使用 `club.kid7.bannermaker.service.MessageService` 發送訊息。
- **禁止**使用 `player.sendMessage()` 或 `Bukkit.broadcastMessage()`。
- `Language.tl()` 返回 Adventure `Component`。
- **顏色代碼機制**：
    - `Language.tl()` 智能支援 **MiniMessage**（如 `<red>`、`<gradient>`）與 **Legacy**（如 `&c`）格式。
    - 字串包含 MiniMessage 標籤（`<` 與 `>`）時優先使用 MiniMessage 解析，否則回退至 Legacy。
    - **格式偏好**：新增的翻譯字串建議使用 MiniMessage 標籤（`<red>`、`<gradient:red:blue>`、`<bold>` 等）—— 語意更明確、支援漸層 / hex / 互動事件等進階特性。Legacy `&` 色碼**仍持續支援**，既有譯文與舊範本不需重寫，新貢獻者亦可使用熟悉的格式。不設退場時程。
- **參數替換**：
    - **新方式（推薦）**：`tl("key", TagUtil.tag("arg", value))` 使用 `<arg>` 佔位符；搭配 `club.kid7.bannermaker.util.TagUtil` 進行安全轉義。
    - **舊方式**：`tl("key", arg1)` 使用 `{0}` 佔位符（已基本完全遷移，新程式碼不應再使用）。
- 推薦使用 `Language.tl(NamedTextColor color, String path, Object... args)` 重載方法以簡化帶顏色的翻譯 Component 建立。

### ACF 整合

- **自動 Help 系統**：使用 ACF 內建的 `@HelpCommand` 自動生成 `/bm help`。
- **權限過濾**：Help 訊息會依玩家權限自動過濾顯示。
- **語言同步**：ACF 系統訊息（如「未知指令」）依 `config.yml` 的 `Language` 設定自動切換（`zh_TW`、`en`、`auto`）。
- **指令描述多語系**：`language/*.yml` 中的 `command.description.*` 已透過 `Language.registerCommandDescriptions()` 注入 ACF Locales。

### GUI 開發

- **必須**使用 `InventoryFramework` 實作所有選單。
- GUI 類別位於 `club.kid7.bannermaker.gui` 套件下，已取代舊有的 `customMenu` 系統。
- GUI 標題若必須為 `String`，請使用 `LegacyComponentSerializer.legacySection().serialize(component)` 轉換。
- GUI 標題透過 `gui.title.*` 鍵值統一管理，所有語系檔已對齊。

### 物品建構

規則拆分為「跨版本相容」與「複雜建構」兩個獨立關注點：

- **跨版本相容**：對於**可能在最低支援版本（1.21.0）不存在的材料**（典型：1.21.2+ 才加入的 `FIELD_MASONED_BANNER_PATTERN`、`BORDURE_INDENTED_BANNER_PATTERN`），**必須**使用 `Material.matchMaterial("NAME")`（檢查 null）或 `XMaterial.X.isSupported()` + `parseItem()` 動態探測。**禁止**寫死 `Material.NEWER_THING` 而不做 null 處理。
- **複雜建構**：當 ItemStack 需要設置 name、lore、enchantments 等 metadata 時，使用 `club.kid7.bannermaker.util.ItemBuilder` 保持鏈式 API 一致。
- **基礎穩定材料**（如 `STICK`、`BRICK`、`VINE`、`CREEPER_HEAD`、`OXEYE_DAISY`、各色 dye/wool 等自 1.13 以前已穩定者，含 1.21.0 已存在的 `PIGLIN_BANNER_PATTERN`、`FLOW_BANNER_PATTERN` 等）：直接 `new ItemStack(Material.X)` **允許**，不需強制走 `ItemBuilder`。
- `ItemBuilder` 支援 `name(Component)`、`lore(Component...)`、`addLore(Component...)`。

### 工具類

- `BannerUtil.isBanner()` 使用 `XTag.BANNERS` 判斷 `ItemStack` 或 `Material` 是否為旗幟。

### 旗幟 wire format（`/bm view` 字串）

- 詳細規格參見 [`docs/wire-format.md`](docs/wire-format.md)。
- 現行格式為 **v2**：base64 字串以 `Qk0` 開頭（內嵌 `BM\x02` magic + UTF-8 payload）。
- **v1 backward compat**：舊字串（以 `rO0` 開頭，內含 `BukkitObjectOutputStream` 包裝）永遠可解，但 `serialize()` 不再產出 v1。
- `BannerSerializer.deserialize` 失敗時拋 `BannerDeserializationException` 子類（`InvalidBannerFormatException` / `UnknownBannerPatternException` / `UnknownBannerColorException`）；對玩家可見的 `/bm view` 訊息**故意**維持通用「無效旗幟字串」，不依例外型別切換，避免揭露 wire format 細節。

### 套件層級規約

`util/`、`registry/`、`configuration/` 套件**禁止**反向 import `gui/`、`command/`、`service/`。整體層級為：

```
util / registry / configuration（底層）
   ↓
service
   ↓
gui / command
   ↓
BannerMaker（進入點）
```

GUI 自身所需的「進入前狀態設定」邏輯應當收攏在 GUI class 內部（透過 public 入口接收必要參數、再由 private renderer 渲染，如 `BannerInfoGUI.open(Player, ItemStack)` 委派給 `refresh(...)`），不應抽至 `util/`，也不應透過 `PlayerData` 在 GUI 間隱式傳遞臨時狀態。

### Service 取用慣例

整個專案沿用 service locator 模式：`BannerMaker.getInstance().getXxxService()` 取得 service 實例。重構時逐步往依賴注入靠攏，**新撰程式碼**遵循以下優先順序：

1. **若已有 plugin 參考**（例如 class field `private final BannerMaker plugin;`、或方法收到 `BannerMaker bm` 參數）：用 `plugin.getXxxService()` / `bm.getXxxService()`，不要再呼叫 `BannerMaker.getInstance()`。
2. **新建立的 class**：以建構式注入 plugin 或所需 service，而非在每個方法靜態取用。
3. **既有程式碼**（已用 `BannerMaker.getInstance()` 散落各處）：**不溯及既往**，動到該段時順手改即可，不主動掃改。

長期目標是讓 `BannerMaker.getInstance()` 只在進入點與絕對必要處出現。

### 跨版本相容性陷阱

Bukkit API 在 1.21.x 期間有數次 binary-incompatible 變化，撰寫或修改相關邏輯時須注意：

- **interface ↔ class 演化**：當變數型別涉及曾為 enum 後改 interface 的 Bukkit 類別（如 `org.bukkit.block.banner.PatternType`、`org.bukkit.block.Biome`），對該變數呼叫實例方法（特別是 `.equals()`）時，把變數宣告為 `Object`，使 bytecode 走 `invokevirtual Object.equals` 而非 `invokeinterface`。靜態欄位存取（`PatternType.BRICKS`）不受影響，可直接寫。
- **新材料動態探測**：對於可能不存在於最低支援版本（1.21.0）的 `Material`（如 1.21.2+ 才有的 `FIELD_MASONED_BANNER_PATTERN`、`BORDURE_INDENTED_BANNER_PATTERN`），用 `Material.matchMaterial("NAME")` + null 檢查 或 `XMaterial.X.isSupported()` + `parseItem()`。**禁止**寫死 `Material.NEWER_THING` 而不處理 null。
- **PR 規範**：涉及 Pattern / Material / Registry 相關邏輯時，CHANGELOG 與 commit message 應明示已驗證的 MC 版本（理想含 1.21.0、1.21.2、最新版三點）。

## 語系檔慣例

- **檔名採用 IETF BCP 47 短橫線格式**（`zh-TW.yml`、`de-DE.yml` 等），不使用底線。
- `Language.getFileName()` 透過 `Locale.toLanguageTag()` 產生檔名。
- `Language.loadLanguage()` 啟動時自動將使用者自訂的 `xx_YY.yml` 改名為 `xx-YY.yml`，保留既有翻譯。
- 檔案開頭**不含註解標頭**：歷史上的 `# Language(xx)` / `# For BannerMaker vX` / `# Contributor(s):` 已全數移除（Crowdin 下載時也會覆蓋這些註解）。譯者署名統一管理於 `CONTRIBUTORS.md`。
- `parseLocale()` 仍接受 `zh_TW` 與 `zh-TW` 兩種寫法於 `config.yml` 的 `Language` 欄位，以維持向後相容。

## Crowdin 翻譯同步

- **`zh-TW` 與 `en-US` 由維護者本機掌控**；其餘 11 個語系由社群譯者於 Crowdin 上維護。
- Crowdin 對多數語言使用兩字母代碼（`de`、`fr`、`hu`、`it`、`nl`、`pl`、`ru`、`uk`），其餘採國別碼（`es-ES`、`pt-BR`、`zh-CN`、`zh-TW`）。`crowdin.yml` 的 `languages_mapping` 將 Crowdin 代碼轉換為 repo 檔名，同時擔任「下載白名單」（故意不列 zh-TW）。
- `.github/workflows/crowdin-sync.yml` 每週一 00:00 UTC（台北 08:00）與手動觸發時執行，使用 `--exclude-language zh-TW --skip-untranslated-strings` 拉取譯文並開 PR。
- 未翻譯的 key **不會**寫入語系檔；執行階段由 `Language.checkConfig()` 從 `en-US.yml` 補回。

## 工具鏈：限定 pnpm

`package.json` 僅作為 Crowdin 同步 script 的 wrapper（透過 `dotenv-cli`），以 `packageManager` 與 `preinstall: only-allow pnpm` 雙重保險強制使用 pnpm。`.gitignore` 阻擋 `package-lock.json` 與 `yarn.lock`；`pnpm-lock.yaml` 進版控。

可用 scripts：

```
pnpm run crowdin:upload-sources   # 推 en-US.yml 至 Crowdin
pnpm run crowdin:upload-zhtw      # 推 zh-TW.yml 作為譯者參考
pnpm run crowdin:download         # 拉回其他 11 個語系（不含 zh-TW）
pnpm run crowdin:status           # 查詢各語系翻譯進度
```

需要從 `.env.example` 複製 `.env` 並填入 token（內含建立 token 所需的最小 scope 清單）。

## 版本支援政策

- **Minecraft 最低支援版本**：1.21.0（鎖在 `plugin.yml` 的 `api-version: 1.21`）。
- **不回頭支援更舊的 MC 主版本**（1.20、1.19 …）。舊版使用者應下載歷史 tag（v2.4.0 for 1.20、v2.3.2 for 1.17–1.19 等）。
- 1.21.x 範圍內的小版本差異由 XSeries 與少數 workaround 處理（如 `MessageService` 對 Paper 1.21.7+ ClickEvent 的繞道）。
- **禁止引入 Paper 專屬 API**（`io.papermc.paper.*`、`com.destroystokyo.paper.*`、NMS）。本插件需可同時在 Spigot 與 Paper 1.21.0+ 上執行；`co.aikar.commands.PaperCommandManager` 在 Spigot 上會自動降級。
- 註：`pom.xml` 中 `paper.api.version` 仍綁在 1.21.4-R0.1-SNAPSHOT，這是給 MockBukkit 4.41.1 測試用的（其 BiomeMock 預期 Biome 為 interface，於 1.21.2+ 才如此）；真正的編譯目標由 `spigot.api.version` 1.21-R0.1-SNAPSHOT 控制。

## 開發慣例

- **語言**：專案文件與程式碼註解使用**臺灣正體中文**。
- **測試策略**：
    - 核心邏輯與工具類必須有單元測試（`src/test/java`）。
    - 必須使用 `MockBukkit` 模擬伺服器環境。
    - 透過 `isUnitTest()`（檢查 `MockBukkit` 類別）來避免測試中初始化 bStats Metrics 或其他外部連線。
    - 重載方法中對 `null` 參數的測試，請使用顯式轉型（例如 `(ItemStack) null`）避免編譯歧義。

## 提交訊息慣例

- 標題使用祈使句、無前綴（**不用** `feat:`、`fix:` 等）。參考 `git log --oneline` 了解既有語氣。
- 內文解釋**為什麼**而非僅列出做了什麼；引用具體檔案位置時更佳。
- 一個主題一個 commit — 不相關的工作（如工具設定與內容變更）拆分為獨立 commit。

## 專案地圖

- `src/main/java/club/kid7/bannermaker/`
    - `BannerMaker.java`：插件進入點，初始化 services 與 managers
    - `configuration/`：
        - `ConfigManager.java`：中央 YAML 存取
        - `Language.java`：多語系系統（MiniMessage/Legacy 雙模式）
    - `gui/`：使用者介面實作（`MainMenuGUI` 等）
    - `command/acf/`：ACF 指令處理（`BannerMakerCommand`）
    - `service/`：核心服務（`MessageService`、`BannerService`、`EconomyService`、`BannerRepository`）
    - `util/`：通用工具
        - `BannerUtil` / `BannerSerializer` / `BannerCost` / `BannerPatternLayout`：旗幟相關工具，職責拆分為「判斷類型」「序列化」「合成材料計算」「3x3 配方版面」
        - `ItemBuilder` / `TagUtil` / `InventoryUtil` / `PersistentDataUtil` / `MaterialUtil` / `MessageComponentUtil` / `SerializationUtil`：通用基礎工具
    - `registry/`：列舉式靜態註冊（`DyeColorRegistry`）

## 已知議題與技術債

- `Language.tl()` 是 static facade，caller 端 10+ 檔案透過 `import static` 使用。內部已改為 lookup via `BannerMaker.getLanguage()`（單例持有從 `Language` 內部 static field 搬到 BannerMaker，constructor 不再有自我 register 的副作用）。長期方向：caller 透過建構式注入 Language、移除 facade。
- `BannerPatternLayout.getPatternRecipe()` 產出的 3x3 合成格圖示對應 1.14 之前的 vanilla 合成 recipe；自 1.14 起 vanilla 已移除 banner pattern 的 3x3 合成（只能用 loom）。BannerMaker GUI 仍展示這個 grid 作為視覺參考（玩家實際取得 banner 走插件內部的 buy / craft 路徑，不依賴 vanilla 合成）。長期可考慮改成 loom 樣式或加註說明。
