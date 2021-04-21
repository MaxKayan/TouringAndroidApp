package net.inqer.touringapp.di

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import net.inqer.touringapp.MainActivity
import net.inqer.touringapp.R
import net.inqer.touringapp.service.RouteService
import javax.inject.Named

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(@ApplicationContext context: Context): PendingIntent =
            PendingIntent.getActivity(
                    context,
                    420,
                    Intent(context, MainActivity::class.java).apply {
                        this.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT
            )

    @ServiceScoped
    @Provides
    @Named("RouteService")
    fun provideNotificationBuilder(
            @ApplicationContext context: Context,
            pendingIntent: PendingIntent): NotificationCompat.Builder =
            NotificationCompat.Builder(context, RouteService.CHANNEL_ID)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.osm_ic_center_map)
                    .setContentTitle(context.getString(R.string.route_following))
                    .setContentText("00:00:00")
                    .setContentIntent(pendingIntent)

    @ServiceScoped
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

}