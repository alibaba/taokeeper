call mvn clean -U eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=false -Declipse.addVersionToProjectName=true
call mvn deploy -Dmaven.test.skip -f pom_deploy.xml
@pause
