import 'dart:async';
import 'package:flutter_test/flutter_test.dart';
import 'package:soplay/core/player/playback_readiness.dart';

PlaybackReadiness sample({
  int width = 0,
  int height = 0,
  bool video = true,
  bool audio = true,
  String? error,
}) => PlaybackReadiness(
  width: width,
  height: height,
  hasVideo: video,
  hasAudio: audio,
  duration: const Duration(minutes: 90),
  error: error,
);

void main() {
  test(
    'mpv codec failure falls back once for remote and downloaded video',
    () async {
      for (final path in ['https://cdn/video.mp4', '/downloads/video.mp4']) {
        var replacements = 0;
        final recovered = await initializeWithDecoderFallback(
          initialize: () => awaitPlaybackReadiness(
            read: () => sample(error: 'Could not open codec.'),
            signals: [],
            canceled: Completer<void>().future,
          ),
          canFallback: () => true,
          fallback: () async {
            replacements++;
          },
        );
        expect(recovered, isTrue, reason: path);
        expect(replacements, 1);
      }
    },
  );

  test('a platform failure propagates without another fallback loop', () async {
    var replacements = 0;
    await expectLater(
      initializeWithDecoderFallback(
        initialize: () async => throw StateError('Could not open codec.'),
        canFallback: () => true,
        fallback: () async {
          replacements++;
          throw StateError('platform decoder failed');
        },
      ),
      throwsStateError,
    );
    expect(replacements, 1);
  });

  test(
    'network, HTML and disposal failures do not retry with another engine',
    () async {
      for (final message in [
        'Failed to open https://cdn/x',
        'Failed to recognize file format.',
        'Player disposed',
      ]) {
        var replacements = 0;
        await expectLater(
          initializeWithDecoderFallback(
            initialize: () async => throw StateError(message),
            canFallback: () => true,
            fallback: () async {
              replacements++;
            },
          ),
          throwsStateError,
        );
        expect(replacements, 0);
      }
    },
  );

  test(
    'duration cannot initialize video before decoder dimensions arrive',
    () async {
      final signals = StreamController<void>.broadcast(sync: true);
      final canceled = Completer<void>();
      var state = sample();
      var finished = false;
      final waiting = awaitPlaybackReadiness(
        read: () => state,
        signals: [signals.stream],
        canceled: canceled.future,
      ).then((_) => finished = true);
      signals.add(null);
      await Future<void>.delayed(Duration.zero);
      expect(finished, false);
      state = sample(width: 3840, height: 0);
      signals.add(null);
      await Future<void>.delayed(Duration.zero);
      expect(finished, false);
      state = sample(width: 3840, height: 2160);
      signals.add(null);
      await waiting;
      expect(finished, true);
      await signals.close();
    },
  );

  test('audio-only media is ready without invented video dimensions', () async {
    await awaitPlaybackReadiness(
      read: () => sample(video: false),
      signals: [],
      canceled: Completer<void>().future,
    );
  });

  test(
    'stalled video times out instead of becoming initialized black output',
    () async {
      await expectLater(
        awaitPlaybackReadiness(
          read: sample,
          signals: [],
          canceled: Completer<void>().future,
          timeout: Duration.zero,
        ),
        throwsA(isA<TimeoutException>()),
      );
    },
  );

  test('closing a pending player cancels its readiness wait', () async {
    final canceled = Completer<void>();
    final waiting = awaitPlaybackReadiness(
      read: sample,
      signals: [],
      canceled: canceled.future,
    );
    final assertion = expectLater(waiting, throwsStateError);
    canceled.complete();
    await assertion;
  });

  test('decoder errors are not reported as successful readiness', () async {
    await expectLater(
      awaitPlaybackReadiness(
        read: () => sample(error: 'codec failed'),
        signals: [],
        canceled: Completer<void>().future,
      ),
      throwsStateError,
    );
  });
}
