package io.zenandroid.onlinego.data.ogs

import io.reactivex.Completable
import io.reactivex.Single
import io.zenandroid.onlinego.BuildConfig
import io.zenandroid.onlinego.data.model.ogs.JosekiPosition
import io.zenandroid.onlinego.data.model.ogs.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by alex on 02/11/2017.
 */
interface OGSRestAPI {

    @GET("login/google-oauth2/")
    fun initiateGoogleAuthFlow(): Single<Response<ResponseBody>>

    @GET("/complete/google-oauth2/?scope=email+profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile&authuser=0&prompt=none")
    fun loginWithGoogleAuth(
            @Query("code") code: String,
            @Query("state") state: String
    ): Single<Response<ResponseBody>>

    @POST("api/v0/login")
    fun login(@Body request: CreateAccountRequest): Single<UIConfig>

    @GET("api/v1/ui/config/")
    fun uiConfig(): Single<UIConfig>

    @GET("api/v1/games/{game_id}")
    fun fetchGame(@Path("game_id") game_id: Long): Single<OGSGame>

    @GET("api/v1/ui/overview")
//    @GET("api/v1/players/126739/full")
    fun fetchOverview(): Single<Overview>

    @POST("api/v0/register")
    fun createAccount(@Body request: CreateAccountRequest): Single<UIConfig>

    @GET("api/v1/players/{player_id}/games/?source=play&ended__isnull=false&annulled=false&ordering=-ended")
    fun fetchPlayerFinishedGames(
            @Path("player_id") playerId: Long,
            @Query("page_size") pageSize: Int = 10,
            @Query("page") page: Int = 1): Single<PagedResult<OGSGame>>

    @GET("api/v1/players/{player_id}/games/?source=play&ended__isnull=false&annulled=false&ordering=-ended")
    fun fetchPlayerFinishedBeforeGames(
            @Path("player_id") playerId: Long,
            @Query("page_size") pageSize: Int = 10,
            @Query("ended__lt") ended: String,
            @Query("page") page: Int = 1): Single<PagedResult<OGSGame>>

    // NOTE: This is ordered the other way as all the others!!!
    @GET("api/v1/players/{player_id}/games/?source=play&ended__isnull=false&annulled=false&ordering=ended")
    fun fetchPlayerFinishedAfterGames(
            @Path("player_id") playerId: Long,
            @Query("page_size") pageSize: Int = 100,
            @Query("ended__gt") ended: String,
            @Query("page") page: Int = 1): Single<PagedResult<OGSGame>>

    @GET("/api/v1/me/challenges?page_size=100")
    fun fetchChallenges(): Single<PagedResult<OGSChallenge>>

    @POST("/api/v1/me/challenges/{challenge_id}/accept")
    fun acceptChallenge(@Path("challenge_id") id: Long): Completable

    @DELETE("/api/v1/me/challenges/{challenge_id}")
    fun declineChallenge(@Path("challenge_id") id: Long): Completable

    @POST("/api/v1/challenges")
    fun openChallenge(@Body request: OGSChallengeRequest): Completable

    @POST("/api/v1/players/{id}/challenge")
    fun challengePlayer(@Path("id") id: Long, @Body request: OGSChallengeRequest): Completable

    @GET("/api/v1/ui/omniSearch")
    fun omniSearch(@Query("q") q: String): Single<OmniSearchResponse>

    @Headers("x-godojo-auth-token: foofer")
    @GET("/godojo/positions?mode=0")
    fun getJosekiPositions(@Query("id") id: String): Single<List<JosekiPosition>>

    @GET("api/v1/players/{player_id}/")
    fun getPlayerProfile(@Path("player_id") playerId: Long): Single<OGSPlayer>

    @GET("termination-api/player/{player_id}/v5-rating-history?speed=overall&size=0")
    fun getPlayerStats(@Path("player_id") playerId: Long): Single<Glicko2History>

    @GET("termination-api/my/game-chat-history-since/{last_message_id}")
    fun getMessages(@Path("last_message_id") lastMessageId: String): Single<List<Chat>>
}

/*
Other interesting APIs:

https://online-go.com/api/v1/players/89194/full -> gives full list of moves!!!

https://forums.online-go.com/t/ogs-api-notes/17136
https://ogs.readme.io/docs/real-time-api
https://ogs.docs.apiary.io/#reference/games

https://github.com/flovo/ogs_api
https://forums.online-go.com/t/live-games-via-api/1867/2

power user - 126739
 */