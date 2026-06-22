# NutriScanner 🍏

Barkod tarama yoluyla gıdaların besin analizlerini ve ekolojik skorlarını (Eco-Score) sunan Android mobil uygulaması.

## 🚀 Projeyi Bilgisayarınızda Test Etme Adımları

Projeyi yerelde sorunsuz çalıştırmak ve test etmek için aşağıdaki adımları takip edebilirsiniz:

1. **Projeyi İndirin:** Sağ üstteki yeşil `Code` butonuna tıklayıp `Download ZIP` diyerek projeyi bilgisayarınıza indirin ve klasöre çıkartın.
2. **Android Studio ile Açın:** Android Studio'yu açın, `File -> Open` adımlarını takip ederek `my-portfolio` klasörü altındaki `NutriScanner` klasörünü seçin.
3. **Gradle Senkronizasyonu:** Proje açıldığında Android Studio'nun Gradle bağımlılıklarını indirmesini bekleyin (Sağ alttaki yükleme barının bitmesini bekleyin).
4. **API Kontrolü (Önemli):** Uygulama, gıda verilerini çekmek için açık kaynaklı **OpenFoodFacts API**'sini kullanmaktadır. Test etmek için aktif bir internet bağlantınızın olduğundan emin olun.
5. **Çalıştırma:** Bir emülatör (Sanal cihaz) veya gerçek bir Android cihaz bağlayarak üst menüdeki yeşil **Run (Oynat)** butonuna basıp uygulamayı başlatabilirsiniz.

## 🛠️ Kullanılan Teknolojiler
* **Dil:** Kotlin
* **Mimari:** MVVM / Clean Architecture
* **Kütüphaneler:** Retrofit (API entegrasyonu), Room Database, ViewBinding
