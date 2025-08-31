## Path traversal

**Path traversal** je tip napada na server prilikom kog napadač pomoću specijalnih nizova poput `../` (Unix) ili `..\` (Windows) izađe iz predviđenog direktorijuma na serveru i pristupi fajlovima van dozvoljene strukture. Napadač potencijalno može čitati osetljive fajlove, poput konfiguracija, kodova, kredencijala ili operativnih fajlova, a u nekim slučajevima i pisati u fajlove i tako preuzeti kontrolu nad serverom.

### Uticaji eksploatacije:

- poverljivosti: napadač može da pristupi osetljivim fajlovima poput `/etc/passwd`, fajlovima sa lozinkama, konfiguracijama itd.
- integritet: u situacijama kada endpoint dozvoljava pisanje/čuvanje fajlova, napadač može da izmeni fajlove i utiče na izvršavanje aplikacije
- dostupnost: pisanje, menjanje i brisanje fajlova može da omogući napadaču da onesposobi sistem

### Ranjivosti koje omogućavaju napad:

- nedostatak validacije korisničkog ulaza u svrhu sprečavanja nedozvoljenih karaktera
- neispravno blokiranje traversal sekvenci: `../` `....//` `...\/` `%2e%2e/` `%252e%252e%252f`
- najznačajniji propust koji omogućuje ovaj napad je korišćenje korisničkog ulaza za pristupanje fajlovima; gotovo uvek je moguće zameniti ovo korišćenjem server generisane putanje kojoj se pristupa kroz bazu

### Kontramere

- referenciranje potrebnih fajlova po index-u ili delu putanje
- korišćenje generisanih putanja koje su asocirane uz podatke o fajlu (recimo u bazi)
- whitelist validacija ulaza - samo dozvoljeni karakteri
- kanonizacija i verifikacija putanje gde se proverava čime putanja počinje
- izolovanje fajlova putem chroot (Unix) ili logičkog diska (Windows)

## CSRF

**Cross Site Cross-site request forgery** je napad u kojem napadač navede već prijavljenog korisnika da nenamerno izvrši neku akciju na web aplikaciji (npr. promeni lozinku, pošalje formular, izvrši transakciju). Problem nastaje jer browser automatski šalje kolačiće sesije zajedno sa zahtevima, pa aplikacija ne razlikuje da li zahtev dolazi od korisnika ili od napadača.je tip napada koji omogućava napadaču da navede korisnike da izvrše neke akcije koje ne bi želeli.

### Uticaji eksploatacije:

- promena korisničkog naloga (email adrese, lozinke).
- neželjene transakcije (npr. transfer novca, poručivanje robe)
- administrativna eskalacija – ako napadač prevari administratora, može dobiti potpunu kontrolu nad aplikacijom ili korisnicima
- kombinovani napadi – CSRF se može koristiti zajedno sa XSS-om ili drugim ranjivostima za širu kompromitaciju

### Ranjivosti koje omogućavaju napad:

- oslanjanje samo na kolačiće za autentifikaciju (browser ih automatski šalje)
- korišćenje nesigurnih metoda (GET) za akcije koje menjaju stanje
- nedostatak anti-CSRF tokena u formama ili zahtevima
- neproveravanje Origin ili Referer zaglavlja
- loša validacija tokena (npr. token nije vezan za sesiju ili se može pogoditi)

### Kontramere

- CSRF tokeni:
    - u svaki POST/PUT/DELETE zahtev ubaciti jedinstven, nasumičan token vezan za korisnikovu sesiju
    - server mora da proveri token i odbaci zahtev ako nije validan

- double Submit Cookie pattern:
    - token se postavlja i u kolačić i u skriveno polje/formu ili header. Server proverava da li se oba poklapaju

- sameSite atribut za kolačiće:
    - podešavanje SameSite=Strict ili SameSite=Lax sprečava automatsko slanje kolačića u cross-site zahtevima

- provera zaglavlja `Origin` i `Referer`:
    - server može odbaciti zahtev ako ne dolazi sa očekivane domene. Nije savršeno, ali dodaje dodatni sloj zaštite

- korišćenje sigurnih metoda:
    - akcije koje menjaju stanje (npr. promena lozinke, slanje novca) moraju koristiti POST (ne GET)
