#!/bin/bash

# 定义程序的名称
PROGRAM="ClipboardMonitor"

chmod +x ./"$PROGRAM"

cd ..

# 循环启动程序
while true; do
    # 启动程序并将其放入后台
    ./bin/"$PROGRAM" &

    # 等待程序退出
    wait $!

    # 当程序异常退出时，重新启动
    echo "$PROGRAM has exited. Restarting..."

    # 等待一段时间后再重启，以避免快速重启导致资源耗尽
    sleep 2
done

