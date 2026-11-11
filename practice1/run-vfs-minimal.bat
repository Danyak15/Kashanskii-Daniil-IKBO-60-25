@echo off
cd /d "%~dp0"
call gradlew.bat run --args="data/minimal.csv scripts/startup.txt"