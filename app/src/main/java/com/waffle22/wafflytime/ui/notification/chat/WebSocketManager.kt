import android.content.SharedPreferences
import  android.util.Log
import com.squareup.moshi.Moshi
import com.waffle22.wafflytime.network.WafflyApiService
import com.waffle22.wafflytime.ui.notification.chat.MessageListener
import com.waffle22.wafflytime.util.AuthStorage
import com.waffle22.wafflytime.util.TokenInterceptor
import okhttp3.*
import okio.ByteString
import  java.util.concurrent.TimeUnit

class WebSocketManager(
    private val sharedPreferences: SharedPreferences,
    private val authStorage: AuthStorage,
    private val moshi: Moshi
) {
    private val TAG = WebSocketManager::class.java.simpleName
    private val MAX_NUM  =  5  // Maximum number of reconnections
    private val MILLIS  =  5000  // Reconnection interval, milliseconds
    private lateinit var client: OkHttpClient
    private lateinit var request: Request
    private lateinit var messageListener: MessageListener
    private lateinit var mWebSocket: WebSocket
    private var isConnect = false
    private var connectNum = 0

    fun init(url: String, _messageListener: MessageListener) {
        client = OkHttpClient.Builder()
            .writeTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(TokenInterceptor(sharedPreferences, authStorage, moshi))
            .build()
        request = Request.Builder().url(url).build()
        messageListener = _messageListener
    }

    /**
     * connect
     */
    fun connect() {
        if (isConnect()) {
            Log.i(TAG, "web socket connected")
            return
        }
        client.newWebSocket(request, createListener())
    }

    /**
     * Reconnection
     */
    fun reconnect() {
        if (connectNum <= MAX_NUM) {
            try {
                Thread.sleep(MILLIS.toLong())
                connect()
                connectNum++
            } catch (e: InterruptedException) {
                e.printStackTrace ()
            }
        } else {
            Log.i(
                TAG,
                "reconnect over $MAX_NUM,please check url or network"
            )
        }
    }

    /**
     * Whether to connect
     */
    fun isConnect(): Boolean {
        return isConnect
    }

    /**
     * send messages
     *
     * @param text string
     * @return boolean
     */
    fun sendMessage(text: String): Boolean {
        return if (!isConnect()) false else mWebSocket.send(text)
    }

    /**
     * send messages
     *
     * @param byteString character set
     * @return boolean
     */
    fun sendMessage(byteString: ByteString): Boolean {
        return if (!isConnect()) false else mWebSocket.send(byteString)
    }

    /**
     * Close connection
     */
    fun close() {
        if (isConnect()) {
            mWebSocket.cancel()
            mWebSocket.close( 1001 , "The client actively closes the connection " )
        }
    }

    private fun createListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(
                webSocket: WebSocket,
                response: Response
            ) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "open:$response")
                mWebSocket = webSocket
                isConnect = response.code == 101
                if (!isConnect) {
                    reconnect()
                } else {
                    Log.i(TAG, "connect success.")
                    messageListener.onConnectSuccess()
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                messageListener.onMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                messageListener.onMessage(bytes.base64())
            }

            override fun onClosing(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                super.onClosing(webSocket, code, reason)
                isConnect = false
                messageListener.onClose()
            }

            override fun onClosed(
                webSocket: WebSocket,
                code: Int,
                reason: String
            ) {
                super.onClosed(webSocket, code, reason)
                isConnect = false
                messageListener.onClose()
            }

            override fun onFailure(
                webSocket: WebSocket,
                t: Throwable,
                response: Response?
            ) {
                super.onFailure(webSocket, t, response)
                if (response != null) {
                    Log.i(
                        TAG,
                        "connect failed：" + response.message
                    )
                }
                Log.i(
                    TAG,
                    "connect failed throwable：" + t.message
                )
                isConnect = false
                messageListener.onConnectFailed()
                reconnect()
            }
        }
    }
}