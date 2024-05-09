#!/bin/bash

mvn clean
if [ $? -eq 0 ]; then
  echo "Maven clean successful."
else
  echo "Maven clean failed. Exiting."
  exit 1
fi

java -cp ../target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.DeleteTopics

echo "Terminating Java applications..."
jps | grep 'CategoryCount\|LowercaseWords\|TagWords' | awk '{print $1}' | xargs kill -9

echo "Terminating Kafka processes..."
pkill -f kafka-server-start.sh
pkill -f kafka-console-producer.sh

echo "Terminating processes by port..."
for port in 9092; do
    fuser -k ${port}/tcp
done

echo "Cleanup complete."