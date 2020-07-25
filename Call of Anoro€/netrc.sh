#!/bin/sh

cp server/build/libs/* aws
cd aws
aws deploy push --application-name call-of-shit --s3-location s3://call-of-shit-bucket/call.zip --source .
aws deploy create-deployment --application-name call-of-shit --s3-location bucket=call-of-shit-bucket,key=call.zip,bundleType=zip  --deployment-config-name CodeDeployDefault.AllAtOnce --deployment-group-name call-group --ignore-application-stop-failures
