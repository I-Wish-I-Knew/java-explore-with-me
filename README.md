# java-explore-with-me
Template repository for ExploreWithMe project.

[Pull request](https://github.com/I-Wish-I-Knew/java-explore-with-me/pull/2#issue-1445108678)

This service is designed to help people quickly and easily find events they are interested in 
and share information about upcoming events with other users.

## Project structure

- **ewmService** - main service 

- **statisticService** - statistics collector

## Requirements
The application can be run locally or in a docker container, the requirements for each setup are listed below.

## Local
- [Java 11 SDK](https://www.oracle.com/de/java/technologies/javase/jdk11-archive-downloads.html)
- [Maven](https://maven.apache.org/download.cgi)

## Docker
- [Docker](https://www.docker.com/products/docker-desktop/)

## Run Local
````
$ mvn spring-boot:run -pl ewmService
````
ewmService will run by default on port 8080
````
$ mvn spring-boot:run -pl statisticService
````
statisticService will run by default on port 9090

Configure the port by changing server.port in application.properties

## Run Docker
First build the image:
````
$ docker-compose build
````
When ready, run it:
````
$ docker-compose up
````
Application will run by default on port 8080

Configure the port by changing ewm-service.port in docker-compose.yml.