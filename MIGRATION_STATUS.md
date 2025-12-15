# BannerMaker 現代化遷移狀態報告 (Migration Status)

**最後更新日期**: 2025年12月16日  
**目標**: 將 BannerMaker 從舊有的 PluginUtilities 架構遷移至現代化 Spigot 開發堆疊 (Adventure, ACF, InventoryFramework,
XSeries)。

## 🟢 目前狀態 (Current Status)

### 1. 基礎設施 (Infrastructure) - ✅ 完成

* **Java 版本**: 21
* **構建工具**: Maven (pom.xml 已更新)
* **核心依賴**:
    * Adventure (Platform Bukkit 4.4.1 + MiniMessage 4.17.0)
    * ACF Paper (0.5.1-SNAPSHOT)
    * InventoryFramework (0.10.14)
    * XSeries (11.3.0)
    * JUnit 5 + MockBukkit (4.41.1 for 1.21.4)

### 2. 文字系統 (Text System) - ✅ 完成

* **MessageService**: 已建立 (`club.kid7.bannermaker.service.MessageService`)。
* **MessageUtil**: 舊工具類已移除，所有呼叫已替換。
* **顏色處理**: 優先使用 `LegacyComponentSerializer` 處理 `&` 顏色代碼，確保舊版相容性與正確渲染。

### 3. 指令系統 (Command System) - ✅ 完成

* **ACF 遷移**: 所有指令 (`/bm`, `help`, `reload`, `see`, `hand`, `view`) 已遷移至 ACF (
  `club.kid7.bannermaker.command.acf.BannerMakerCommand`)。
* **舊代碼**: 舊的 `command` package 及其下檔案已刪除。

### 4. 物品系統 (Item System) - ✅ 完成

* **ItemBuilder**: 已建立 (`club.kid7.bannermaker.util.ItemBuilder`)，整合了 XMaterial 與 Adventure。
* **應用**: 已全面替換舊有的 `KItemStack`。

### 5. GUI 介面 (GUI) - ✅ 完成

* **所有舊的 `customMenu` 選單 (MainMenu, BannerInfoMenu, CreateBannerMenu, ChooseAlphabetMenu, CreateAlphabetMenu)**
  已成功遷移至 `InventoryFramework`。
* 已建立新的 GUI 類別：`MainMenuGUI`, `BannerInfoGUI`, `CreateBannerGUI`, `ChooseAlphabetGUI`, `CreateAlphabetGUI`。
* 所有對舊 GUI 的引用已更新為新的 GUI 實現。
* 舊的 `customMenu` 目錄下的所有檔案已刪除。

---

## 📋 待辦事項清單 (To-Do List)

### Phase 6: 清理與測試 (Cleanup & Testing)

-   [ ] **修復運行時問題 (Runtime Issues Fixes)**:
    - **AIR ItemMeta 異常**: 解決 `MainMenuGUI`、`/bm see`、`/bm hand` 中出現的
      `item must be able to have ItemMeta (it mustn't be AIR)` 錯誤。
    - **GUI 排版錯亂**: 全面檢查並修正所有 `StaticPane` 的座標設定，確保與舊版 6x9 佈局一致。
-   [ ] **移除 PluginUtilities**: 當 GUI 和 ItemStack 都遷移完畢後，從 `pom.xml` 移除依賴。
-   [ ] **重建單元測試**:
    - 恢復並修復 `BannerUtilTest`。
    - 為 `ItemBuilder` 撰寫測試。
    - 為新 GUI 邏輯撰寫測試 (如果可行)。
-   [ ] **Jar 檔案瘦身 (Optimization)**:
    - 目前 Jar 檔約 12MB，因為包含了所有依賴的完整內容。
    - **任務**: 在 `pom.xml` 的 `maven-shade-plugin` 設定中啟用 `<minimizeJar>true</minimizeJar>`，移除未使用的類別。

---

## ⚠️ 編譯與運行問題 (Compilation & Runtime Issues)

### Maven 編譯指令

* 目前使用的完整 Maven 編譯指令為：
  `& "C:\Users\jyhsu\AppData\Local\Programs\IntelliJ IDEA Ultimate\plugins\maven\lib\maven3\bin\mvn.cmd" clean package`
* **狀態**: ✅ 編譯成功 (BUILD SUCCESS)。

### 運行時問題 (Runtime Issues)

1. **主畫面與指令中的 AIR ItemMeta 異常**:
    * **症狀**: 主畫面點擊已儲存旗幟、使用 `/bm see` 或 `/bm hand` 時，出現
      `java.lang.IllegalArgumentException: item must be able to have ItemMeta (it mustn't be AIR)`。
    * **可能原因**: `IOUtil.loadBannerList` 載入的旗幟列表、`ItemBuilder` 處理空物品時，或 `InventoryFramework`
      處理點擊事件時，可能傳遞了 `AIR` 類型的物品。
    * **待解決**: 需要在 `BannerUtil`、`ItemBuilder` 和各 GUI 點擊事件中增加對 `AIR` 的防禦性檢查。

2. **GUI 排版位置錯亂**:
    * **症狀**: 多個 GUI (如 `CreateBannerGUI`, `CreateAlphabetGUI`) 的元件位置偏移。
    * **可能原因**: `StaticPane` 的座標計算 (x, y) 可能有誤，特別是與舊版 `index` (0-53) 的轉換。需注意 `PaginatedPane` 和
      `StaticPane` 混用時的層級與座標。
    * **待解決**: 重新審查所有 GUI 類別的 `addItem` 座標參數。

### 已解決的編譯錯誤

1. **`MessageComponentUtil.java`**: 解決了 `BukkitAdapter` 找不到的問題，改為在 `MessageComponentUtil` 中封裝
   `HoverEvent` 的創建，並使用原始路徑導入 `BukkitAdapter`。
2. **`BannerInfoGUI.java`**: 解決了 `sendMessage` 不兼容問題（改用 `MessageService.send`），以及 `HoverEvent` 類型不匹配問題。

---

## 📝 開發注意事項 (Notes)

* **語言**: 開發者慣用 **正體中文 (Traditional Chinese)**，註解請使用正體中文。
* **顏色代碼**: 目前 `MessageService` 為了相容性，`formatToString` 方法直接使用
  `ChatColor.translateAlternateColorCodes('&', ...)`。在完全轉向 Component 之前，請保持此行為。
* **依賴版本**: MockBukkit 版本鎖定為 `4.41.1` 以支援 Spigot 1.21.4。
* **Maven**: 每次修改 `pom.xml` 後，建議執行 `mvn clean` 以避免類別版本衝突。
