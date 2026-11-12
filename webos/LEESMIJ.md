# NOVA Home – installeren op je LG G5

## Eenmalig voorbereiden
1. Maak een gratis account aan op https://webostv.developer.lge.com en log daarmee in op de tv.
2. Installeer op de tv de app **Developer Mode** uit de LG Content Store. Open hem, zet **Dev Mode Status** aan en laat de tv opnieuw opstarten.
3. Open Developer Mode opnieuw en zet **Key Server** aan. Noteer het IP-adres dat je ziet.
4. Installeer op je laptop Node.js (https://nodejs.org) en daarna de LG-tools:
   `npm install -g @webos-tools/cli`
5. Koppel de tv:
   `ares-setup-device` → kies *add*, geef als naam `tv` en vul het IP-adres in.
   `ares-novacom --device tv --getkey` → vul de passphrase in die de tv toont.

## Installeren
Open een terminal in de map **boven** de map `nova-home` en voer uit:
```
ares-package nova-home
ares-install --device tv nl.nova.home_1.0.0_all.ipk
```
NOVA Home verschijnt nu bij je apps. Verplaats hem naar het begin van de rij met apps.

## Aanpassen (bovenaan in index.html)
- `PLAATS`: jouw woonplaats met coördinaten, voor het weer.
- `APPS`: de streamingtegels. Een app die niet op de tv staat wordt automatisch gedimd weergegeven.
- `INPUTS`: Live TV, HDMI 1 t/m 4, browser en instellingen.
Daarna opnieuw `ares-package` en `ares-install` uitvoeren.

## Goed om te weten
- De ontwikkelaarsmodus loopt na een tijd af. Open de app Developer Mode en druk op **Extend** om hem te verlengen. Loopt hij toch af, dan verdwijnt NOVA en installeer je hem opnieuw.
- Het startscherm van LG zelf blijft bestaan. NOVA is een app die je met één klik opent.
