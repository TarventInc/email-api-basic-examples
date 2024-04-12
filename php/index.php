<?php
$apiKey = "0123456789012345678901263456789012345678901234567890123456789012";
$accountId = "012345678901234567";
$txRequest = createTransaction();

$baseUri = "https://api.tarvent.com/graphql";
$headers = [
    "Accept: application/json",
    "Content-Type: application/json",
    "X-API-KEY: $apiKey",
    "Account: $accountId",
];

echo $txRequest;

$ch = curl_init($baseUri);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
curl_setopt($ch, CURLOPT_HTTP_VERSION, CURL_HTTP_VERSION_2_0);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_VERBOSE, true);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
curl_setopt($ch, CURLOPT_POSTFIELDS, $txRequest);

$response = curl_exec($ch);
curl_close($ch);

if ($response === FALSE) {
    echo "cUrl error (#%d): %s<br>\n";
}

echo $response;

function createTransaction()
{
    $tarventTxSettings = [
        "tracking" => [
            "opens" => true,
            "clicks" => true
        ],
        "ignoreSuppressCheck" => false
    ];

    $tarventTxHeader = [
        "from" => [
            "name" => "Tarvent Team",
            "emailAddress" => "hello@yoursendingdomain.com"
        ],
        "subject" => "This is a test"
    ];

    $tarventTxContents = [
        "templateId" => "null",
        "contentBodies" => [
            [
                "clickTracking" => true,
                "mimeType" => "HTML",
                "charset" => "UTF8",
                "bodyContent" => "<html><body><p>Hello {{Tx.VariableData.FirstName}},</p><p>OMG, it's working!</p></body></html>"
            ]
        ]
    ];

    $recipientList = [
        addRecipient("Developer", "developer@tarvent.com", null, "TO", [], [[ "name" => "FirstName", "value" => "Developer" ]])
    ];

    $transactionRequest = [
        "groupName" => "",
        "settings" => $tarventTxSettings,
        "header" => $tarventTxHeader,
        "content" => $tarventTxContents,
        "recipients" => $recipientList
    ];

    $graphqlCall = [
        "query" => 'mutation createTransaction($input: CreateTransactionInput!) { createTransaction(input: $input) { emailAddress errorCode errorMsg requestId transactionId }}',
        "variables" => ["input" => $transactionRequest]
    ];

    $txRequest = json_encode($graphqlCall);

    return $txRequest;
}

function addRecipient($name, $email, $contactId, $type, $metadata, $variables)
{
    $recipient = new stdClass();
    $recipient->name = $name;
    $recipient->emailAddress = $email;
    $recipient->contactId = $contactId;
    $recipient->type = $type;
    $recipient->metadata = $metadata;
    $recipient->variables = $variables;

    return $recipient;
}
?>