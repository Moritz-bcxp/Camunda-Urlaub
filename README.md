# Camunda 8 Urlaub Process Application

Eine Spring Boot Anwendung zur Ausführung von Camunda 8 BPMN Prozessen für Urlaubsanträge.

## Projektstruktur

```
src/
├── main/
│   ├── java/com/camunda/urlaub/
│   │   ├── UrlaubProcessApplication.java     # Haupt-Spring Boot Anwendung
│   │   ├── controller/
│   │   │   └── UrlaubController.java         # REST API Controller
│   │   ├── model/
│   │   │   └── UrlaubsAntrag.java           # Datenmodell für Urlaubsantrag
│   │   ├── service/
│   │   │   └── MitarbeiterService.java      # Service für Mitarbeiterdaten
│   │   └── worker/
│   │       ├── MitarbeiterDatenWorker.java  # Job Worker für Mitarbeiterdaten
│   │       └── UrlaubGenehmigungWorker.java # Job Worker für Genehmigung/Ablehnung
│   └── resources/
│       ├── application.yml                  # Konfiguration
│       ├── Urlaub.bpmn                      # BPMN Prozess
│       └── Urlaub-Antrag-Form.form          # Camunda Form
└── test/
    └── java/com/camunda/urlaub/
        └── UrlaubProcessApplicationTests.java # Tests
```

## Voraussetzungen

- Java 17+
- Maven 3.6+
- Camunda 8 Platform (Zeebe, Operate, Tasklist)

## Camunda 8 Setup

### Option 1: Lokales Setup mit Docker

```bash
# Camunda 8 Platform lokal starten
git clone https://github.com/camunda/camunda-platform.git
cd camunda-platform
docker-compose up -d
```

### Option 2: Camunda 8 SaaS

Registrieren Sie sich bei [Camunda 8 SaaS](https://camunda.io) und aktualisieren Sie die Konfiguration in `application.yml`.

## Anwendung starten

1. **Maven Dependencies installieren:**

   ```bash
   mvn clean install
   ```

2. **Anwendung starten:**

   ```bash
   mvn spring-boot:run
   ```

3. **Oder JAR ausführen:**
   ```bash
   mvn package
   java -jar target/urlaub-process-1.0.0.jar
   ```

## BPMN Prozess

Der Urlaubsprozess (`Urlaub.bpmn`) umfasst folgende Schritte:

1. **Start Event:** Urlaub beantragen
2. **User Task:** Urlaubsantrag ausfüllen (Mitarbeiter)
3. **Service Task:** Mitarbeiterdaten abrufen
4. **Gateway:** Prüfung verfügbarer Urlaubstage
5. **Service Task:** Urlaub genehmigen (bei ausreichenden Tagen)
6. **Service Task:** Urlaub ablehnen (bei unzureichenden Tagen)
7. **End Events:** Verschiedene Endergebnisse

## API Endpoints

### Urlaubsprozess starten

```bash
POST http://localhost:8080/api/urlaub/start
Content-Type: application/json

{
  "antragsteller": "Max Mustermann",
  "tageAnzahl": 5,
  "vonDatum": "2025-08-20",
  "bisDatum": "2025-08-24",
  "grund": "Erholung"
}
```

### Health Check

```bash
GET http://localhost:8080/api/urlaub/health
```

## Job Workers

Die Anwendung enthält folgende Job Workers:

- **MitarbeiterDatenWorker:** Verarbeitet `get-mitarbeiter-daten` Jobs
- **UrlaubGenehmigungWorker:** Verarbeitet `urlaub-genehmigen` und `urlaub-ablehnen` Jobs

## Konfiguration

Wichtige Konfigurationen in `application.yml`:

```yaml
camunda:
  client:
    zeebe:
      gateway-address: localhost:26500
    operate:
      base-url: http://localhost:8081
    tasklist:
      base-url: http://localhost:8082
```

## Testen

```bash
# Unit Tests ausführen
mvn test

# Integration Tests (benötigt laufende Camunda Platform)
mvn verify
```

## Überwachung

- **Operate:** http://localhost:8081 - BPMN Prozess-Monitoring
- **Tasklist:** http://localhost:8082 - User Task Management
- **H2 Console:** http://localhost:8080/h2-console - Datenbank-UI

## Entwicklung

### BPMN Prozess bearbeiten

Verwenden Sie den Camunda Web Modeler oder Desktop Modeler zum Bearbeiten der `Urlaub.bpmn` Datei.

### Neue Job Worker hinzufügen

Erstellen Sie neue `@JobWorker` annotierte Methoden in den Worker-Klassen.

### Formular anpassen

Bearbeiten Sie die `Urlaub-Antrag-Form.form` Datei mit dem Camunda Form Builder.

## Deployment

Für Produktionsumgebungen:

1. Aktualisieren Sie die Camunda 8 Verbindungseinstellungen
2. Konfigurieren Sie eine Produktionsdatenbank
3. Setzen Sie entsprechende Logging-Level
4. Aktivieren Sie Spring Security falls erforderlich
