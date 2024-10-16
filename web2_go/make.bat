cd .

SET GOOS=windows
SET GOARCH=amd64
go build -o ./bin/window/ClipboardMonitor.exe

SET GOOS=linux
SET GOARCH=amd64
go build -o  ./bin/ClipboardMonitor

pause


