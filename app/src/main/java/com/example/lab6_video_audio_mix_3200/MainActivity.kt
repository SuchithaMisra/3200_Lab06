package com.example.lab6_video_audio_mix_3200

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.lab6_video_audio_mix_3200.viewModels.MainViewModel

class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MainViewModel(
                        savedStateHandle = SavedStateHandle(),
                        videoPlayer = ExoPlayer.Builder(this@MainActivity).build(),
                        audioPlayer = ExoPlayer.Builder(this@MainActivity).build()
                    ) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(MainViewModel::class.java)

        //videos
        mainViewModel.addVideoUri(Uri.parse("android.resource://com.example.lab6_video_audio/"+ R.raw.minion), "minion")
        mainViewModel.addVideoUri(Uri.parse("android.resource://com.example.lab6_video_audio/"+R.raw.panda), "panda")
        //audios
        mainViewModel.addAudioUri(Uri.parse("https://rss3.domint.net:8114/stream"), "radio1")
        mainViewModel.addAudioUri(Uri.parse("https://radios.sonidoshd.com/8058/stream"), "radio2")


        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Display the main video player
                        AndroidView({ context -> PlayerView(context).apply { player = mainViewModel.videoPlayer as ExoPlayer } },
                            modifier = Modifier.height(200.dp).fillMaxWidth())

                        // Buttons to control main video playback
                        LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                            items(mainViewModel.videoUrisValue) { (videoUri, name) ->
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(onClick = {
                                        if (mainViewModel.videoPlayer.isPlaying) {
                                            mainViewModel.pauseVideo()
                                        } else {
                                            mainViewModel.playVideo(videoUri)
                                        }
                                    }) {
                                        Text(if (mainViewModel.videoPlayer.isPlaying) "Pause Video: $name" else "Play Video: $name")
                                    }
                                }
                            }
                        }

                        // Buttons to control audio playback
                        LazyColumn(modifier = Modifier.padding(vertical = 8.dp)) {
                            items(mainViewModel.audioUrisValue) { (audioUri, name) ->
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Button(onClick = {
                                        if (mainViewModel.audioPlayer.isPlaying) {
                                            mainViewModel.pauseAudio()
                                        } else {
                                            mainViewModel.playAudio(audioUri)
                                        }
                                    }) {
                                        Text(if (mainViewModel.audioPlayer.isPlaying) "Stop Audio: $name" else "Play Audio: $name")
                                    }
                                    Button(onClick = {
                                        mainViewModel.audioPlayer.volume = if (mainViewModel.audioPlayer.volume == 0f) 1f else 0f
                                    }) {
                                        Text(if (mainViewModel.audioPlayer.volume == 0f) "Unmute Audio: $name" else "Mute Audio: $name")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
