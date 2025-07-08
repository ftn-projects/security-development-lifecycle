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

**VAŽNO**: Log poruke **ne smeju sadržati poverljive i osetljive podatke** poput lozini, brojeva kreditnih kartica, tokena za autentifikaciju. Takav sadržaj predstavlja bezbednosni i pravni rizik. Ova tema će biti detaljnije obrađena u narednim sekcijama


## Logrotate


## Nacini cuvanja logova

### Lokalno

### Centralizovano

### Cloud-based logovanje


## ELK stack

### Logstash

### Elasticsearch

### Kibana