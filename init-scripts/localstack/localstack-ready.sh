#!/bin/bash

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test

awslocal s3 mb s3://resource-permanent-bucket-mp3-microservices-stack
awslocal s3 mb s3://resource-staging-bucket-mp3-microservices-stack
echo "Resource s3 bucket created"