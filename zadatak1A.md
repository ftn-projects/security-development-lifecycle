## A. Enkripcija tajni (pa i lozinki)

Za bezbednu implementaciju mehanizma za kreiranje ključa potrebno je koristiti **Argon2id**.
Za razliku od ostalih mehanizama, **Argon2** daje kontrolu korisniku da upravlja većom količinom parametara poput potrošnje memorije, vremena izvršavanja i nivoa paralelizma, dok se npr. **PBKDF2** oslanja na količinu iteracija kako bi povećao sigurnost.
Ovaj algoritam ima 3 tipa koji se koriste u zavisnosti od vrste napada od koje želimo jaču zaštitu:

* **Argon2i** koji povećava otpornost na GPU napad
* **Argon2d** koji povećava otpornosti na bočne napade
* **Argon2id** koji je mešavina prethodna dva

S obzirom da ne možemo znati na koji način ćemo biti napadnuti, **Argon2id** je najbolja opcija i takođe je preporučen od strane **OWASP**-a. Minimalne parametre koje bi trebalo koristiti su 19 MiB sa 2 iteracije i 1 nivoom paralelizma.

Za bezbednu simetričnu enkripciju/dekripciju globalni standard je **AES256-GCM**.

**AES** radi na blokovima od 128 bita, pa je za veličinu ključa preporučeno koristiti najmanje 128 bita ali je poželjno koristiti 256 jer je algoritam s ključem te veličine otporniji. **AES** ima nekoliko mod-ova koji se koriste u zavisnosti od svrhe: **ECB, CBC, CTR, GCM, CFB i OFB**. Mod koji je najbolji za enkripciju je **GCM**, ima visok nivo zaštite i podržava autentikaciju kao i paralelizaciju.

**AES256-GCM** kao parametre prima ključ, koji nama generiše Argon2id, inicijalni vektor koji je unikatan za svaki blok od 256 bita, podatke koje procesuje samo autentikacijom i podatke koje procesuje enkripcijom i autentikacijom, a kao rezultat dobijamo enkriptovane podatke i TAG. Kako bi dekriptovali podatke potrebno je poslati ih sa TAG-om.

Oba algoritma možemo naći u **Open SSL** biblioteci. Najnovija verzija (*3.5.1*) za sada nema poznate ranjivosti i rešila je probleme prošlih verzija.
