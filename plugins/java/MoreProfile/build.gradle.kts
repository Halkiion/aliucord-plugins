version = "1.0.5"
description = "Adds more account/profile options in the settings menu, such as for changing display name and pronouns"

aliucord {

    changelog.set("""
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