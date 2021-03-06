package br.com.redcode.easyreftrofit.samplekotlin.network.impl

import br.com.redcode.easyreftrofit.library.AbstractNetworkAndErrorHandler
import br.com.redcode.easyreftrofit.library.CallbackNetworkRequest
import br.com.redcode.easyreftrofit.library.model.ErrorHandled
import com.squareup.moshi.Moshi
import br.com.redcode.easyreftrofit.samplekotlin.data.payload.PayloadError
import br.com.redcode.easyreftrofit.samplekotlin.domain.toLogcat
import br.com.redcode.easyreftrofit.samplekotlin.domain.toModel

class NetworkAndErrorHandler(override val callbackNetworkRequest: CallbackNetworkRequest?) :
    AbstractNetworkAndErrorHandler() {

    private val message by lazy { "Erro %d do servidor" }

    companion object {
        val moshi by lazy { Moshi.Builder().build() }
    }

    override fun catchedException(exception: Throwable) {
        when {
            true.not() -> "Throwable message: ${exception.message}".toLogcat() // somente em debug
            else -> exception.printStackTrace()
        }
    }

    override fun parseBodyError(errorBodyAsString: String, networkError: Int): ErrorHandled {
        return try {
            val adapter = moshi.adapter(PayloadError::class.java)
            val payloadError: PayloadError? = adapter.fromJson(errorBodyAsString)
            val modelError = payloadError?.toModel(networkError)
            val result: ErrorHandled = modelError ?: ErrorHandled(message = message, networkError = networkError)
            result
        } catch (e: Exception) {
            val error = PayloadError(msg = String.format(message, networkError), msg_dev = e.message)
            val errorHandled: ErrorHandled = error.toModel(networkError)
            "Error in method 'parseBodyError' from class 'NetworkAndErrorHandler.kt': ${e.message}".toLogcat()
            errorHandled
        }
    }

}