package com.hexl.rxjava2.api;

import com.hexl.rxjava2.entity.LoginResponse;
import com.hexl.rxjava2.entity.RegisterResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Company: 辽宁众信同行软件开发有限公司
 * <p>
 * Description:
 *
 * @author hexl
 * @date 2017/8/27 on 22:39
 */
public interface Api {
    @POST
    Observable<LoginResponse> login(@Body LoginResponse request);

    @POST
    Observable<RegisterResponse> register(@Body RegisterResponse request);
}
