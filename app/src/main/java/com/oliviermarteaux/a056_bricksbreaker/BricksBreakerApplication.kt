package com.oliviermarteaux.a056_bricksbreaker

import android.app.Application
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.oliviermarteaux.a056_bricksbreaker.di.BricksBreakerAppContainer
import com.oliviermarteaux.a056_bricksbreaker.di.BricksBreakerContainer
import dagger.hilt.android.HiltAndroidApp

/**
 * The application class for the BricksBreaker application.
 * This class serves as the entry point for the application and can be used for global application-level
 * initialization tasks such as dependency injection setup using Hilt.
 */
@HiltAndroidApp
class BricksBreakerApplication : Application() /*,SingletonImageLoader.Factory*/ {

    lateinit var bricksBreakerContainer: BricksBreakerContainer
        internal set

    /**
     * Creates a new [ImageLoader] for the application.
     *
     * @param context The application context.
     * @return A new [ImageLoader] instance.
     */
//    override fun newImageLoader(context: Context): ImageLoader {
//        return ImageLoader.Builder(context = context)
//            .build()
//    }

    /**
     * Called when the application is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     */
    override fun onCreate() {
        super.onCreate()

        bricksBreakerContainer = createContainer()

        try {
            //_ Firebase authentification: sign out user at app start
            FirebaseAuth.getInstance().signOut()
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            Log.d("OM_TAG", "BrickBreakerApplication: onCreate(): FirebaseAuth signed out")
            Log.i("OM_TAG", "BrickBreakerApplication: onCreate(): firebaseUser = $firebaseUser")

//            //_ Firebase cloud messaging: create notif channel and subscribe topic
//            //_ not needed if only one default channel as it is created by MyFirebaseMessaging class
////            createDeviceNotificationChannel(
////                notifManager = getSystemService(NotificationManager::class.java)
////            )
//            //_ Firebase cloud messaging: subscribe to Firebase topic (Mandatory to receive notifs)
//            subscribeToFcmNotificationTopic()

            // manage application exceptions
        } catch (e: Exception) {
            Log.e("OM_TAG", "BrickBreakerApplication: onCreate(): FirebaseApp initialization failed", e)
        }
    }

    private fun createContainer(): BricksBreakerContainer {
        return try {
            // 👇 class exists ONLY in androidTest
            val androidTestContainerClass = Class.forName(
                "com.oliviermarteaux.a055_bricksBreaker.di.BricksBreakerTestContainer"
            )

            val androidTestContainerConstructor =
                androidTestContainerClass.getConstructor(Context::class.java)
            Log.d("OM_TAG", "BrickBreakerApplication::createContainer: 🧪 Test container loaded via reflection")
            androidTestContainerConstructor.newInstance(this) as BricksBreakerContainer

        } catch (e: ClassNotFoundException) {
            Log.d("OM_TAG", "BrickBreakerApplication::createContainer: 🚀 Prod container loaded")
            BricksBreakerAppContainer(this)
        }
    }
}