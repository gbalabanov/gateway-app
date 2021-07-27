For running the gateway-app along with its dependnecies (PostgreSQL, RabbitMQ, Redis) use docker-compose up. Then use the existing Postman colletion in /misc/ folder to make requests.

For running the gateway-app test:
  1. use docker-compose -f docker-compose-env-only.yml up
  2. ./mvnw clean test

So far the project has one test only, which acts as a load test - executes predefined set of requests simultaneiously and then verifies all queues got equal load. This is to ensue the round-robbin mechanism.
