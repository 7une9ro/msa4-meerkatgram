package com.msa4meerkatgram.global.annotations.openapi;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ApiResponse(
    responseCode = "401"
    , description = "인증 실패 에러"
    , content = @Content(
        mediaType = "application/json"
        , examples = {
            @ExampleObject(
                name = "로그인 실패"
                , value = """
                        {
                            "code": "E01"
                            , "message": "NOT_REGISTERED_ERROR"
                        }
                    """
            ),
            @ExampleObject(
                name = "인증 실패 에러"
                , value = """
                        {
                            "code": "E02"
                            , "message": "UNAUTHENTICATED_ERROR"
                        }
                    """
            ),
            @ExampleObject(
                    name = "토큰 검증 에러"
                    , value = """
                            {
                                "code": "E04"
                                , "message": "UNAUTHORIZED_ERROR"
                            }
                        """
            )
        }
    )
)
public @interface ApiUnauthenticatedErrorResponse {
}
