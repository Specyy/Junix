:: Clear UNC Message
cls

@echo off

:: Create a temporary drive letter mapped to your UNC root location
:: and effectively CD to that location
pushd \\DESKTOP-5SD3OPR\alvyn\eclipse-workspace\Junix\exports\Junix - 0.0.1

:: Do your work
java -jar "Junix - 0.0.1.jar"

:: Remove the temporary drive letter and return to your original location
popd

:: Press any key to continue...
pause