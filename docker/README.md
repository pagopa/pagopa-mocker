# Docker Environment 🐳

`run_docker.sh` is a script to launch the image of this microservice and all the dependencies on
Docker.

## How to use 💻

You can use `local`, `dev` or `uat` images

`sh ./run_docker.sh <local|dev|uat>`

You can skip to recreate the images of Docker with `--skip-recreate`

---

ℹ️ _Note_: for **PagoPa ACR** is **required** the login `az acr login -n <acr-name>` (eg. az acr login --name pagopadcommonacr.azurecr.io)

ℹ️ _Note_: If you run the script without the parameter, `local` is used as default.

ℹ️ _Note_: When you select `local`, a new image of this microservice is created from your branch,
but the `dev` dependencies are used.

ℹ️ _Note_: The docker profile is used by the app inside the container (for the docker environment variables definition see: **docker/config/.env** file)