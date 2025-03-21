import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class RequestBody(val query: String)

interface ApiService {
    @POST("/")
    fun sendQuery(@Body request: RequestBody): Call<ResponseBody>?
}