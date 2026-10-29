@echo off
cd /d "%~dp0"
copy /y "data\vfs.csv" "data\reset.csv" >nul
call gradlew.bat run --args="data/reset.csv scripts/all-commands.txt"