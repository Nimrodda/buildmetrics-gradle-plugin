import com.nhaarman.mockitokotlin2.mock
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test

class BuildDurationTrackerTest {
    @Test
    fun trackBuildFinished() {
        val server = MockWebServer()
        server.enqueue(MockResponse())
        server.start()
        val baseUrl = server.url("/collect/")

        val tracker = BuildDurationTracker(
            extension = BuildMetricsExtension(mock()),
            user = User(uid = "555", name = "Foo"),
            isOffline = false,
            httpClient = OkHttpClient(),
            url = baseUrl
        )

        tracker.trackBuildFinished(true, 40L)

        server.takeRequest()
    }
}
