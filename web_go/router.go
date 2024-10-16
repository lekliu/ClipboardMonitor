package main

import (
	"github.com/gorilla/mux"
	"net/http"
)

func NewRouter() *mux.Router {
	r := mux.NewRouter()
	// Serve static files
	r.PathPrefix("/static/").Handler(http.StripPrefix("/static/", http.FileServer(http.Dir("static/"))))

	r.HandleFunc("/api/clipdata", saveClipboardData).Methods("POST")
	r.HandleFunc("/api/clipdata", getClipData).Methods("GET")
	r.HandleFunc("/insert/clipdata", insertClipboardPage).Methods("POST")

	r.HandleFunc("/show/clipdata", showMainPage).Methods("GET")
	r.HandleFunc("/", showMainPage).Methods("GET")
	return r
}
