# AI Agent Context & Guidelines (AGENTS.md)

## 📍 專案概觀 (Project Overview)

BannerMaker 是一個 Spigot/Paper Minecraft 插件，允許玩家透過 GUI 製作與管理旗幟。
本專案已完成現代化遷移，並持續優化核心組件，目標是維護一個高效能、模組化且易於擴展的程式碼庫。

## 🛠 技術堆疊 (Tech Stack)

- **Language**: Java 21
- **Platform**: Spigot / Paper 1.21.4+
- **Build System**: Maven (支援 `minimizeJar` 優化)
- **Local Env**: `& "C:\Users\jyhsu\AppData\Local\Programs\IntelliJ IDEA Ultimate\plugins\maven\lib\maven3\bin\mvn.cmd"`
- **Key Libraries**:
    - **Adventure**: `4.17.0` (Text) / `4.3.4` (Platform Bukkit) - 文字與訊息處理核心。
    - **ACF (Paper)**: `0.5.1-SNAPSHOT` - 指令管理系統。
    - **InventoryFramework**: `0.10.14` - GUI 介面框架。
    - **XSeries**: `11.3.0` - 跨版本材質與聲音相容性。
    - **MockBukkit**: `4.41.1` - 單元測試框架 (針對 1.21.4)。

## 📐 架構規範 (Architecture & Patterns)

### 1. 設定與資料 (Configuration)

- **必須** 使用 `club.kid7.bannermaker.configuration.ConfigManager` 進行所有 YAML 檔案存取。
- **禁止** 直接實例化 `YamlConfiguration` 或使用 Bukkit API 的預設 config 方法 (除非在 Manager 內部)。
- **單元測試**: 測試結束時 (`tearDown`) **必須** 呼叫 `ConfigManager.reset()` 以清除靜態狀態，防止測試間汙染。
- `Language.java` 中的翻譯鍵值若在執行時缺漏，將不再觸發同步磁碟寫入，以避免性能瓶頸。

### 2. 訊息處理 (Messaging)

- **必須** 使用 `club.kid7.bannermaker.service.MessageService` 發送訊息。
- **禁止** 使用 `player.sendMessage()` 或 `Bukkit.broadcastMessage()`。
- `club.kid7.bannermaker.configuration.Language.tl()` 方法現在返回 Adventure `Component`。
- **顏色代碼機制**:
    - `Language.tl()` 智能支援 **MiniMessage** (如 `<red>`, `<gradient>`) 與 **Legacy** (如 `&c`) 格式。
    - 若字串包含 MiniMessage 標籤 (`<` 和 `>`)，優先使用 MiniMessage 解析；否則回退至 Legacy 解析。
- **參數替換**:
    - **舊方式 (Legacy)**: `tl("key", arg1)` 使用 `{0}` 佔位符（不推薦，有注入風險）。
    - **新方式 (Recommended)**: `tl("key", TagUtil.tag("arg", value))` 使用 `<arg>` 佔位符。配合
      `club.kid7.bannermaker.util.TagUtil` 進行安全轉義。
- 推薦使用 `Language.tl(NamedTextColor color, String path, Object... args)` 重載方法來簡化帶顏色的翻譯 Component 的創建。

### 3. ACF 整合 (Command Framework)

- **自動 Help 系統**: 使用 ACF 內建的 `@HelpCommand` 自動生成指令幫助訊息 (`/bm help`)。
- **權限過濾**: Help 訊息會根據玩家擁有的權限自動過濾顯示的指令。
- **語言同步**: ACF 的系統訊息語言（如「未知指令」）會自動根據 `config.yml` 中的 `Language` 設定（`zh_TW`, `en`, `auto`
  ）進行同步。若設為 `auto`，則使用伺服器系統語言。

### 4. GUI 開發

- **必須** 使用 `InventoryFramework` 實作所有選單。
- GUI 類別位於 `club.kid7.bannermaker.gui` 套件下，取代舊有的 `CustomGUI` 系統。
- GUI 標題若必須為 `String`，請使用 `LegacyComponentSerializer.legacySection().serialize(component)` 進行轉換。

### 5. 物品建構 (Item Building)

- **必須** 使用 `club.kid7.bannermaker.util.ItemBuilder` 建立 `ItemStack`。
- `ItemBuilder` 現在支援 `name(Component)`, `lore(Component...)` 和 `addLore(Component...)`。
- **禁止** 直接使用 `new ItemStack()`，以確保 XMaterial 的跨版本支援。

### 6. 工具類 (Utilities)

- `club.kid7.bannermaker.util.BannerUtil.isBanner()` 方法已重構，使用 `XTag.BANNERS` 判斷 `ItemStack` 或 `Material`
  是否為旗幟，提供更準確和優雅的判斷方式。

## 📝 開發慣例 (Conventions)

- **主要語言**: 專案文件與代碼註解使用 **正體中文 (Traditional Chinese)**。
- **測試策略**:
    - 核心邏輯與工具類必須包含單元測試 (`src/test/java`)。
    - 必須使用 `MockBukkit` 模擬伺服器環境。
    - 測試環境判斷：使用 `isUnitTest()` 方法（檢查 MockBukkit 類別）來避免在測試中初始化 bStats Metrics 或其他不必要的外部連線。
  - 針對重載方法中對 `null` 參數的測試，請使用顯式轉型 (例如 `(ItemStack) null`) 來避免編譯歧義。

## 🗺️ 專案地圖 (Codebase Map)

- `src/main/java/club/kid7/bannermaker/`
    - `BannerMaker.java`: 插件進入點 (Entry Point)，負責初始化 Services 與 Managers。
    - `configuration/`:
        - `ConfigManager.java`: 核心設定管理。
      - `Language.java`: 多語言系統 (已優化，支援 MiniMessage/Legacy 混用)。
    - `gui/`: 使用者介面實作 (`MainMenuGUI` 等)。
    - `command/acf/`: ACF 指令處理 (`BannerMakerCommand`)。
    - `service/`: 核心服務 (`MessageService`)。
  - `util/`: 通用工具 (`BannerUtil`, `ItemBuilder`, `TagUtil` 等)。

## ✅ 當前狀態 (Current State)

- **已完成遷移**:
    - 移除 `PluginUtilities` 依賴，實現完全本地化。
    - 導入 Adventure, ACF, InventoryFramework, XSeries。
    - 建立 `ConfigManager` 取代舊系統。
    - 建立 `ItemBuilder` 取代舊 `KItemStack`。
    - 重建單元測試環境，並解決 bStats 與 ConfigManager 的測試相容性問題。
  - 語言系統已現代化：`Language.tl()` 支援 MiniMessage 與 Legacy 雙重解析，並支援 TagResolver 參數。
  - ACF 整合：已啟用自動 Help 系統，並實現語言設定與 `config.yml` 同步。
  - `ItemBuilder` 增強了對 `Component` 類型 Lore 的支援。
  - `BannerUtil.isBanner` 方法已優化為使用 `XTag`，提高了判斷的準確性和優雅性。
- **已知問題/待辦**:
    - `Language.java` 仍是靜態單例模式，這在單元測試中仍有潛在的狀態污染風險（儘管 `ConfigManager.reset()`
      已經處理了大部分）。未來可能考慮將其重構為依賴注入的形式。
  - **參數佔位符遷移**: 逐步將現有的 `{0}` 格式參數替換遷移至新的 `TagUtil` 與 `TagResolver` 機制，以提升安全性和可讀性。
  - **ACF 指令描述多語系**: 實作機制將 `language/*.yml` 中的指令描述（如 `command.description.*`）注入到 ACF 的 Locales
    系統，使 `/bm help` 中的描述能支援多語系顯示。
