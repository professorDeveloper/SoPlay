// Device-level regression suite. Serve generated fixtures on localhost:8766
// and use `adb reverse tcp:8766 tcp:8766` before running on Android.
import 'dart:io';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';
import 'package:path_provider/path_provider.dart';
import 'package:soplay/core/player/local_hls_proxy.dart';
import 'package:soplay/core/player/media_controller.dart';
import 'package:soplay/core/system/platform_utils.dart';
import 'package:soplay/features/download/data/datasources/download_native_data_source.dart';
import 'package:soplay/features/download/domain/entities/download_failure.dart';

const base = String.fromEnvironment(
  'FIXTURE_BASE',
  defaultValue: 'http://127.0.0.1:8766',
);
void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  const native = DownloadNativeDataSource();
  late Directory scratch;
  final ids = <String>[];
  setUpAll(() async {
    await initTvPlatform();
    scratch = await Directory(
      '${(await getTemporaryDirectory()).path}/hotfix-device-test',
    ).create(recursive: true);
  });
  tearDownAll(() async {
    for (final id in ids) {
      await native.forget(id);
    }
    await scratch.delete(recursive: true);
  });

  Future<void> play(
    WidgetTester tester,
    PlayerController controller,
    int height,
  ) async {
    try {
      await tester.pumpWidget(
        MaterialApp(home: Scaffold(body: controller.buildView())),
      );
      await controller.initialize().timeout(const Duration(seconds: 35));
      expect(controller.value.isInitialized, isTrue);
      expect(controller.value.hasError, isFalse);
      expect(
        controller.value.size.height,
        height.toDouble(),
        reason: 'Actual decoded video must match chosen resolution',
      );
      await controller.play();
      final deadline = DateTime.now().add(const Duration(seconds: 12));
      while (controller.value.position.inMilliseconds < 800 &&
          DateTime.now().isBefore(deadline)) {
        await Future<void>.delayed(const Duration(milliseconds: 250));
        await tester.pump();
      }
      expect(
        controller.value.position.inMilliseconds,
        greaterThanOrEqualTo(800),
        reason: 'Video must actually advance, not just initialize',
      );
      expect(controller.value.hasError, isFalse);
      await controller.pause();
    } finally {
      await tester.pumpWidget(const SizedBox());
      await controller.dispose();
    }
  }

  Future<NativeDownloadState> download(
    String name,
    String url,
    String kind, {
    int? height,
  }) async {
    final id = 'hotfix-$name-${DateTime.now().microsecondsSinceEpoch}';
    ids.add(id);
    final path =
        '${scratch.path}/$name/${kind == 'hls' ? 'index.m3u8' : 'video.mp4'}';
    expect(
      await native.start(
        id: id,
        title: 'Hotfix verification',
        url: url,
        artefactPath: path,
        headers: const {},
        kind: kind,
        pageUrls: const [],
        wifiOnly: false,
        videoHeight: height,
      ),
      isTrue,
    );
    final deadline = DateTime.now().add(const Duration(seconds: 45));
    while (DateTime.now().isBefore(deadline)) {
      for (final state in await native.states()) {
        if (state.id == id &&
            ['completed', 'failed', 'cancelled'].contains(state.status)) {
          return state;
        }
      }
      await Future<void>.delayed(const Duration(milliseconds: 250));
    }
    fail('Native download did not finish: $name');
  }

  for (final height in [720, 1080]) {
    testWidgets('HLS chosen $height decodes at $height', (tester) async {
      final proxy = LocalHlsProxy(Dio());
      try {
        final url = await proxy.register(
          upstreamUrl: '$base/master.m3u8',
          headers: const {},
          hlsHeight: height,
        );
        await play(
          tester,
          PlayerController.networkUrl(
            Uri.parse(url),
            formatHint: VideoFormat.hls,
          ),
          height,
        );
      } finally {
        await proxy.dispose();
      }
    });
    testWidgets('Android download $height retains quality and plays offline', (
      tester,
    ) async {
      await tester.pumpWidget(const MaterialApp(home: Scaffold()));
      final result = await download(
        'hls-$height',
        '$base/master.m3u8',
        'hls',
        height: height,
      );
      expect(result.status, 'completed', reason: result.error);
      expect(await File(result.artefactPath).exists(), isTrue);
      final playlist = await File(result.artefactPath).readAsString();
      expect(
        playlist,
        isNot(contains('http://')),
        reason: 'Offline playlist must reference only local segments',
      );
      await play(
        tester,
        PlayerController.file(File(result.artefactPath)),
        height,
      );
    });
  }
  testWidgets(
    'Wrapped CDN HLS downloads through relay and plays offline',
    (tester) async {
      await tester.pumpWidget(const MaterialApp(home: Scaffold()));
      final result = await download(
        'relay-720',
        '$base/relay-720.m3u8',
        'hls',
        height: 720,
      );
      expect(result.status, 'completed', reason: result.error);
      await play(tester, PlayerController.file(File(result.artefactPath)), 720);
    },
    skip: !const bool.fromEnvironment('LIVE_PROVIDER_CHECK'),
  );

  testWidgets('Android MP4 download plays from disk', (tester) async {
    await tester.pumpWidget(const MaterialApp(home: Scaffold()));
    final result = await download('mp4', '$base/1080/video.mp4', 'video');
    expect(result.status, 'completed', reason: result.error);
    await play(tester, PlayerController.file(File(result.artefactPath)), 1080);
  });
  testWidgets('HTML response is rejected instead of saved as playable video', (
    tester,
  ) async {
    await tester.pumpWidget(const MaterialApp(home: Scaffold()));
    final result = await download('html', '$base/blocked.html', 'video');
    expect(result.status, 'failed');
    expect(
      DownloadFailureKind.classify(result.error),
      DownloadFailureKind.notMedia,
    );
    expect(await File('${scratch.path}/html/video.mp4').exists(), isFalse);
  });
}
