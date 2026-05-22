# Ad Aggregator

This project is a Java 21 command-line application that processes a large advertising CSV file and produces aggregated campaign analytics.

## Setup Instructions

### Prerequisites

- Java 21
- Maven 3.9+ or the Maven Wrapper included in this repository

### Project Setup

Clone the repository and move into the project directory:

```bash
git clone [<repository-url>](https://github.com/NinhLe2806/java-FV-SEC001)
cd java-FV-SEC001
```

Build the project:

```bash
./mvnw clean package
```

On Windows:

```powershell
.\mvnw.cmd clean package
```

## How to Run the Program

Run the shaded JAR file after building:
Note: Before running the program, please make sure that the `ad_data.csv` file is placed at the same level as the `src` folder in the project directory.

```bash
java -jar target/java-FV-SEC001-1.0-SNAPSHOT.jar --input ad_data.csv --output results
```

Example on Windows:

```powershell
java -jar target\java-FV-SEC001-1.0-SNAPSHOT.jar --input ad_data.csv --output results
```

The program generates these files in the output directory:

- `campaign_summary.csv`
- `top10_ctr.csv`
- `top10_cpa.csv`

## Libraries Used

- `picocli`
  For CLI argument parsing.

- `univocity-parsers`
  For fast and memory-efficient CSV parsing.

- `slf4j-api`
  Logging API used by the application.

- `slf4j-simple`
  Simple SLF4J logging backend for console output.

- `lombok`
  Used for `@Slf4j` logger generation.

- `junit-jupiter`
  Test framework.

- `assertj-core`
  Fluent assertions for tests.

## Processing Time for the 1GB File

Measured run on the current machine:

- Reading and aggregating CSV: `17006 ms` (`17.006 s`)
- Writing output files: `13 ms` (`0.013 s`)
- Total processing time: `17024 ms` (`17.024 s`)

This project uses a streaming approach, so it does not load the full CSV file into memory. Memory usage grows mainly with the number of distinct campaigns, not with the total number of rows.
