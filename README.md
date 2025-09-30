# ITIC Paris - Student App Microservices

Application mobile Flutter pour les étudiants de l'ITIC Paris avec architecture microservices Spring Boot.

## Architecture

Flutter App → API Gateway → [Auth Service, Ypareo Service, Gamification Service, Wallet Service]
 ↓ Eureka Server (Service Discovery) ↓ MySQL + Redis (Databases & Cache)


## Services

### Implémentés
- **eureka-server** (8761) - Service Discovery
- **auth-service** (8081) - Authentification (en cours)
- **api-gateway** (8080) - Routage et sécurité (en cours)

### En développement
- **ypareo-service** (8082) - Fetch des données Ypareo
- **gamification-service** (8083) - Système de points et classements
- **wallet-service** (8084) - Génération cartes étudiantes

## Technologies

- **Backend** : Spring Boot 3.2, Spring Cloud 2023
- **Service Discovery** : Netflix Eureka
- **API Gateway** : Spring Cloud Gateway
- **Database** : MySQL 8.0
- **Cache** : Redis 7
- **Security** : Spring Security + JWT
- **Monitoring** : Spring Boot Actuator
- **Frontend** : Flutter (repo séparé)


### 1. Cloner le projet
```bash
git clone https://github.com/VOTRE-USERNAME/itic-student-app-microservices.git
cd itic-student-app-microservices