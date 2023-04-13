package com.vla.sksu.app.manager

import android.content.Context
import com.vla.sksu.app.common.SingletonHolder
import com.vla.sksu.app.data.*
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

    fun logout(completion: (ServiceResponse<Void>) -> Unit) {
        service.logout().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
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

    fun updatePushToken(token: String?, completion: (ServiceResponse<Void>) -> Unit){
        service.updatePushToken(token).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    fun clearPushToken(completion: (ServiceResponse<Void>) -> Unit) {
        service.clearPushToken().enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    // --

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

    fun getBooks(categoryId: Int, completion: (ServiceResponse<ArrayList<Book>>) -> Unit) {
        service.getBooks(categoryId).enqueue(object : Callback<ArrayList<Book>> {
            override fun onResponse(
                call: Call<ArrayList<Book>>,
                response: Response<ArrayList<Book>>
            ) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<ArrayList<Book>>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    fun getBook(bookId: Int, completion: (ServiceResponse<Book>) -> Unit) {
        service.getBook(bookId).enqueue(object : Callback<Book> {
            override fun onResponse(
                call: Call<Book>,
                response: Response<Book>
            ) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<Book>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    fun borrow(bookId: Int, completion: (ServiceResponse<Void>) -> Unit) {
        service.borrow(bookId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    fun getHistories(completion: (ServiceResponse<ArrayList<History>>) -> Unit) {
        service.getHistories().enqueue(object : Callback<ArrayList<History>> {
            override fun onResponse(
                call: Call<ArrayList<History>>,
                response: Response<ArrayList<History>>
            ) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<ArrayList<History>>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }

    fun getHistory(historyId: Int, completion: (ServiceResponse<History>) -> Unit) {
        service.getHistory(historyId).enqueue(object : Callback<History> {
            override fun onResponse(
                call: Call<History>,
                response: Response<History>
            ) {
                val serviceResponse = ServiceResponse(response.body())
                serviceResponse.status = response.code()
                serviceResponse.success = response.isSuccessful
                serviceResponse.errorString = response.errorBody()?.string()

                completion(serviceResponse)
            }

            override fun onFailure(call: Call<History>, t: Throwable) {
                completion(ServiceResponse(success = false, error = t))
            }
        })
    }
}