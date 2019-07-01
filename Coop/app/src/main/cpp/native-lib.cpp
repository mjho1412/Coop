#include <jni.h>
#include <string>

#define HOST "http://118.69.82.125:8001/api/v1/"
#define MAPBOX_KEY "pk.eyJ1Ijoibm9uMjM1IiwiYSI6ImNpbzI5YzRhbzFheGF1a20za2JzODNndDMifQ.ono5LA8H3OlJUo5jjDCi2Q"
#define VBD_KEY ""
#define SECRET_KEY "najvj5u2sd5svty"

#define KEY_APP "HAIBUI!@123456"

std::string gl_key = "";

bool checkKey() {
    return !(gl_key.length() == 0 || gl_key.compare(KEY_APP) != 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_hb_coop_app_App_setKey(JNIEnv *env, jobject instance, jstring key_) {
    const char *key = env->GetStringUTFChars(key_, 0);

    gl_key = key;

    env->ReleaseStringUTFChars(key_, key);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_hb_coop_app_App_getMapboxToken(JNIEnv *env, jobject instance) {

    if (!checkKey()) {
        return env->NewStringUTF("");
    }

    return env->NewStringUTF(MAPBOX_KEY);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_hb_coop_di_module_NetModule_getHost(JNIEnv *env, jobject instance) {
    if (!checkKey()) {
        return env->NewStringUTF("");
    }

    return env->NewStringUTF(HOST);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_hb_coop_utils_http_HttpHeaderInterceptor_getVBDKey(JNIEnv *env, jobject instance) {
    if (!checkKey()) {
        return env->NewStringUTF("");
    }

    return env->NewStringUTF(VBD_KEY);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_hb_coop_utils_AppOTP_getSecretKey(JNIEnv *env, jobject instance) {
    if (!checkKey()) {
        return env->NewStringUTF("");
    }

    return env->NewStringUTF(SECRET_KEY);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_hb_coop_utils_Compression_getZipCode(JNIEnv *env, jobject instance) {

    if (!checkKey())
        return 0;

    return 2000;

}