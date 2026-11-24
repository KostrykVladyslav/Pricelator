#Pricelator 📸💸

Pricelator is a mobile app for Android that allows you to instantly scan price tags with your camera and convert prices to the desired currency in no time. The perfect assistant for traveling and shopping abroad.

## ✨ Key features

* **First-time scanning:** Place your camera on the price tag to instantly determine the price.
* **Currency conversion:** Automatically converts prices to your home currency.
* **Offline operation:** OCR works without an internet connection.
* **Simple interface:** Clean and intuitive design for quick use.
* **Multilingual and themes:** Supports multiple languages and light/dark themes.
## Setup

API secrets are not stored in the repository. Add them to `local.properties` (git-ignored):

```properties
EXCHANGE_RATE_BASE_URL=https://v6.exchangerate-api.com/
EXCHANGE_RATE_API_KEY=your_key_from_exchangerate-api.com
```

They are exposed to the code through `BuildConfig` of the `data` module. On CI, the same names can be set as environment variables.
