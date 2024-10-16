package main

import (
	"github.com/gorilla/mux"
)

func NewRouter() *mux.Router {
	r := mux.NewRouter()
	r.HandleFunc("/api/clipdata", saveClipboardData).Methods("POST")
	r.HandleFunc("/api/clipdata", getClipboardData).Methods("GET")
	r.HandleFunc("/show/clipdata", showClipboardPage).Methods("GET")
	r.HandleFunc("/insert/clipdata", insertClipboardPage).Methods("POST")
	return r
}
