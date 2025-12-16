# AI Agent Context & Guidelines (AGENTS.md)

## 📍 專案概觀 (Project Overview)

BannerMaker 是一個 Spigot/Paper Minecraft 插件，允許玩家透過 GUI 製作與管理旗幟。
本專案已完成現代化遷移，目標是維護一個高效能、模組化且易於擴展的程式碼庫。

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

### 2. 訊息處理 (Messaging)

- **必須** 使用 `club.kid7.bannermaker.service.MessageService` 發送訊息。
- **禁止** 使用 `player.sendMessage()` 或 `Bukkit.broadcastMessage()`。
- **顏色代碼**: 目前暫時使用 `&` 顏色代碼 (Legacy) 相容模式，但在新功能開發時應優先考慮 Adventure 的 MiniMessage 格式。

### 3. GUI 開發

- **必須** 使用 `InventoryFramework` 實作所有選單。
- GUI 類別位於 `club.kid7.bannermaker.gui` 套件下，取代舊有的 `CustomGUI` 系統。

### 4. 物品建構 (Item Building)

- **必須** 使用 `club.kid7.bannermaker.util.ItemBuilder` 建立 ItemStack。
- **禁止** 直接使用 `new ItemStack()`，以確保 XMaterial 的跨版本支援。

## 📝 開發慣例 (Conventions)

- **主要語言**: 專案文件與代碼註解使用 **正體中文 (Traditional Chinese)**。
- **測試策略**:
    - 核心邏輯與工具類必須包含單元測試 (`src/test/java`)。
    - 必須使用 `MockBukkit` 模擬伺服器環境。
    - 測試環境判斷：使用 `isUnitTest()` 方法（檢查 MockBukkit 類別）來避免在測試中初始化 bStats Metrics 或其他不必要的外部連線。

## 🗺️ 專案地圖 (Codebase Map)

- `src/main/java/club/kid7/bannermaker/`
    - `BannerMaker.java`: 插件進入點 (Entry Point)，負責初始化 Services 與 Managers。
    - `configuration/`:
        - `ConfigManager.java`: 核心設定管理。
        - `Language.java`: 多語言系統 (待重構)。
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
- **已知問題/待辦**:
    - `Language.java`: 存在執行緒阻塞風險 (I/O on main thread) 與舊式字串處理，需整合至 `MessageService` 並支援
      MiniMessage。
