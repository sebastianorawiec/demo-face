# demo-face
Demo application allow to upload images - photos and next recognize persons on photos base on Azure AI solution for faces detection/recognition 


# How to build ?
before You start install azure cli, docker, docker compose and make free azure account 
cli can be downloaded from here 
https://docs.microsoft.com/en-us/cli/azure/install-azure-cli?view=azure-cli-latest 


after login to azure - create face recognition service 

az login

az group create --name ResourceGroup1 --location "West Europe"

az appservice plan create --name myAppServicePlan --resource-group ResourceGroup1 --sku S1 --is-linux

az cognitiveservices account create -n <name> -g ResourceGroup1 --kind Face --sku S0 -l WestEurope --yes

az cognitiveservices account keys list --name <name> -g ResourceGroup1 

Now we need to create persons gruop

curl -v -X PUT -H "Content-Type: application/json" -H "Ocp-Apim-Subscription-Key: <here You api key>" https://<name>.cognitiveservices.azure.com/face/v1.0/persongroups/group1 --data '{"name": "group1", "recognitionModel": "recognition_02"}'

You have  to add API key and url of service  to properties files but pls encrypt it before with jasypt 
next pls start You application with  -Djasypt.encryptor.password=herepassword

https://www.devglan.com/online-tools/jasypt-online-encryption-decryption


for local development pls strat DB , docker-compose -f docker/docker-compose.yml up -d


# Deployment to Azure


In order to deploy in Azure app services

#docker registry
az acr create --name <your docker registry> --resource-group ResourceGroup1 --sku Basic --admin-enabled true 

az acr credential show --name dokcerregistryseba

docker login <your docker registry>.azurecr.io --username <your docker registry>

#make custom postgres image
docker build  -t  postgres-facedemo -f Dockerfile.postgres .

docker tag postgres-facedemo dokcerregistryseba.azurecr.io/postgres-facedemo

docker push <your docker registry>.azurecr.io/postgres-facedemo


#build docker for app

mvn clean package 

docker tag facedemo1 <your docker registry>.azurecr.io/facedemo1

docker push <your docker registry>.azurecr.io/facedemo1

or 

mvn clean package docker:build -Dmaven.test.skip=true -DpushImage

(before) setup You registry and credetials to regisry in mave settings.xml locate in Your home dir ~./m2/settings.xml

<settings>
<servers>
<server>
    <id>azure</id>
    <username>registryname</username>
    <password>password</password>
</server>
</servers>
</settings>


change in dokcer docker-compose-azure.yml  urls to Your registry


az webapp create --resource-group ResourceGroup1 --plan myAppServicePlan --name <appname> --multicontainer-config-type compose --multicontainer-config-file docker-compose-azure.yml 

az webapp deployment container config --enable-cd true --name <appname>--resource-group ResourceGroup1

az webapp config appsettings set --name <appname> --resource-group ResourceGroup1 --settings ENCPASS='password used for jasypt' DOCKER_REGISTRY_SERVER_PASSWORD='password' DOCKER_REGISTRY_SERVER_URL='https://yourregistry.azurecr.io' DOCKER_REGISTRY_SERVER_USERNAME='name'

# Diagnostic


credentials for accessing dignostic web page 

az webapp deployment user set --user-name <name> --password <pass>

Every application has own domain with diagnostic tools, for our application it's 

<appname>.scm.azurewebsites.net , rule of thumb: application name + scm.azurewebsites.net

SSH access

az webapp create-remote-connection  --resource-group ResourceGroup1 -n <appname>

Verifying if app is running....
App is running. Trying to establish tunnel connection...
Opening tunnel on port: 44135
SSH is available { username: root, password: Docker! }
Ctrl + C to close

we can connect and even create tunnel to our postgre sql database

ssh root@localhost  -c aes256-cbc -p 44135 -L5555:dbpostgresql:5432 
The authenticity of host '[localhost]:44135 ([127.0.0.1]:44135)' can't be established.
ECDSA key fingerprint is SHA256:MPk7xB1IJ9NefQB5qEi/Br55nIYFDHyefUzjgwM8+HA.
Are you sure you want to continue connecting (yes/no/[fingerprint])? yes
Warning: Permanently added '[localhost]:44135' (ECDSA) to the list of known hosts.
root@localhost's password: 
root@ced822760293:~#  
