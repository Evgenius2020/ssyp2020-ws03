#!/bin/sh
# cat >> ~/.netrc << EOM
# machine api.heroku.com
#   login ilyamerzlakov@gmail.com
#   password $HSECRET
# machine git.heroku.com
#   login ilyamerzlakov@gmail.com
#   password $HSECRET
# EOM

# cd server/build/libs
# git init
# git config --global user.email "nope@nope.com"
# git config --global user.name "nope"
# heroku git:remote -a call-of-anoroc
# git add -A
# git commit -a -m "oh shit i am sorry"
# git push --force heroku master
cp server/build/libs/* aws
cd aws
aws deploy push --application-name call-of-shit --s3-location s3://call-of-shit-bucket/call.zip --source .
aws deploy create-deployment --application-name call-of-shit --s3-location bucket=call-of-shit-bucket,key=call.zip,bundleType=zip  --deployment-config-name CodeDeployDefault.AllAtOnce --deployment-group-name call-group --ignore-application-stop-failures
