# Microservices Kafka Communication System

Ce projet est une architecture microservices démontrant la communication asynchrone via Apache Kafka. Il illustre comment gérer les commandes clients à travers différents services qui collaborent via un système d'événements.

## Architecture du projet

L'application est composée de trois microservices principaux et d'une bibliothèque partagée:

- **order-service**: Reçoit les commandes des clients et publie des événements dans Kafka
- **stock-service**: Écoute les événements de commandes pour gérer l'inventaire
- **email-service**: Écoute les événements de commandes pour envoyer des notifications
- **base-domains**: Contient les DTO et modèles communs utilisés par tous les services

## Fonctionnalités

- Communication asynchrone basée sur les événements avec Apache Kafka
- Architecture découplée permettant une scalabilité indépendante de chaque service
- Modèles de données partagés via le module base-domains
- Gestion robuste des erreurs et désérialisation sécurisée

## Schéma de communication

```
┌─────────────┐         ┌─────────────┐         ┌──────────────┐         ┌──────────────┐
│             │ Commande │             │ Événement│              │         │              │
│   Client    │────────►│ order-service│────────►│  Topic Kafka  │────────►│ stock-service │
│             │         │             │         │ 'order_topics' │         │              │
└─────────────┘         └─────────────┘         └───────┬──────┘         └──────────────┘
                                                       │                         
                                                       │                  ┌──────────────┐
                                                       └─────────────────►│ email-service │
                                                                          │              │
                                                                          └──────────────┘
```

## Technologies utilisées

- **Spring Boot**: Framework Java pour le développement des microservices
- **Apache Kafka**: Plateforme de streaming distribuée pour la communication asynchrone
- **Spring Kafka**: Intégration de Kafka avec Spring
- **Lombok**: Réduction du code boilerplate
- **Maven**: Gestion des dépendances et construction des projets

## Prérequis

- Java 17 ou supérieur
- Maven 3.6 ou supérieur
- Apache Kafka 3.x
- Docker (optionnel, pour la conteneurisation)

## Installation et démarrage

1. **Configurer Kafka**

```bash
# Démarrer Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Démarrer Kafka
bin/kafka-server-start.sh config/kafka-server.properties

# Créer le topic
bin/kafka-topics.sh --create --topic order_topics --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1
```

2. **Compiler le module base-domains**

```bash
cd base-domains
mvn clean install
```

3. **Compiler et exécuter les services**

```bash
# Compiler et démarrer le service des commandes
cd order-service
mvn spring-boot:run

# Compiler et démarrer le service de gestion de stock
cd stock-service
mvn spring-boot:run

# Compiler et démarrer le service d'emails
cd email-service
mvn spring-boot:run
```

## Configuration

### order-service (producteur)

```properties
spring.application.name=order-service
server.port=8080

spring.kafka.producer.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer
spring.kafka.topic.name=order_topics
```

### stock-service et email-service (consommateurs)

```properties
spring.application.name=stock-service
server.port=8081

spring.kafka.consumer.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=stock
spring.kafka.consumer.auto-offset-reset=earliest
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.springframework.kafka.support.serializer.JsonDeserializer
spring.kafka.consumer.properties.spring.json.trusted.packages=net.amine.base_domains.dtos
spring.kafka.consumer.properties.spring.json.value.default.type=net.amine.base_domains.dtos.OrderEvent
spring.kafka.topic.name=order_topics
```

## Modèles de données

### OrderEvent

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private String message;
    private String status;
    private Order order;
}
```

### Order

```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String orderId;
    private String orderName;
    private int quantity;
    private double price;
}
```

## Points d'attention

- Assurez-vous que les packages des DTOs soient correctement spécifiés dans la configuration `spring.json.trusted.packages`
- Utilisez l'`ErrorHandlingDeserializer` pour une gestion robuste des erreurs de désérialisation
- Vérifiez que la configuration du consumer correspond bien à ce que le producer envoie

## Contribuer

Les contributions sont les bienvenues ! N'hésitez pas à ouvrir une issue ou à soumettre une pull request.
