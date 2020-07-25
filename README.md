# ssyp2020-ws03: “Call of Anoro€”  ![Deploy](https://github.com/Evgenius2020/ssyp2020-ws03/workflows/Deploy/badge.svg?branch=master)     
2d multiplayer shooter

## Features
* A lot of breathtaking gameplay mechanics, such as explosion on player exit
* Stats overlay
* Nickname selection
* Tilemaps
* Continuous deployment on Amazon EC2

## How to use
### Get binaries
* Build from source
  1. Clone this repo
  1. Go to “Call of Anoro€” directory
  1. Execute following commands: `./gradlew client:packageJvmFatJar` and `./gradlew server:assembleShadowDist`
  1. Built files now in `client/build/libs` and `server/build/libs`
* Download latest version [here](https://github.com/Evgenius2020/ssyp2020-ws03/releases)
### Launch
* Play on global server: `java -jar client-all-4.10.1.jar`
* Play over LAN
  1. Launch server: `java -jar server-all.jar`
  2. Launch client: `java -jar client-all-4.10.1.jar <server ip without port>`
 
