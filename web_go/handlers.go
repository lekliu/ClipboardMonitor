package main

import (
	"encoding/json"
	"html/template"
	"net/http"
	"sync"
)

type ClipboardData struct {
	Key   string `json:"key"`
	Value string `json:"value"`
}

var (
	clipData = make(map[string]string)
	mu       sync.RWMutex
)

func initData() {
	for i := 0; i < 10; i++ {
		clipData[generateKey(i)] = "0"
	}
}

func generateKey(i int) string {
	return "c" + string('0'+i)
}

func saveClipboardData(w http.ResponseWriter, r *http.Request) {
	if r.Method == http.MethodPost {
		var data map[string]string
		if err := json.NewDecoder(r.Body).Decode(&data); err != nil {
			http.Error(w, err.Error(), http.StatusBadRequest)
			return
		}

		mu.Lock()
		defer mu.Unlock()

		for k, v := range data {
			clipData[k] = v
		}

		w.WriteHeader(http.StatusOK)
		return
	}
}

func getClipData(w http.ResponseWriter, r *http.Request) {
	mu.RLock()
	defer mu.RUnlock()

	//w.Header().Set("Content-Type", "application/json")
	//json.NewEncoder(w).Encode(clipData)

	data := make([]ClipboardData, 0, 10)
	for i := 0; i < 10; i++ {
		data = append(data, ClipboardData{Key: generateKey(i), Value: clipData[generateKey(i)]})
	}
	// fmt.Println(data)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(data)
}

func insertClipboardPage(w http.ResponseWriter, r *http.Request) {
	var data []ClipboardData
	if err := json.NewDecoder(r.Body).Decode(&data); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	mu.RLock()
	defer mu.RUnlock()

	for i := 9; i > 0; i-- {
		clipData[generateKey(i)] = clipData[generateKey(i-1)]
	}
	for _, item := range data {
		clipData[generateKey(0)] = item.Value
	}
	//fmt.Println(clipboardMap)
	w.WriteHeader(http.StatusOK)
}

func showMainPage(w http.ResponseWriter, r *http.Request) {
	mu.RLock()
	defer mu.RUnlock()

	tmpl := template.Must(template.New("index.html").ParseFiles("templates/index.html"))

	if err := tmpl.Execute(w, nil); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
}
