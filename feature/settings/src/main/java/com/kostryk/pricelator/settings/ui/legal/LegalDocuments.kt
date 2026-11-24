package com.kostryk.pricelator.settings.ui.legal

data class LegalSection(val title: String, val body: String)

data class LegalDocument(
    val lastUpdated: String,
    val sections: List<LegalSection>
)

private const val CONTACT_EMAIL = "nvlad8725@gmail.com"

object LegalDocuments {

    val privacyPolicy = LegalDocument(
        lastUpdated = "November 24, 2026",
        sections = listOf(
            LegalSection(
                title = "Overview",
                body = "Pricelator is developed by Vladyslav Kostryk. This policy explains what data the app " +
                    "processes and why. Pricelator does not require an account, does not show ads and does " +
                    "not sell or share your personal data."
            ),
            LegalSection(
                title = "Camera",
                body = "The app uses your camera only to read prices from price tags. Camera frames are " +
                    "processed on your device by on-device text recognition (Google ML Kit). Images are not " +
                    "saved and are never uploaded to any server."
            ),
            LegalSection(
                title = "Exchange rates",
                body = "To convert prices, the app downloads current exchange rates from ExchangeRate-API " +
                    "(exchangerate-api.com). These requests do not include any personal data, although the " +
                    "provider can see your device's IP address as with any internet request. Rates are " +
                    "cached on your device so the app also works offline."
            ),
            LegalSection(
                title = "Crash reports",
                body = "Release versions of the app use Firebase Crashlytics (Google) to collect crash " +
                    "reports. A report can include the device model, operating system version, app version " +
                    "and an installation identifier. This data is used only to find and fix errors. See " +
                    "Google's privacy policy for how Firebase processes data."
            ),
            LegalSection(
                title = "Data stored on your device",
                body = "Your settings (language, theme, preferred currencies) and cached exchange rates are " +
                    "stored locally on your device. They are deleted when you clear the app's data or " +
                    "uninstall the app."
            ),
            LegalSection(
                title = "Children",
                body = "Pricelator is not directed at children under 13 and does not knowingly collect " +
                    "personal data from them."
            ),
            LegalSection(
                title = "Changes to this policy",
                body = "This policy may be updated when the app changes. The date at the top shows the " +
                    "latest version."
            ),
            LegalSection(
                title = "Contact",
                body = "If you have questions about this policy, contact $CONTACT_EMAIL."
            )
        )
    )

    val termsAndConditions = LegalDocument(
        lastUpdated = "November 24, 2026",
        sections = listOf(
            LegalSection(
                title = "Acceptance",
                body = "By installing or using Pricelator you agree to these terms. If you do not agree, " +
                    "please do not use the app."
            ),
            LegalSection(
                title = "The service",
                body = "Pricelator reads prices from price tags with your camera and converts them into " +
                    "another currency. The app is provided free of charge for personal use."
            ),
            LegalSection(
                title = "Accuracy",
                body = "Conversions are for information only. Exchange rates come from a third-party " +
                    "provider, may be delayed or outdated, and can differ from the rates used by banks, " +
                    "card issuers or shops. Text recognition can misread a price. Always check the original " +
                    "price before making a purchase or financial decision."
            ),
            LegalSection(
                title = "No warranty",
                body = "The app is provided \"as is\" and \"as available\", without warranties of any kind, " +
                    "including accuracy, availability or fitness for a particular purpose."
            ),
            LegalSection(
                title = "Limitation of liability",
                body = "To the extent permitted by law, the developer is not liable for any loss or damage " +
                    "arising from the use of the app or from relying on the prices and conversions it shows."
            ),
            LegalSection(
                title = "Third-party services",
                body = "The app relies on third-party services such as ExchangeRate-API, Google ML Kit and " +
                    "Firebase Crashlytics. Their own terms and policies apply to those services."
            ),
            LegalSection(
                title = "Intellectual property",
                body = "The app, its design and its code belong to the developer. You may not copy, modify " +
                    "or redistribute the app except as permitted by law."
            ),
            LegalSection(
                title = "Changes to these terms",
                body = "These terms may be updated from time to time. Continuing to use the app after an " +
                    "update means you accept the new terms."
            ),
            LegalSection(
                title = "Governing law",
                body = "These terms are governed by the laws of Ukraine."
            ),
            LegalSection(
                title = "Contact",
                body = "If you have questions about these terms, contact $CONTACT_EMAIL."
            )
        )
    )
}
