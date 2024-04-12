using System.Dynamic;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;

Console.Write(CreateTransaction());
const string apikey = "0123456789012345678901263456789012345678901234567890123456789012";
const string accountId = "012345678901234567";
var txRequest = CreateTransaction();

using HttpClient client = new();
client.BaseAddress = new Uri("https://api.tarvent.com/graphql");
client.DefaultRequestHeaders.Add("Accept", "application/json");
client.DefaultRequestHeaders.Add("Content-Type", "application/json");
client.DefaultRequestHeaders.Add("X-API-KEY", apikey);
client.DefaultRequestHeaders.Add("Account", accountId);
var apiResult = await client.PostAsync("", txRequest, new CancellationToken());
var responseBody = apiResult.Content.ReadAsStringAsync(new CancellationToken()).Result;
Console.Write(responseBody);

static StringContent CreateTransaction()
{
    dynamic tarventTxSettings = new
    {
        tracking = new
        {
            opens = true,
            clicks = true,
        },
        ignoreSuppressCheck = false,
    };
    dynamic tarventTxHeader = new
    {
        from = new
        {
            name = "Tarvent Team",
            emailAddress = "hello@yoursendingdomain.com"
        },
        subject = "Tarvent Email Test"
    };
    dynamic tarventTxContents = new
    {
        templateId = "null",
        contentBodies = new List<dynamic> {
    new {
      clickTracking = true,
      mimeType = "HTML",
      charset = "UTF8",
      bodyContent = "<html><body><p>Hello {{Tx.VariableData.FirstName}},</p><p>OMG, it's working!</p></body></html>"
      }}
    };
    var recipientList = new List<ExpandoObject>
  {
    AddRecipient("The Developer", "developer@tarvent.com", null, "TO", new List<dynamic>(), new List<dynamic> {
      new { name = "FirstName", value = "Developer" } })
    };
    dynamic transactionRequest = new
    {
        settings = tarventTxSettings,
        header = tarventTxHeader,
        content = tarventTxContents,
        recipients = recipientList
    };
    var options = new JsonSerializerOptions
    {
        DefaultIgnoreCondition = JsonIgnoreCondition.Never,
        DictionaryKeyPolicy = JsonNamingPolicy.CamelCase,
        WriteIndented = true,
        PropertyNamingPolicy = JsonNamingPolicy.CamelCase
    };
    var graphqlCall = "{\"query\": \"mutation createTransaction($input: CreateTransactionInput!) { createTransaction(input: $input) { emailAddress errorCode errorMsg requestId transactionId }}\"," +
      "\"variables\": {\"input\": " + JsonSerializer.Serialize(transactionRequest, options) + "}}";
    var txRequest = new StringContent(graphqlCall, Encoding.UTF8, "application/json");
    return txRequest;
}

static ExpandoObject AddRecipient(string name, string email, string contactId, string type, List<dynamic> metadata, List<dynamic> variables)
{
    dynamic recipient = new ExpandoObject();
    recipient.name = name;
    recipient.emailAddress = email;
    recipient.contactId = contactId;
    recipient.type = type;
    recipient.metadata = metadata;
    recipient.variables = variables;
    return recipient;
}