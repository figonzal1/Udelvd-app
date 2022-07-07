package cl.udelvd

import android.app.Application
import cl.udelvd.refactor.interviewee_feature.data.remote.IntervieweeAPI
import cl.udelvd.refactor.project_feature.data.remote.ProjectAPI
import cl.udelvd.refactor.stats_feature.data.remote.StatsAPI
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

class ApplicationController : Application() {

    override fun onCreate() {
        super.onCreate()

        when {
            BuildConfig.DEBUG -> Timber.plant(Timber.DebugTree())
            else -> {}
        }
    }

    private val okHttpClient = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val udelvdAPIService: Retrofit =
        Retrofit.Builder()
            .baseUrl("http://192.168.1.101:8888/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val statsApi: StatsAPI = udelvdAPIService.create(StatsAPI::class.java)
    val intervieweesApi: IntervieweeAPI = udelvdAPIService.create(IntervieweeAPI::class.java)
    val projectApi: ProjectAPI = udelvdAPIService.create(ProjectAPI::class.java)
}