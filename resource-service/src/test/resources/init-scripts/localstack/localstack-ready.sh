#!/bin/bash

export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test

awslocal s3 mb s3://resource-bucket-test
echo "Resource s3 bucket created"