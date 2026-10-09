# Rüya Boyutu — Fabric 1.21.11 (başlangıç projesi)

Bu ZIP, modun ilk kodlama aşaması için bir Fabric proje iskeletidir. Henüz bütün özellikleri içeren bitmiş bir mod değildir.

## Gerekenler
- Minecraft Java Edition 1.21.11
- Java Development Kit (JDK) 21
- IntelliJ IDEA Community (önerilir)
- Fabric Loader ve Fabric API

## Derleme
1. ZIP'i bir klasöre çıkar.
2. Klasörü IntelliJ IDEA ile aç.
3. Gradle eşitlemesinin bitmesini bekle.
4. Terminalde Windows için `gradlew.bat build`, macOS/Linux için `./gradlew build` çalıştır.
5. Başarılı olursa `build/libs/ruya-boyutu-0.1.0.jar` dosyası oluşur.
6. Bu JAR'ı Minecraft 1.21.11 Fabric profilinin `mods` klasörüne koy. Fabric API de `mods` klasöründe olmalı.

## Şu anki prototipte
- Safir, ham safir, paladyum hurdası, paladyum, Ark Reaktörü ve Kloroful eşyaları kaydedilir.
- Paladyum zırhını giyme süresi takip edilir; 11 dakikada bir Paladyum zehirlenmesi başlatır.
- Reaktör oyuncunun ana envanterindeki özel eşyaysa, canı 3 kalbin altına indiğinde 100 HP'lik koruma etkinleşir. Bu ilk prototipte özel GUI bölmesi yerine geçici olarak envanterde aranır.
- Kloroful kullanımı Paladyum zehirlenmesini 15 dakika bastırır.
- Bu sürüm henüz özel boyutları/biyomları, özel kalp HUD'unu, zırh modellerini, cevher dünya üretimini, özel tariflerin tamamını veya gece 03.00 uyku geçişini tamamlamaz. Bunlar sonraki aşamalarda eklenmelidir.

Not: Minecraft/Fabric sürümleri değiştikçe derleme ayarları güncellenebilir.


## Nightmare eklemeleri (taslak)
- Kâbus Odunu ve Kâbus Tahtası blokları ve 16×16 özel dokuları eklendi.
- `nightmare_scream.ogg` adlı özgün, sentetik korku sesi eklendi.
- Ses tetikleyicisi `ruya_boyutu:nightmare` boyut kimliğini bekler. Nightmare boyutu henüz tanımlanmadığı için boyut eklenmeden ses oyunda tetiklenmez.
- Herobrine ve Steve silüetlerinin gerçek özel varlık/render sistemi bu sürümde henüz eklenmedi; bu bir sonraki kodlama aşaması.
