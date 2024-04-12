const axios = require('axios');
(async () => {
  console.log(await createTransaction());
}
)();
async function createTransaction() {
  const tarventTxSettings = {
    tracking: {
      opens: true,
      clicks: true
    },
    ignoreSuppressCheck: false
  };
  const tarventTxHeader = {
    from: {
      name: 'Tarvent team',
      emailAddress: 'hello@yoursendingdomain.com'
    },
    subject: 'This is a test',
  };
  const tarventTxContents = {
    templateId: null,
    contentBodies: [{
      clickTracking: true,
      mimeType: "HTML",
      charset: "UTF8",
      bodyContent: "<html><body><p>Hello {{Tx.VariableData.FirstName}},</p><p>OMG, it's working!</p></body></html>"
    }]
  };
  const recipientList = [
    addRecipient('Developer', 'developer@tarvent.com', null, 'TO', [], [
      { name: "FirstName", value: "Developer" }
    ])
  ];
  const transactionRequest = {
    groupName: '',
    settings: tarventTxSettings,
    header: tarventTxHeader,
    content: tarventTxContents,
    recipients: recipientList,
  };
  const graphqlCall = {
    query: 'mutation createTransaction($input: CreateTransactionInput!) { createTransaction(input: $input) { emailAddress errorCode errorMsg requestId transactionId }}',
    variables: {
      input: transactionRequest,
    }
  };
  const response = await axios.post('https://api.tarvent.com/graphql', graphqlCall, {
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json',
      'X-API-KEY': '0123456789012345678901263456789012345678901234567890123456789012',
      'Account': '012345678901234567',
    }
  });
  console.log(JSON.stringify(response.data));
}
function addRecipient(name, email, contactId, type, metadata, variables) {
  const recipient = {
    name: name,
    emailAddress: email,
    contactId: contactId,
    type: type,
    metadata: metadata,
    variables: variables
  };
  return recipient;
}
