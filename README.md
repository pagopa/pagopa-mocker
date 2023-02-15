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

### Run the project

In order to simulate the Lambda function triggering in local environment, you must install AWS SAM CLI. If you haven't installed AWS SAM client, please refer to [this guide](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/install-sam-cli.html#install-sam-cli-instructions).  
After the installation of AWS SAM client, initialize the Lambda function code running this command:  

`sam build -t utilities/test.sam.yaml`

After the compilation, execute this command for run the Lambda function with AWS SAM client:  

`sam local invoke -e utilities/events/lambda.test.json TestFunction`

This command will generate and run a Docker container with the Lambda code in it using a custom event defined in the passed JSON.

### Testing 🧪

#### Unit testing

For run the unit tests, execute the command:  
`npm run test`

---

## Contributors 👥

Made with ❤️ by PagoPa S.p.A.

### Mainteiners

See `CODEOWNERS` file