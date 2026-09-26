// Where a horizontal swipe on the video lands: linear, minutes per full
// swipe rather than the whole video, and nothing when the finger comes back.
import 'package:flutter_test/flutter_test.dart';
import 'package:soplay/features/detail/domain/scrub_curve.dart';

void main() {
  const episode = 24 * 60 * 1000;
  const film = 2 * 60 * 60 * 1000;
  const width = 832.0; // Galaxy S22, landscape, logical px
  int at(double px, {int from = 600000, int duration = episode}) =>
      ScrubCurve.targetMs(
        baselineMs: from,
        durationMs: duration,
        deltaPx: px,
        widthPx: width,
      );

  test('a finger back at its start changes nothing', () {
    expect(at(0), 600000);
    expect(at(ScrubCurve.deadZonePx - 1), 600000);
    expect(at(-(ScrubCurve.deadZonePx - 1)), 600000);
    expect(ScrubCurve.cancels(ScrubCurve.deadZonePx - 1), isTrue);
    expect(ScrubCurve.cancels(ScrubCurve.deadZonePx + 1), isFalse);
  });

  test('linear: twice the travel is twice the time, both ways', () {
    final one = at(ScrubCurve.deadZonePx + 100) - 600000;
    final two = at(ScrubCurve.deadZonePx + 200) - 600000;
    expect((two - 2 * one).abs(), lessThanOrEqualTo(1000));
    expect(600000 - at(-(ScrubCurve.deadZonePx + 100)), one);
  });

  test('a full swipe is minutes, not the whole video', () {
    expect(at(width, from: 0), inInclusiveRange(120000, 180000));
    expect(at(width, from: 0, duration: film), inInclusiveRange(150000, 180000));
    expect(at(width, from: 0, duration: 90000), inInclusiveRange(20000, 45000));
  });

  test('stays inside the video', () {
    expect(at(40 * width), episode);
    expect(at(-40 * width), 0);
  });
}
