# Kinomania Website

## Prerequisites

Before you begin, ensure you have met the following requirements:

- You have installed Java 17.
- You have MySQL Server 8.0 or higher.
- You have Apache Maven 3.9.3 or higher.
- Your MySQL DBMS has two databases - one for production and another one for testing purposes.
- You have installed Node.js 18.14.2.

## Setting Up

1. Clone the project from GitHub.
2. Open the `kinweb-backend` folder in any IDE (preferably Netbeans).
3. Clean and build the project so that all dependencies are present and the target folder appears.

## Configuring the Application

1. Change the following property values in `application.properties` and `application-test.properties` to reflect your database parameters:
    - `spring.datasource.url`
    - `spring.datasource.username`
    - `spring.datasource.password`<br />  
   Update these properties with the credentials of your production database in `application.properties`, and with your testing database in `application-test.properties`.

2. Position yourself in the `kinweb-backend` folder with the command prompt, and type `mvn flyway:migrate` to initialize the structure of your production database.

## Running the Backend

You can start the backend of the website either by running it in the IDE with `--seed` as its argument, or by positioning yourself in the recently created target folder with the command prompt and typing `java -jar kinomania-website-backend-0.0.1-SNAPSHOT.jar --seed`. This starts your backend and runs a seeder before starting the app that creates a `kinweb_images` folder in your C drive, starts downloading movies and shows from "https://www.themoviedb.org/" and populates your database tables with their JSON data; the `kinweb_images` folder with image data and creates a user with administrator privilege that has username admin and password admin. Note, you only need to pass `--seed` the first time you run the backend. Every other time you either run it in the IDE without any arguments, or by typing `java -jar kinomania-website-backend-0.0.1-SNAPSHOT.jar` in the command prompt. This is so that your production database doesn't get overwritten.

## Configuring the Frontend

After configuring the backend, position yourself in the `kinweb-frontend` folder with the command prompt and type `npm install`. This will install all necessary dependencies for the frontend.

## Running the Website

If you wish to see how it all looks, run the backend first either in the IDE without arguments or by the command prompt `java -jar kinomania-website-backend-0.0.1-SNAPSHOT.jar` while being in the target folder of `kinweb-backend`, and after that type `npm run dev` in the command prompt while being in the `kinweb-frontend` folder.

## Stopping the Application

If you wish to stop the backend from running, just type `Ctrl+C` in the same command prompt where the backend is running. The same goes for the frontend - if you wish to stop the frontend from running, type `Ctrl+C` in the same command prompt where the frontend is running.