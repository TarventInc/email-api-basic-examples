import json
import requests

def create_transaction():
  tarvent_tx_settings = {
    "tracking": {
      "opens": True,
      "clicks": True,
    },
    "ignoreSuppressCheck": False
  }
  tarvent_tx_header = {
    "from": {
      "name": "Tarvent Team",
      "emailAddress": "hello@yoursendingdomain.com"
    },
    "subject": "This is a test",
  }
  tarvent_tx_contents = {
    "templateId": "null",
    "contentBodies": [{
      "clickTracking": True,
      "mimeType": "HTML",
      "charset": "UTF8",
      "bodyContent": "<html><body><p>Hello {{Tx.VariableData.FirstName}},</p><p>OMG, it's working!</p></body></html>"
    }]
  }
  recipient_list = [
    add_recipient("Developer", "developer@tarvent.com", "", "TO", [{ "name": "FirstName", "value": "Developer"}], [])
  ]
  transaction_request = {
    "groupName": "",
    "settings": tarvent_tx_settings,
    "header": tarvent_tx_header,
    "content": tarvent_tx_contents,
    "recipients": recipient_list
  }
  graphql_call = {
    "query": "mutation createTransaction($input: CreateTransactionInput!) { createTransaction(input: $input) { emailAddress errorCode errorMsg requestId transactionId }}",
    "variables": {"input": transaction_request}
  }
  return json.dumps(graphql_call)

def add_recipient(name, email, contactId, type, metadata, variables):
  recipient = {
    "name": name,
    "emailAddress": email,
    "contactId": contactId,
    "type": type,
    "metadata": metadata,
    "variables": variables
  }
  return recipient

apikey = "0123456789012345678901263456789012345678901234567890123456789012"
account_id = "012345678901234567"
tx_request = create_transaction()
url = "https://api.tarvent.com/graphql"
headers = {
  "Accept": "application/json",
  "Content-Type": "application/json",
  "X-API-KEY": apikey,
  "Account": account_id
}
response = requests.post(url, data=tx_request, headers=headers)
response_body = response.text
print(response_body)
