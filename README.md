# WellSync – Sistem software pentru monitorizarea inteligentă a stilului de viață

WellSync este o platformă web full-stack modernă, concepută pentru monitorizarea parametrilor de sănătate, activitate fizică și nutriție. Sistemul integrează grafice, prin **ApexCharts.js** și dispune de un asistent virtual inteligent bazat pe tehnologii AI, utilizând modelul **Google Gemini** prin intermediul framework-ului **Spring AI**.

---

## Informații Depozit Git

- **Repository Oficial:** [https://github.com/rusuandreiraul/Licenta]
- **Autor:** Rusu Andrei Raul

---

## Arhitectura Sistemului & Tehnologii Utilizate

Aplicația este construită respectând bunele practici de decuplare a componentelor prin arhitectura **Client-Server**.

### Backend (Componenta Server)

- **Limbaj:** Java 21
- **Framework Principal:** Spring Boot 3.x
- **Securitate:** Spring Security bazat pe **JWT (JSON Web Tokens)**
- **Integrare AI:** Spring AI (Gemini API Client)
- **Persistență & ORM:** Spring Data JPA / Hibernate, PostgreSQL
- **Build Tool:** Maven

### Frontend (Componenta Client)

- **Framework:** Nuxt.js 3 (bazat nativ pe Vue.js 3 și motorul de randare Nitro)
- **Stil:** Tailwind CSS / Nuxt UI Component Toolkit
- **Grafice interactive:** ApexCharts (randate exclusiv pe partea de client via `<client-only>`)

### Baza de Date

- **SGBD:** PostgreSQL 17

---

## Pașii de Configurare și Lansare Locală

### 1. Configurare Bază de Date & Variabile de Mediu

Înainte de pornirea serviciilor backend, asigurați-vă că aveți un server PostgreSQL activ și o bază de date goală creată cu numele `wellsync`.

Deschideți și editați fișierul `application.properties` localizat în `application/src/main/resources/`:

```properties
# Sincronizare Conexiune PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/wellsync
spring.datasource.username=postgres
spring.datasource.password=andrei
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true


# Configurare Strategie JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect


# Cheia trebuie să fie un string de minimum 32 de caractere pentru algoritmul HS256
jwt.secret=CHEIE_SECRETA_SUTA_LA_SUTA_MANUAL_GENERATA_AICI

spring.ai.openai.base-url=https://generativelanguage.googleapis.com/v1beta/openai/


spring.ai.openai.api-key=CHEIA_PENTRU_API_EXTERN


# 3. model AI Gemini folosit
spring.ai.openai.chat.options.model=gemini-3-flash-preview
```

**Pentru rulare Backend**

```bash
# 1. Navigarea în directorul aplicației Spring Boot

cd application

# 2. Curățarea compilărilor anterioare și împachetarea în format .jar (fără rularea testelor)

mvn clean package -DskipTests

# 3. Pornirea efectivă a aplicației pe portul implicit 8080

mvn spring-boot:run
```

**Pentru rulare Frontend**

```bash
# 1. Navigarea în folderul interfeței grafice Nuxt

cd healthTracking

# 2. Instalarea pachetelor și dependențelor esențiale (Tailwind, ApexCharts)

npm install

# 3. Executarea serverului în regim de dezvoltare asincron (Hot Reload activat)

npm run dev
```
