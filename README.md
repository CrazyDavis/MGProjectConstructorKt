# MGProjectConstructorKt
[ ![Download](https://api.bintray.com/packages/water/mgbase/mg-project-constructor-kt/images/download.svg) ](https://bintray.com/water/mgbase/mg-project-constructor-kt/_latestVersion) 
![](https://img.shields.io/badge/language-kotlin-orange.svg)  

一個Android APP專案的基底

包含以下功能的封裝:  
1. 動畫位移相關layout  
2. api request 單獨拉出, 供自訂widget, 不屬於 aty 與 fgt 的地方使用  
3. fragment跳轉 單獨拉出, 供fragment內部有子freagment跳轉使用  
4. activity 跟 fragment 的基底已經封裝好了 api request 跟 fragment跳轉  
5. qrcode 掃描view的封裝(MGZbarView: 接入bga-qrcode-zbar)  
6. 圖片選擇器調用封裝(MGPhotoPickerHelper)  
7. 多國語系(MGLocalManager)  

## 版本:  
0.1.6 - 修復app縮小太久導致頁面被銷毀重新進入 activity 的 onCreate 方法時, 重新設置fragmentManager造成的頁面初始化資訊錯誤  

## 添加依賴  

### Gradle  
compile 'org.mgwater.mgbase:mg-project-constructor-kt:{version}'  
( 其中 {version} 請自行替入此版號 [ ![Download](https://api.bintray.com/packages/water/mgbase/mg-project-constructor-kt/images/download.svg) ](https://bintray.com/water/mgbase/mg-project-constructor-kt/_latestVersion) )
