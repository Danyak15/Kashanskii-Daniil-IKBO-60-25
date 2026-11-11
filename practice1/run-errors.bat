@echo off
cd /d "%~dp0"
call gradlew.bat run --args="data/vfs.csv scripts/errors.txt"