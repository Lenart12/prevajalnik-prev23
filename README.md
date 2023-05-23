# Prevajalnik PREV23

## Test it remote at [PREV23 online compiler](https://wgn.si/prev23)


Usage:

`java -jar prev23-$version.jar [--arguments] program.p23`

Available arguments:

| Argument        | Default                    | Description                                          |
|-----------------|----------------------------|------------------------------------------------------|
| --interpreter   | *not present*              | Run program through an interpreter                   |
| --num-regs      | 4                          | Amount of local registers used in the program        |
| --xsl           | *empty*                    | Path to xsl stylesheets to be used for generated xml |
| --logged-phase  | "all"                      | Which phase to log                                   |
| --target-phase  | "all"                      | At which phase the compiler will stop                |
| --src-file-name | *last positional argument* | The path to the program source code                  |
| --dst-file-name | *src-file-name*`.mms`      | The path to the compiler output                      |

Available phases are `none|lexan|synan|abstr|seman|memory|imcgen|imclin|asmgen|livean|regall|epilogue`

Grab a build from [releases](https://github.com/Lenart12/prevajalnik-prev23/releases) or visit the [online compiler](https://wgn.si/prev23)

Install [VS Code language support](https://marketplace.visualstudio.com/items?itemName=Lenart12.prev23-language-support)

---

## Navodila za Avtomatsko ustvarjanje zipa, objava na ucilnico in release prevedenega projekta

## Nastavitve:

Github -> `ti/tvoj-prevajalnik-prev23` -> Settings -> Secrets and variables -> Actions

Nastavi enkrat v Secrets (Secrets -> New repository secret):
* `MOODLE_NAME` nastavi na svoj ucilnica mail (`ti1234@student.uni-lj.si`)
* `MOODLE_PW` nastavi na svoj ucilnica geslo
* `STUDENTID` nastavi na svoj student id

Nastavi vsak teden (Variables -> New repository variable):

* `SUBMISSION` nastavi na `$id_oddaje`-`$ime_oddaje` (npr. `52200-imcgen`)
* Če je `$ime_oddaje` prazno (npr. `52200-`, nujno ne pozabi `-` na koncu), je ustvarjen zip `$STUDENTID.zip`

Id oddaje dobis iz zadnjega dela linka ko odpres stran za oddajo - https://ucilnica.fri.uni-lj.si/mod/assign/view.php?id=`52200`

## Prilagoditev:

Preglej `./create_zip.sh` in dodaj `rm -f` za kasne datoteke, ki jih `make clean` ne odstrani sam.

## Uporaba:

Prenesi [semver](https://gist.github.com/Lenart12/058f2efe2ec4890b251557d0d98eae5f) tukaj.

Predlagam da si `./semver` skripto premaknes v `$HOME/.local/bin` lahko pa tudi pustis kjer hočeš.

Commitaj svoje delo in uporabi naslednji ukaz:

`./semver (major/minor/patch)` (ali samo `semver (major/minor/patch)` ce je premaknjen v `.local`)

* major - poveca v`X`.-.-
* minor - poveca v-.`X`.-
* patch - poveca v-.-.`X`

in nato se pojavi editor vpises opis releasea.

Če te moti da imas prazen tag brez uspesne verzije zaradi napake se to tako popravi.

1. Popravi kodo z novim `git commit` ali `git rebase -i`
2. `git tag -d v0.6.6` oz. ime nesupešne verzije
3. Ponovi spet `./semver patch`
4. ... 

### Primer

```sh
$ git commit -m "Dodan CICD za avtomatski relese"
$ ./semver patch
Current Version: v0.6.5
(patch) updating v0.6.5 to v0.6.6
-------------------------------------------------
Ta nova verzija... blabla
#
# Write a message for tag:
#   v0.6.6
# Lines starting with '#' will be ignored.
-------------------------------------------------
Tagged with v0.6.6
Enumerating objects: 1, done.
Counting objects: 100% (1/1), done.
Writing objects: 100% (1/1), 171 bytes | 0 bytes/s, done.
Total 1 (delta 0), reused 0 (delta 0), pack-reused 0
To github.com:ti/tvoj-prevajalnik-prev23.git
 * [new tag]         v0.6.6 -> v0.6.6
```

Na ucilnici se potem avtomatsko odda .zip z najnovejso verzijo in pod releases na Github se ustvari `prev23-vX.X.X.jar` in dobis na mail dve obvestili.

### Primer

Github e-mail
```
RELEASE-v0.6.6

Repository: ti/tvoj-prevajalnik-prev23 · Tag: v0.6.6 · Commit: b4332c4 · Released by: github-actions[bot]
—

This release has 3 assets:

    prev23-v0.6.6.jar
    Source code (zip)
    Source code (tar.gz)
```

in ucilnica e-mail

```
prbuni ->Naloga ->ASSIGNMENT: Memory layout
________________________________________
Oddali ste nalogo za ASSIGNMENT: Memory layout.
Tu si lahko ogledate stanje svoje oddane naloge .

```


Ce build ne uspe, upload na moodle ne dela ali gre karkoli drugega narobe se ne objavi verzija in dobis obvestilo na mail.

### Primer

E-mail: Create .ZIP release: All jobs have failed -> View workflow run -> deployzip -> Compile project

```java
 src/prev23/Compiler.java:158: error: cannot find symbol
					Abstr.tree.accept(new CodeGenerator(), null);
					                      ^
  symbol:   class CodeGenerator
  location: class Compiler
1 error
make: *** [Makefile:11: all] Error 1
make: Leaving directory '/home/runner/work/prevajalnik-prev23/prevajalnik-prev23/prev23'
Error: Process completed with exit code 2.
```

