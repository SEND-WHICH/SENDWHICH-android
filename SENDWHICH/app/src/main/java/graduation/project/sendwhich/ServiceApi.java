package graduation.project.sendwhich;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceApi {
    @POST("/user/login")
    Call<LoginResponse> userLogin(@Body LoginData data);

    @POST("/user/join")
    Call<JoinResponse> userJoin(@Body JoinData data);

    @POST("/sendFileData")
    Call<sendFileinfoResponse> sendFiledata(@Body sendFileinfoData data);

    @GET("/connect")
    Call<FileUploadUtils> connecting();

    @Multipart
    @POST("/upload")
    Call<ResponseBody> upload(@Part MultipartBody.Part file, @Part("name") RequestBody description);
}
