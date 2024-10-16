package main

import (
	"encoding/json"
	"html/template"
	"net/http"
	"strings"
	"sync"
)

type ClipboardData struct {
	Key   string `json:"key"`
	Value string `json:"value"`
}

var (
	clipboardMap = make(map[string]string)
	mu           sync.Mutex
)

// 定义所有可能的键
var keys = []string{"C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9"}

func saveClipboardData(w http.ResponseWriter, r *http.Request) {
	var data []ClipboardData
	if err := json.NewDecoder(r.Body).Decode(&data); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	mu.Lock()
	defer mu.Unlock()

	for _, item := range data {
		clipboardMap[item.Key] = item.Value
	}
	//fmt.Println(clipboardMap)
	w.WriteHeader(http.StatusOK)
}

func getClipboardData(w http.ResponseWriter, r *http.Request) {
	mu.Lock()
	defer mu.Unlock()

	data := make([]ClipboardData, 0, len(keys))
	for _, k := range keys {
		v, exists := clipboardMap[k]
		if !exists {
			v = "" // 填充空值
		}
		data = append(data, ClipboardData{Key: k, Value: v})
	}
	// fmt.Println(data)

	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(data)
}

func showClipboardPage(w http.ResponseWriter, r *http.Request) {
	mu.Lock()
	defer mu.Unlock()

	data := make(map[string]string)
	for _, k := range keys {
		v, exists := clipboardMap[k]
		if !exists {
			v = "" // 填充空值
		}
		data[k] = v
	}

	// 获取当前请求的主机（包含端口）
	host := r.Host
	data["Host"] = host

	tmpl := template.Must(template.New("index.html").Funcs(template.FuncMap{
		"hasPrefix": strings.HasPrefix,
	}).ParseFiles("templates/index.html"))

	if err := tmpl.Execute(w, data); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}
	// fmt.Println(data)
}

func insertClipboardPage(w http.ResponseWriter, r *http.Request) {
	var data []ClipboardData
	if err := json.NewDecoder(r.Body).Decode(&data); err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}

	mu.Lock()
	defer mu.Unlock()

	for i := len(keys) - 1; i > 0; i-- {
		v, exists := clipboardMap[keys[i-1]]
		if !exists {
			v = ""
		}
		clipboardMap[keys[i]] = v
	}
	for _, item := range data {
		clipboardMap[keys[0]] = item.Value
	}
	//fmt.Println(clipboardMap)
	w.WriteHeader(http.StatusOK)
}
