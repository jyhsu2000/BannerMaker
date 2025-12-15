# BannerMaker 現代化遷移狀態報告 (Migration Status)

**最後更新日期**: 2025年12月15日  
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

### 5. GUI 介面 (GUI) - 🔴 未開始

* 目前仍使用舊的 `PluginUtilities` 中的 `CustomGUI` 系統。
* 之前的遷移嘗試因複雜度過高已回滾。

---

## 📋 待辦事項清單 (To-Do List)

### Phase 4: 物品建構現代化 (Item Builder Adoption) - ✅ 完成

目標：移除對 `PluginUtilities.kitemstack.KItemStack` 的依賴。

- [x] **找出所有引用**: 搜尋 `KItemStack` 的使用位置。
- [x] **替換為 ItemBuilder**: 將 `new KItemStack(...)` 替換為 `new ItemBuilder(...)`。
    - 注意 `ItemBuilder` 目前的 API 設計 (fluent API) 與 `KItemStack` 的差異。
    - 確保 `XMaterial` 正確解析跨版本材料。

### Phase 3: GUI 介面遷移 (GUI Migration)

目標：使用 `InventoryFramework` (IF) 重寫所有選單。

- [ ] **MainMenu (主選單)**
    - 建立 `club.kid7.bannermaker.gui.MainMenuGUI`。
    - 使用 `PaginatedPane` 顯示旗幟列表。
- [ ] **BannerInfoMenu (旗幟資訊)**
    - 建立 `club.kid7.bannermaker.gui.BannerInfoGUI`。
    - 顯示旗幟預覽、材料清單、合成表。
- [ ] **CreateBannerMenu (製作選單)**
    - 最複雜的 GUI，需處理顏色選擇、圖案預覽。
- [ ] **Alphabet Menus (字母選單)**
    - `ChooseAlphabetMenu` 和 `CreateAlphabetMenu`。

### Phase 5: 清理與測試 (Cleanup & Testing)

- [ ] **移除 PluginUtilities**: 當 GUI 和 ItemStack 都遷移完畢後，從 `pom.xml` 移除依賴。
- [ ] **重建單元測試**:
    - 恢復並修復 `BannerUtilTest`。
    - 為 `ItemBuilder` 撰寫測試。
    - 為新 GUI 邏輯撰寫測試 (如果可行)。
- [ ] **Jar 檔案瘦身 (Optimization)**:
    - 目前 Jar 檔約 12MB，因為包含了所有依賴的完整內容。
    - **任務**: 在 `pom.xml` 的 `maven-shade-plugin` 設定中啟用 `<minimizeJar>true</minimizeJar>`，移除未使用的類別。

---

## 📝 開發注意事項 (Notes)

* **語言**: 開發者慣用 **正體中文 (Traditional Chinese)**，註解請使用正體中文。
* **顏色代碼**: 目前 `MessageService` 為了相容性，`formatToString` 方法直接使用
  `ChatColor.translateAlternateColorCodes('&', ...)`。在完全轉向 Component 之前，請保持此行為。
* **依賴版本**: MockBukkit 版本鎖定為 `4.41.1` 以支援 Spigot 1.21.4。
* **Maven**: 每次修改 `pom.xml` 後，建議執行 `mvn clean` 以避免類別版本衝突。
