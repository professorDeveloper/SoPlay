import 'package:dio/dio.dart';
import 'package:soplay/core/player/local_hls_proxy.dart';
import 'package:soplay/core/player/media_segment_relay.dart';
// Opt-in live upstream verification: --dart-define=LIVE_PROVIDER_CHECK=true.
// Requires fresh anikage-sources.json served from localhost:8766 via adb reverse.
import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:soplay/core/player/media_controller.dart';
import 'package:soplay/core/network/user_agent.dart';
import 'package:soplay/core/player/webview_stream_extractor.dart';
import 'package:soplay/core/system/platform_utils.dart';
import 'package:soplay/features/detail/domain/entities/extractor_config_entity.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  const live = bool.fromEnvironment('LIVE_PROVIDER_CHECK');
  setUpAll(() async {
    await initTvPlatform();
    await initSozoUserAgent();
  });
  Future<void> verify(
    WidgetTester tester,
    String url,
    Map<String, String> headers,
    VideoFormat? format,
  ) async {
    final c = PlayerController.networkUrl(
      Uri.parse(url),
      httpHeaders: headers,
      formatHint: format,
    );
    try {
      await tester.pumpWidget(MaterialApp(home: Scaffold(body: c.buildView())));
      await c.initialize().timeout(const Duration(seconds: 45));
      expect(c.value.size.height, greaterThan(0));
      await c.play();
      final end = DateTime.now().add(const Duration(seconds: 25));
      while (c.value.position.inMilliseconds < 1500 &&
          DateTime.now().isBefore(end)) {
        await Future<void>.delayed(const Duration(milliseconds: 250));
        await tester.pump();
      }
      expect(c.value.hasError, isFalse, reason: c.value.errorDescription);
      expect(c.value.position.inMilliseconds, greaterThanOrEqualTo(1500));
      debugPrint(
        'LIVE_VERIFIED ${c.value.size.width}x${c.value.size.height} position=${c.value.position.inMilliseconds}ms',
      );
    } finally {
      await tester.pumpWidget(const SizedBox());
      await c.dispose();
    }
  }

  testWidgets('Nested player capture retains the inner frame origin', (
    tester,
  ) async {
    await tester.pumpWidget(const MaterialApp(home: Scaffold()));
    final stream = await WebViewStreamExtractor().extract(
      pageUrl: 'http://127.0.0.1:8766/nested-player.html',
      config: const ExtractorConfigEntity(
        hostPattern: '127.0.0.1',
        urlPatterns: ['.m3u8'],
        captureHeaders: ['Referer', 'Origin', 'User-Agent'],
        timeoutMs: 12000,
      ),
      pageHeaders: const {},
    );
    expect(stream, isNotNull);
    expect(stream!.headers['Referer'], 'http://10.0.2.2:8766/');
    expect(stream.headers['Origin'], 'http://10.0.2.2:8766');
  }, skip: !live);

  testWidgets('Reported PrimeSrc LuluStream embed extracts and plays', (
    tester,
  ) async {
    await tester.pumpWidget(const MaterialApp(home: Scaffold()));
    final stream = await WebViewStreamExtractor().extract(
      pageUrl: 'https://luluvdoo.com/e/u8f0iid8h1ze',
      config: const ExtractorConfigEntity(
        hostPattern: 'primesrc.me',
        urlPatterns: ['.m3u8', '.mpd', '.mp4'],
        captureHeaders: ['Referer', 'Origin', 'User-Agent', 'Cookie'],
        timeoutMs: 25000,
      ),
      pageHeaders: const {'Referer': 'https://primesrc.me/'},
    );
    expect(
      stream,
      isNotNull,
      reason: 'Embed must yield a media URL before reaching native player',
    );
    await verify(
      tester,
      stream!.url,
      stream.headers,
      stream.playType == 'hls' ? VideoFormat.hls : null,
    );
  }, skip: !live);
  for (final provider in ['Megg', 'Wave']) {
    testWidgets('Reported Anikage episode plays through $provider', (
      tester,
    ) async {
      final client = HttpClient();
      late List<dynamic> sources;
      try {
        final r = await (await client.getUrl(
          Uri.parse('http://127.0.0.1:8766/anikage-sources.json'),
        )).close();
        sources =
            jsonDecode(await utf8.decoder.bind(r).join()) as List<dynamic>;
      } finally {
        client.close();
      }
      final s = sources.cast<Map<String, dynamic>>().firstWhere(
        (s) => (s['label'] as String).startsWith('$provider · Sub'),
      );
      await verify(
        tester,
        s['url'] as String,
        Map<String, String>.from(s['headers'] as Map),
        s['container'] == 'hls' ? VideoFormat.hls : null,
      );
    }, skip: !live);
  }
  testWidgets('AV1 10-bit 1080p plays on Android platform decoder', (
    tester,
  ) async {
    await verify(tester, 'http://127.0.0.1:8766/av1-10bit.mp4', const {}, null);
  }, skip: !live);
  testWidgets('PrimeSrc current alternate mirrors provide a playable source', (
    tester,
  ) async {
    final client = HttpClient();
    late Map<String, dynamic> data;
    try {
      final r = await (await client.getUrl(
        Uri.parse('http://127.0.0.1:8766/primesrc-episodes.json'),
      )).close();
      data =
          jsonDecode(await utf8.decoder.bind(r).join()) as Map<String, dynamic>;
    } finally {
      client.close();
    }
    var passed = 0;
    for (final source
        in (data['videoSources'] as List).cast<Map<String, dynamic>>()) {
      if (source['type'] != 'iframe' ||
          (source['host'] as String).contains('lulu')) {
        continue;
      }
      if (const bool.fromEnvironment('STREAMWISH_ONLY') &&
          source['host'] != 'streamwish') {
        continue;
      }
      final label = source['quality'];
      try {
        await tester.pumpWidget(const MaterialApp(home: Scaffold()));
        final stream = await WebViewStreamExtractor().extract(
          pageUrl: source['videoUrl'] as String,
          config: const ExtractorConfigEntity(
            hostPattern: 'primesrc.me',
            urlPatterns: ['.m3u8', '.mpd', '.mp4'],
            captureHeaders: ['Referer', 'Origin', 'User-Agent', 'Cookie'],
            timeoutMs: 25000,
          ),
          pageHeaders: Map<String, String>.from(source['headers'] as Map),
        );
        if (stream == null) {
          debugPrint('MIRROR_FAILED $label extraction timeout');
          continue;
        }
        final proxy = LocalHlsProxy(Dio());
        try {
          final target = MediaSegmentRelay.needsManifestProxy(stream.url)
              ? await proxy.register(
                  upstreamUrl: stream.url,
                  headers: stream.headers,
                  keepOriginHeaders: true,
                )
              : stream.url;
          await verify(
            tester,
            target,
            stream.headers,
            stream.playType == 'hls' ? VideoFormat.hls : null,
          );
        } finally {
          await proxy.dispose();
        }
        passed++;
        debugPrint('MIRROR_PASSED $label');
      } catch (e) {
        debugPrint('MIRROR_FAILED $label $e');
      }
    }
    expect(
      passed,
      const bool.fromEnvironment('STREAMWISH_ONLY')
          ? equals(2)
          : greaterThan(0),
      reason: 'Selected mirrors must actually play',
    );
  }, skip: !live);
  testWidgets('UHDMovies fresh episode 2 opens on Android', (tester) async {
    final client = HttpClient();
    late Map<String, dynamic> data;
    try {
      final r = await (await client.getUrl(
        Uri.parse('http://127.0.0.1:8766/uhdmovies-media.json'),
      )).close();
      data =
          jsonDecode(await utf8.decoder.bind(r).join()) as Map<String, dynamic>;
    } finally {
      client.close();
    }
    await verify(
      tester,
      data['videoUrl'] as String,
      Map<String, String>.from(data['headers'] as Map? ?? {}),
      null,
    );
  }, skip: !live);
}
