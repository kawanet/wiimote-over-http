cd %~p0
call mvn clean
call mvn assembly:assembly -DdescriptorId=src
pause