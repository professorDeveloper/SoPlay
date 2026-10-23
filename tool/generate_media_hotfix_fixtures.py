#!/usr/bin/env python3
"""Generate synthetic, freely shareable media for android_media_hotfix_test.dart.

Requires ffmpeg with libx264 and libsvtav1. Usage:
  python3 tool/generate_media_hotfix_fixtures.py /tmp/sozo-playback-fixtures
  python3 -m http.server 8766 --bind 127.0.0.1 --directory /tmp/sozo-playback-fixtures
  adb reverse tcp:8766 tcp:8766
  flutter test integration_test/android_media_hotfix_test.dart -d emulator-5554
"""
import argparse
from pathlib import Path
import subprocess

parser = argparse.ArgumentParser(description=__doc__)
parser.add_argument('directory', type=Path)
args = parser.parse_args()
root = args.directory.resolve()
root.mkdir(parents=True, exist_ok=True)
variants = [(480, 854), (720, 1280), (1080, 1920)]
for height, width in variants:
    folder = root / str(height)
    folder.mkdir(exist_ok=True)
    subprocess.run([
        'ffmpeg', '-hide_banner', '-loglevel', 'error', '-y',
        '-f', 'lavfi', '-i', f'testsrc2=size={width}x{height}:rate=12',
        '-f', 'lavfi', '-i', 'sine=frequency=440:sample_rate=48000',
        '-t', '8', '-c:v', 'libx264', '-preset', 'ultrafast', '-crf', '28',
        '-pix_fmt', 'yuv420p', '-g', '24', '-c:a', 'aac',
        '-movflags', '+faststart', str(folder / 'video.mp4'),
    ], check=True)
    subprocess.run([
        'ffmpeg', '-hide_banner', '-loglevel', 'error', '-y',
        '-i', str(folder / 'video.mp4'), '-c', 'copy', '-hls_time', '2',
        '-hls_playlist_type', 'vod', '-hls_segment_filename',
        str(folder / 'seg%d.ts'), str(folder / 'index.m3u8'),
    ], check=True)
subprocess.run([
    'ffmpeg', '-hide_banner', '-loglevel', 'error', '-y',
    '-f', 'lavfi', '-i', 'testsrc2=size=1920x1080:rate=12',
    '-f', 'lavfi', '-i', 'sine=frequency=440:sample_rate=48000',
    '-t', '5', '-c:v', 'libsvtav1', '-preset', '12', '-crf', '45',
    '-pix_fmt', 'yuv420p10le', '-c:a', 'aac', '-movflags', '+faststart',
    str(root / 'av1-10bit.mp4'),
], check=True)
(root / 'master.m3u8').write_text('#EXTM3U\n' + ''.join(
    f'#EXT-X-STREAM-INF:BANDWIDTH={height * 2000},RESOLUTION={width}x{height}\n'
    f'{height}/index.m3u8\n' for height, width in variants
))
(root / 'blocked.html').write_text('<html><body>Upstream challenge</body></html>')
(root / 'nested-player.html').write_text('<!doctype html><html><body style="margin:0"><iframe src="http://10.0.2.2:8766/inner-player.html" style="width:100vw;height:100vh;border:0"></iframe></body></html>')
(root / 'inner-player.html').write_text('<!doctype html><html><body style="margin:0"><button style="width:100vw;height:100vh" onclick="fetch(\'/720/index.m3u8\')">Play</button></body></html>')
print(f'Fixtures ready: {root}')
