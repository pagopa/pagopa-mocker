# Template for AWS Lambda functions
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=TODO-set-your-id&metric=alert_status)](https://sonarcloud.io/dashboard?id=TODO-set-your-id)

TODO: add a description

---

## Technology Stack

 - TypeScript  
 - ...

---

## Start Project Locally 🚀

### Prerequisites

 - Docker
 - npm Package Manager
 - AWS CLI
 - ...

---

## Develop Locally 💻

### Prerequisites

- git
- npm
- ...

### Run the project - AWS SAM

In order to simulate the Lambda function triggering in local environment, you must install AWS SAM CLI. If you haven't installed AWS SAM client, please refer to [this guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html#install-sam-cli-instructions).  
After the installation of AWS SAM client, initialize the Lambda function code running this command:  

`sam build -t utilities/test.sam.yaml`

After the compilation, it is possible to generate the needed tables executing this commands in `docker` folder:

`docker-compose up -d && sh db-init.sh`

After the generation, execute this command for run the Lambda function with AWS SAM client:  

`sam local invoke -e utilities/events/ec.lambda_test_1.ok.json TestFunction`

This command will generate and run a Docker container with the Lambda code in it using a custom event defined in the passed JSON.

### Run the project - ExpressJS

In order to simulate the barebone logic with ExpressJS, you must execute the following commands:

`tsc && node dist/expresstest`

Before this, if the DB container is not up or the tables were not generated, execute this commands in `docker` folder:

`docker-compose up -d && sh db-init.sh`

After this, you can call the endpoint at URL `http://localhost:3000/`.


### Testing 🧪

#### Unit testing

For run the unit tests, execute the command:  
`npm run test`

---

## Contributors 👥

Made with ❤️ by PagoPa S.p.A.

### Mainteiners

See `CODEOWNERS` file