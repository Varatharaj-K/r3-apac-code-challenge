# Developer Readme
Asset platform is a sample Corda And Backend Api integrated project.
Please refer ./asset-backend/design.png for full Flow

###PreRequisite:
- Oracle Java 1.8
- Gradle 6.1

## Deployment
###Corda nodes:
- Navigate to asset-corda directory \
`cd asset-corda/`
- Install Asset Corda to get local Maven repository \
`./gradlew clean install`
- After Build is success, deploy the Nodes (Notary and AssetHub) \
`./gradlew clean deployNodes`
- Once nodes are deployed, start the nodes \
`cd build/nodes/Notary/` \
`java -jar corda.jar`
- Once Notary started, from new tab start the AssetHub node \
`cd build/nodes/AssetHub/`\
`java -jar corda.jar`
-  Now Corda nodes are Started

### Backend (Spring-boot APIs)
- Navigate to asset-backend Directory \
`cd asset-backend`
- Then Start the Server \
`./gradlew runController`
- Now Backend server is started

## How to use Apis
- Import the below postman collection\
`asset-backend/Asset_Platform.postman_collection.json`
- Create three corda Accounts using Create Account API
Note: In this sample I have created the below accounts\
  `Varatha`\
  `John`\
  `Revanth`
- Then Using Create Asset Api, create the Asset
- Using ShipAsset Api ship the Asset
- Using Deliver Asset Api Update the Delivery detail
- Also, Fetch My asset is used to fetch all assets based on accounts
  

