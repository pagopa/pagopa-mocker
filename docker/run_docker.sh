# sh ./run_docker.sh <local|dev|uat> --skip-recreate

ENV=$1
RECREATE=$2

if [ -z "$ENV" ]
then
  ENV="local"
  echo "No environment specified: local is used."
fi

pip3 install yq

if [ "$ENV" = "local" ]; then
  image="service-local:latest"
  ENV="dev"
else
  if [ "$ENV" = "dev" ]; then
    containerRegistry="pagopadcommonacr.azurecr.io"
    echo "Running all dev images"
  elif [ "$ENV" = "uat" ]; then
    containerRegistry="pagopaucommonacr.azurecr.io"
    echo "Running all uat images"
  else
    echo "Error with parameter: use <local|dev|uat>"
    exit 1
  fi

  repository=$(yq -r '."microservice-chart".image.repository' ../helm/values-$ENV.yaml)
  image="${repository}:latest"
fi

export containerRegistry=${containerRegistry}
export image=${image}

FILE=.env
if test -f "$FILE"; then
    rm .env
fi
config=$(yq  -r '."microservice-chart".envConfig' ../helm/values-$ENV.yaml)
for line in $(echo $config | jq -r '. | to_entries[] | select(.key) | "\(.key)=\(.value)"'); do
    echo $line >> .env
done

keyvault=$(yq  -r '."microservice-chart".keyvault.name' ../helm/values-$ENV.yaml)
secret=$(yq  -r '."microservice-chart".envSecret' ../helm/values-$ENV.yaml)
for line in $(echo $secret | jq -r '. | to_entries[] | select(.key) | "\(.key)=\(.value)"'); do
  IFS='=' read -r -a array <<< "$line"
  response=$(az keyvault secret show --vault-name $keyvault --name "${array[1]}")
  value=$(echo $response | jq -r '.value')
  echo "${array[0]}=$value" >> .env
done

stack_name=$(cd .. && basename "$PWD")
if [ "$RECREATE" = "--skip-recreate" ]; then
    docker compose -p "${stack_name}" up -d
  else
    docker compose -p "${stack_name}" up -d --remove-orphans --force-recreate --build
fi

# waiting the containers
printf 'Waiting for the service'
attempt_counter=0
max_attempts=50
until $(curl --output /dev/null --silent --head --fail http://localhost:8080/info); do
    if [ ${attempt_counter} -eq ${max_attempts} ];then
      echo "Max attempts reached"
      exit 1
    fi

    printf '.'
    attempt_counter=$((attempt_counter+1))
    sleep 5
done
echo 'Service Started'