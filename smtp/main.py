import smtplib
import ssl
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart

# SMTP server configuration
smtp_server = "smtp.yourmailserver.com"
port = 587
username = "your_username"
password = "your_password"
from_email = "hello@yoursendingdomain.com"
to_email = "accountId@smtpapi.trvt.io"

# Create the MIMEText object
message = MIMEMultipart()
message["From"] = from_email
message["To"] = to_email

# Add custom headers (x-api-key, x-smtpapi, etc.)
message.add_header("x-api-key", "Your_API_Key")
message.add_header("x-smtpapi", '{"groupName":"","settings":{"tracking":{"opens":true,"clicks":true},"ignoreSuppressCheck":false},"header":{"from":{"name":"Tarvent Team","emailAddress":"hello@yoursendingdomain.com"},"subject":"This is a test"},"content":{"templateId":null,"contentBodies":[{"clickTracking":true,"mimeType":"HTML","charset":"UTF8","bodyContent":"<html><body><p>Hello,</p><p>OMG, it\'s working!</p></body></html>"}]},"recipients":[{"name":null,"emailAddress":"developer@tarvent.com","contactId":null,"type":"TO","metadata":[],"variables":[]}]}')

# Create SSL context
context = ssl.create_default_context()
# Send email
try:
    # Connect to SMTP server
    with smtplib.SMTP(smtp_server, port) as server:
        server.starttls(context=context)  # Secure the connection
        server.login(username, password)  # Log in to the SMTP server
        server.sendmail(from_email, to_email, message.as_string())  # Send the email
    print("Email sent successfully!")

except Exception as e:
    print("Failed to send email: {e}")