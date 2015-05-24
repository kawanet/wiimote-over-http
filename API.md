## Request URL ##
http://localhost:8080/WiimoteOverHTTP/execute?[parameters]
## parameters ##
| **parameter key** | **value type** | **required** | **default value** | **note** |
|:------------------|:---------------|:-------------|:------------------|:---------|
| method            | string         | ○          |　                | specified method　(※refer to details)　---　メソッドを指定します。(※メソッド一覧参照) |
| wiimote           | integer        | △          |　                | specified wiimote number　---　Wii リモコンの番号を指定します。 |
| button            | string         | △          |　                | specified button　(※refer to details)　---　Wii リモコンのボタンを指定します。 |
| time              | integer        | △          |　                | specified time (milli sec)　---　実行時間をミリ秒で指定します。 |
| light             | string         | △          |　                | specified LED pattern (comma delimited 4 numbers [0:off 1:on])　---　LED の点灯パターンを 0：消灯 と 1：点灯 のカンマ区切りで指定します。 |
| responseType      | string         | ○          | xml               | "xml" or "json"　---　レスポンスの形式を xml か json で指定します。 |
| callback          | string         | △          |　                | The response becomes JSONP by specifying the callback function name when "responseType=json".　---　responseType が json の状態でこのパラメータを指定すると、指定されたコールバック関数名の JSONP 形式のレスポンスになります。 |

### details ###
**method**
| **parameter value** | **note** | **indispensable parameters** |
|:--------------------|:---------|:-----------------------------|
| findWiimote         | Wiiリモコン探索・接続 |　                           |
| isConnected         | Wiiリモコン接続状況取得 | wiimote                      |
| setVibrate          | バイブレーション操作 | wiimote, time                |
| isPressed           | ボタン押下状況取得 | wiimote, button              |
| getStatus           | Wiiリモコン操作情報取得 | (wiimote)                    |
| getInfo             | Wiiリモコン情報取得 | (wiimote)                    |
| setLED              | LED点灯・消灯操作 | wiimote, time, light         |
| releaseWiimote      | Wiiリモコン切断 | (wiimote)                    |

**button**
| **parameter value** | **note** |
|:--------------------|:---------|
| ONE                 | button　"1" |
| TWO                 | button　"2" |
| A                   | button　"A" |
| B                   | button　"B" |
| HOME                | button　"HOME" |
| PLUS                | button　"+"|
| MINUS               | button　"－" |
| UP                  | button　"↑" |
| DOWN                | button　"↓" |
| RIGHT               | button　"→" |
| LEFT                | button　"←" |
| C                   | nunchuk button　"C" |
| Z                   | nunchuk button　"Z" |