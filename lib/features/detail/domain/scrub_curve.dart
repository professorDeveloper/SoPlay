import 'dart:math' as math;

/// Where a horizontal swipe on the video lands.
///
/// Linear, as in MX Player: the same travel always moves the same time, so a
/// viewer can aim, and a still thumb's wobble moves a fraction of a second,
/// not several. One swipe across the whole screen covers [spanSec] — about
/// 4·√duration seconds: half a minute on a short clip, two and a half on an
/// episode, three at most on a film. The seek bar is there for longer jumps.
///
/// It used to be a VLC-style cubic in centimetres that reached half the
/// video by eight of them, counted at 38 logical pixels a centimetre —
/// Flutter's desktop figure; a phone has about 60. On a Galaxy S22 two
/// centimetres of travel already moved over a minute of a film, and past
/// three every pixel was four seconds.
///
/// Within [deadZonePx] of the start the answer is the start itself: a
/// finger brought back to where it began means "never mind".
class ScrubCurve {
  ScrubCurve._();

  /// Travel that still means "never mind", in logical pixels.
  static const double deadZonePx = 16;

  /// Seconds that one swipe across the whole screen moves.
  static double spanSec(int durationMs) =>
      (4.0 * math.sqrt(durationMs / 1000)).clamp(30.0, 180.0).toDouble();

  /// [widthPx] is the long side of the screen, so turning the phone does not
  /// change the rate.
  static int targetMs({
    required int baselineMs,
    required int durationMs,
    required double deltaPx,
    required double widthPx,
  }) {
    if (durationMs <= 0 || !widthPx.isFinite || widthPx <= 0) {
      return baselineMs;
    }
    // Measured from the edge of the dead zone, so leaving it starts at zero
    // instead of jumping by the zone's width.
    final travel = deltaPx.abs() - deadZonePx;
    if (travel <= 0) return baselineMs.clamp(0, durationMs);
    // Whole seconds: the label reads m:ss, and the seek should land on what
    // it said.
    final seconds = (travel / widthPx * spanSec(durationMs)).round();
    final target = baselineMs + deltaPx.sign.toInt() * seconds * 1000;
    return target.clamp(0, durationMs);
  }

  /// Whether [deltaPx] is inside the dead zone.
  static bool cancels(double deltaPx) => deltaPx.abs() < deadZonePx;
}
