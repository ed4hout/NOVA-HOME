# NOVA Home op een LG-tv (webOS)

GitHub bouwt de LG-versie automatisch mee. De nieuwste staat altijd hier:
https://github.com/ed4hout/NOVA-HOME/releases/latest/download/nova-home.ipk

## Eenmalig: ontwikkelaarsmodus op de tv
1. Maak een gratis account aan op https://webostv.developer.lge.com.
2. Installeer op de tv de app **Developer Mode** uit de LG Content Store. Log in, zet **Dev Mode Status** aan en laat de tv opnieuw opstarten.
3. Open Developer Mode opnieuw, zet **Key Server** aan en noteer het IP-adres.

## Installeren vanaf je laptop (makkelijkst: webOS Dev Manager)
1. Installeer **webOS Dev Manager** (gratis, Windows/Mac/Linux): https://github.com/webosbrew/dev-manager-desktop/releases
2. Voeg je tv toe met het IP-adres en de passphrase die Developer Mode toont.
3. Download `nova-home.ipk` via de link hierboven en sleep hem in webOS Dev Manager, of kies **Install**.
4. Zet NOVA Home vooraan in je rij met apps.

Alternatief met de LG-tools op de opdrachtregel:
```
npm install -g @webos-tools/cli
ares-setup-device            # tv toevoegen
ares-novacom --device tv --getkey
ares-install --device tv nova-home.ipk
```

## Goed om te weten
- De ontwikkelaarsmodus loopt na een tijd af. Verleng hem in de app Developer Mode met **Extend**; webOS Dev Manager kan dat ook automatisch.
- Automatisch opstarten bij aanzetten en zelf bijwerken kunnen op LG niet: dat staat webOS niet toe voor zelf geïnstalleerde apps.
