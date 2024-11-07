# Spring Boot CI/CD with GitHub Actions

This guide walks you through setting up a Continuous Integration and Continuous Deployment (CI/CD) pipeline for a Spring Boot application using GitHub Actions and deploying it on an AWS EC2 instance.

## Author - Ramanuj Das

## Steps

### 1. Create an EC2 Instance with Amazon Linux

1. Go to the AWS Console.
2. Launch a new EC2 instance with an Amazon Linux image.

### 2. Connect to the Instance

Connect to your EC2 instance via SSH.

### 3. Install Required Software

#### Install Java

```bash

sudo yum install java-21-amazon-corretto-headless -y

```


## 6. Create a Script for the Spring Boot Application

1. Create a new directory:

    ```bash
    mkdir -p /home/ec2-user/deployment
    ```

2. Create a systemd service file:

    ```bash
    sudo nano /etc/systemd/system/spring-boot-app.service
    ```

    Service file content:

```ini
[Unit]
Description=Spring Boot App
After=syslog.target
After=network.target[Service]
User=username
Type=simple

[Service]
ExecStart=java -jar /home/ec2-user/deployment/target/spring-boot-app.jar
Restart=always
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=springbootapp

[Install]
WantedBy=multi-user.target

```


## Setting Up CI/CD with GitHub Actions

### Creating Secrets

1. Push your Spring Boot project to GitHub.
2. Go to the Settings tab of your repository.
3. Navigate to Security > Secrets and variables > Actions.

Add the following secrets:

- `DEPLOY_HOST`: Copy your EC2 instance’s public IPv4 DNS and paste it here.
- `DEPLOY_KEY`: Open your .pem key file, copy the contents, and paste it.

### Creating ci-cd.yml

Create the workflow file for GitHub Actions:

1. Create the file `.github/workflows/ci-cd.yml`.
2. Add the following configuration:

```yaml

name: CI/CD for Spring Boot App

on:
  push:
    branches:
      - main

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up JDK
        uses: actions/setup-java@v3
        with:
          distribution: 'adopt'
          java-version: '21'

      - name: Build with Maven
        run: mvn clean package

      - name: Archive production artifacts
        uses: actions/upload-artifact@v3
        with:
          name: packaged-application
          path: target/*.jar

  deploy:
    needs: build
    runs-on: ubuntu-latest

    steps:
      - name: Download artifacts
        uses: actions/download-artifact@v3
        with:
          name: packaged-application
          path: target/

      - name: Setup key
        id: setup-key
        env:
          DEPLOY_KEY: ${{ secrets.DEPLOY_KEY }}
        run: |
          echo "$DEPLOY_KEY" >> $HOME/key.pem
          chmod 400 $HOME/key.pem

      - name: Copy JAR to EC2
        uses: appleboy/scp-action@master
        with:
          host: ${{ secrets.DEPLOY_HOST }}
          username: ec2-user
          key: ${{ secrets.DEPLOY_KEY }}
          source: "target/*.jar"
          target: "/home/ec2-user/deployment/"

      - name: Connect SSH and Restart Services
        run: |
          ssh -o StrictHostKeyChecking=no -i $HOME/key.pem ec2-user@${{ secrets.DEPLOY_HOST }} '
            sudo systemctl restart spring-boot-app
          '

```

Note: Ensure to replace placeholders with your EC2 instance’s actual details.

## Verifying the Deployment

You can check the deployment logs in the Actions tab on GitHub.

Now, access your Spring Boot API using your EC2 instance’s public IPv4 DNS and the port on which you have configured your spring boot app

### Congratulations! You have successfully deployed your Java Spring Boot application on an AWS EC2 instance with CI/CD integration using GitHub Actions.


## Additionally Deploying with Postgres Database

To deploy a Spring Boot application with a PostgreSQL database, we will use PostgreSQL on Docker.

### 1. Install Docker

```bash

sudo yum install docker -y

```

### 2. Start and configure Docker Service

```bash
sudo service docker start

sudo usermod -aG docker ec2-user

sudo systemctl restart docker

newgrp docker

```


### 3. Pull the PostgreSQL Image

```bash
docker pull postgres
```

### 4. Run the PostgreSQL Container

```bash
docker run --name postgres -e POSTGRES_PASSWORD=password -d -p 5432:5432 postgres
```

### 5. Update the Spring Boot Application Properties


### Congratulations! You have successfully deployed your Java Spring Boot application on an AWS EC2 instance with CI/CD integration using GitHub Actions.

