import 'package:flutter_test/flutter_test.dart';
import 'package:soplay/core/player/media_segment_relay.dart';

void main() {
  test('only known wrapped segment URLs use relay and signed query survives', () {
    const raw =
        'https://p16-ad-site-sign-sg.tiktokcdn.com/ad-site-i18n-sg/a.image?x-signature=a%2Bb&x-expires=123';
    final rewritten = MediaSegmentRelay.rewrite(Uri.parse(raw))!;
    expect(Uri.parse(rewritten).queryParameters['url'], raw);
    expect(Uri.parse(rewritten).path, '/api/contents/media-segment');
    expect(
      MediaSegmentRelay.rewrite(Uri.parse('https://cdn.test/video.ts')),
      isNull,
    );
    expect(
      MediaSegmentRelay.rewrite(
        Uri.parse(
          'https://p16-ad-site-sign-sg.tiktokcdn.com.evil.test/ad-site-i18n-sg/a.image',
        ),
      ),
      isNull,
    );
    expect(
      MediaSegmentRelay.needsManifestProxy(
        'https://rotated-cdn.test/stream/token/master.m3u8',
      ),
      isTrue,
    );
    expect(
      MediaSegmentRelay.needsManifestProxy(
        'https://hgplaycdn.com/stream/master.m3u8',
      ),
      isTrue,
    );
  });
}
