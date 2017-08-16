# YouBike 開放資料轉換為 KML 檔 
YouBike 是台灣的一種公共自行車, 租車或還車必須在指定的地點完成.
*其它語言: [English](README.md)

本工具可從[開放資料](http://data.taipei/opendata/datalist/datasetMeta?oid=8ef1626a-892a-4218-8344-f7ac46e1aa48)
取出站點資料, 並儲存為 [KML](https://zh.wikipedia.org/zh-tw/KML) 檔案, KML 檔案可在地圖相關服務中使用.

#### 如何使用?
本工具以 java 撰寫, 它需要 JRE 1.5 或更高的版本.
- 若 .jar 檔案已經與 javaw 建立關聯, 執行 YoubikeKML.jar 即會顯示圖形介面. 
- 在命令列下執行 "java -jar YoubikeKML.jar" 也會顯示圖形介面. 
- 也可以執行 "java -jar YoubikeKML.jar -h" 查看支援的命令列選項.

**output** 資料夾下有一些轉換好的 .kml 可供參考.