# Mail_Template
A small test project on Deep Linking.

Implemented the Deep Linking feature using Android Intent as well as Branch API.
Deep link to email apps with sender, receiver and content info.
Deep link to a cutomized url using Branch. (url: https://rxc7.app.link/mt)

User Guide:
In app deep linking: Choose or create a mail template and press the send floating action bar, 
                     the app will automatically redirect to the e-mail in the device (if exists),
                     and  auto-fill the receivers, title and content info from the template.

Web deep linking: Accessing the given url: https://rxc7.app.link/mt, 
                  the android device will redirect to the MailTemplate app if it's been installed,
                  if not, it will redirect the user to the download page.
