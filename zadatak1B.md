# Logging (Auditing)

## Uvod

Logovanje (ili auditing) je proces beleženja važnih događaja koji se dešavaju u softverskom sistemu. Ovi događaju mogu uključivati greške, pokušaje pristupa, promene u konfiguraciji, autentifikaciju korisnika i druge kritične akcije.

Logovi predstavljaju ključni izvor informacija za:
- Otkrivanje i rešavanje problema (debugging)
- Praćenje bezbednosnih aktivnosti i pokušaja zloupotrebe
- Audit i forenzičku istragu kada se mora utvrditi ko je šta uradio
- Monitoring performansi i ponašanja sistema u realnom vremenu

## Struktura loga

Log poruka je zapis jednog događaja koji se čuva u tekstualnom ili najčešće u struktuiranom formatu (npr. JSON). Da bi log bio koristan za analizu i audit, svaka poruka treba da sadrži jasno definisane elemente.

Najčešće komponente log poruke su:
- Vremenka oznaka (timestamp): Prezizan trenutak kada se događaj desio. Poželjno je koristiti UTC i ISO 8601 format (npr. `2025-07-08T10:43:12Z`) radi doslednosti i lakšeg poređenja.
- Nivo loga (log level): Označava važnost događaja. Najčešći nivoi su: `DEBUG`, `INFO`, `WARNING`, `ERROR`, `CRITICAL`.
- Poruka (message): Ljudski čitljiv opis događaja (npr. "Neuspešna prijava korisnika").
- Akter (actor, user, service): Informacije o entitetu koji je izazvao događaj, poput: korisničko ime, ID, IP adresa, naziv servisa koji je inicirao radnju.
- ID log poruke: Unikatni identifikator log poruke radi lakšeg povezivanja i pretrage

### Primer log poruke

```json
{
    "timestamp": "2025-07-08T10:43:12Z",
    "level": "ERROR",
    "service": "auth-service",
    "user": "marko",
    "ip": "182.168.1.10",
    "message": "Failed login attempt"
}
```

**VAŽNO**: Log poruke **ne smeju sadržati poverljive i osetljive podatke** poput lozinki, brojeva kreditnih kartica, tokena za autentifikaciju. Takav sadržaj predstavlja bezbednosni i pravni rizik. Ova tema će biti detaljnije obrađena u narednim sekcijama


## Rotacija logova

Kako aplikacije i sistemi neprekidno generišu log poruke, veličina log fajlova može brzo da poraste u zauzme mnogo prostora na disku. Rotacija logova (log rotation) je mehanizam kojim se ograničava rast log fajlova, zamenjujući ih novim fajlovima i arhivirajući stare.

Rotacija logova pomaže da se:
- spreči popunjavanje diska
- održi preglednost i lakše upravljanje logovima
- uspostavi jasan mehanizam retencije (koliko dugo čuvati koje podatke)

### Rotacija u lokalnim sistemima

U tradicionalnim (lokalnim) sistema kao što su Linux serveri, log fajlovi se načešće nalaze u direktorijumu `/var/log`. Za nihovu rotaciju se koristi alat **logrotate**, standarni Linux servis koji automatski rotira, kompresuje, briše ili arhivira log fajlove prema unapred zadatim pravilima.

#### Kako funkcioniše

`logrotate` se pokreće periodično (npr. svakog dana). Prolazi kroz konfiguracije fajlove i provera da li je log fajl dostigao kriterijum za rotaciju (veličina, broj dana).
Ako jeste:
- Preimenuje fajl (`app.log`->`app.log.1`)
- Kreira prazan `app.log` fajl za nove logove
- Poštuje retenciju (`rotate 7` - čuva 7 starih fajlova)

#### Primer konfiguracije

```conf
/var/log.myapp.log {
    daily
    rotate 7
    compress
    missingok
    notifempty
    create 0640 root root
    postrotate
        systemctl reload myapp
    endscript
}
```

#### Bezbednosne ranjivosti logrotate mehanizma

Log injection:

- Ako aplikacija ne sanitizuje unose koji se loguju, napadač može umetnuti specijalne znakove (npr. novi red, escape sekvence) koje remete strukturu log fajla.
- U nekim slučajevima to može dovesti do log forginga - umetanja lažnih poruka
- Prevencija:
    - Log biblioteka mora sanitizovati ulazne podatke
    - Koristiti struktuirane logove (JSON)

Race condition:
- Ako se log fajl rotira dok aplikacija još uvek piše u njega, može doći do gubitka logova ili pisanja u pogrešan fajl. To je naročito opasno ako `logrotate` rotira fajl, ali aplikacija i dalje piše u stari (ne prepoznaje novi fajl).
- Prevencija:
    - Koristici copy `copytruncate` ako aplikacija ne podržava `SIGHUP` (zatvori pa otvori fajl).
    - Ili koristiti `postrotate` blok za restartovanje aplikacije da bi otvorila novi fajl.

Symlink attack:
- Ako napadač zameni log fajl simboličkim linkom ka nekom sistemskom fajlu, `logrotate` mo
e prepisati tajl fajl sa log sadržajem
- Prevencija:
    - Podešavanjem permisija i vlasništva log fajlova (npr. `create 0640 root root`)
    - Pokretanjem aplikacija pod zasebnim korisnikom bez privilegija




### Rotacija u Docker-u

Docker kontejneri takođe generišu logove, bilo putem `stdout` ili `stderr`, ili iz same aplikacije. Po defaultu, DOcker koristi `json-file` log-driver koji zapisuje sve logove kontejnera u fajlove na disku host mašine, obično u `var/lib/docker/containers/<container-id>/<container-id>-json.log`

#### Konfiguracija rotacije

Prilikom pokretanja kontejnera, rotacija se podešava na sledeći način:

```bash
docker run
    --log-driver=json-file
    --log-opt max-size=10m
    --log-opt max-file=3
    myapp
```

`max-size=10m` - kada log fajl dostigne 10MB, automatski se rotira
`max-file=3` - čuva se maksimalno 3 fajla (npr. `log`, `log.1`, `log.2`), najstariji se briše


#### Podržani log-driveri i rotacija
Osim `json-file`, Docker podržava i druge `log-driver` opcije:
- `syslog` - logovi se šalju sistemskom `syslog` servisu (može koristiti `logrotate`)
- journald - logovi idu u `systemd-journal`
- `awslogs`, `gelf`, `fluentd`, `splunk`, `logstash` - za direktnu integraciju sa spoljnim sistemima


Više o Docker Logovima na:
- [Docker Logs](https://docs.docker.com/engine/logging/)
- [Sematext](https://sematext.com/guides/docker-logs/)

### Rotacija u cloud-native rešenjima

Cloud-native logovanje podrazumeva korišćenje servisa u oblaku koji omogućavaju prikupljanje, pretragu, analiziranje i automatsko upravljanje logovima iz različitih izvora, bez manuelnog rada sa log fajlovima. U ovim sistemima korisnik ne mora da brine o fajl sistemu, rotaciji fajlova ili lokalnoj memoriji. Umesto toga, koristi jednostavne postavke retencije, eksportovanja i analitike.

#### AWS - CloudWatch Logs
**Prikupljanje logova**

AWS CloudWatch logs automatski ili putem agenta prikupla logove iz:
- aplikacija koje koriste AWS SDK (npr. Node.js, Python, Java aplikacije),
- sistemskih servisa na EC2 instancama (kroz CloudWatch Agent),
- AWS servisa kao što su Lambda, ECS, API Gateway

**Retencija**

Za svaku grupu logova korisnik može da podesi koliko dana se logovi zadržavaju, gde se logovi stariji od tog roka automatski brišu. Primer:
```bash
aws logs put-retention-policy 
  --log-group-name /aws/lambda/my-function
  --retention-in-days 14
```

**Eksplicitno čuvanje (arhiviranje)**
Ako korisnik želi da sačuva određene logove trajno, može ih eksportovati u S3

**Analiza i pretraga**
CloudWatch nudi mogućnost pretrage logova po tekstu, filterija i vremenskom opsegu. Omogućava pravljenje alarma, grafika, automatskih obaveštenja.

#### Google Cloud - Cloud Logging

**Prikupljanje logova**
Cloud Logging na GCP platformi automatski prikuplja logove sa:
- COmpute Enginge VM-ova (pomoću Cloud Ops agenta),
- Google Kubernetes Engine (GKE) kontejnera
- App Engine aplikacija
- bilo koje aplikacije koja koristi GCP SDK

**Retencija**
Po defaultu se logovi zadržavaju 30 dana, ali korisnik može da promeni taj period. Podešavanje se vrši kroz konzolu (Log Explorer) ili putem pravila u Log Router-u.

**Export i dugoročno čuvanje**
Korisnik može da podesi da se određeni logovi automatski šalju u:
- Cloud Storage
- BigQuery
- Pub/Sub

**Analiza i vizualizacija**



## ELK stack

### Logstash

### Elasticsearch

### Kibana