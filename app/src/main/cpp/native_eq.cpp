#include <jni.h>
#include <cmath>
#include <cstring>

namespace {

constexpr int kMaxBands = 5;
constexpr int kMaxChannels = 2;
constexpr float kPi = 3.14159265358979323846f;

struct Biquad {
    float b0 = 1.0f, b1 = 0.0f, b2 = 0.0f, a1 = 0.0f, a2 = 0.0f;
    float x1[kMaxChannels] = {0};
    float x2[kMaxChannels] = {0};
    float y1[kMaxChannels] = {0};
    float y2[kMaxChannels] = {0};

    void setPeaking(float sampleRate, float freq, float gainDb, float q) {
        float a = std::pow(10.0f, gainDb / 40.0f);
        float w0 = 2.0f * kPi * freq / sampleRate;
        float cosW0 = std::cos(w0);
        float alpha = std::sin(w0) / (2.0f * q);
        float a0 = 1.0f + alpha / a;
        b0 = (1.0f + alpha * a) / a0;
        b1 = (-2.0f * cosW0) / a0;
        b2 = (1.0f - alpha * a) / a0;
        a1 = (-2.0f * cosW0) / a0;
        a2 = (1.0f - alpha / a) / a0;
    }

    void reset() {
        std::memset(x1, 0, sizeof(x1));
        std::memset(x2, 0, sizeof(x2));
        std::memset(y1, 0, sizeof(y1));
        std::memset(y2, 0, sizeof(y2));
    }

    inline float process(float input, int channel) {
        float output = b0 * input + b1 * x1[channel] + b2 * x2[channel]
                       - a1 * y1[channel] - a2 * y2[channel];
        x2[channel] = x1[channel];
        x1[channel] = input;
        y2[channel] = y1[channel];
        y1[channel] = output;
        return output;
    }
};

Biquad gBands[kMaxBands];
int gBandCount = 0;
int gChannels = kMaxChannels;
float gSampleRate = 44100.0f;
float gGain = 1.0f;

inline float clampSample(float value) {
    if (value > 1.0f) return 1.0f;
    if (value < -1.0f) return -1.0f;
    return value;
}

}  // namespace

extern "C" {

JNIEXPORT void JNICALL
Java_xyz_larkzhh_echomusic_core_audio_NativeEq_nativeInit(JNIEnv *, jobject, jint sampleRate, jint channels) {
    gChannels = channels > 0 && channels <= kMaxChannels ? channels : kMaxChannels;
    gSampleRate = sampleRate > 0 ? static_cast<float>(sampleRate) : 44100.0f;
    gBandCount = 0;
    gGain = 1.0f;
    for (auto &band : gBands) {
        band = Biquad{};
        band.reset();
    }
}

JNIEXPORT void JNICALL
Java_xyz_larkzhh_echomusic_core_audio_NativeEq_nativeSetBand(JNIEnv *, jobject, jint index,
                                                             jfloat freq, jfloat gainDb, jfloat q) {
    if (index < 0 || index >= kMaxBands) {
        return;
    }
    gBands[index].setPeaking(static_cast<float>(gSampleRate), freq, gainDb, q > 0.0f ? q : 1.0f);
    gBands[index].reset();
    if (index + 1 > gBandCount) {
        gBandCount = index + 1;
    }
}

JNIEXPORT void JNICALL
Java_xyz_larkzhh_echomusic_core_audio_NativeEq_nativeSetGain(JNIEnv *, jobject, jfloat gainDb) {
    gGain = std::pow(10.0f, gainDb / 20.0f);
}

JNIEXPORT void JNICALL
Java_xyz_larkzhh_echomusic_core_audio_NativeEq_nativeReset(JNIEnv *, jobject) {
    for (auto &band : gBands) {
        band.reset();
    }
}

JNIEXPORT void JNICALL
Java_xyz_larkzhh_echomusic_core_audio_NativeEq_nativeProcessShort(JNIEnv *env, jobject,
                                                                  jshortArray buffer, jint length) {
    if (buffer == nullptr || length <= 0) {
        return;
    }
    jshort *samples = env->GetShortArrayElements(buffer, nullptr);
    if (samples == nullptr) {
        return;
    }
    const int channels = gChannels;
    for (int i = 0; i < length; ++i) {
        int channel = i % channels;
        float sample = samples[i] / 32768.0f;
        for (int band = 0; band < gBandCount; ++band) {
            sample = gBands[band].process(sample, channel);
        }
        sample = clampSample(sample * gGain);
        samples[i] = static_cast<jshort>(sample * 32767.0f);
    }
    env->ReleaseShortArrayElements(buffer, samples, 0);
}

}
