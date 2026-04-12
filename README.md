# SEPA XML Importer

Ein hochleistungsfähiger Importer fuer grosse SEPA XML-Dateien (bis zu 600+ MB) mit Spring Boot 3.4, Java 21 und Spring Batch.

## Features

- Streaming XML-Parser (StAX/Woodstox) - Kein OutOfMemory bei groessen Dateien
- Spring Batch Integration - Robuste Job-Steuerung mit Restart-Faehigkeit
- REST API - Job-Steuerung und Monitoring ueber HTTP
- Retry-Logik - Automatische Wiederholung bei temporaren Datenbankfehlern
- Dead Letter Queue - Fehlgeschlagene Records werden protokolliert
- File Upload Support - Direktes Hochladen ueber REST API
- SQL Server 2019+ - Optimiert fuer Microsoft SQL Server 15.x
- Actuator Endpoints - Health Checks und Batch-Metriken

## Technologien

| Komponente | Version |
|------------|---------|
| Java | 21 (LTS) |
| Spring Boot | 3.4.3 |
| Spring Batch | 5.x |
| Maven | 3.9.x |
| SQL Server | 2019 (15.x) |
| XML Parser | Woodstox 7.0.0 |
| Web Server | Embedded Tomcat |

## Architektur

Spring Boot Application (einzelner Prozess)
- Embedded Tomcat (Port 8080)
  - REST API Endpoints (/api/jobs/...)
  - HTTP Listener
- Spring Batch Engine
  - Job Launcher
  - StaxXmlReader (Streaming)
  - SepaProcessor (Validierung)
  - ErrorLoggingWriter (Batch + DLQ)
- JDBC Connection Pool (HikariCP)
  |
  v
SQL Server (Port 1433)

## Voraussetzungen

- Java 21 (LTS) - https://adoptium.net/
- Maven 3.9.x - https://maven.apache.org/
- SQL Server 2019+ - https://www.microsoft.com/sql-server
- Git (optional) - Fuer Versionierung

### Ueberpruefung

```bash
java -version  # Sollte 21.x.x anzeigen
mvn -version   # Sollte 3.9.x anzeigen
sqlcmd -V      # SQL Server Client sollte installiert sein
