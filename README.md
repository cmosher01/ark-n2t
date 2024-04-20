# ark-n2t

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



## Actions

#### Resolve
* Given an `{ARK}`, find (the single, if any) associated `{URL}` in the `{datastore}`.

#### Find
* Given a `{URL}`, find (any or all) associated `{ARK}`'s in the `{datastore}`.

#### Mint
* Given a `{URL}`, generate a random-unique `{ARK}` (with given `{NAAN}`,
  `{shoulder}`, `{alphabet}`, `{blade-length}`, and `{checksum-algorithm}`),
  and store them (liked together) in the `{datastore}`.

#### Analyze
* Find all `{NAAN}`'s and their associated `{shoulder}`'s in the `{datastore}`.
* Verify all `{checksum}`'s in the `{datastore}`
  (given an `{alphabet}`, `{blade-length}`, and `{checksum-algorithm}`).



## Notes

Parsing, analyzing, or changing `{URL}`'s is out of scope.

Recommended `{alphabet}`: `bcdfghjkmnpqrstvwxz23456789`

Trivial SQL datastore:

```SQL
CREATE TABLE Ark (
    ark VARCHAR(256),
    shoulder INTEGER,
    url VARCHAR(2048)
)
```

where:
* `ark` is the `{ARK}` (could be stored either with or without `ark:` label),
* `shoulder` is a boolean indicating whether the `{ARK}` has a `{shoulder}` or not, and
* `url` is the `{URL}`.
