#!/bin/sh
cat >> ~/.netrc << EOM
machine api.heroku.com
  login ilyamerzlakov@gmail.com
  password $HSECRET
machine git.heroku.com
  login ilyamerzlakov@gmail.com
  password $HSECRET
EOM

heroku apps