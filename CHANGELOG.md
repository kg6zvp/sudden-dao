# Changelog

### 1.0.0-SNAPSHOT

CRUD:
- get a `TypedQuery<T>` with `getAllQuery()` and `getMatchingQuery(T keyObject)` (especially useful with the streaming API)

`find()`: enhancing the criteria API one step at a time
- `sortBy(Model_.attribute).ascending()` allows you to write a one-liner instead of a complex criteria query

### 1.0.0

CRUD:
- persist (create)
- get by id/primary key (read)
- save (update)
- remove (delete)

Additional features:
- ability to find objects like a given object in the database
