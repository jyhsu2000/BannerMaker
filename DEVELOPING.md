# 開發指引

本文件協助新進開發者快速上手。**完整的架構說明、規範、技術債清單，請見 [`CLAUDE.md`](CLAUDE.md)。**

## 環境需求

- **JDK 21**（Temurin、Microsoft Build 皆可）
- **Maven**（或 IntelliJ IDEA 內建的 Maven）
- （選用）**Node.js + pnpm 11.1.2+** — 僅用於 Crowdin 翻譯同步

## 第一次設定

```powershell
git clone https://github.com/jyhsu2000/BannerMaker.git
cd BannerMaker
mvn test   # 驗證環境，預期 45 個測試全綠
```

Windows + IntelliJ IDEA 使用者若 `mvn` 不在 PATH 上：

```powershell
& "C:\Users\<you>\AppData\Local\Programs\IntelliJ IDEA Ultimate\plugins\maven\lib\maven3\bin\mvn.cmd" test
```

## 常用指令

```powershell
mvn test                       # 全部測試（~10 秒）
mvn test -Dtest=LanguageTest   # 單一測試類別
mvn package                    # 產出 target/BannerMaker.jar
```

## 本機伺服器測試

1. `mvn package` 產出 `target/BannerMaker.jar`
2. 把 jar 丟進你的 Paper/Spigot 1.21.4+ 伺服器的 `plugins/` 資料夾
3. 啟動伺服器，進遊戲輸入 `/bm` 開選單

## 常見開發任務速查

| 想做什麼 | 從哪裡下手 | 規範 |
|---|---|---|
| 加新訊息 | 改 `language/en-US.yml` + `zh-TW.yml`，Java 用 `Language.tl("your.key")` | 不要手動編輯其餘 11 個語系（Crowdin 管） |
| 加新指令 | 編輯 `command/acf/BannerMakerCommand.java`，用 ACF `@Subcommand` | 不要寫手動 `CommandExecutor` |
| 改 GUI | 編輯 `gui/*.java`，使用 InventoryFramework | 不要用 Bukkit 原生 Inventory |
| 建立 ItemStack | 使用 `util.ItemBuilder` | 不要用 `new ItemStack(...)` |
| 發送訊息給玩家 | 使用 `service.MessageService` | 不要用 `player.sendMessage()` |
| 讀寫 YAML | 使用 `configuration.ConfigManager` | 不要直接 `new YamlConfiguration()` |

完整的「為什麼」與細節見 `CLAUDE.md`。

## 翻譯同步（選用）

維護者本機掌控 `en-US.yml` 與 `zh-TW.yml`；其餘 11 個語系由 Crowdin 社群譯者維護，每週一自動透過 GitHub Actions 拉回 PR。

若要在本機手動同步翻譯：

```powershell
copy .env.example .env   # 接著編輯 .env 填入 Crowdin token
pnpm install
pnpm run crowdin:status    # 看各語系翻譯進度
pnpm run crowdin:download  # 拉回最新翻譯
```

`.env.example` 內含 Crowdin token 所需的最小 scope 清單。

## 接下來

- **完整架構與規範**（必讀）：[`CLAUDE.md`](CLAUDE.md)
- **PR 流程**：[`CONTRIBUTING.md`](CONTRIBUTING.md)
- **更新紀錄**：[`CHANGELOG.zh-tw.md`](CHANGELOG.zh-tw.md)

遇到設計問題或不確定的選擇，先看 `CLAUDE.md` 的「架構規範」與「已知議題與技術債」，再開 issue 討論。
