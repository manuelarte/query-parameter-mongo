# Query Parameter Mongo

Query Parameter MongoDB is a library that allows you to add a query parameter to your endpoints and apply that query to Spring Mongo Data.

# Table of Contents
1. [Prerequisites](#prerequisites)
2. [Installation](#installation)
3. [Concatenate params](#concatenate-params)

## Prerequisites

- Spring Data Mongo
- The repositories you want to query needs to implement MongoRepository<D, ID>

## Installation

Add the following dependency in your project to start using JPA Query Param.

```bash
implementation 'org.manuel.spring:query-parameter-mongo:{latest-version}'
```

## Usage

Add the @EnableQueryParameter annotation in your spring boot application, to import the configuration.

```java
@SpringBootApplication
@EnableQueryParameter
public class SpringBootAppApplication {
```

Then add the @QueryParameter annotation in a controller you would like to use the query param.
Here you can see an example:

```java
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DocumentEntity>> findByQuerying(@QueryParameter(document = DocumentEntity.class) Query query) {
        return ResponseEntity.ok(entityService.findAll(query));
    }
```

Then call that endpoint and use the available query operations:

```bash
GET /documents?q=firstName::Manuel;age:<18
```

## Query Operations supported

# Equals (::)

Below an example to match firstName == Manuel
```bash
GET /documents?q=firstName::Manuel
```

# Lower than (:<)

Below an example to match age < 18
```bash
GET /documents?q=age:<18
```

# Lower than or equal (:<=)

Below an example to match age <= 18
```bash
GET /documents?q=age:<=18
```

# Greater than (:>)

Below an example to match age > 18
```bash
GET /documents?q=age:>18
```

# Greater than or equal (:>=)

Below an example to match age >= 18
```bash
GET /documents?q=age:>=18
```

# In (:in:(xxx,yyy,...))

Below an example get gender in MALE and FEMALE
```bash
GET /documents?q=gender:in:(MALE,FEMALE)
```

## Concatenate params

It's possible to concatenate params AND (;), OR (|).

As an example:
If we want documents whose firstname is Manuel *and* age less than 18
```bash
GET /documents?q=firstName::Manuel;age:<18
```
If we want entities whose firstname is Manuel *or* age less than 18
```bash
GET /documents?q=firstName::Manuel|age:<18
```

## Customization

### Allow or not allow keys

It's possible to filter the keys that are allow or not allow to be queried (by default every key is allowed).
As an example, if I only want to query by firstName and lastName
```java
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Entity>> findByQuerying(@QueryParameter(entity = Entity.class, allowedKeys={"firstName", "lastName"}) Specification<Entity> query) {
        return ResponseEntity.ok(entityService.findAll(query));
    }
```
Or if only want to not allow createdBy field
```java
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Entity>> findByQuerying(@QueryParameter(entity = Entity.class, notAllowedKeys="createdBy") Specification<Entity> query) {
        return ResponseEntity.ok(entityService.findAll(query));
    }
```

Then the parser will throw a QueryParserException if finds a not allowed key.

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)