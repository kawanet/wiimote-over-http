cd %~p0
call mvn clean
call mvn assembly:assembly -DoutputDirectory=jar-with-dependencies
pause