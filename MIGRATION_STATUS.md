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

-   [ ] **移除 PluginUtilities**: 當 GUI 和 ItemStack 都遷移完畢後，從 `pom.xml` 移除依賴。
-   [ ] **重建單元測試**:
    - 恢復並修復 `BannerUtilTest`。
    - 為 `ItemBuilder` 撰寫測試。
    - 為新 GUI 邏輯撰寫測試 (如果可行)。
-   [ ] **Jar 檔案瘦身 (Optimization)**:
    - 目前 Jar 檔約 12MB，因為包含了所有依賴的完整內容。
    - **任務**: 在 `pom.xml` 的 `maven-shade-plugin` 設定中啟用 `<minimizeJar>true</minimizeJar>`，移除未使用的類別。

---

## ⚠️ 編譯問題與待辦事項 (Compilation Issues & Pending Tasks)

### Maven 編譯指令

* 目前使用的完整 Maven 編譯指令為：
  `& "C:\Users\jyhsu\AppData\Local\Programs\IntelliJ IDEA Ultimate\plugins\maven\lib\maven3\bin\mvn.cmd" clean package`

### 當前編譯錯誤 (位於 `BannerInfoGUI.java` 和 `MessageComponentUtil.java`)

* **錯誤詳情**:
    1. **`MessageComponentUtil.java`**: 報錯 `cannot find symbol class BukkitAdapter` (
       `net.kyori.adventure.platform.bukkit.BukkitAdapter`) 以及 `package net.kyori.adventure.item does not exist`。
        * **初步診斷**: 這可能是由於 `maven-shade-plugin` 的重定位配置（`net.kyori` -> `club.kid7.bannermaker.lib.kyori`
          ）導致編譯器在原始碼編譯時無法正確找到 `BukkitAdapter` 的原始路徑。
        * **目前的嘗試與回溯**: 曾嘗試修改 `MessageComponentUtil.java` 中的 `import` 語句以匹配重定位路徑，但這並不正確。原始碼中的
          `import` 語句應始終使用原始庫的包路徑。
        * **待解決**: 需要進一步確認 `adventure-platform-bukkit` 依賴在編譯時是否正確被包含，以及 `BukkitAdapter`
          的正確使用方式。
    2. **`BannerInfoGUI.java`**: 報錯
       `incompatible types: net.kyori.adventure.text.event.HoverEvent.ShowItem cannot be converted to net.kyori.adventure.text.event.HoverEventSource<?>`
       以及 `no suitable method found for sendMessage(net.kyori.adventure.text.TextComponent)`。
        * **初步診斷**:
            * `HoverEvent` 的錯誤是因為 `HoverEvent.showItem` 方法的參數類型不匹配 Adventure API 的期望。
            * `sendMessage` 的錯誤是因為 `Player.sendMessage` 方法在編譯環境中可能不接受 Adventure `Component` 類型，或
              `MessageService.send(player, String)` 的重載方法被意外匹配。
        * **目前的嘗試**: `MessageComponentUtil.java` 已被調整為返回 Bukkit 的 `ItemStack`，讓 `BannerInfoGUI` 負責使用
          `BukkitAdapter.adapt` 進行轉換。同時，`BannerInfoGUI` 中所有訊息發送都已改為透過 `messageService.format()`
          來統一處理。

### 功能一致性與註記

* **MainMenuGUI**: 遷移後的功能與舊版 `MainMenu` 保持一致。
* **BannerInfoGUI**: 遷移後的功能與舊版 `BannerInfoMenu` 保持一致，包括合成表第 10 格 (Slot 42) 用於展示合成結果的功能。
* **CreateBannerGUI**: 遷移後的功能與舊版 `CreateBannerMenu` 保持一致，透過重新開啟 GUI 模擬舊版 `openPrevious` 的刷新行為。
* **ChooseAlphabetGUI**: 遷移後的功能與舊版 `ChooseAlphabetMenu` 保持一致。
* **CreateAlphabetGUI**: 遷移後的功能與舊版 `CreateAlphabetMenu` 保持一致。
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
