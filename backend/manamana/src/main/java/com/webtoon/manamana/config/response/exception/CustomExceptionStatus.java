package com.webtoon.manamana.config.response.exception;

import lombok.*;

@Getter
@RequiredArgsConstructor
public enum CustomExceptionStatus {

    /*common error*/
    REQUEST_ERROR(false, 400, "요청을 확인해주세요."),
    //EMPTY_JWT(false, 401, "JWT를 입력해주세요."),
    INVALID_JWT(false, 401, "유효하지 않은 JWT입니다."),
    //INVALID_USER_JWT(false,403,"권한이 없는 유저의 접근입니다."),
    //NOT_AUTHENTICATED_ACCOUNT(false, 403, "로그인이 필요합니다."),

    REQUEST_QUERY_ERROR(false, 400, "잘못된 쿼리 요청입니다."),

    /*association user*/
    NOT_FOUNT_USER(false, 401,"해당하는 유저정보가 없습니다."),

    /*유저 웹툰 연관*/
    NOT_FOUND_USER_WEBTOON(false,400,"관심웹툰이 아니거나 이미 삭제한 관심웹툰입니다."),

    /*유저 장르 연관*/
    NOT_FOUNT_GENRE(false, 400, "해당하는 장르가 없습니다."),

    /*웹툰 관련*/
    NOT_FOUNT_WEBTOON(false, 400, "해당하는 웹툰이 없습니다."),


    ;
    private final boolean isSuccess;

    private final int code;

    private final String message;
}
