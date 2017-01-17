# InLink
Connect everywhere, disconnect anytime.

## Inspiration
1. Annoyed at having to exchange contact information with new people you meet?
Just bump phones or scan a QR code to automatically exchange contact information and also connect on your preferred social networks!

2. Need to maintain a business relationship with somebody only for a limited duration of time? Say a tour guide you need to keep in 
touch with only for a day or two, and you would rather not share your real contact info?
InLink lets you share an InLink number,that the contact can call or text on, and expires after a set duration of time, 
letting you keep your privacy.

## How we built it
1. For the first part, when you bump phones, it exchanges contact information. Since the official facebook API doesn't allow sending 
friend requests, we use some custom javascript running on an offscreen webview to actually do the sending and requesting part. Similar 
feature planned for future releases.

2. For the temporary phone numbers, we use Twilio to generate a custom phone number per set of users, routing the call between them 
until expiry of that relationship.
