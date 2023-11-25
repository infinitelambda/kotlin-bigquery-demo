# Real-time Data Analytics with Kotlin and BigQuery

### Overview
This repository contains a demo application showcasing real-time data analytics with Kotlin and BigQuery. The demo
includes a form where users can submit their favorite food, express their interest in Kotlin, and provide comments on
the talk. The submitted data is processed in real-time, and the results are displayed on a live monitoring dashboard. 

### Table of contents
- [Prerequisites](#prerequisites)
- [Setup](#setup)
- [Usage](#usage)
- [Technologies Used](#technologies-used)
- [Live Demo](#live-demo)
- [License](#license)

### Prerequisites
Ensure that the following prerequisites are installed before running the demo:
- git
- Java 1.8 and above
- Docker (if you want to run the app as a container)

### Setup
1. Clone the repository:
```bash
git clone git@github.com:infinitelambda/kotlin-bigquery-demo.git
 ```

2. Navigate to the project directory:
```bash
cd kotlin-bigquery-demo
```
3. Compile the project to make sure all dependencies are satisfied
```bash
./gradlew clean compile
```
4. Create a Google Cloud project and enable the BigQuery API
5. Create a service account with Big Query Data Owner and Big Query Job User permissions
6. Setup up authentication on you local environment 
7. Create a BigQuery dataset with a table to store the results and a view to aggregate them for the dashboard. Example script can be found [here](big-query/schema.sql).

### Usage
Follow these steps to run the demo:

1. Provide the required environment variables:
    - `PROJECT_ID` - your Google Cloud project id
    - `DATASET_NAME` - your BigQuery dataset name
    - `RESULTS_TABLE_NAME` - name of that table that stores the submitted form results
    - `AGG_RESULTS_VIEW_NAME` - name of the view that aggregates the form results for dashboard presentation
    - `HOST` - optional. The embedded server host. Defaults to `0.0.0.0`.
    - `PORT` - optional. The embedded server port. Defaults to `8080`
2. Run the app 
    - As a standalone server
    ```bash
    ./gradlew run
    ```
   - As a docker container
   ```bash 
   ./gradlew runDocker
   ```
3. Build a docker image for distribution to a prod environment (or example Cloud Run)
```bash
./gradlew buildImage  -PtargetEnv=PROD
```

### Technologies Used
The demo application leverages the following technologies:

- **Kotlin**: Robust, concise and expressive. Used to build the backend and frontend of the app. It has 100% interoperability with existing Java libraries so integration with Google Cloud technologies is easy and fast. 
- **BigQuery**: Fast and performant for big data sets. Supports streaming data inserts. Build in advanced functionalities like ML model traning and execution.

### Live Demo
Visit [this link](https://kotlin-bigquery-demo-service-l4q4uvpkra-uc.a.run.app/) to access the live demo. Experience real-time form submissions and instant updates on the monitoring dashboard.



### License
This project is licensed under the GNU GPL version 3 - see the [LICENSE](LICENSE) file for details.
