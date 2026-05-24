# Pathfinder 2e Character List

## Почему не всё переведено
pf2.ru очень сильно просит не парсить их сайт, а открытого API там нет\
Перевели всё, что успели

### Desktop
Run the desktop application: `./gradlew :desktopApp:run`  
Run the desktop **hot reload** application: `./gradlew :desktopApp:hotRun --auto`

### Web Distribution
Build web distribution: `./gradlew :webApp:composeCompatibilityBrowserDistribution`  
Deploy a dir `webApp/build/dist/composeWebCompatibility/productionExecutable` to a web server  

### JS Browser
Run the browser application: `./gradlew :webApp:jsBrowserDevelopmentRun`  

### Wasm Browser
Run the browser application: `./gradlew :webApp:wasmJsBrowserDevelopmentRun`  

