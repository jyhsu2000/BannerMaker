# BannerMaker Wire Format

本文件描述 BannerMaker 在 `/bm view`、share command、未來 Web 匯入匯出等需要把
旗幟表達為文字字串時所使用的 wire format。

> 玩家側可見的字串為 base64，**內部結構刻意對玩家隱藏**——`/bm view` 對玩家只給
> 通用「無效旗幟字串」訊息，避免使用者誤把它當作「可手動編輯的明文代碼」。
> 本文件僅供開發者與外部工具（如未來 Web 介面）參考。

## v2（現行產出格式）

### Byte layout（base64 decode 後）

```
┌──────┬──────┬─────────┬──────────────────────────────┐
│ 0x42 │ 0x4D │  0x02   │  UTF-8 payload bytes         │
└──────┴──────┴─────────┴──────────────────────────────┘
  'B'    'M'   version
```

| Offset | Bytes | 內容 |
|---:|---:|---|
| 0 | 1 | `0x42` (`'B'`) — magic byte 1 |
| 1 | 1 | `0x4D` (`'M'`) — magic byte 2 |
| 2 | 1 | `0x02` — wire format version |
| 3 | * | UTF-8 編碼的 payload 字串 |

base64 編碼後的 wire string **永遠以 `Qk0C` 開頭**（即 `0x42 0x4D 0x02` 的 base64）。
肉眼即可辨識版本。

### Payload 格式

```
<base_color>(;<pattern_key>:<color>)*
```

| 欄位 | 來源 | 範例 |
|---|---|---|
| `base_color` | `DyeColor.name()` lowercase | `red`, `white`, `black`, ... |
| `pattern_key` | `Keyed#getKey().getKey()`（NamespacedKey 後綴） | `cross`, `border`, `rhombus`, `creeper`, ... |
| `color` | 同 `base_color` | 同上 |

namespace 永遠是 `minecraft`，wire 上省略。

### 範例

| 描述 | Payload | Wire string |
|---|---|---|
| 純紅旗幟 | `red` | `Qk0CcmVk` |
| 紅底 + 白十字 | `red;cross:white` | `Qk0CcmVkO2Nyb3NzOndoaXRl` |
| 5-pattern 旗幟 | `red;cross:white;border:black;rhombus:yellow;bordure:orange;creeper:green` | ~100 chars |

## v1（保留解析、不再產出）

舊版透過 `BukkitObjectOutputStream` 把 String 包裝後 base64。base64 字串以 `rO0`
開頭（`0xAC 0xED 0x00 0x05` 的 base64）。

內部 dataString：

```
<colorCode>;<patternId>:<colorCode>;...
```

- 色碼為 pre-1.13 wool metadata 數字（0=BLACK ... 15=WHITE）
- pattern 用 `PatternType.getByIdentifier()` 的縮寫（`cr`, `bo`, `ls`, ...）

**v1 字串永遠可解**（backward compat），但 `serialize()` 不再產出 v1。

## Deserialize 分流邏輯

```
base64 decode → 看第 1 byte：
  ├─ 0xAC → v1 路徑（Java OOS unwrap → 數字色碼 + 縮寫 patternId）
  └─ 0x42 → 驗證第 2 byte 為 0x4D → 讀 version byte → v2+ 路徑
```

兩條路徑互不干擾。`0xAC` 與 `0x42` 永遠不會混淆，因為 v2 magic 是固定的。

## 結構化例外

`BannerSerializer.deserialize` 失敗時拋 `BannerDeserializationException` 的子類別：

| 例外 | 觸發條件 |
|---|---|
| `InvalidBannerFormatException` | base64 損壞、空 payload、未知 magic、版本不支援、欄位個數錯誤 |
| `UnknownBannerPatternException` | pattern key 不存在於當前 server（典型：1.21.2+ 字串在 1.21.0 解析） |
| `UnknownBannerColorException` | DyeColor 名稱無法 enum match（v2）或數字超出 0-15（v1） |

玩家可見的 `/bm view` 訊息**故意不依例外型別切換**，一律給通用「無效旗幟字串」
提示，避免揭露 wire format 細節。例外型別主要供：
- 未來 Web API 給結構化 4xx + reason
- plugin 內部 log / debug

## 跨語言實作（為 Web 介面準備）

### 解碼（JavaScript）

```javascript
function parseBannerString(b64) {
  const bytes = Uint8Array.from(atob(b64), c => c.charCodeAt(0));
  if (bytes[0] !== 0x42 || bytes[1] !== 0x4D) {
    throw new Error('Not a v2 BannerMaker string');
  }
  if (bytes[2] !== 0x02) {
    throw new Error(`Unsupported version: ${bytes[2]}`);
  }
  const payload = new TextDecoder('utf-8').decode(bytes.slice(3));
  const [baseColor, ...patternParts] = payload.split(';');
  return {
    baseColor,
    patterns: patternParts.map(p => {
      const [type, color] = p.split(':');
      return { type, color };
    })
  };
}
```

### 編碼（JavaScript）

```javascript
function buildBannerString({ baseColor, patterns }) {
  const payload = [
    baseColor,
    ...patterns.map(p => `${p.type}:${p.color}`)
  ].join(';');
  const utf8 = new TextEncoder().encode(payload);
  const bytes = new Uint8Array(3 + utf8.length);
  bytes[0] = 0x42;
  bytes[1] = 0x4D;
  bytes[2] = 0x02;
  bytes.set(utf8, 3);
  return btoa(String.fromCharCode(...bytes));
}
```

## 限制與不變

- **不存 metadata**：display name、lore、enchantments 不在 wire format 內。若 banner
  有 PersistentData `banner-key`（個人收藏索引），deserialize 後也不會帶回——這是
  預期行為，`view` 取得的旗幟是「拋棄式」，不能用 GUI 內 delete 鈕刪除。
- **跨版本陷阱**：v2 字串若含 1.21.2+ 才有的 pattern key（如 `field_masoned`），
  在 1.21.0 server 解析會拋 `UnknownBannerPatternException`。發布到外部的字串應
  避免使用最新版才有的 pattern。
- **PatternType 二進位相容**：`BannerSerializer.serialize` 內透過 `Keyed` interface
  呼叫 `getKey()`，避開 PatternType 在 1.21.x 期間從 enum 變 interface 的雷區
  （參考 `CLAUDE.md` 中「跨版本相容性陷阱」）。

## 未來方向

### v3 / v4 升級

升 v3 只需把 version byte 從 `0x02` 改為 `0x03`，外觀仍以 `Qk0` 開頭、第 4 個
base64 字元改變（`Qk0D` 代表 v3）。Deserialize 分流邏輯不變。

### Web API 用 JSON wire format

若未來要做 Web 介面或 REST API，base64 形式適合作為「玩家可分享代碼」、不適合
作為 API 主要 schema。建議另外設計 JSON wire format（如下），跟 base64 形式
並行，互相轉換：

```json
{
  "version": 1,
  "baseColor": "RED",
  "patterns": [
    { "type": "minecraft:cross", "color": "WHITE" },
    { "type": "minecraft:border", "color": "BLACK" }
  ]
}
```

JSON 形式對 Web 端最友善：欄位自我描述、錯誤可結構化、不必處理 base64。
plugin 端僅需新增一個 `BannerJsonSerializer` class（與 `BannerSerializer` 平行），
本檔範圍不涵蓋。
