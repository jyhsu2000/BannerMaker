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
    - **AIR ItemMeta 異常**: **✅ 已全數解決。** 已在 `BannerInfoGUI` 的 `updateCraftingRecipeSection` 方法中增加對
      `Material.AIR` 的過濾，避免將空氣物品加入 GUI。相關的 `MainMenuGUI` 和指令 (`/bm see`, `/bm hand`) 中可能涉及 `AIR`
      物品的邏輯也已檢查並修正，確保 GUI 和物品操作的穩定性。
    - **GUI 排版錯亂**: **✅ 已解決。** 已全面檢查並修正所有 `StaticPane` 的座標設定，確保與舊版 6x9 佈局一致，並新增詳細註解。
    - **GUI 物品取下問題**: **✅ 已解決。** 已在所有 GUI 中添加 `gui.setOnGlobalClick(event -> event.setCancelled(true));`
      ，防止玩家取下無功能的物品。
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

### 運行時問題 (Runtime Issues) - 已解決

1. **主畫面與指令中的 AIR ItemMeta 異常**:
    * **狀態**: ✅ 已全數解決。
    * **解決方式**: 透過在 `BannerUtil.getMaterials` 中過濾 `AIR` 物品，並在各 GUI 的 `addItem` 處增加防禦性檢查，確保只有有效物品才會被加到
      GUI 中。同時修正了 `BannerMakerCommand` 相關邏輯，確保操作的物品有效。

### 已解決的編譯錯誤

1. **`MessageComponentUtil.java`**: 解決了 `BukkitAdapter` 找不到的問題，改為在 `MessageComponentUtil` 中封裝
   `HoverEvent` 的創建。
2. **`BannerInfoGUI.java`**: 解決了 `sendMessage` 不兼容問題（改用 `MessageService.send`），以及 `HoverEvent` 類型不匹配問題。

### 功能一致性與註記

* **所有 GUI**: 遷移後的功能與舊版保持一致，並已修復排版錯位問題。
* **TODO/FIXME 註記**:
    * `BannerMakerCommand.java`: 在 `onDefault` 方法中，已加入
      `// TODO: (GUI 遷移) 未來若有需要，可考慮整合 PlayerData 中的頁碼記憶功能。`。
    * 所有 GUI 檔案的註解已更新為正體中文，以增強說明性。

---

## 📝 開發注意事項 (Notes)

* **語言**: 開發者慣用 **正體中文 (Traditional Chinese)**，註解請使用正體中文。
* **顏色代碼**: 目前 `MessageService` 為了相容性，`formatToString` 方法直接使用
  `ChatColor.translateAlternateColorCodes('&', ...)`。在完全轉向 Component 之前，請保持此行為。
* **依賴版本**: MockBukkit 版本鎖定為 `4.41.1` 以支援 Spigot 1.21.4。
* **Maven**: 每次修改 `pom.xml` 後，建議執行 `mvn clean` 以避免類別版本衝突。
