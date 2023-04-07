package com.vla.sksu.app.manager

import android.content.Context
import com.vla.sksu.app.common.SingletonHolder
import com.vla.sksu.app.data.Category
import com.vla.sksu.app.data.ServiceResponse
import com.vla.sksu.app.data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val LOG_TAG = "APIManager"

class APIManager private constructor(context: Context) {
    companion object : SingletonHolder<APIManager, Context>(::APIManager)

    private val service = ServerService.getInstance(context)

    private var _onUnauthorized: (() -> Unit)? = null

    fun onUnauthorized(completion: () -> Unit){
        _onUnauthorized = completion
    }

    fun login(username: String?, password: String?, completion: (ServiceResponse<String>) -> Unit) {
        service.login(username, password).enqueue(object: Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    fun whoAmI(completion: (ServiceResponse<User>) -> Unit) {
        service.whoAmI().enqueue(object: Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    fun getCategories(completion: (ServiceResponse<ArrayList<Category>>) -> Unit) {
        service.getCategories().enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    fun getSubCategories(categoryId: Int, completion: (ServiceResponse<ArrayList<Category>>) -> Unit) {
        service.getSubCategories(categoryId).enqueue(object : Callback<ArrayList<Category>> {
            override fun onResponse(
                call: Call<ArrayList<Category>>,
                response: Response<ArrayList<Category>>
            ) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<ArrayList<Category>>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    fun updatePushToken(token: String?, completion: (ServiceResponse<Any>) -> Unit){
        // TODO: updatePushToken
//        service.updatePushToken(token).enqueue(object: Callback<ServiceResponse<Any>> {
//            override fun onFailure(call: Call<ServiceResponse<Any>>, t: Throwable) {
//                Timber.tag(LOG_TAG).e(t)
//                completion(ServiceResponse(success = false, error = t))
//            }
//
//            override fun onResponse(
//                call: Call<ServiceResponse<Any>>,
//                response: Response<ServiceResponse<Any>>
//            ) {
//                val serviceResponse = response.body() ?: ServiceResponse()
//                serviceResponse.status = response.code()
//                serviceResponse.success = response.code() == HttpURLConnection.HTTP_OK
//
//                response.errorBody()?.string()?.let {
//                    Timber.tag(LOG_TAG).e(it)
//
//                    val errorResponse = Gson().fromJson(it, ServiceResponse::class.java)
//                    serviceResponse.message = errorResponse.message
//                }
//
//                completion(serviceResponse)
//            }
//
//        })
    }

    fun clearPushToken(completion: (ServiceResponse<Any>) -> Unit) {
        completion(ServiceResponse(success = true))
        // TODO: clearPushToken
//        service.clearPushToken().enqueue(object: Callback<ServiceResponse<Any>> {
//            override fun onFailure(call: Call<ServiceResponse<Any>>, t: Throwable) {
//                Timber.tag(LOG_TAG).e(t)
//                completion(ServiceResponse(success = false, error = t))
//            }
//
//            override fun onResponse(
//                call: Call<ServiceResponse<Any>>,
//                response: Response<ServiceResponse<Any>>
//            ) {
//                val serviceResponse = response.body() ?: ServiceResponse()
//                serviceResponse.status = response.code()
//                serviceResponse.success = response.code() == HttpURLConnection.HTTP_OK
//
//                response.errorBody()?.string()?.let {
//                    Timber.tag(LOG_TAG).e(it)
//
//                    val errorResponse = Gson().fromJson(it, ServiceResponse::class.java)
//                    serviceResponse.message = errorResponse.message
//                }
//
//                completion(serviceResponse)
//            }
//
//        })
    }
}