version = "1.0.8"
description = "Adds more account/profile options in the settings menu, such as for changing display name and pronouns"

aliucord {

    changelog.set("""
        1.0.8
        - Improved optimisation when fetching user values
        - Fixed several bugs with save button showing at incorrect times on user profile screen
        - Rotating device on user profile screen no longer resets recently typed pronouns
        - Enter button now hides keyboard on pronouns field

        1.0.7
        - Only sends API request to update when necessary

        1.0.6
        - Fixed bug where cursor would reset to the start of the pronouns edit field when focusing
        - Fixed bug where pronouns would be underlined on preview when unfocusing from the edit field

        1.0.5
        - More closely mimics Discord's stock save button behaviour on User Profile screen for pronouns field
        - Remove shadow from pronouns edit field

        1.0.4
        - Fixed deranged bug where the 'User Profile' screen would show and set another user's pronouns

        1.0.3
        - More improvements to API request headers and super properties handling

        1.0.2
        - Hotfix

        1.0.1
        - Improved API request headers and super properties handling
        - Added more translations and UI now respects the app's chosen language
        - Fixed issue where accounts screen would display incorrectly

        1.0.0
        - First release!
    """.trimIndent())

    excludeFromUpdaterJson.set(false)
}