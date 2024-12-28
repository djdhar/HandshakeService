### Create Kafka Server
https://medium.com/@amberkakkar01/getting-started-with-apache-kafka-on-docker-a-step-by-step-guide-48e71e241cf2

#### Step 1
```
docker-compose up -d
```
#### Step 2
```
docker ps
```
#### Step 3
```
docker exec -it <kafka-container-id> /opt/kafka/bin/kafka-topics.sh --create --zookeeper zookeeper:2181 --replication-factor 1 --partitions 1 --topic djdhar
```

### SMTP Server Settings

#### Step 1
Change `application.properties`
Modify them with your service gmail address and app password
```
spring.mail.username=YOUR_GMAIL
spring.mail.password=YOUR_PASSWORD
```
#### Step 2
Run the SpringBoot app