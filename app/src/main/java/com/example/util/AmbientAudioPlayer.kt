package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.*
import java.util.Random

class AmbientAudioPlayer {
    private var audioTrack: AudioTrack? = null
    private var playJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    fun startPlaying(soundType: String) {
        stopPlaying()
        playJob = scope.launch {
            try {
                val sampleRate = 22050
                val minBufferSize = AudioTrack.getMinBufferSize(
                    sampleRate,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )
                if (minBufferSize <= 0) return@launch

                audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                audioTrack?.play()

                val bufferSize = 1024
                val buffer = ShortArray(bufferSize)
                val random = Random()

                // Sound state variables for physical synthesis
                var lastVal = 0.0f
                var phase = 0.0f
                var beatPhase = 0.0f
                val beatPeriod = sampleRate * 1.5f // ~40 bpm beat loop

                while (isActive && audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    for (i in 0 until bufferSize) {
                        when (soundType) {
                            "White Noise" -> {
                                val value = (random.nextGaussian() * 0.08).toFloat()
                                buffer[i] = (value.coerceIn(-1.0f, 1.0f) * 32767).toInt().toShort()
                            }
                            "Rain Drops" -> {
                                // Deep brownian-filtered rain fall
                                val raw = random.nextGaussian().toFloat() * 0.06f
                                lastVal = lastVal * 0.94f + raw * 0.06f
                                var valOut = lastVal
                                
                                // Random rain drops/patter splashes
                                if (random.nextFloat() < 0.002f) {
                                    valOut += (random.nextFloat() * 0.2f - 0.10f)
                                }
                                buffer[i] = (valOut.coerceIn(-1.0f, 1.0f) * 32767).toInt().toShort()
                            }
                            "Lo-Fi Beats" -> {
                                // Synthesizes a relaxing lo-fi hum & rhythmic muffled bass
                                phase += 180.0f / sampleRate
                                if (phase > 1.0f) phase -= 1.0f
                                val carrier = kotlin.math.sin(phase * 2.0 * kotlin.math.PI).toFloat() * 0.10f
                                
                                // Dust crackling vinyl
                                val crackle = if (random.nextFloat() < 0.003f) (random.nextFloat() * 0.10f - 0.05f) else 0.0f

                                // Slow heartbeat kick
                                beatPhase += 1.0f
                                if (beatPhase > beatPeriod) beatPhase -= beatPeriod
                                
                                val beatEnvelope = if (beatPhase < sampleRate * 0.20f) {
                                    val t = beatPhase / (sampleRate * 0.20f)
                                    kotlin.math.exp(-6.0 * t).toFloat()
                                } else 0.0f
                                
                                val kickPhase = beatPhase * 50.0f / sampleRate
                                val kickVal = kotlin.math.sin(kickPhase * 2.0 * kotlin.math.PI).toFloat() * beatEnvelope * 0.30f
                                
                                val out = carrier + crackle + kickVal
                                buffer[i] = (out.coerceIn(-1.0f, 1.0f) * 32767).toInt().toShort()
                            }
                            else -> {
                                buffer[i] = 0
                            }
                        }
                    }
                    val currentTrack = audioTrack
                    if (isActive && currentTrack != null && currentTrack.playState == AudioTrack.PLAYSTATE_PLAYING) {
                        currentTrack.write(buffer, 0, bufferSize)
                    } else {
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopPlaying() {
        playJob?.cancel()
        playJob = null
        try {
            audioTrack?.apply {
                if (playState == AudioTrack.PLAYSTATE_PLAYING) {
                    stop()
                }
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        audioTrack = null
    }

    fun release() {
        stopPlaying()
        scope.cancel()
    }
}
