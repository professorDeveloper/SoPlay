// Exercises the actual libmpv -> platform recovery path with AV1 10-bit.
// Emulator routing is intentionally not initialized: this tests the controller
// selection used on physical Android phones, rather than the emulator override.
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:integration_test/integration_test.dart';
import 'package:path_provider/path_provider.dart';
import 'package:soplay/core/constants/app_constants.dart';
import 'package:soplay/core/player/media_controller.dart';
import 'package:soplay/core/player/player_engine.dart';

const fixture = String.fromEnvironment(
  'CODEC_FIXTURE',
  defaultValue: 'av1-10bit.mp4',
);
const expectedHeight = int.fromEnvironment('CODEC_HEIGHT', defaultValue: 1080);

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  late Directory dir;
  late Box<dynamic> settings;
  setUpAll(() async {
    dir = await Directory(
      '${(await getTemporaryDirectory()).path}/av1-decoder-check',
    ).create(recursive: true);
    Hive.init(dir.path);
    settings = await Hive.openBox<dynamic>(AppConstants.settingsBox);
    await settings.put(AppConstants.playerEngineKey, PlayerEngine.mediaKit.id);
    final client = HttpClient();
    try {
      final response = await (await client.getUrl(
        Uri.parse('http://127.0.0.1:8766/$fixture'),
      )).close();
      expect(response.statusCode, 200);
      await response.pipe(File('${dir.path}/av1.mp4').openWrite());
    } finally {
      client.close();
    }
  });
  tearDownAll(() async {
    await Hive.close();
    await dir.delete(recursive: true);
  });
  for (final local in [false, true]) {
    testWidgets(
      '$fixture ${local ? "local" : "network"} mpv controller recovers or plays',
      (tester) async {
        final c = local
            ? PlayerController.file(File('${dir.path}/av1.mp4'))
            : PlayerController.networkUrl(
                Uri.parse('http://127.0.0.1:8766/$fixture'),
              );
        expect(
          c.runtimeType.toString(),
          '_MediaKitController',
          reason: 'Exercise mpv, not emulator native shortcut',
        );
        try {
          await tester.pumpWidget(
            MaterialApp(home: Scaffold(body: c.buildView())),
          );
          await c.initialize().timeout(const Duration(seconds: 50));
          expect(c.value.size.height, expectedHeight);
          await c.play();
          final end = DateTime.now().add(const Duration(seconds: 15));
          while (c.value.position.inMilliseconds < 1000 &&
              DateTime.now().isBefore(end)) {
            await Future<void>.delayed(const Duration(milliseconds: 250));
            await tester.pump();
          }
          expect(c.value.hasError, isFalse, reason: c.value.errorDescription);
          expect(c.value.position.inMilliseconds, greaterThanOrEqualTo(1000));
          debugPrint(
            'CODEC_VERIFIED fixture=$fixture local=$local size=${c.value.size}',
          );
        } finally {
          await tester.pumpWidget(const SizedBox());
          await c.dispose();
        }
      },
    );
  }
}
