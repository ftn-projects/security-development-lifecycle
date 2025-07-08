# C. Multifaktorska autentikacija

## Kategorije

### Znanje

| Tip                 | Prednosti     | Nedostaci                                         |
| ------------------- | ------------- | ------------------------------------------------- |
| Lozinka             | univerzalnost | ponavljanje, phishing, brute-force                |
| PIN                 | lako se pamti | lako se napada brute-force (ograničena entropija) |
| Bezbednosna pitanja | jednostavnost | može se pogoditi i mogu biti javne informacije    |

### Posedovanje

| Tip                             | Prednosti                   | Nedostaci                                                         |
| ------------------------------- | --------------------------- | ----------------------------------------------------------------- |
| Hardverski tokeni               | imuni na malware i phishing | zahtevaju posebne uređaje, skupi                                  |
| Authenticator aplikacije (TOTP) | lako se podešavaju (QR kod) | malware, krađa telefona, sinhronizacija vremena                   |
| SMS                             | nema podešavanja            | zamena SIM kartice, presretanje, nekriptovan transport            |
| Email                           | pristupačnost               | jedinstvena tačka otkaza, presretanje u transportu (ako nema TLS) |

### Biometrija (inherence)

| Tip                                                 | Prednosti          | Nedostaci                                   |
| --------------------------------------------------- | ------------------ | ------------------------------------------- |
| Otisak prsta, prepoznavanje lica, dužice oka, glasa | unikatnost, brzina | mogućnost greške, falsifikacija, privatnost |

### Lokacija

| Tip                    | Prednosti                        | Nedostaci                     |
| ---------------------- | -------------------------------- | ----------------------------- |
| pronalaženje IP adrese | javni podatakž                   | IP falsifikacija              |
| GPS podaci             | korišćenje na mobilnim uređajima | GPS falsifikacija, privatnost |

Nedostaci takođe uključuju mogućnost blokiranja prilikom legitimnog putovanja i udaljenog rada.

## MFA zahtevi (lozinka i biometrija)

### Implementacija

Odabrani tipovi MFA su: lozinka i biometrija. Budući da je rukovanje lozinkama druga tema, fokus je na implementaciji biometrije.

Za autentikaciju putem biometrije je preporučeno korišćenje **WebAuthn** API koji je razvijen od strane W3C. **WebAuthn** podržava brojne vidove passwordless autentikacije u kojima je između ostalih **platform autentikator** koji može da podrazumeva autentikaciju na laptopu (kamera, fingerprint) ili mobilnom uređaju. U slučaju novijih Windows uređaja, većina ima podršku za **Windows Hello** koji pored autentikacije PIN-om podržava i biometriju.

Prilikom prve prijave korisnika sa uređaja, vrši se registracija — autentikator kreira par ključeva (privatni i javni), pri čemu se javni ključ šalje serveru i čuva na bezbedan način.

Prilikom kasnijeg logovanja:
- Server kreira jedinstveni challenge i šalje ga klijentu.
- Autentikator lokalno potpisuje challenge privatnim ključem.
- Potpisani odgovor se vraća serveru, koji proverava potpis koristeći prethodno sačuvani javni ključ.

### Najčešće greške i nedostaci

- **Nepostojanje fallback metode**. Ukoliko korisnik izgubi uređaj ili senzor prestane da radi, on mora da ima recovery metodu. Mnogi servisi implementiraju backup kodove kojima je moguće pristupiti zaključanom nalogu.
- **Liveness detekcija**. Biometrijske podatke je moguće falsifikovati (slikama, snimcima glasa) i zato treba koristiti autentikatore sa ugrađenom liveness detekcijom (Windows Hello, Face ID).
- **Recikliranje "challenge"**. U slučaju da server ne menja "challenge" za svaki zahtev, napadači mogu da ga koriste i zloupotrebe.
- **Prenos i skladištenje biometrijskih podataka na mreži**. Ozbiljan problem privatnosti koji može da ima pravne posledice. Koristiti WebAuthn koji nikada ne prenosi biometriju.

### ELK integration

Kibana nema ugrađen mehanizam za MFA i biometriju ali ima opciju za registraciju Identity Provider-a. Kako bi ovaj web servis bio kvalifikovan da bude IdP, mora da implementira neki od dva standarda: **OpenID Connect** ili **SAML**. U slučaju implementacije OIDC, server će morati da ima endpointe za:
- /.well-known/openid-configuration
- /authorize - vraća autorizacioni kod
- /token - razmena autorizacionog koda za ID token

Kibana se tada ponaša kao OIDC klijent: korisnika preusmerava na tvoj /authorize endpoint, a zatim verifikuje ID token koji dobije putem /token poziva.
