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


## V6 changes

- Android status bar background changed to black (`#000000`).
- Status bar system icons are configured for white-on-black.
- Toolbar glyph artwork (refresh, expand, close) reduced further.
- Black toolbar pill width, height, position, and spacing remain unchanged.
- Refresh remains functional; the other two icons remain decorative.
- Navigation bar remains `#848BFF` with white system icons.
- App version: 1.5 (versionCode 6).


## V7 — reference-matched toolbar geometry

The toolbar capsule was resized using the supplied 686 × 1536 reference image:
- Approximate measured capsule bounds: 407 × 78 px.
- Width: 59.3% of screen width.
- Width-to-height ratio: 5.22:1.
- Capsule height is calculated from its width, so the shape scales proportionally across screen sizes.
- Corner radius is half the capsule height.
- The capsule remains horizontally centered independently of the clock and battery indicators.
- The existing black status bar, blue-violet navigation bar, small glyphs, and functional refresh button are preserved.
- App version: 1.6 (versionCode 7).


## V8 — clock suffix clipping fix

- The custom clock now uses `HH:mm:ss` in both 12-hour and 24-hour device settings.
- This removes the AM/PM suffix that was being clipped by the fixed clock area beside the centered pill.
- The pill width, height, center position, icon sizes, status bar, navigation bar, WebView, and refresh behavior are unchanged.
- App version: 1.7 (versionCode 8).
