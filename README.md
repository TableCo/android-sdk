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
    implementation 'com.github.TableCo:android-sdk:0.2.1'
}
```

# Usage

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
TableSDK.registerUnidentifiedUser("anonymous_user_id", this)
```

### Sign Out
```kotlin
TableSDK.logout()
```

### Show the Table conversation list to the user using the navigator
```kotlin
TableSDK.showConversationList(this);
```
