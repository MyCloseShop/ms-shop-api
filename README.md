# MS-COMMERCE

## Description

`mvn clean && install`

```yaml
datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3307/db-ms-commerce?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC}
    username: ${SPRING_DATASOURCE_USERNAME:user}
    password: ${SPRING_DATASOURCE_PASSWORD:secret}
```

Ensuite `docker compose up -d db-ms-commerce`

Ensuite pour run le projet, juste lancé l'app, si besoin changé dans le `resources/application.yml` les urls et port pour la
connection à la base de données lancé depuis docker.

À changé si besoin avant de lancé l'app:


En plus vérifier si la base est bien créé sur le container... 😉 