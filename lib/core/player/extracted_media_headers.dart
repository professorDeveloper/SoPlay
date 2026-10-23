/// Headers for media discovered inside an embed page.
Map<String, String> extractedMediaHeaders({
  required String pageUrl,
  required String userAgent,
  required Map<String, String> pageHeaders,
  required Map<String, dynamic> capturedHeaders,
  required List<String> captureHeaders,
}) {
  final headers = <String, String>{};
  capturedHeaders.forEach((key, value) {
    if (value == null || key.isEmpty) return;
    if (captureHeaders.any((h) => h.toLowerCase() == key.toLowerCase()) ||
        key.toLowerCase().startsWith('x-')) {
      headers[key] = value.toString();
    }
  });
  // Navigation into the embed uses the catalog's Referer. Its subsequent
  // media request belongs to the embed, and uses the WebView's real agent.
  // Actual intercepted request headers remain authoritative.
  bool contains(String name) =>
      headers.keys.any((h) => h.toLowerCase() == name.toLowerCase());
  pageHeaders.forEach((key, value) {
    if (const {'user-agent', 'referer', 'origin'}.contains(key.toLowerCase())) {
      return;
    }
    if (!contains(key)) headers[key] = value;
  });
  if (!contains('User-Agent')) headers['User-Agent'] = userAgent;
  if (!contains('Referer')) {
    headers['Referer'] = '${Uri.parse(pageUrl).origin}/';
  }
  if (!contains('Origin')) headers['Origin'] = Uri.parse(pageUrl).origin;
  return headers;
}
