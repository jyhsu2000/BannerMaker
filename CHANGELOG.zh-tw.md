# 更新紀錄
## 未釋出版本
- 暫無

## v2.1.1 (for 1.13.x)
- 修正合成旗幟時缺少染料的錯誤

## v2.1.0 (for 1.13.x)
- 新增簡易預覽模式（可於「建立旗幟」選單切換）
  - https://imgur.com/a/ydF8frS

## v2.0.1 (for 1.13.x)
- 修正材料的排序
- 清理程式碼

## v2.0.0 (for 1.13.x)
- 升級至1.13.2
- 不再支援1.8.x ~ 1.12.x

## v1.9.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)
- 優化程式碼
- 現在只支援 Java 8
- 更新西班牙文(es)翻譯（感謝 [C4BR3R4](https://www.spigotmc.org/members/c4br3r4.26779/)）

## v1.8.1 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)
- 優化程式碼
- 完成新的靈活的指令系統

## v1.8.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)
- 現在玩家可以使用材料合成旗幟
- 如果玩家擁有`BannerMaker.getBanner.free`權限，他將只能看到`取得旗幟`按鈕，購買及合成按鈕將不會出現
- 更新物品欄選單系統（現在大多數按鈕只接受左鍵點擊）

## v1.7.2 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)
- 新增匈牙利文(hu)翻譯（感謝 [montlikadani](https://www.spigotmc.org/members/toldi.251100/)）

## v1.7.1 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)
- 新增西班牙文(es)翻譯（感謝 [stevejone1997](https://www.spigotmc.org/members/stevejone1997.432373/)）

## v1.7.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x, 1.12.x)
- 升級至1.12
- 修正需要重新載入兩次以套用新語言的錯誤

## v1.6.3 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)
- 新增荷蘭文(nl)翻譯（感謝 [DeTrollers](https://www.spigotmc.org/members/detrollers.174265/)）

## v1.6.2 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)
- 新增葡萄牙文(pt-br)翻譯（感謝 [Rhander](https://www.spigotmc.org/members/rhander.103119/)）

## v1.6.1 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)
- 以更準確的方式識別GUI按鈕
- 優化選單切換流程

## v1.6.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)
- 新指令
  - /bm help: 指令清單
  - /bm hand: 顯示手上持有的旗幟的資訊
  - /bm see: 顯示看著的旗幟的資訊
- 重新撰寫指令系統，並加上Tab自動完成
- 移除用於從v1.0或v1.1更新的旗幟資料更新功能

## v1.5.0 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)
- 令字母與數字選單可切換開關
- 改進價格設定
- 優化程式碼
- 分割 README 和 CHANGELOG
- 使用 [bStats](https://bstats.org/plugin/bukkit/BannerMaker) 代替 MCStats

## v1.4.6 (for 1.8.x, 1.9.x, 1.10.x, 1.11.x)
- 升級至1.11
- 新增俄文(ru)翻譯（感謝 [sdir01](https://www.spigotmc.org/members/sdir01.238854/)）

## v1.4.5 (for 1.8.x, 1.9.x)
- 修正「Title cannot be longer than 32 characters」例外

## v1.4.4 (for 1.8.x, 1.9.x)
- 優化程式碼
- 防止「Title cannot be longer than 32 characters」例外
- 新增 Plugin Metrics

## v1.4.3 (for 1.8.x, 1.9.x)
- 新增法文(fr)翻譯（感謝 [RedNesto](https://github.com/RedNesto)）
- 更新有框字母D和R的合成表
- 更新語系檔檢查機制
- 檢查材料是否足夠，並顯示於旗幟資訊頁面

## v1.4.2 (for 1.8.x, 1.9.x)
- 更新有框字母S的合成表
- 新增德文(de)翻譯（感謝 [Marrarus](https://github.com/Marrarus)）
- 優化程式碼

## v1.4.1 (for 1.8.x, 1.9.x)
- 新增字母與數字選單
- 新增合成材料估算
- 令GUI標題前綴可編輯
- 修正黑色旗幟的羊毛顏色錯誤
- 優化效能

## v1.4 (for 1.8.x, 1.9.x)
- 升級至1.9.2
- 移除舊的預覽模式（旗幟圖標將不會再消失）
- 令訊息前綴可編輯
- 新增「複製並編輯」按鈕

## v1.3.2 (for 1.8.x)
- 移除非必要之除錯訊息
- 若包包已滿，取得旗幟時，旗幟將會掉在地上
- 嘗試修復IndexOutOfBoundsException
- 在建立旗幟頁面新增切換預覽模式的按鈕

## v1.3.1 (for 1.8)
- 修正一些錯誤

## v1.3 (for 1.8)
- 新增支援經濟功能（需要Vault）

## v1.2 (for 1.8)
- 支援UUID
- 多國語言  
（在config.yml設定，目前支援英文(en)、正體中文(zh-tw)、簡體中文(zh-cn)）

## v1.1 (for 1.8)
- 旗幟資料移至「banner」資料夾
- 更清晰、更小的資料格式  
（將所有 &lt;玩家&gt;.yml 移至「banner」資料夾，它將會自動更新到新格式）

## v1.0 (for 1.8)
- 第一次釋出
