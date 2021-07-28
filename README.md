For running the gateway-app along with its dependencies (PostgreSQL, RabbitMQ, Redis) use:
 1. ./mvnw clean package; docker-compose up. 

Then use the existing Postman collection in /misc/ folder to make requests.
-----------------------------------------------------------------------------------------------
For running the gateway-app test use :
 
 1. docker-compose -f docker-compose-env-only.yml up -d; sleep 10; ./mvnw clean test; docker-compose -f docker-compose-env-only.yml down

So far the project has one test only, which acts as a load test - executes predefined set of requests simultaneously and then verifies all queues got equal load. This is to ensue the round-robbin mechanism.
