# NOVA Home voor Sony Bravia (Google TV)

NOVA wordt hier een Android-app. Hij leest zelf je geïnstalleerde apps uit, met hun echte logo's, en ook je HDMI-ingangen.

## 1. APK bouwen

### Met Android Studio (laptop)
1. Installeer Android Studio (gratis): https://developer.android.com/studio
2. Pak deze zip uit. Kies in Android Studio **Open** en selecteer de map `nova-android`.
3. Wacht tot onderin "Gradle sync" klaar is. De eerste keer downloadt hij een paar onderdelen. Klik op **Install** als hij om een SDK vraagt.
4. Kies in het menu **Build → Build App Bundle(s) / APK(s) → Build APK(s)**.
5. Klik in de melding die verschijnt op **locate**. Je APK heet `app-debug.apk`.

### Zonder laptop: via GitHub
1. Maak een gratis GitHub-account en een nieuwe repository aan. Upload de inhoud van `nova-android`, inclusief de verborgen map `.github`.
2. Onder **Actions** wordt de APK automatisch gebouwd.
3. Open de laatste run en download onderaan **nova-home-apk**.

## 2. Op de tv zetten
1. Zet op de tv de ontwikkelaarsopties aan: ga naar **Instellingen → Systeem → Over** en klik 7 keer op **OS-build van Android TV**.
2. De makkelijkste route is de app **Send files to TV**. Installeer die op je telefoon én op de tv, en stuur de APK naar de tv.
3. Open de APK op de tv. Sta toe dat die app onbekende apps mag installeren, en kies **Installeren**.

Alternatief met ADB op een laptop: zet in de ontwikkelaarsopties **USB-foutopsporing** aan en voer uit:
```
adb connect <IP-adres van de tv>
adb install app-debug.apk
```

## 3. Vooraan zetten
Ga op het startscherm van Google TV naar **Apps** en houd OK ingedrukt op NOVA Home. Kies **Verplaatsen** en zet hem helemaal vooraan, of zet hem bij je favorieten.

## Aanpassen
De weergave staat in `app/src/main/assets/index.html`. Daarin pas je bovenaan aan:
- `NAAM`: de naam in de begroeting.
- `PLAATS`: je woonplaats voor het weer.
- `APPS`: de streamingtegels. NOVA zoekt ze op pakketnaam (`pkg`) of op naam.

Bouw daarna opnieuw en installeer over de oude versie heen.

## Goed om te weten
- De **Terug**-knop doet niets in NOVA. Met **Home** ga je naar Google TV.
- Het startscherm van Google TV zelf kun je niet vervangen. NOVA is een app die je vooraan zet.
- Ingangen en Live TV gebruiken standaardfuncties van Android TV. Werkt een tegel niet, laat dan weten welke.
