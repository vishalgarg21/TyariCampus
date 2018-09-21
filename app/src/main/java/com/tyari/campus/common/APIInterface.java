package com.tyari.campus.common;

import com.tyari.campus.model.Book;
import com.tyari.campus.model.ChangePasswordRequest;
import com.tyari.campus.model.PurchaseTestRequest;
import com.tyari.campus.model.Test;
import com.tyari.campus.model.UserRequest;
import com.tyari.campus.model.ForgotPasswordRequest;
import com.tyari.campus.model.GenericResponse;
import com.tyari.campus.model.GetQuestionRequest;
import com.tyari.campus.model.GetSubjectRequest;
import com.tyari.campus.model.GetTestRequest;
import com.tyari.campus.model.Job;
import com.tyari.campus.model.LoginRequest;
import com.tyari.campus.model.Offer;
import com.tyari.campus.model.PackageInfo;
import com.tyari.campus.model.Question;
import com.tyari.campus.model.RegisterRequest;
import com.tyari.campus.model.ResetPasswordRequest;
import com.tyari.campus.model.Result;
import com.tyari.campus.model.Subject;
import com.tyari.campus.model.SubjectRequest;
import com.tyari.campus.model.User;
import com.tyari.campus.model.Video;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {

    @POST("api/register")
    Call<GenericResponse> register(@Body RegisterRequest request);

    @POST("api/login")
    Call<GenericResponse<User>> login(@Body LoginRequest request);

    @POST("api/updateProfile")
    Call<GenericResponse> updateProfile(@Body RegisterRequest request);

    @POST("api/changePassword")
    Call<GenericResponse> changePassword(@Body ChangePasswordRequest request);

    @POST("api/resetPassword")
    Call<GenericResponse> resetPassword(@Body ResetPasswordRequest request);

    @POST("api/forgotPassword")
    Call<GenericResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @POST("api/logout")
    Call<GenericResponse> logout(@Body UserRequest request);

    @POST("api/getSubjectTests")
    Call<GenericResponse<List<PackageInfo>>> getTests(@Body GetTestRequest request);

    @POST("admin/api/getTestQuestions")
    Call<GenericResponse<List<Question>>> getQuestions(@Body GetQuestionRequest request);

    @GET("api/getCourses")
    Call<GenericResponse<List<Subject>>> getCourses();

    @POST("api/getCourseSubjects")
    Call<GenericResponse<List<Subject>>> getSubjects(@Body GetSubjectRequest request);

    @POST("api/getCourseSubjects")
    Call<GenericResponse<Subject>> getSubjectById(@Body GetSubjectRequest request);

    @POST("api/saveSubjects")
    Call<GenericResponse> saveSubjects(@Body SubjectRequest request);

    @POST("api/getUserSubjects")
    Call<GenericResponse<SubjectRequest>> fetchSubjects(@Body UserRequest request);

    @POST("api/saveTestResult")
    Call<GenericResponse> saveResult(@Body Result result);

    @POST("api/getTestResults")
    Call<GenericResponse<List<Result>>> fetchResults(@Body UserRequest request);

    @POST("api/purchaseTest")
    Call<GenericResponse> purchaseTest(@Body PurchaseTestRequest request);

    @POST("api/fetchPurchasedTest")
    Call<GenericResponse<List<Test>>> fetchPurchasedTests(@Body UserRequest request);

    @POST("api/getTest")
    Call<GenericResponse<Test>> getTestById(@Body GetQuestionRequest request);

    @GET("api/getSolvedQA")
    Call<GenericResponse<List<Question>>> fetchSolvedQuestions();

    @GET("api/getOffers")
    Call<GenericResponse<List<Offer>>> getOffers();

    @GET("api/getJobs")
    Call<GenericResponse<List<Job>>> getJobs();

    @GET("api/getBooks")
    Call<GenericResponse<List<Book>>> getBooks();

    @GET("api/getVideos")
    Call<GenericResponse<List<Video>>> getVideos();
}
