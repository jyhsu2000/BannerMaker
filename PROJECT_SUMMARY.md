# BannerMaker 現代化專案摘要 (Modernization Project Summary)

**最後更新日期**: 2025年12月16日  
**專案目標**: 將 BannerMaker 從舊有的 PluginUtilities 架構，成功遷移至現代化 Spigot 開發堆疊，以提升效能、維護性與未來擴展性。

## ✅ 現代化完成項目

### 1. 基礎設施 (Infrastructure)

* **Java 版本**: 已升級至 Java 21。
* **構建工具**: 沿用 Maven (pom.xml 已更新以支援新依賴)。
* **核心依賴**:
    * **Adventure**: 整合了 Adventure (Platform Bukkit 4.4.1 + MiniMessage 4.17.0) 作為新的文字與訊息處理框架。
    * **ACF Paper**: 將指令系統遷移至 ACF (0.5.1-SNAPSHOT)，實現更靈活的指令管理。
    * **InventoryFramework**: 所有 GUI 介面已成功遷移至 InventoryFramework (0.10.14)，提供現代化的 GUI 設計與互動。
    * **XSeries**: 引入 XSeries (11.3.0) 以確保跨版本相容性。
    * **測試框架**: 升級至 JUnit 5 並整合 MockBukkit (4.41.1 for 1.21.4) 進行單元測試。

### 2. 文字系統 (Text System)

* **MessageService**: 建立了專用的 `MessageService` (`club.kid7.bannermaker.service.MessageService`) 處理所有插件訊息。
* **舊工具類移除**: 舊有的 `MessageUtil` 已移除，所有相關呼叫已替換為 `MessageService`。
* **顏色處理**: 優先使用 `LegacyComponentSerializer` 處理 `&` 顏色代碼，確保舊版相容性與正確渲染。

### 3. 指令系統 (Command System)

* **ACF 遷移**: 所有指令 (`/bm`, `help`, `reload`, `see`, `hand`, `view`) 已成功遷移至 ACF (
  `club.kid7.bannermaker.command.acf.BannerMakerCommand`)。
* **舊指令代碼清理**: 舊的 `command` package 及其下檔案已刪除。

### 4. 物品系統 (Item System)

* **ItemBuilder**: 建立了現代化的 `ItemBuilder` (`club.kid7.bannermaker.util.ItemBuilder`)，整合了 XMaterial 與
  Adventure，全面替換舊有的 `KItemStack`。

### 5. GUI 介面 (GUI)

* **InventoryFramework 遷移**: 所有舊的 `customMenu` 選單 (MainMenu, BannerInfoMenu, CreateBannerMenu,
  ChooseAlphabetMenu, CreateAlphabetMenu) 已成功遷移至 `InventoryFramework`。
* **新 GUI 類別**: 建立了新的 GUI 類別 (`MainMenuGUI`, `BannerInfoGUI`, `CreateBannerGUI`, `ChooseAlphabetGUI`,
  `CreateAlphabetGUI`)。
* **舊 GUI 代碼清理**: 舊的 `customMenu` 目錄下的所有檔案已刪除。
* **功能一致性**: 遷移後的功能與舊版保持一致，並已修復排版錯位問題。

### 6. 清理與優化 (Cleanup & Optimization)

* **PluginUtilities 移除**: 成功將 `PluginUtilities` 函式庫替換為本地實作的 `ConfigManager`，並從 `pom.xml`
  移除了該依賴，實現了專案的完全獨立。
* **Jar 檔案瘦身**: 在 `pom.xml` 的 `maven-shade-plugin` 設定中啟用了 `<minimizeJar>true</minimizeJar>`
  ，成功移除未使用的類別，有效縮減了插件檔案大小。

### 7. 單元測試 (Unit Testing)

* **恢復與新建**: 恢復並修復了 `BannerUtilTest`，並為 `ItemBuilder` 撰寫了新的測試。
* **測試環境優化**: 解決了 MockBukkit 環境中 bStats Metrics 初始化問題，使測試環境更加穩定。
* **測試通過**: 所有現有單元測試均成功通過，確保了程式碼的品質與穩定性。

---

## 📝 開發注意事項 (Notes)

* **語言**: 開發者慣用 **正體中文 (Traditional Chinese)**，註解請使用正體中文。
* **顏色代碼**: `MessageService` 目前為相容性使用 `ChatColor.translateAlternateColorCodes('&', ...)`，在完全轉向
  Component 之前請保持此行為。
* **依賴版本**: MockBukkit 版本鎖定為 `4.41.1` 以支援 Spigot 1.21.4。
* **Maven**: 每次修改 `pom.xml` 後，建議執行 `mvn clean` 以避免類別版本衝突。
* **ConfigManager**: 為了測試，`ConfigManager` 新增了 `reset()` 方法以清除靜態狀態，確保測試隔離。
