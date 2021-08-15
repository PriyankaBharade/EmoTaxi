package com.emotaxi.retrofit

import android.content.Context
import android.net.Uri
import com.emotaxi.model.*
import com.emotaxi.widget.FileUtils
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File


interface BackEndApi {
    companion object {
        fun prepareFilePart(
            context: Context?,
            partName: String?,
            fileUripath: String
        ): MultipartBody.Part {
            return if (!fileUripath.isEmpty()) {
                val fileUri = Uri.fromFile(File(fileUripath))
                val file: File = FileUtils.getFile(context, fileUri)
                // create RequestBody instance from file
                val requestFile = RequestBody.create(
                    MediaType.parse("multipart/form-data"),
                    file
                )
                MultipartBody.Part.createFormData(partName.toString(), file.name, requestFile)
            } else {
                val attachmentEmpty =
                    RequestBody.create(MediaType.parse("text/plain"), "")
                MultipartBody.Part.createFormData("attachment", "", attachmentEmpty)
            }
        }
    }

    @GET("vehicule/proximite/?")
    fun vehicleList(
        @Header("Content-Type") type: String,
        @Header("Authorization") authorization: String,
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("radius") radius: String,
        @Query("limit") limit: String
    ): Call<VehicleListModel>


    @Headers("Content-Type: application/json")
    @POST("course/estimation")
    fun getEstimation(
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): Call<EstimationModel>

    @Headers("Content-Type: application/json")
    @POST("course")
    fun course(
        @Header("Authorization") authorization: String,
        @Body body: JsonObject
    ): Call<BookingModel>

    @FormUrlEncoded
    @POST("Payment_stripe/getToken")
    fun getToken(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<TokenModel>

    @FormUrlEncoded
    @POST("Payment_stripe/createCustomer")
    fun createCustomer(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<CreateCustomer>

    @FormUrlEncoded
    @POST("Payment_stripe/getcard")
    fun getcard(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<CardListModel>

    @FormUrlEncoded
    @POST("Payment_stripe/removecard")
    fun removecard(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<CardListModel>


    @FormUrlEncoded
    @POST("Payment_stripe/addcard")
    fun addcard(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<AddCardModel>

    @FormUrlEncoded
    @POST("users/profile_update")
    fun profileUpdate(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<TokenModel>

    @FormUrlEncoded
    @POST("users/signup")
    fun signUpApi(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<SignUpModel>


    @FormUrlEncoded
    @POST("users/verify_otp")
    fun verifyOtp(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<SignUpModel>

    @FormUrlEncoded
    @POST("users/get_profile")
    fun getProfile(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<GetProfileModel>


    @Multipart
    @POST("users/profile_update")
    fun profileUpdate(
        @Part image: MultipartBody.Part,
        @Part("user_id") user_id: RequestBody,
        @Part("username") full_name: RequestBody,
        @Part("email") email: RequestBody,
        @Part("version") version: RequestBody,
        @Part("device_type") device_type: RequestBody
    ): Call<TokenModel>

    @FormUrlEncoded
    @POST("Payment_stripe/checkout")
    fun checkout(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("Payment_stripe/refund")
    fun refund(
        @HeaderMap headers: Map<String, String>,
        @FieldMap hashMap: HashMap<String, String>
    ): Call<RefundModel>

    @FormUrlEncoded
    @POST("update_usertype")
    fun update(
        @FieldMap hashMap: HashMap<String, String>
    ): Call<TokenModel>

    @GET
    fun course(
        @Header("Content-Type") type: String,
        @Header("Authorization") authorization: String,
        @Url url: String
    ): Call<BookingDetailsModel>

    @DELETE
    fun coursedelete(
        @Header("Content-Type") type: String,
        @Header("Authorization") authorization: String,
        @Url url: String
    ): Call<String>

}