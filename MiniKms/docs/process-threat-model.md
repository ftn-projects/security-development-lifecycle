## Process threat model

### Authentication / authorization

#### Spoofing

- napadač bi mogao da lažira JWT tokene ili koristi istekle
- odbrana: validacija *issuer* i uvođenje *revoked* liste

#### Tampering

- nesigurno podešavanje `jwt.secret` može omogućiti napadačima da ubace svoj *secret*
- odbrana: u produkciji bi secret trebalo da se podešava sigurnim putem (kao kroz env varijable)

#### Repudiation

- korisnik može da negira da je izvršio neke operacije zbog nedostatka informacije o identitetu korisnika u logu
- odbrana: logovanje operacija sa *id*-jem korisnika i *timestamp*-om

#### Information disclosure

- wrapped ili non-wrapped materijali ključeva mogu biti izloženi u error porukama ili debug log-ovima
- odbrana: sanitizacija osetljivih informacija

#### Denial of service

- napadači mogu da izvrše *flooding* napad na API (pogotovo na otvorene endpointe kao `/auth`)
- odbrana: uvesti rate limiting

#### Elevation of privilege

- bez adekvatne autorizacije autentifikovani korisnici bi mogli da kreiraju i brišu ključeve
- odbrana implementirana: izolacija privilegija i RBAC

---

### Create / rotate / delete, encrypt, decrypt, sign, verify

#### Spoofing

- u slučaju da se ne čita autor izvršene operacije, korisnici bi mogli da se pretvaraju da su neko drugi
- odbrana: kontrola vlasništva ključa (ukoliko je to jedan od zahteva)

#### Tampering

- ako ne bi bilo provere integriteta ključa, napadač bi mogao da zameni keyId i verziju bez detekcije
- odbrana implementirana: materijal ključa se generiše na serverskoj strani i AES-GCM sa AAD sprečava tampering

#### Repundation

- u slučaju nedostatka audit log-a, korisnik bi mogao da negira da je manipulisao nekim ključem
- odbrana: struktuirani audit log sa informacijom o korisnicima i timestamp-u izvršene operacije

#### Information Disclosure

- u slučaju da se bajtovi ključa ne anuliraju nakon wrapovanja, materijali bi mogli ostati u memoriji
- u slučaju da se osetljivi metapodaci loguju oni bi bili izloženi
- odbrana implementirana: bajtovi ključa se brišu iz memorije po završenoj operaciji

#### Denial of service

- odbrana: uvođenje paginacije na listu ključeva, rate limiting i ograničenja na veličinu podataka (enkripcija i potpisivanje)

#### Elevation of priveledge

- korisnik bi mogao da iskoristi neki ključ za operaciju koju on ne podržava
- odbrana delom implementirana: provera `allowedOperations` nad ključevima
