package example.couroutine.com.kotlincoroutine

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.suspendCoroutine

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        println("ffffff")
        launch {
            println("hello world")
        }
        println("out side the suspend")
        //testRunFunction()
         button.setOnClickListener{
             launch(UI) {setTextAfterDelay(2,"hello")  } //The UI specifying the UI thread
             /**
              * There are other coroutine dispatcher like commonpools and defaultpools both are pointing to pool of background thread
              * The defaultpool is used when no thread is specified
              */

         }

        button2.setOnClickListener{

            launch (UI){
                var data=""
//                try{
//                    data = async(CommonPool) {downloadDataBlockingWithCallback()  }.await()
//                }catch ( e:Exception){
//                    data="Error"
//                }

                data= async(CommonPool) {downloadDataBlockingWithCallback()  }.await()
                setTextAfterDelay(0,data)
            }
        }

        println("Main thread execution end")
    }

    private suspend fun setTextAfterDelay(seconds: Long, text: String) {
        delay(seconds, TimeUnit.SECONDS)
        textView.text = text
    }




    private fun downloadDataBlocking(): String {
        val client = OkHttpClient()
        val request = Request.Builder()
                .url("https://jsonplaceholder.typicode.com/posts")
                .build()

        val response = client.newCall(request).execute()
        return response.body()?.string() ?: ""
    }

    /**
     * handling callback
     */
    private suspend fun downloadDataBlockingWithCallback(): String {
      
        return suspendCoroutine { continuation ->

            val client = OkHttpClient()
            val request = Request.Builder()
                    .url("https://jsonplaceholder.typicode.com/posts")
                    .build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resumeWithException(e)
                }
                override fun onResponse(call: Call, response: Response) {
                    continuation.resume(response.body()?.string() ?: "")
                }
            })
        }
    }

    fun testRunFunction() {
        // Start a coroutine

        launch {
            println("In start : launch")
            Thread.sleep(200)
            println("In ended : launch")
        }

        run {
            println("Out start: main")
            Thread.sleep(300)
            println("Out ended: main")
        }
    }
}
