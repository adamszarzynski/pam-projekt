# Projekt Semestralny Programowanie aplikacji mobilnych

Aplikacja mobilna na system Android służąca do tworzenia osobistej bazy ulubionych miejsc. Pozwala użytkownikowi na zapisywanie lokalizacji wraz ze zdjęciem, opisem oraz danymi GPS, a także na ich późniejsze przeglądanie, edycję i udostępnianie.


**Główne funkcjonalności:**
* Dodawanie nowych miejsc z tytułem i opisem.
* Wykonywanie zdjęć miejsc bezpośrednio w aplikacji.
* Automatyczne pobieranie lokalizacji (współrzędne GPS + adres z Geocodera).
* Przeglądanie listy zapisanych miejsc z możliwością wyszukiwania.
* Szczegółowy podgląd miejsca z opcją otwarcia lokalizacji w Google Maps.
* Edycja i usuwanie wpisów.
* Udostępnianie informacji o miejscu innym aplikacjom (e-mail, SMS, komunikatory).



## Wykorzystane sensory i funkcje urządzenia

Zgodnie z wymaganiami projektowymi, aplikacja wykorzystuje następujące źródła danych i funkcje systemowe:

1.  **Aparat Fotograficzny (Camera)**
    * Wykonywanie zdjęć dodawanych miejsc.
    * Obsługa uprawnień `android.permission.CAMERA`.
    * Zapis zdjęć do pamięci wewnętrznej aplikacji.

2.  **Lokalizacja GPS (Location Services)**
    * Pobieranie aktualnych współrzędnych geograficznych (Latitude/Longitude) za pomocą `FusedLocationProviderClient`.
    * Wykorzystanie **Geocodera** do zamiany współrzędnych na czytelny adres zamieszkania/pobytu.
    * Obsługa uprawnień `ACCESS_FINE_LOCATION`.

3.  **Pamięć Urządzenia (Storage & Persistence)**
    * Zapisywanie bazy danych miejsc w formacie JSON (`places_database.json`) w prywatnym katalogu aplikacji.
    * Zapisywanie i odczyt plików graficznych (zdjęć) z pamięci urządzenia.

4.  **Mechanizm Udostępniania (Sharing Intent)**
    * Generowanie systemowej intencji (`Intent.ACTION_SEND`) pozwalającej na wysłanie zdjęcia i opisu miejsca przez zewnętrzne aplikacje (np. Gmail, SMS).

5.  **Integracja z Mapami**
    * Wywoływanie zewnętrznej aplikacji map (np. Google Maps) w celu nawigacji do zapisanego punktu.


## Zrzuty Ekranu

<p align="center">
  <img width="366" height="818" alt="image" src="https://github.com/user-attachments/assets/5bedea28-6ad0-45c4-be59-f775e18f36eb" />
  <img width="366" height="818" alt="image" src="https://github.com/user-attachments/assets/74a4117b-eda2-4717-99c7-75480228224b" />
</p>

<p align="center">
  <img width="366" height="818" alt="image" src="https://github.com/user-attachments/assets/425b948a-8a9f-47e6-87ba-31fba46042ae" />
  <img width="366" height="818" alt="image" src="https://github.com/user-attachments/assets/39a03559-65e1-4a2d-8369-efe78bb3d6a6" />
</p>


## Technologie i Architektura

Aplikacja została napisana w języku **Kotlin** z wykorzystaniem nowoczesnych narzędzi Android Jetpack:

* **UI:** Jetpack Compose (deklaratywny interfejs użytkownika).
* **Nawigacja:** Navigation Compose (Type-safe routes).
* **Architektura:** MVVM (Model-View-ViewModel). Logika biznesowa wydzielona w `PlaceViewModel`.
* **Biblioteki:**
    * `Coil` - ładowanie i wyświetlanie obrazów.
    * `Gson` - serializacja danych do formatu JSON.
    * `Coroutines` - obsługa operacji asynchronicznych (zapis plików, obsługa GPS).



## Instrukcja Uruchomienia

1.  **Wymagania:**
    * Android Studio (najnowsza wersja rekomendowana).
    * Urządzenie fizyczne lub emulator z systemem Android (Min SDK 24).
    * Włączona obsługa GPS w urządzeniu (dla funkcji lokalizacji).

2.  **Kroki instalacji:**
    * Sklonuj repozytorium: `git clone <adres-repozytorium>`
    * Otwórz projekt w Android Studio.
    * Poczekaj na synchronizację projektu Gradle.
    * Uruchom aplikację przyciskiem (Run).

3.  **Uprawnienia:**
    * Przy pierwszym użyciu funkcji aparatu lub GPS, aplikacja poprosi o nadanie stosownych uprawnień. Należy je zaakceptować, aby funkcje te działały poprawnie.

---

**Autor:** [Adam Szarzyński]
