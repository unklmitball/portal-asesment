# Portal Asesmen — V4

Android WebView wrapper for `https://asesemen-ekamas.carrd.co`.

## V4 changes

- Toolbar pill is positioned at the exact horizontal center of the screen and uses 64% of the screen width. The clock and battery indicators are separate overlays, so they cannot push the pill left or right.
- Restores the system status/navigation bars and the custom clock + battery row.
- Replaces text glyphs with simple drawn white line icons. All three icons remain decorative and have no click actions.
- Keeps normal HTTP/HTTPS navigation in the same WebView, including redirects. With multiple-window support disabled, links using `target="_blank"` and `window.open()` are treated as top-level navigation in the current WebView.
- Adds cleartext HTTP permission for the portal's current HTTP destination.

## Build APK using GitHub Actions

1. Upload the **contents** of this folder to the root of the GitHub repository (not the outer `PortalAsesmenV4` folder).
2. Make sure `.github/workflows/build-apk.yml` is uploaded too. On Windows, enable **View → Show → Hidden items** if `.github` is not visible.
3. Open **Actions** in the repository and run **Build APK**.
4. When the run completes, download artifact `PortalAsesmen-APK-v4` and extract `app-debug.apk`.

## Security note

This build permits unencrypted HTTP traffic because the current portal redirects to an HTTP server. HTTP does not encrypt login details or page contents. Avoid entering passwords or other sensitive information on an HTTP page; the portal operator should ideally provide HTTPS.


## V5 changes

- Navigation bar color set to `#848BFF`, with white Android navigation icons.
- The left refresh glyph now reloads the currently displayed WebView page.
- The center and right glyphs remain decorative.
- Toolbar pill dimensions and centered placement are unchanged.
- Only the three white glyph drawings were reduced in size; the black pill background was not resized.
- App version: 1.4 (versionCode 5).
