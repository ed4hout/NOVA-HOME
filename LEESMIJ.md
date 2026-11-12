# NOVA Home voor Sony Bravia (Google TV)

NOVA wordt hier een Android-app. Hij leest zelf je geïnstalleerde apps uit, met hun echte logo's, en ook je HDMI-ingangen.

## 1. APK downloaden
Bij elke wijziging bouwt GitHub de APK automatisch. De nieuwste versie staat altijd hier:

https://github.com/ed4hout/NOVA-HOME/releases/latest/download/nova-home.apk

Zelf bouwen kan ook: open deze map in Android Studio en kies **Build → Build APK(s)**.

## 2. Op de tv zetten
1. Zet op de tv de ontwikkelaarsopties aan: ga naar **Instellingen → Systeem → Over** en klik 7 keer op **OS-build van Android TV**.
2. Installeer op de tv de app **Downloader** (van AFTVnews) uit de Play Store.
3. Typ in Downloader de link hierboven. Sta toe dat Downloader apps mag installeren, en kies **Installeren**.

Alternatief: download de APK op je telefoon en stuur hem naar de tv met de app **Send files to TV**.

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
