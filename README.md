# NoiseAware Back-end Developer Assessment

## Stack
* Language: Kotlin 1.4.32 
* Application framework: [Micronaut 2.5.6](https://docs.micronaut.io/latest/guide/index.html)
* Testing framework: [Kotest 4.3.0](https://kotest.io/docs/framework/framework.html) & [Mockk 1.11.0](https://mockk.io/)
* Database: [H2](https://www.h2database.com/html/quickstart.html)

## Getting started

### Prerequisites(MacOS)
* Java 11: Using SDKMAN! `sdk install java 11.0.11.hs-adpt`
* Docker: https://docs.docker.com/get-docker/
* Minikube: Using Homebrew `brew install minikube`

### Running tests
To run the test suite execute `./gradlew test` 

### Running the API
From root project directory
* Build executable Java artifact and Docker image `./gradlew clean shadowjar; docker build -t noiseaware:latest -f ./Dockerfile .`
* Start minikube `minikube start`
* Set minikube to pull image from local repository `eval $(minikube docker-env)`
* Apply the Kubernetes service and deployments files `kubectl apply -f k8s/service.yml -f k8s/deployment.yml`
* Open a separate terminal and run `minikube tunnel`
  * Note: Ingress type load balancer is usually backed by a cloud provider's load balancers. In absence of that, minikube provides the tunnel command to expose the external IP directly to any program running on the host operating system. To learn more visit [here](https://minikube.sigs.k8s.io/docs/handbook/accessing/#using-minikube-tunnel). 
* Service can be then accessed via `localhost:8080/messages`
    * For simplicity the application uses the H2 in memory database, so the Kubernetes deployment only uses a single instance.

## Examples
POST pump message: `curl -X POST -H "Content-Type: application/json" -d '{"timestamp": 1,"symbol":"aaa","volume":20,"temperature":15}' 'localhost:8080/messages'`

Optionally, load the provided sample .csv file via a script(set to silent mode to not overload your terminal window... live and learn :facepalm): `./upload_all.sh` 

Query messages for pump: `curl 'localhost:8080/messages?queryType=TOTAL_VOLUME&symbol=aaa'`

Query type is specified using a query parameter with applicable values being(case insensitive): `MAX_TIME_GAP, TOTAL_VOLUME, MAX_TEMPERATURE, WEIGHTED_AVERAGE_TEMPERATURE`

**Note: both `queryType` and `symbol` query parameters must be provided for the GET /messages endpoint or else an HTTP 400 will be returned**
