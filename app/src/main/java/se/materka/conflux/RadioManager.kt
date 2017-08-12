/**
 * Copyright 2016 Mattias Karlsson

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.materka.conflux

import android.app.ActivityManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.arch.lifecycle.MutableLiveData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.AudioManager
import android.media.AudioManager.*
import android.net.Uri
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import se.materka.conflux.model.Station
import se.materka.conflux.service.RadioEvent
import se.materka.conflux.service.RadioService
import se.materka.exoplayershoutcastplugin.Metadata


object RadioManager : ServiceConnection, AudioManager.OnAudioFocusChangeListener {
    private val metadataObservable: PublishSubject<Metadata> = PublishSubject.create()
    private val radioStatusObservable: PublishSubject<RadioEvent> = PublishSubject.create()
    private var service: RadioService? = null
    private var audioManager: AudioManager? = null
    var currentStation: Station? = null
        private set
    var currentMetadata: Metadata? = null
        private set (value) {
            field = value
            metadataObservable.onNext(value)
        }


    var playing = MutableLiveData<Boolean>()

    fun init(context: Context) {
        bindService(context)
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    fun metadata(): Observable<Metadata> {
        return metadataObservable
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        audioManager?.abandonAudioFocus(this)
        service = null
    }

    override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
        audioManager?.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        service = (binder as RadioService.StreamServiceBinder).service

        service?.let { s ->
            s.isPlaying
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { p ->
                        playing.postValue(p)
                    }
            s.metadata
                    .subscribeOn(Schedulers.newThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({ metadata ->
                        currentMetadata = metadata
                    })

            s.event
                    .subscribeOn(Schedulers.newThread())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({ event ->
                        radioStatusObservable.onNext(event)
                        when (event) {
                            RadioEvent.STATUS_STOPPED -> {
                                metadataObservable.onNext(Metadata("", "", "", "", "", "", "", "")) // TODO: add static empty flag to metadata class
                            }
                        }
                    })
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        service?.let {
            when (focusChange) {
                AUDIOFOCUS_LOSS -> {
                    stop()
                }
                AUDIOFOCUS_LOSS_TRANSIENT -> {
                    stop()
                }
                AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> { /* lower volume */
                }
                AUDIOFOCUS_GAIN -> {
                    play()
                }
            }
        }
    }

    private fun bindService(context: Context) {
        val intent = Intent(context, RadioService::class.java)

        if (!isServiceRunning(context, RadioService::class.java)) {
            context.startService(intent)
        }
        context.bindService(intent, this, Context.BIND_AUTO_CREATE)
    }

    fun pause() {
        stop()
    }

    fun stop() {
        service?.let(RadioService::stop)
    }


    fun play(station: Station?) {
        currentStation = station ?: currentStation
        play()
    }

    private fun play() {
        service?.let { s ->
            Uri.parse(currentStation?.url)?.let { uri ->
                s.preparePlay(uri)
            }
        }
    }

    private fun isServiceRunning(context: Context, cls: Class<*>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return manager.getRunningServices(Integer.MAX_VALUE).any { cls.name == it.service.className }
    }
}

