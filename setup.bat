call mvn clean package -DskipTests
call copy src\main\resources\* bin
call copy target\test-ftp-1.0.0.jar bin\
call mkdir bin\upload