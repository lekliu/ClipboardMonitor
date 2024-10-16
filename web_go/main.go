package main

import (
	"log"
	"net/http"
)

func main() {
	router := NewRouter() // 创建新的路由
	log.Println("Starting server on :3388...")
	if err := http.ListenAndServe(":3388", router); err != nil {
		log.Fatalf("Failed to start server: %v", err)
	}
}