import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:soplay/features/detail/domain/entities/media_resolve_entity.dart';

/// Checks mixed mirror candidates before enqueueing one. Reads only a prefix,
/// never the movie, and rejects an HTTP-success HTML error as well as 403s.
class DownloadMediaProbe {
  DownloadMediaProbe(this.dio);
  final Dio dio;

  Future<bool> accepts(MediaResolveEntity media) async {
    final cancel = CancelToken();
    try {
      final response = await dio
          .get<ResponseBody>(
            media.videoUrl,
            cancelToken: cancel,
            options: Options(
              headers: {...media.headers, 'Range': 'bytes=0-2047'},
              responseType: ResponseType.stream,
              sendTimeout: const Duration(seconds: 8),
              receiveTimeout: const Duration(seconds: 8),
              validateStatus: (_) => true,
            ),
          )
          .timeout(const Duration(seconds: 8));
      if ((response.statusCode ?? 500) >= 400 || response.data == null) {
        return false;
      }
      final bytes = <int>[];
      await for (final chunk in response.data!.stream.timeout(
        const Duration(seconds: 8),
      )) {
        bytes.addAll(chunk.take(2048 - bytes.length));
        if (bytes.length >= 512) break;
      }
      final text = utf8.decode(bytes, allowMalformed: true).trimLeft();
      if (media.type == 'hls' || media.videoUrl.contains('.m3u8')) {
        return text.startsWith('#EXTM3U');
      }
      if (media.type == 'dash' || media.videoUrl.contains('.mpd')) {
        return RegExp(r'<MPD(?:\s|>)').hasMatch(text);
      }
      if (text.toLowerCase().startsWith('<!doctype') ||
          text.toLowerCase().startsWith('<html')) {
        return false;
      }
      return bytes.length >= 12 &&
          (String.fromCharCodes(bytes.sublist(4, 8)) == 'ftyp' ||
              (bytes[0] == 0x1a &&
                  bytes[1] == 0x45 &&
                  bytes[2] == 0xdf &&
                  bytes[3] == 0xa3) ||
              bytes[0] == 0x47 ||
              text.startsWith('#EXTM3U'));
    } catch (_) {
      return false;
    } finally {
      cancel.cancel('Media prefix checked');
    }
  }
}
