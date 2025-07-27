## A. Napadači i motivacija

| Klasa        | Veština           | Resursi             | Nivo pristupa | Krajnji ciljevi                            |
| ------------ | ----------------- | ------------------- | ------------- | ------------------------------------------ |
| rekreativci  | amateri           | ograničeni          | eksterni      | sticanje reputacije, rešavanje izazova     |
| insajderi    | varira            | ograničeni          | interni       | trgovina podacima, sabotaža                |
| kriminalci   | napredni          | srednji             | eksterni      | trgovina podacima, iznuđivanje, vandalizam |
| aktivisti    | napredni          | srednji             | eksterni      | sabotaža, vandalizam                       |
| teroristi    | napredni/eksperti | obimni              | eksterni      | krađa podataka, targetovanje ljudi         |
| konkurencija | napredni/eksperti | obimni/neograničeni | varira        | sabotaža, špijunaža                        |
| vlade        | eksperti          | neograničeni        | varira        | nadgledanje, krađa podataka, sabotaža      |

## B. Imovina

- pravni rizik
- gubitak novca
- gubitak reputacije

### 1. Podaci o zaposlenima

#### **Inherentna izloženost**:
- HR
- menadžment
- IT odeljenje
- odeljenje finansija

| Bezbednosni cilj | Uticaj oštećenja cilja                                                      |
| ---------------- | --------------------------------------------------------------------------- |
| poverljivost     | šteta na reputaciju, targetiranje zaposlenih (od javnosti ili konkurencije) |
| integritet       | gubitak poverenja zaposlenih                                                |
| dostupnost       | kašnjenje odeljenja finansija (legalne akcije zaposlenih)                   |


### 2. Pristupni podaci zaposlenih i autentikacija za infrastrukturu

#### **Inherentna izloženost**
- zaposleni (svoje podatke)
- administratori sistema/baze
- DevOps (infrastruktura, API ključevi, secrets)

(1) **Gubitak poverljivosti ove imovine može da za rezultat ima kompromitovanje većine ostale ugrožene imovine** (šteta zavisi od nivoa pristupa zaposlenog čiji pristupni podaci su ukradeni).

| Bezbednosni cilj | Uticaj oštećenja cilja                                                       |
| ---------------- | ---------------------------------------------------------------------------- |
| poverljivost     | (1)                                                                          |
| integritet       | stopiranje rada zaposlenih, onesposobljavanje infrastrukture (secrets, APIs) |
| dostupnost       | isto                                                                         |


### 3. Lični podaci i kredencijali klijenata

#### **Inherentna izloženost**:
- klijenti (svoje podatke)
- zaposleni koji asistiraju klijentima (agenti, podrška, odeljenje finansija)
- administratori sistema/baze

| Bezbednosni cilj | Uticaj oštećenja cilja                          |
| ---------------- | ----------------------------------------------- |
| poverljivost     | značajna šteta na reputaciju, gubitak klijenata |
| integritet       | stopiranje korišćenja usluga sistema            |
| dostupnost       | isto                                            |


### 4. Rizična intelektualna svojina i poslovne tajne

#### **Inherentna izloženost**:
- programeri i DevOps (izvorni kod aplikacije)
- menadžment
- odeljenje finansija
- administratori sistema/baze

| Bezbednosni cilj | Uticaj oštećenja cilja                                                                       |
| ---------------- | -------------------------------------------------------------------------------------------- |
| poverljivost     | značajna finansijska šteta na duže staze, gubitak prednosti nad konkurencijom, pravni rizici |
| integritet       | gubitak poverenja partnera i korisnika                                                       |
| dostupnost       | pravni rizici (gubitak dokumenata), stopiranje poslovnog razvoja                             |


### 5. Tehnička infrastruktura

#### **Inherentna izloženost**:
- administratori sistema
- DevOps

| Bezbednosni cilj | Uticaj oštećenja cilja                                                                            |
| ---------------- | ------------------------------------------------------------------------------------------------- |
| poverljivost     | rizik od napada na infrastrukturu                                                                 |
| integritet       | onesposobljivanje sistema (uživo), smanjivanje performansi korišćenja sistema (denial of service) |
| dostupnost       | isto                                                                                              |


## C. Površina napada

### Ljudski faktori

- klijenti
  - phishing (lažni emailovi, lažna klijentska podrška)
  - slabe lozinke (kratke, lako se pogode)

- zaposleni
  - phishing (lažni interni emailovi)
  - društveni inženjering (intelektualna svojina kompanije, poverljive informacije)
  - zloupotreba radnog mesta (privilegije, insider pretnje)

- spoljni partneri
  - pristup poslovnim tajnama (ugovori, strategije poslovanja)

### Digitalni faktori

- eksterni softver i servisi
  - hosting (serveri)
  - baze podataka
  - nesigurni logovi
  - komunikacija (email, chatovi)
  - finansije (platni procesori)

- aplikacioni softver
  - nezaštićeni servisi (backend)
  - web aplikacija (XSS, IDOR, iframe phishing)
  - interna baza podataka (nekriptovani podaci, SQL injection)
  - mane u autentikaciji i autorizaciji

- mrežni pristup (VPN)

### Fizički faktori

- lokacija (kancelarije, serveri, interni dokumenti)
- oprema zaposlenih (radne stanice, telefoni, USB)
- WiFi zaposlenih (udaljeni rad)
