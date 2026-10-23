import 'dart:io';
import 'package:dio/dio.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:soplay/core/player/download_media_probe.dart';
import 'package:soplay/features/detail/domain/entities/media_resolve_entity.dart';

void main() {
  test(
    'probe rejects expired token and HTML, accepts real HLS and MP4 with wrong MIME',
    () async {
      final server = await HttpServer.bind(InternetAddress.loopbackIPv4, 0);
      server.listen((req) async {
        switch (req.uri.path) {
          case '/expired':
            req.response.statusCode = 403;
            break;
          case '/html':
            req.response.write('<html>Access denied</html>');
            break;
          case '/playlist':
            req.response.write(
              '#EXTM3U\n#EXT-X-TARGETDURATION:10\n#EXTINF:10,\nseg.ts\n',
            );
            break;
          case '/video':
            req.response.headers.contentType = ContentType('image', 'jpeg');
            req.response.add([
              0,
              0,
              0,
              24,
              102,
              116,
              121,
              112,
              109,
              112,
              52,
              50,
            ]);
        }
        await req.response.close();
      });
      final dio = Dio();
      final probe = DownloadMediaProbe(dio);
      try {
        Future<bool> check(String path, String type) => probe.accepts(
          MediaResolveEntity(
            videoUrl: 'http://127.0.0.1:${server.port}/$path',
            headers: const {},
            type: type,
          ),
        );
        expect(await check('expired', 'hls'), isFalse);
        expect(await check('html', 'hls'), isFalse);
        expect(await check('playlist', 'hls'), isTrue);
        expect(await check('video', 'mp4'), isTrue);
      } finally {
        dio.close(force: true);
        await server.close(force: true);
      }
    },
  );
}
