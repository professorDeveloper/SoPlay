import 'package:flutter_test/flutter_test.dart';
import 'package:soplay/core/player/extracted_media_headers.dart';

void main() {
  test('media uses embed context rather than catalog navigation headers', () {
    final headers = extractedMediaHeaders(
      pageUrl: 'https://embed.test/e/123',
      userAgent: 'Device WebView',
      pageHeaders: const {
        'User-Agent': 'Old backend agent',
        'Referer': 'https://catalog.test/',
        'Origin': 'https://catalog.test',
        'X-Token': 'token',
      },
      capturedHeaders: const {},
      captureHeaders: const ['User-Agent', 'Referer', 'Origin'],
    );
    expect(headers['User-Agent'], 'Device WebView');
    expect(headers['Referer'], 'https://embed.test/');
    expect(headers['Origin'], 'https://embed.test');
    expect(headers['X-Token'], 'token');
  });
  test(
    'captured headers are preserved case-insensitively without duplicates',
    () {
      final headers = extractedMediaHeaders(
        pageUrl: 'https://embed.test/e/123',
        userAgent: 'Device WebView',
        pageHeaders: const {
          'User-Agent': 'Old agent',
          'Referer': 'https://catalog.test/',
        },
        capturedHeaders: const {
          'user-agent': 'Captured agent',
          'referer': 'https://frame.test/',
          'origin': 'https://frame.test',
          'x-token': 'one-shot',
        },
        captureHeaders: const ['User-Agent', 'Referer', 'Origin'],
      );
      expect(headers['user-agent'], 'Captured agent');
      expect(headers['referer'], 'https://frame.test/');
      expect(headers['origin'], 'https://frame.test');
      expect(
        headers.keys.where((k) => k.toLowerCase() == 'user-agent').length,
        1,
      );
      expect(headers.keys.where((k) => k.toLowerCase() == 'referer').length, 1);
      expect(headers['x-token'], 'one-shot');
    },
  );
}
