## One Click Run the Application
```
docker-compose up -d
```

## To Custom run your application
### SMTP Server Settings

#### Step 1
Change `application.properties`
Modify them with your service gmail address and app password
```
spring.mail.username=YOUR_GMAIL
spring.mail.password=YOUR_PASSWORD
```
#### Step 2
Build the Spring Boot App with Java 17+
```
cd makehandshake/makehandshake
mvn clean compile install -DskipTests
```
#### Step 3
```
cd ..
cd ..
docker-compose -f my-docker-compose.yml up
```