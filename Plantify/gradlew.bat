@rem Gradle wrapper script for Windows

@echo off
if not "%JAVA_HOME%" == "" set JAVA_EXE="%JAVA_HOME%\bin\java.exe"
if "%JAVA_EXE%" == "" set JAVA_EXE=java

%JAVA_EXE% -jar "%~dp0gradle\wrapper\gradle-wrapper.jar" %*
