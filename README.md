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