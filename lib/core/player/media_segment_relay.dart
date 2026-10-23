import 'package:soplay/core/constants/app_constants.dart';

/// Only the image-wrapped TS CDN needs the server's network route. Signed
/// manifests must still be fetched by the device that extracted their token.
class MediaSegmentRelay {
  static bool needsManifestProxy(String url) {
    final uri = Uri.tryParse(url);
    if (uri == null) return false;
    // The embed rotates CDN domains. Its stream route is stable; restricting
    // this to yesterday's two hostnames silently bypasses segment rewriting.
    return uri.path.startsWith('/stream/') && uri.path.endsWith('.m3u8');
  }

  static String? rewrite(Uri url, {String? endpoint}) {
    if (url.scheme != 'https' ||
        url.userInfo.isNotEmpty ||
        url.port != 443 ||
        !RegExp(
          r'^p(?:16|19)-ad-site-sign-sg\.tiktokcdn\.com$',
        ).hasMatch(url.host) ||
        !url.path.startsWith('/ad-site-i18n-sg/') ||
        !url.path.endsWith('.image')) {
      return null;
    }
    return Uri.parse(
      endpoint ?? '${AppConstants.baseUrl}/contents/media-segment',
    ).replace(queryParameters: {'url': url.toString()}).toString();
  }
}
