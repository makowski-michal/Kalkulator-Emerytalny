@echo off
setlocal

SET "JAVA_HOME=C:\Program Files\Java\jdk-22"
SET "PATH=%JAVA_HOME%\bin;%PATH%"

SET "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.9.9"
SET "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
SET "MAVEN_ZIP=%TEMP%\maven-3.9.9.zip"

IF NOT EXIST "%MAVEN_CMD%" (
    echo Downloading Apache Maven 3.9.9...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol=[Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip' -OutFile '%MAVEN_ZIP%'"
    powershell -Command "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%USERPROFILE%\.m2\wrapper\dists' -Force"
    del "%MAVEN_ZIP%"
    echo Maven ready.
)

"%MAVEN_CMD%" %*
endlocal
