# AI Agent Context & Guidelines (AGENTS.md)

## 📍 專案概觀 (Project Overview)

BannerMaker 是一個 Spigot/Paper Minecraft 插件，允許玩家透過 GUI 製作與管理旗幟。
本專案已完成現代化遷移，並持續優化核心組件，目標是維護一個高效能、模組化且易於擴展的程式碼庫。

## 🛠 技術堆疊 (Tech Stack)

- **Language**: Java 21
- **Platform**: Spigot / Paper 1.21.4+
- **Build System**: Maven (支援 `minimizeJar` 優化)
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
- **顏色代碼**: `Language.tl()` 支援 `&` 顏色代碼，並會自動轉換為 `Component`。程式碼中硬編碼的顏色應優先使用
  `Component.empty().color(NamedTextColor.COLOR)` 或 `NamedTextColor`。
- 推薦使用 `Language.tl(NamedTextColor color, String path, Object... args)` 重載方法來簡化帶顏色的翻譯 Component 的創建。

### 3. GUI 開發

- **必須** 使用 `InventoryFramework` 實作所有選單。
- GUI 類別位於 `club.kid7.bannermaker.gui` 套件下，取代舊有的 `CustomGUI` 系統。
- GUI 標題若必須為 `String`，請使用 `LegacyComponentSerializer.legacySection().serialize(component)` 進行轉換。

### 4. 物品建構 (Item Building)

- **必須** 使用 `club.kid7.bannermaker.util.ItemBuilder` 建立 `ItemStack`。
- `ItemBuilder` 現在支援 `name(Component)`, `lore(Component...)` 和 `addLore(Component...)`。
- **禁止** 直接使用 `new ItemStack()`，以確保 XMaterial 的跨版本支援。

### 5. 工具類 (Utilities)

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
      - `Language.java`: 多語言系統 (已優化)。
    - `gui/`: 使用者介面實作 (`MainMenuGUI` 等)。
    - `command/acf/`: ACF 指令處理 (`BannerMakerCommand`)。
    - `service/`: 核心服務 (`MessageService`)。
    - `util/`: 通用工具 (`BannerUtil`, `ItemBuilder`, `EconUtil` 等)。

## ✅ 當前狀態 (Current State)

- **已完成遷移**:
    - 移除 `PluginUtilities` 依賴，實現完全本地化。
    - 導入 Adventure, ACF, InventoryFramework, XSeries。
    - 建立 `ConfigManager` 取代舊系統。
    - 建立 `ItemBuilder` 取代舊 `KItemStack`。
    - 重建單元測試環境，並解決 bStats 與 ConfigManager 的測試相容性問題。
  - 語言系統已現代化：`Language.tl()` 返回 Adventure `Component`，移除了執行時的 I/O 阻塞，並新增了帶顏色參數的重載方法以簡化使用。
  - `ItemBuilder` 增強了對 `Component` 類型 Lore 的支援。
  - `BannerUtil.isBanner` 方法已優化為使用 `XTag`，提高了判斷的準確性和優雅性。
- **已知問題/待辦**:
    - `Language.java` 仍是靜態單例模式，這在單元測試中仍有潛在的狀態污染風險（儘管 `ConfigManager.reset()`
      已經處理了大部分）。未來可能考慮將其重構為依賴注入的形式。
