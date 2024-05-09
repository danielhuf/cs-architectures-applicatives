#!/bin/bash

echo "Starting the setup and run script..."

# Check if fichier.txt exists
if [ ! -f "../fichier.txt" ]; then
  echo "Error: fichier.txt not found in the root directory. Exiting script."
  exit 1
else
  echo "fichier.txt found. Proceeding..."
fi

echo "Formatting Kafka storage..."
(kafka-storage.sh format -t $(kafka-storage.sh random-uuid) -c /opt/kafka/config/kraft/server.properties > kafka-storage.log 2>&1) &
echo "Kafka storage formatted. Check kafka-storage.log for details."

echo "Starting Kafka server..."
(kafka-server-start.sh /opt/kafka/config/kraft/server.properties > kafka-server.log 2>&1) &
echo "Kafka server is starting. Check kafka-server.log for details."

# Giving Kafka some time to start up (we're not really checking the execution of the server)
echo "Waiting for Kafka to start up..."
sleep 10
echo "Kafka should be up now."

echo "Packaging Java application..."
mvn package > mvn-package.log 2>&1
echo "Java application packaged. Check mvn-package.log for details."

echo "Creating Kafka topics..."
java -cp ../target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.CreateTopics > create-topics.log 2>&1
echo "Kafka topics created. Check create-topics.log for details."

echo "Starting Java applications for processing..."
(java -cp ../target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.LowercaseWords > lowercase-words.log 2>&1) &
echo "LowercaseWords application started. Check lowercase-words.log for details."

(java -cp ../target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.TagWords > tag-words.log 2>&1) &
echo "TagWords application started. Check tag-words.log for details."

echo "Running CategoryCount application..."
java -cp ../target/tp-kafka-0.0.1-SNAPSHOT.jar if4030.kafka.CategoryCount VER &
echo "CategoryCount application is running. Output is shown directly."

# Giving Java programs some time to initialize
sleep 5

echo "Sending fichier.txt to Kafka producer..."
(cat ../fichier.txt | kafka-console-producer.sh --bootstrap-server localhost:9092 --topic lines-stream > producer.log 2>&1) &
echo "fichier.txt sent to Kafka producer. Check producer.log for details."

echo "Setup and initial executions are complete."
echo "Proceed with running the interactive_producer.sh script for interactive Kafka producer."
