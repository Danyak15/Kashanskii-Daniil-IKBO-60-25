@echo off
cd /d "%~dp0"
call gradlew.bat run --args="data/files.csv scripts/startup.txt"