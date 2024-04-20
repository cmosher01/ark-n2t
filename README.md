# ark-n2t

Copyright © 2024, Christopher Alan Mosher, New York, New York, USA, <cmosher01@gmail.com>.

[![License](https://img.shields.io/github/license/cmosher01/Gedcom-XY-Editor.svg)](https://www.gnu.org/licenses/gpl.html)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)

ARK resolver servlet.



## Definitions

``{ARK}`` format: `"ark:"` `{NAAN}` `"/"` [`{shoulder}`] `{blade}` `{checksum}`

* `{blade}` has an associated `{blade-length}` and `{alphabet}`
* `{checksum}` has an associated `{checksum-algorithm}`
* `{shoulder}` is optional
* `{shoulder}` format:
  one or more alpha (from `{alphabet}`) followed
  by exactly one numeric (from `{alphabet}`)

`{URL}` is a URL.

`{datastore}` is a database, such as a SQL relational database.

`{checksum-algorithm}` takes as input a `{NAAN}`, `{shoulder}`, `{blade}`, and
`{alphabet}`; and returns a single character from `{alphabet}`.

`{alphabet}` is an ordered list of alphanumeric characters.

## Actions

#### Resolve
* Given an `{ARK}`, find (the single, if any) associated `{URL}` in the `{datastore}`.

#### Find
* Given a `{URL}`, find (any or all) associated `{ARK}`'s in the `{datastore}`.

#### Mint
* Given a `{URL}`, generate a random-unique `{ARK}` (with given `{NAAN}`,
  `{shoulder}`, `{alphabet}`, `{blade-length}`, and `{checksum-algorithm}`),
  and store them (linked together) in the `{datastore}`.

#### Analyze
* Given an `{ARK}` from the `{datastore}`, get its `{NAAN}` or `{shoulder}`,
  or verify its `{checksum}` (using its associated
  `{alphabet}` and `{checksum-algorithm}`).



## Notes

Parsing, analyzing, or changing `{URL}`'s is out of scope.

Recommended `{alphabet}`: `bcdfghjkmnpqrstvwxz23456789`

Trivial SQL datastore:

```SQL
CREATE TABLE Ark (
    ark VARCHAR(256),
    url VARCHAR(2048)
)
```

where:
* `ark` is the `{ARK}` (could be stored either with or without `ark:` label),
* `url` is the `{URL}`.

Complete representation would also need these pieces of information,
which would apply to a set of Ark rows:

* Whether the `{ARK}`'s have a `{shoulder}` or not.
* Which `{checksum-algorithm}` was used to generate the `{checksum}`'s.
* The `{alphabet}` used in minting the `{ARK}`'s.
