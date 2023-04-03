package com.webtoon.manamana.recommand.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webtoon.manamana.config.response.exception.CustomException;
import com.webtoon.manamana.config.response.exception.CustomExceptionStatus;
import com.webtoon.manamana.entity.user.User;
import com.webtoon.manamana.entity.user.UserWebtoon;
import com.webtoon.manamana.entity.webtoon.Author;
import com.webtoon.manamana.entity.webtoon.Webtoon;
import com.webtoon.manamana.recommand.dto.request.ApiAuthorDTO;
import com.webtoon.manamana.recommand.dto.request.RecommendApiRequestDTO;
import com.webtoon.manamana.recommand.dto.request.RecommandWebtoonRequestDTO;
import com.webtoon.manamana.recommand.dto.request.WorldCupRequestDTO;
import com.webtoon.manamana.recommand.dto.response.*;
import com.webtoon.manamana.user.repository.user.*;
import com.webtoon.manamana.webtoon.repository.webtoon.WebtoonGenreRepository;
import com.webtoon.manamana.webtoon.repository.webtoon.WebtoonGenreRepositorySupport;
import com.webtoon.manamana.webtoon.repository.webtoon.WebtoonRepository;
import com.webtoon.manamana.webtoon.repository.webtoon.WebtoonRepositorySupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

import static com.webtoon.manamana.config.response.exception.CustomExceptionStatus.NOT_FOUNT_USER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommandServiceImpl implements RecommandService {

    private final UserRepository userRepository;
    private final UserRepositorySupport userRepositorySupport;
    private final UserGenreRepositorySupport userGenreRepositorySupport;
    private final UserWebtoonRepository userWebtoonRepository;
    private final UserWebtoonRepositorySupport userWebtoonRepositorySupport;
    private final WebtoonRepository webtoonRepository;
    private final WebtoonRepositorySupport webtoonRepositorySupport;
    private final WebtoonGenreRepository webtoonGenreRepository;
    private final WebtoonGenreRepositorySupport webtoonGenreRepositorySupport;

    /* 추천 알고리즘을 통한 웹툰 조회 */
    @Override
    public List<RecommandWebtoonResponseDTO> recommandUserWebtoon(long userId) throws Exception {

        /*
            TODO : DB에서 데이터 가져오는 로직, Exception 던졌던거 다시 처리
         */


        /* 테스트용 */
        List<RecommandWebtoonRequestDTO> recommandWebtoonRequestDTOS = new ArrayList<>();

        recommandWebtoonRequestDTOS.add(
                RecommandWebtoonRequestDTO.builder()
                        .id(1)
                        .name("1초")
                        .grade("전체이용가")
                        .status("연재중")
                        .authors("시니")
                        .genres("드라마")
                        .days("금")
                        .build()
        );

        recommandWebtoonRequestDTOS.add(
                RecommandWebtoonRequestDTO.builder()
                        .id(2)
                        .name("상남자")
                        .grade("전체이용가")
                        .status("연재중")
                        .authors("하늘소")
                        .genres("드라마")
                        .days("금")
                        .build()
        );
        /* 테스트용 */

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ObjectMapper objectMapper = new ObjectMapper();

        String request = objectMapper.writeValueAsString(recommandWebtoonRequestDTOS);

        HttpEntity entity = new HttpEntity(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();

        // url 바꿔야함
        ResponseEntity<String> response = restTemplate.exchange("http://127.0.0.1:8000/recommend", HttpMethod.POST, entity, String.class);

        List<RecommandWebtoonResponseDTO> recommandWebtoonResponseDTOS = objectMapper.readValue(response.getBody(), ApiResponseDTO.class).getResult();

        return recommandWebtoonResponseDTOS;
    }

    /* 사용자 선호 장르 기반 웹툰 추천 */
    @Override
    public List<RecommandWebtoonResponseDTO> recommandByGenre(long userId) throws Exception {

        List<Integer> userGenreMaxWeightList = userGenreRepositorySupport.findByMaxWeightGenre(userId);
        int listLen = userGenreMaxWeightList.size();

        int genreId;
        Random random = new Random();
        switch (listLen) {
            case 0: // 선호 장르가 없을 때
                genreId = random.nextInt(16) + 1;
                System.out.println("case0 : " + genreId);
                break;
            case 1:
                genreId = userGenreMaxWeightList.get(0);
                System.out.println("case1 : " + genreId);
                break;
            default: // 선호 장르 가중치가 최대인 값이 여러개일 때
                genreId = userGenreMaxWeightList.get(random.nextInt(listLen));
                System.out.println("many : " + genreId);
                break;
        }

        List<Long> webtoonIdList = webtoonGenreRepositorySupport.findWebtoonAllByGenre(genreId);
        List<RecommendApiRequestDTO> recommendApiRequestDTOS = new ArrayList<>();

        for (long webtoonId : webtoonIdList) {
            System.out.println("webtoonId = " + webtoonId);
            List<UserWebtoon>  userWebtoonList = userWebtoonRepositorySupport.findByWebtoonIdAndIsDeletedFalse(webtoonId);

            for (UserWebtoon userWebtoon : userWebtoonList) {
                recommendApiRequestDTOS.add(
                        RecommendApiRequestDTO.builder()
                                .userId(userWebtoon.getUser().getId())
                                .webtoonId(userWebtoon.getWebtoon().getId())
                                .score(userWebtoon.getScore())
                                .build()
                );
            }
        }

        HashMap<Long, List<RecommendApiRequestDTO>> map = new HashMap<>();
        map.put(userId, recommendApiRequestDTOS);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(map);

        HttpEntity entity = new HttpEntity(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://127.0.0.1:8000/userbased", HttpMethod.POST, entity, String.class);

        List<AssosiationWebtoonResponseDTO> assosiationWebtoonResponseDTOS = objectMapper.readValue(response.getBody(), AssosiationApiResponseDTO.class).getResult();
        List<RecommandWebtoonResponseDTO> recommandWebtoonResponseDTOS = new ArrayList<>();

        for (AssosiationWebtoonResponseDTO assosiationWebtoonResponseDTO : assosiationWebtoonResponseDTOS) {

            Webtoon webtoon = webtoonRepositorySupport.findWebtoonOne(assosiationWebtoonResponseDTO.getWebtoonId())
                    .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUNT_WEBTOON));

            List<ApiAuthorDTO> apiAuthorDTOS = new ArrayList<>();

            for (Author author : webtoon.getAuthors()) {
                apiAuthorDTOS.add(
                        ApiAuthorDTO.builder()
                                .id(author.getId())
                                .name(author.getName())
                                .build()
                );
            }

            recommandWebtoonResponseDTOS.add(
                    RecommandWebtoonResponseDTO.builder()
                            .id(webtoon.getId())
                            .name(webtoon.getName())
                            .imagePath(webtoon.getImagePath())
                            .authors(apiAuthorDTOS)
                            .build()
            );
        }

        return recommandWebtoonResponseDTOS;
    }

    /* 사용자 연령대 기반 웹툰 추천 */
    @Override
    public List<RecommandWebtoonResponseDTO> recommandByAge(long userId) throws Exception {

        User user = userCheck(userId);

        int userAge = user.getAge();

        List<RecommendApiRequestDTO> recommendApiRequestDTOS = new ArrayList<>();

        // 유저랑 같은 연령대 조회
        List<Long> userIdByAgeList = userRepositorySupport.findUserIdByAge(userAge);

        for (long userIdByAge : userIdByAgeList) {
            List<UserWebtoon> userWebtoonList = userWebtoonRepositorySupport.findByUserIdAndIsDeletedFalse(userIdByAge);

            for (UserWebtoon userWebtoon : userWebtoonList) {
                recommendApiRequestDTOS.add(
                        RecommendApiRequestDTO.builder()
                                .userId(userWebtoon.getUser().getId())
                                .webtoonId(userWebtoon.getWebtoon().getId())
                                .score(userWebtoon.getScore())
                                .build()
                );
            }
        }

        HashMap<Long, List<RecommendApiRequestDTO>> map = new HashMap<>();
        map.put(userId, recommendApiRequestDTOS);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(map);

        HttpEntity entity = new HttpEntity(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://127.0.0.1:8000/userbased", HttpMethod.POST, entity, String.class);

        List<AssosiationWebtoonResponseDTO> assosiationWebtoonResponseDTOS = objectMapper.readValue(response.getBody(), AssosiationApiResponseDTO.class).getResult();
        List<RecommandWebtoonResponseDTO> recommandWebtoonResponseDTOS = new ArrayList<>();

        for (AssosiationWebtoonResponseDTO assosiationWebtoonResponseDTO : assosiationWebtoonResponseDTOS) {

            Webtoon webtoon = webtoonRepositorySupport.findWebtoonOne(assosiationWebtoonResponseDTO.getWebtoonId())
                    .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUNT_WEBTOON));

            List<ApiAuthorDTO> apiAuthorDTOS = new ArrayList<>();

            for (Author author : webtoon.getAuthors()) {
                apiAuthorDTOS.add(
                        ApiAuthorDTO.builder()
                                .id(author.getId())
                                .name(author.getName())
                                .build()
                );
            }

            recommandWebtoonResponseDTOS.add(
                    RecommandWebtoonResponseDTO.builder()
                            .id(webtoon.getId())
                            .name(webtoon.getName())
                            .imagePath(webtoon.getImagePath())
                            .authors(apiAuthorDTOS)
                            .build()
            );
        }

        return recommandWebtoonResponseDTOS;
    }

    /* 사용자 성별 기반 웹툰 추천 */
    @Override
    public List<RecommandWebtoonResponseDTO> recommandByGender(long userId) throws Exception {

        User user = userCheck(userId);

        String userGender = user.getGender();

        List<RecommendApiRequestDTO> recommendApiRequestDTOS = new ArrayList<>();

        // 유저랑 같은 성별 조회
        List<Long> userIdByGenderList = userRepositorySupport.findUserIdByGender(userGender);

        for (long userIdByGender : userIdByGenderList) {
            List<UserWebtoon> userWebtoonList = userWebtoonRepositorySupport.findByUserIdAndIsDeletedFalse(userIdByGender);

            for (UserWebtoon userWebtoon : userWebtoonList) {
                recommendApiRequestDTOS.add(
                        RecommendApiRequestDTO.builder()
                                .userId(userWebtoon.getUser().getId())
                                .webtoonId(userWebtoon.getWebtoon().getId())
                                .score(userWebtoon.getScore())
                                .build()
                );
            }
        }

        HashMap<Long, List<RecommendApiRequestDTO>> map = new HashMap<>();
        map.put(userId, recommendApiRequestDTOS);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(map);

        HttpEntity entity = new HttpEntity(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://127.0.0.1:8000/userbased", HttpMethod.POST, entity, String.class);

        List<AssosiationWebtoonResponseDTO> assosiationWebtoonResponseDTOS = objectMapper.readValue(response.getBody(), AssosiationApiResponseDTO.class).getResult();
        List<RecommandWebtoonResponseDTO> recommandWebtoonResponseDTOS = new ArrayList<>();

        for (AssosiationWebtoonResponseDTO assosiationWebtoonResponseDTO : assosiationWebtoonResponseDTOS) {

            Webtoon webtoon = webtoonRepositorySupport.findWebtoonOne(assosiationWebtoonResponseDTO.getWebtoonId())
                    .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUNT_WEBTOON));

            List<ApiAuthorDTO> apiAuthorDTOS = new ArrayList<>();

            for (Author author : webtoon.getAuthors()) {
                apiAuthorDTOS.add(
                        ApiAuthorDTO.builder()
                                .id(author.getId())
                                .name(author.getName())
                                .build()
                );
            }

            recommandWebtoonResponseDTOS.add(
                    RecommandWebtoonResponseDTO.builder()
                            .id(webtoon.getId())
                            .name(webtoon.getName())
                            .imagePath(webtoon.getImagePath())
                            .authors(apiAuthorDTOS)
                            .build()
            );
        }

        return recommandWebtoonResponseDTOS;
    }

    /* 관련 웹툰 추천 */
    @Override
    public  List<RecommandWebtoonResponseDTO> recommandAssociationWebtoon(long webtoonId) throws Exception {

        /*
            TODO : Exception 던졌던거 처리
         */

        List<UserWebtoon> userWebtoons =  userWebtoonRepository.findAllByIsDeletedFalse();

        List<RecommendApiRequestDTO> recommendApiRequestDTOS = new ArrayList<>();

        for (UserWebtoon userWebtoon : userWebtoons) {
            recommendApiRequestDTOS.add(
                    RecommendApiRequestDTO.builder()
                            .userId(userWebtoon.getUser().getId())
                            .webtoonId(userWebtoon.getWebtoon().getId())
                            .score(userWebtoon.getScore())
                            .build()
            );
        }

        HashMap<Long, List<RecommendApiRequestDTO>> map = new HashMap<>();
        map.put(webtoonId, recommendApiRequestDTOS);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(map);

        HttpEntity entity = new HttpEntity(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://127.0.0.1:8000/assosiation", HttpMethod.POST, entity, String.class);

        List<AssosiationWebtoonResponseDTO> assosiationWebtoonResponseDTOS = objectMapper.readValue(response.getBody(), AssosiationApiResponseDTO.class).getResult();
        List<RecommandWebtoonResponseDTO> recommandWebtoonResponseDTOS = new ArrayList<>();

        for (AssosiationWebtoonResponseDTO assosiationWebtoonResponseDTO : assosiationWebtoonResponseDTOS) {

            Webtoon webtoon = webtoonRepositorySupport.findWebtoonOne(assosiationWebtoonResponseDTO.getWebtoonId())
                    .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUNT_WEBTOON));

            List<ApiAuthorDTO> apiAuthorDTOS = new ArrayList<>();

            for (Author author : webtoon.getAuthors()) {
                apiAuthorDTOS.add(
                        ApiAuthorDTO.builder()
                                .id(author.getId())
                                .name(author.getName())
                                .build()
                );
            }

            recommandWebtoonResponseDTOS.add(
                    RecommandWebtoonResponseDTO.builder()
                            .id(webtoon.getId())
                            .name(webtoon.getName())
                            .imagePath(webtoon.getImagePath())
                            .authors(apiAuthorDTOS)
                            .build()
            );
        }

        return recommandWebtoonResponseDTOS;
    }

    /* 취향 월드컵 조회 */
    @Override
    public List<WorldCupResponseDTO> worldCupWebtoonSearch() {

        Set<Long> worldCupWebtoon = new HashSet<>();

        for (int i = 1; i <= 16; i++) {
            List<Long> genreTop10 = webtoonGenreRepositorySupport.findGenreWebtoonTOP10(i);

            if (genreTop10.size() > 2) {

                Random random = new Random();
                List<Long> randomTwo = genreTop10.stream()
                        .filter(e -> random.nextBoolean())
                        .limit(2)
                        .collect(Collectors.toList());

                worldCupWebtoon.add(randomTwo.get(0));
                worldCupWebtoon.add(randomTwo.get(1));
            }
        }

        List<WorldCupResponseDTO> worldCupResponseDTOS = new ArrayList<>();

        for (Long webtoonId : worldCupWebtoon) {
            Webtoon webtoon = webtoonRepositorySupport.findWebtoonOne(webtoonId)
                    .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUNT_WEBTOON));

            worldCupResponseDTOS.add(
                    WorldCupResponseDTO.builder()
                            .id(webtoon.getId())
                            .imagePath(webtoon.getImagePath())
                            .build()
            );
        }

        return worldCupResponseDTOS;
    }

    /* 취향 월드컵 결과 저장 */
    @Override
    @Transactional
    public WorldCupResultDTO worldCupWebtoonSave(long userId, WorldCupRequestDTO worldCupRequestDTO) throws Exception {

        // users_and_webtoons 테이블 정보
        List<UserWebtoon> userWebtoons =  userWebtoonRepository.findAllByIsDeletedFalse();
        List<RecommendApiRequestDTO> recommendApiRequestDTOS = new ArrayList<>();

        for (UserWebtoon userWebtoon : userWebtoons) {
            recommendApiRequestDTOS.add(
                    RecommendApiRequestDTO.builder()
                            .userId(userWebtoon.getUser().getId())
                            .webtoonId(userWebtoon.getWebtoon().getId())
                            .score(userWebtoon.getScore())
                            .build()
            );
        }

        // 유저가 선택한 웹툰 정보 추가 (DB에 반영 X)
        for (long webtoonId : worldCupRequestDTO.getId()) {
            recommendApiRequestDTOS.add(
                    RecommendApiRequestDTO.builder()
                            .userId(userId)
                            .webtoonId(webtoonId)
                            .score(5)
                            .build()
            );
        }

        HashMap<Long, List<RecommendApiRequestDTO>> map = new HashMap<>();
        map.put(userId, recommendApiRequestDTOS);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(map);

        HttpEntity entity = new HttpEntity(request, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://127.0.0.1:8000/userbased", HttpMethod.POST, entity, String.class);

        List<AssosiationWebtoonResponseDTO> assosiationWebtoonResponseDTOS = objectMapper.readValue(response.getBody(), AssosiationApiResponseDTO.class).getResult();
        long recommendWebtoonId = assosiationWebtoonResponseDTOS.get(0).getWebtoonId();

        Webtoon webtoon = webtoonRepositorySupport.findWebtoonOne(recommendWebtoonId)
                .orElseThrow(() -> new CustomException(CustomExceptionStatus.NOT_FOUNT_WEBTOON));

        WorldCupResultDTO worldCupResultDTO = WorldCupResultDTO.builder()
                .id(webtoon.getId())
                .name(webtoon.getName())
                .imagePath(webtoon.getImagePath())
                .build();

        return worldCupResultDTO;
    }

    /* 유틸 메서드 - 유저 조회 */
    public User userCheck(long userId) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new CustomException(NOT_FOUNT_USER));

        return user;
    }
}
