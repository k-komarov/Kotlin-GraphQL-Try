# Kotlin-GraphQL-Try

```
./gradlew bootRun

http://localhost:8080/graphiql

Query:
{
  article(sku: "M-MT-KINX4") {
    sku
    name
    accessories {
      sku
      name
    }
  }
}
```