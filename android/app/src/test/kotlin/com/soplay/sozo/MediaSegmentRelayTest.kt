package com.soplay.sozo
import org.junit.Assert.*
import org.junit.Test
import java.net.URLDecoder
class MediaSegmentRelayTest {
    @Test fun onlyWrappedCdnUsesRelayAndPreservesSignature() {
        val raw = "https://p16-ad-site-sign-sg.tiktokcdn.com/ad-site-i18n-sg/a.image?x-signature=a%2Bb"
        val result = MediaSegmentRelay.rewrite(raw, "https://api.test/api/contents/media-segment")!!
        assertEquals(raw, URLDecoder.decode(result.substringAfter("?url="), "UTF-8"))
        assertNull(MediaSegmentRelay.rewrite("https://cdn.test/video.ts", "https://api.test/relay"))
        assertNull(MediaSegmentRelay.rewrite(raw.replace(".com/", ".com.evil.test/"), "https://api.test/relay"))
        assertNull(MediaSegmentRelay.rewrite(raw, null))
    }
}
