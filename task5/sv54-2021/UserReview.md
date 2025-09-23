## User Review
Prva funkcionalnost skripte je provera koji korisnici imaju *uid 0*. Generalno jedini korisnik koji bi trebao da ima *uid 0* je *root*. *Root* nalog ima neograničen pristup sistemu i sve privilegije. Ukoliko ima više takvih naloga, sistem postaje manje bezbedan. 

Bezbednosni rizici:
- povećana površina napada, svaki *root* nalog označava jos jednu potencijalnu tačku ulaza u sistem
- rizik od malware-a, ukoliko je korisnik ulogovan na nalogu sa *root* pristupom u trenutku kada pokupi neki maliciozni program, taj program može da iskoristi *root* privilegije kroisnika i postaje opasniji
- sistemska šteta, ukoliko je neki korisnik ulogovan kao *root*, može lako da napravi namernu ili nenamernu štetu na sistemu brisanjem sistemskih fajlova
