package com.scamofty.cleanarchitecturenoteapp.feature_note.presentation.cloud_messaging

import android.util.Log
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.model.InvalidNoteException
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.model.Note
import com.scamofty.cleanarchitecturenoteapp.feature_note.domain.use_case.NoteUseCases
import com.scamofty.cleanarchitecturenoteapp.feature_note.presentation.add_edit_note.AddEditNoteViewModel.UiEvent
import com.scamofty.cleanarchitecturenoteapp.ui.theme.LightBlue
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Math.random
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class FirebaseCloudMessaging: FirebaseMessagingService() {
    private val scope = MainScope()
    private var messageProcessQueue = Channel<Job>(Channel.UNLIMITED)
    @Inject
    lateinit var noteUseCases: NoteUseCases
    //Use for one time events, more like a xml thing since JetPack Compose doesn't have 1 time events
    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    private var currentNoteId: Int? = null



    init {
        Log.d(TAG, "Booting up the mainframe connection.")
        scope.launch(IO){
            for (job in messageProcessQueue) job.join()
        }
    }


    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ

        Log.d(TAG, "From: " + remoteMessage.from)

        // Check if message contains a data payload.
        if (remoteMessage.data.size > 0) {

            Log.d(TAG, "Message data payload: " + remoteMessage.data)

            if ( /* Check if data needs to be processed by long running job */true) {
                // For long-running tasks (10 seconds or more) use coroutines.
                scheduleJob{
                        try {
                            noteUseCases.addCloudNote(
                                Note(
                                    title = remoteMessage.notification?.title ?:"Error",
                                    content = remoteMessage.notification?.body ?:"Error",
                                    timestamp = remoteMessage.sentTime,
                                    color = Note.noteColors.random().toArgb(),
                                    //TODO: This makes a bug on clashing IDs lol
                                    id = remoteMessage.messageId?.toInt() ?: random().toInt()
                                )
                            )
                        } catch(e: InvalidNoteException) {
                            _eventFlow.emit(
                                UiEvent.ShowSnackbar(
                                    message = e.message ?: "Couldn't save note"
                                )
                            )
                        }
                    //doSomethingWithResult()
                }
            } else {
                // Handle message within 10 seconds
                handleNow()
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(
                TAG, "Message Notification Body: " + remoteMessage.notification!!
                    .body
            )
            val notificationBody = remoteMessage.notification!!.body
            if (remoteMessage.notification!!.body != null) {
                sendNotification(notificationBody)
            }
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


    // [END receive_message]
    // [START on_new_token]
    /**
     * There are two scenarios when onNewToken is called:
     * 1) When a new token is generated on initial app startup
     * 2) Whenever an existing token is changed
     * Under #2, there are three scenarios when the existing token is changed:
     * A) App is restored to a new device
     * B) User uninstalls/reinstalls the app
     * C) User clears app data
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token)
    }

    // [END on_new_token]
    /**
     * Schedule async work using Coroutines.
     */
    private fun scheduleJob(
        context: CoroutineContext = EmptyCoroutineContext,
        block: suspend CoroutineScope.() -> Unit
    ) {
        val job = scope.launch(context, CoroutineStart.LAZY, block)
        println("Processing Job $job")
        messageProcessQueue.trySend(job)
    }
    private fun cancelQueue(){
        messageProcessQueue.cancel()
        scope.cancel()
    }
    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private fun handleNow() {
        Log.d(TAG, "Short lived task is done.")
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM registration token with any
     * server-side account maintained by your application.
     *
     * @param token The new token.
     */
    private fun sendRegistrationToServer(token: String) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String?) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0,  /* Request code */intent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val channelId = getString(R.string.default_notification_channel_id)
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder: NotificationCompat.Builder =
//            NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.ic_stat_ic_notification)
//                .setContentTitle(getString(R.string.fcm_message))
//                .setContentText(messageBody)
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent)
//
//        val notificationManager =
//            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Channel human readable title",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//
//        notificationManager.notify(0,  /* ID of notification */notificationBuilder.build())
    }

    companion object {
        private const val TAG = "FirebaseCloudMessaging"
    }
}