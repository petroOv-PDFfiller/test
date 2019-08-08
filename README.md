
# qa-integrations-tests

Requirements:
1. **Java 8** (newer versions produce errors upon building);
2. Install **lombok plugin** in IntelliJ IDEA (preferences -> plugins -> marketplace -> Lombok plugin). Restart IntelliJ IDEA;
3. [**SETTINGS.XML**](https://github.com/airslateinc/qa-integrations-tests/blob/master/src/test/resources/conf/settings.xml) - put it in Users/username/.m2 folder. Restart IntelliJ IDEA.


## run tests on Selenoid

- Add to mvn line: ```-Dbrowser=selenoid -DsHub=selenoid-qa.cinfra-prod.int```
- **or** to xml: ```<parameter name="browser" value="selenoid"/>  <parameter name="sHub" value="selenoid-qa.cinfra-prod.int"/>```
- Other params (not required): ```sBrowser=chrome/firefox/opera sBrowserVersion=74/66/58 enableVideo=true/false```

### Run Selenoid locally(Docker required) 

1. Start Hub
```docker run --rm -v /var/run/docker.sock:/var/run/docker.sock -v ${HOME}:/root -e OVERRIDE_HOME=${HOME} aerokube/cm:latest-release selenoid start --vnc --tmpfs 128 ```
2. Run UI
```docker run -d --name selenoid-ui --link selenoid -p 8080:8080 aerokube/selenoid-ui --selenoid-uri=http://selenoid:4444```

### Salesforce 

Developer org list with credentials in [**SalesforceOrg**](https://github.com/airslateinc/qa-integrations-tests/blob/master/src/main/java/data/salesforce/SalesforceOrg.java) Enum

Slack tests results channel list: 
- #ddd-at - DaDaDocs for Salesforce 
- #sf-as-api-tests - airSlate for Salesforce api tests
- #sf-as-app-tests - airSlate for Salesforce UI, HTML form, airSlate Bots
- #sf_dx - dx builds info

