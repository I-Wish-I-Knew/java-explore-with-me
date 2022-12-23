# java-explore-with-me
Template repository for ExploreWithMe project.

[Pull request](https://github.com/I-Wish-I-Knew/java-explore-with-me/pull/2#issue-1445108678)

This service is designed to help people quickly and easily find events they are interested in 
and share information about upcoming events with other users.

## Project structure

- **ewmService** - main service 

- **statisticService** - statistic collector

## Features

**Main service**
- Creation, view, moderation and publishing events;
- Event search by different parameters;
- Event invitation;
- Sending, confirming and rejecting participation requests;
- Creating compilations of published events;

**Statistic collector**:
- Saving the number of event uri views;
- Getting the statistic of views in accordance with the query parameters.

## Technologies used

- Java 11, Lombok;
- Spring Boot
- Hibernate, JPA;
- PostgreSQL;
- Swagger;
- Junit, Mockito;
- Docker, docker-compose;
- Maven (multi-module project);
- Postman.

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

## Specifications

- [**Main service**](https://github.com/I-Wish-I-Knew/java-explore-with-me/blob/33cab9892c937119deb0e439d39b4b055e088508/ewm-main-service-spec.json)
- [**Statistic service**](https://github.com/I-Wish-I-Knew/java-explore-with-me/blob/33cab9892c937119deb0e439d39b4b055e088508/ewm-stats-service-spec.json)
