package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
)

func main() {
	txRequest := createTransaction()
	apiKey := "0123456789012345678901263456789012345678901234567890123456789012"
	accountId := "012345678901234567"

	client := &http.Client{}
	url := "https://api.tarvent.com/graphql"
	req, err := http.NewRequest("POST", url, strings.NewReader(txRequest))
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println(req.URL)

	req.Header.Set("Accept", "application/json")
	req.Header.Set("Content-Type", "application/json")
	req.Header.Set("X-API-KEY", apiKey)
	req.Header.Set("Account", accountId)

	resp, err := client.Do(req)

	fmt.Println(resp, err)
	if err != nil {
		fmt.Println(err)
		return
	}

	defer resp.Body.Close()

	responseBody := new(bytes.Buffer)
	_, err = responseBody.ReadFrom(resp.Body)
	if err != nil {
		fmt.Println(err)
		return
	}

	fmt.Println(responseBody.String())
}

func createTransaction() string {
	tarventTxSettings := map[string]interface{}{
		"tracking": map[string]interface{}{
			"opens":  true,
			"clicks": true,
		},
		"ignoreSuppressCheck": false,
	}

	tarventTxHeader := map[string]interface{}{
		"from": map[string]interface{}{
			"name":         "Tarvent Team",
			"emailAddress": "hello@yoursendingdomain.com",
		},
		"subject":       "This is a test",
	}

	tarventTxContents := map[string]interface{}{
		"templateId": "null",
		"contentBodies": []map[string]interface{}{
			{
				"clickTracking": true,
				"mimeType":      "HTML",
				"charset":       "UTF8",
				"bodyContent":   "<html><body><p>Hello {{Tx.VariableData.FirstName}},</p><p>OMG, it's working!</p></body></html>",
			}},
	}

	recipientList := []map[string]interface{}{
		addRecipient("Developer", "developer@tarvent.com", "", "TO", []map[string]interface{}{}, []map[string]interface{}{
			{"name": "FirstName", "value": "Developer"}}),
	}

	transactionRequest := map[string]interface{}{
		"settings":   tarventTxSettings,
		"header":     tarventTxHeader,
		"content":    tarventTxContents,
		"recipients": recipientList,
	}

	jsonBytes, err := json.Marshal(map[string]interface{}{
		"query":     "mutation createTransaction($input: CreateTransactionInput!) { createTransaction(input: $input) { emailAddress errorCode errorMsg requestId transactionId }}",
		"variables": map[string]interface{}{"input": transactionRequest},
	})
	if err != nil {
		fmt.Println("Error:", err)
		return ""
	}

	return string(jsonBytes)
}

func addRecipient(name, email, contactId, typ string, metadata, variables []map[string]interface{}) map[string]interface{} {
	recipient := map[string]interface{}{
		"name":         name,
		"emailAddress": email,
		"contactId":    contactId,
		"type":         typ,
		"metadata":     metadata,
		"variables":    variables,
	}
	return recipient
}
