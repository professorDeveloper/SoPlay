#include <jni.h>
#include <cstdint>
#include <cstddef>
#include <cstring>

// 1) Obfuscated path: "users/{uuid}"
static constexpr char REAL_PATH[] = "users/{uuid}";
static constexpr int  PATH_N      = sizeof(REAL_PATH) - 1;
static constexpr uint8_t KEY      = 0x7F;

// identity shuffle for simplicity—still XOR-obfuscates, but doesn’t permute
static constexpr uint8_t SHUF_PATH[PATH_N] = {
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
};

// reconstruct and return the path
static jstring makePath(JNIEnv* env) {
    char buf[PATH_N+1];
    // 1. place XOR’d bytes (no permutation)
    for (int j = 0; j < PATH_N; j++) {
        buf[ SHUF_PATH[j] ] = REAL_PATH[j] ^ KEY;
    }
    buf[PATH_N] = '\0';
    // 2. un-XOR to get the real chars
    for (int i = 0; i < PATH_N; i++) {
        buf[i] ^= KEY;
    }
    return env->NewStringUTF(buf);
}

static jstring makeSimple(JNIEnv* env, const char* real) {
    size_t N = std::strlen(real);
    char buf[128];
    for (size_t i = 0; i < N; i++) {
        buf[i] = real[i] ^ KEY;
    }
    buf[N] = '\0';
    for (size_t i = 0; i < N; i++) {
        buf[i] ^= KEY;
    }
    return env->NewStringUTF(buf);
}

extern "C" {

// now returns "users/{uuid}"
JNIEXPORT jstring JNICALL
Java_com_azamovme_soplay_utils_NativeHelper_getDostupPath(JNIEnv* env, jclass) {
    return makePath(env);
}

// "Access Denied"
JNIEXPORT jstring JNICALL
Java_com_azamovme_soplay_utils_NativeHelper_getDialogTitle(JNIEnv* env, jclass) {
    return makeSimple(env, "Access Denied");
}

// "You do not have permission to use this app."
JNIEXPORT jstring JNICALL
Java_com_azamovme_soplay_utils_NativeHelper_getDialogMessage(JNIEnv* env, jclass) {
    return makeSimple(env,
                      "You do not have permission to use this app.");
}

// "Exit"
JNIEXPORT jstring JNICALL
Java_com_azamovme_soplay_utils_NativeHelper_getButtonTextExit(JNIEnv* env, jclass) {
    return makeSimple(env, "Exit");
}

}  // extern "C"
