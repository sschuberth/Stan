# Stan - The *St*atement *An*alyzer

| Linux | Windows |
| :---- | :------ |
[ ![Linux build status][1]][2] | [![Windows build status][3]][4] |

[1]: https://travis-ci.org/sschuberth/Stan.svg?branch=master
[2]: https://travis-ci.org/sschuberth/Stan
[3]: https://ci.appveyor.com/api/projects/status/xeyju8npt336ley5/branch/master?svg=true
[4]: https://ci.appveyor.com/project/sschuberth/Stan/branch/master

## What's this?

Stan is both a library and command line tool to convert and analyze bank account statements. It works completely offline by parsing the statement files you specify and does not require any online connection or [HBCI interface](http://www.hbci-zka.de/spec/spezifikation.htm).

## What statement files are supported?

- [Postbank PDF](https://www.postbank.de/privatkunden/docs/Kontoauszug_A4_Privatkunden.pdf)

  The Postbank has changed the format for PDF account statements multiple times. The supported formats are the ones introduced in July 2014 and in June 2017, respectively.

## What file formats can be exported to?

- Custom [JSON](https://www.json.org/)

  The JSON files offer a generic way to further process the statement data.

- [OFX version 1](http://www.ofx.net/downloads.html)

  The OXF files can then be imported into finance applications like [GnuCash](https://www.gnucash.org/), [HomeBank](http://homebank.free.fr/en/index.php) or [jGnash](https://ccavanaugh.github.io/jgnash/).

- [QIF](https://en.wikipedia.org/wiki/Quicken_Interchange_Format)

  The QIF files can then be imported into finance applications like [GnuCash](https://www.gnucash.org/), [HomeBank](http://homebank.free.fr/en/index.php), [jGnash](https://ccavanaugh.github.io/jgnash/) or [Money Manager Ex](https://www.moneymanagerex.org/).
