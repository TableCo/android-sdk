# ta-table-sdk-android
An Android Kotlin and Java SDK for [TABLE.co](https://table.co). Check out the [releases page](https://github.com/TableCo/android-sdk/releases) to ensure that you've got the latest version. 

# Installation Guide

1. Add the JitPack repository to your root build.gradle file at the end of allprojects / repositories:

```groovy
allprojects {
	repositories {	
		maven { url 'https://jitpack.io' }
	}
}
```

1. Add the Table dependency to your app's build.gradle file:

```groovy
dependencies {
    implementation 'com.github.TableCo:android-sdk:0.2.2'
}
```

# Standard Usage

### Initialise the SDK

In your main Application class's `onCreate()` initialise the SDK:

```kotlin
override fun onCreate() {
    super.onCreate()
    TableSDK.init(this, "https://YOUR_WORKSPACE.table.co","api_key")
}
```

### Register a Logged In user
```kotlin
val tableParams = UserParams()
tableParams.email = "email@gmail.com"
tableParams.firstName = "First"
tableParams.lastName = "Last"

TableSDK.registerUser("my_user_id", tableParams, this)
```

### Register an Unidentified user anonymously
```kotlin
TableSDK.registerUnidentifiedUser(this)
```

### Sign Out
```kotlin
TableSDK.logout()
```

### Show the Table conversation list to the user using the navigator
```kotlin
TableSDK.showConversationList(this);
```

# Enable Firebase Cloud Messaging support

## Firebase Setup

If you're not using Firebase at all yet, you might also want to check out the [Android integration guide](https://firebase.google.com/docs/android/setup) on the official Firebase documentation.  

If you're not currently using Firebase Cloud Messaging then you'll need to set this up for your app. This process is [described in detail here](https://firebase.google.com/docs/cloud-messaging/android/client) in the official documentation. 

## Table Integration

The TableSDK APIs are design to work alongside your own push implementation or the implementation used by other 3rd party SDKs. We provide the `TableSDK.isTablePush()` methods to allow you to check incoming messages against our support for them. The Table SDK will then display the incoming message at a time that suits your app UI by calling `TableSDK.showConversation()`.

### Incoming Messages while the App is Open

These messages will appear in your `FirebaseMessagingService` implementation. You can find an example of this in the sample app [here](https://github.com/TableCo/android-sdk/blob/master/sample/src/main/java/com/table/sample/MyFirebaseMessagingService.kt).

```kotlin
override fun onMessageReceived(remoteMessage: RemoteMessage) {
    val message = remoteMessage.data

    if (TableSDK.isTablePushMessage(remoteMessage)) {
        // Let our MainActivity know about the incoming message and we can deal with it appropriately
        val intent = Intent()
        intent.action = MainActivity.NOTIFICATION_INTENT_FILTER
        intent.putExtra(MainActivity.EXTRA_REMOTE_MESSAGE, remoteMessage)
        sendBroadcast(intent)
    }

    // Deal with app-specific messages or messages from other services here

    super.onMessageReceived(remoteMessage)
}
```

It's up to you to decide what to do with these messages and when best to inform the user. The suggested implementation sends a broadcast message back to the main activity, which is important as these messages are not guaranteed to run on the main UI thread.

This message is then picked-up by the Activity like so:

```kotlin
override fun onResume() {
    super.onResume()

    val intentFilter = IntentFilter(NOTIFICATION_INTENT_FILTER)
    broadcastReceiver = object : BroadcastReceiver() {

        // This is where we get informed of new FCM messages from MyFirebaseMessagingService
        override fun onReceive(context: Context, intent: Intent) {
            // Get message from intent
            val message = intent.getParcelableExtra<RemoteMessage>(EXTRA_REMOTE_MESSAGE)
            message?.let {
                if (TableSDK.isTablePushMessage(it)) {
                    // Let's ask the user if they'd like to deal with it first
                    val alert = AlertDialog.Builder(context)
                    alert.setTitle("Incoming Message")
                    alert.setMessage("You have a new support message from our staff")
                    alert.setPositiveButton("Read it") { _, _ ->
                        TableSDK.showConversation(it)
                    }
                    alert.setNeutralButton("Cancel") { _, _ -> }
                    alert.show()
                }

                // Deal with app-specific messages or messages from other services here
            }
        }
    }

    registerReceiver(broadcastReceiver, intentFilter)
}
```

### Handling Messages while the App is Closed or Suspended

These messages will be sent to your app's main activity. A sample implementation can be see in the sample app [here](https://github.com/TableCo/android-sdk/blob/develop/sample/src/main/java/com/table/sample/MainActivity.kt#L33). Again in this instance we simply inform that the message is available and let them navigate to it if they wish.

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // See if we were launched from a notification while the app was in the background
    intent.extras?.let {
        if (TableSDK.isTablePushMessage(it)) {
            // Let's ask the user if they'd like to deal with it first
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Incoming Message")
            alert.setMessage("You have a new support message from our staff")
            alert.setPositiveButton("Read it") { _, _ ->
                TableSDK.showConversation(it)
            }
            alert.setNeutralButton("Cancel") { _, _ -> }
            alert.show()
        }
    }
}
```