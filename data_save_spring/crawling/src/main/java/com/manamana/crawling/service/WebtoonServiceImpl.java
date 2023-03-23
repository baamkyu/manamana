package com.manamana.crawling.service;

import com.manamana.crawling.dto.*;
import com.manamana.crawling.entity.webtoon.Webtoon;
import com.manamana.crawling.entity.webtoon.WebtoonGenre;
import com.manamana.crawling.entity.webtoon.WebtoonProvider;
import com.manamana.crawling.entity.webtoon.codetable.Genre;
import com.manamana.crawling.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class WebtoonServiceImpl implements WebtoonService {

    private final WebtoonRepository webtoonRepository;
    private final WebtoonProviderRepository webtoonProviderRepository;
    private final GradeRepository gradeRepository;
    private final SerialStatusRepository serialStatusRepository;
    private final GenreRepository genreRepository;

    // 웹툰 데이터 리스트 처리
    public void webtoonsData(WebtoonDataArrayDTO webtoonDataArrayDTO) {
        int providerId = webtoonDataArrayDTO.getProvider_id();
        List<WebtoonDataDTO> webtoonData = webtoonDataArrayDTO.getData();
        webtoonData.forEach(w -> {
//            SplitDTO splitDTO = splitWebtoonData(w, providerId);
            Webtoon webtoon = saveWebtoon(providerId, w);
            saveGenre(webtoon, w);
//            saveDay(splitDTO.getDayDTO());
//            saveAuthor(splitDTO.getAuthorDTO());
        });
    }

//    // 웹툰 단건 데이터 분류 (장르, 웹툰, 요일, 작가)
//    public SplitDTO splitWebtoonData(WebtoonDataDTO webtoonDataDTO, int provider) {
//        // 웹툰 데이터 정제
//        String name = webtoonDataDTO.getName();
//        String image = webtoonDataDTO.getImage();
//        String plot = webtoonDataDTO.getPlot();
//        String grade = webtoonDataDTO.getGrade();
//        int gradeId = 0;
//        if (grade.equals("성인")) {
//            gradeId = 1;
//        } else {
//            gradeId = 0;
//        }
//
//        String status = webtoonDataDTO.getStatus();
//        int statusId = 0;
//        if (status.equals("휴재")) {
//            statusId = 2;
//        } else if (status.equals("완결")) {
//            statusId = 1;
//        } else {
//            statusId = 0;
//        }
//
//        String webtoonUrl = webtoonDataDTO.getWebtoon_url();
//        int webtoonIdInt = webtoonDataDTO.getWebtoon_id();
//        String webtoonId = Integer.toString(webtoonIdInt);
//        String startDateStirng = webtoonDataDTO.getStart_date();
//        LocalDate startDate = LocalDate.parse(startDateStirng, DateTimeFormatter.ISO_DATE)
//
//        int totalEp = webtoonDataDTO.getTotal_ep();
//        Boolean isDeleted = false;
//        String colorHsl = webtoonDataDTO.getColorHsl();
//        WebtoonProvider providerId = webtoonProviderRepository.findById(provider).orElseThrow();
//
//        WebtoonDTO webtoonDTO = WebtoonDTO.builder()
//                .name(name)
//                .imagePath(image)
//                .plot(plot)
//                .gradeId(gradeId)
//                .statusId(statusId)
//                .webtoonUrl(webtoonUrl)
//                .webtoonId(webtoonId)
//                .startDate(startDate)
//                .totalEp(totalEp)
//                .colorHsl(colorHsl)
//                .providerId(providerId)
//                .build();
//
//        GenreDTO genreDTO = GenreDTO.builder()
//                .webtoonId(webtoonId)
//                .genreIdArr()
//                .build();
//
//        DayDTO dayDTO = DayDTO.builder()
//                .webtoonId()
//                .codeId()
//                .build();
//
//        AuthorDTO authorDTO = AuthorDTO.builder()
//                .webtoonId()
//                .nameArr()
//                .build();
//
//
//
//        return SplitDTO.builder()
//                .webtoonDTO(webtoonDTO)
//                .genreDTO(genreDTO)
//                .dayDTO()
//                .authorDTO()
//                .build();
//
//    }

    // 웹툰 저장
    public Webtoon saveWebtoon(int provider, WebtoonDataDTO webtoonDataDTO) {
        // 웹툰 데이터 정제
        String name = webtoonDataDTO.getName();
        String image = webtoonDataDTO.getImage();
        String plot = webtoonDataDTO.getPlot();
        String grade = webtoonDataDTO.getGrade();
        int gradeId = 0;
        if (grade.equals("성인")) {
            gradeId = 1;
        } else {
            gradeId = 0;
        }

        String status = webtoonDataDTO.getStatus();
        int statusId = 0;
        if (status.equals("휴재")) {
            statusId = 2;
        } else if (status.equals("완결")) {
            statusId = 1;
        } else {
            statusId = 0;
        }

        String webtoonUrl = webtoonDataDTO.getWebtoon_url();
        int webtoonIdInt = webtoonDataDTO.getWebtoon_id();
        String webtoonId = Integer.toString(webtoonIdInt);
        String startDateStirng = webtoonDataDTO.getStart_date();
        LocalDate startDate = LocalDate.parse(startDateStirng, DateTimeFormatter.ISO_DATE);

        int totalEp = webtoonDataDTO.getTotal_ep();
        Boolean isDeleted = false;
        String colorHsl = webtoonDataDTO.getColorHsl();
        WebtoonProvider providerId = webtoonProviderRepository.findById(provider).orElseThrow();

        Webtoon webtoon = Webtoon.builder()
                .name(name)
                .imagePath(image)
                .plot(plot)
                .gradeId(gradeId)
                .serialId(statusId)
                .webtoonUrl(webtoonUrl)
                .webtoonId(webtoonId)
                .startDate(startDate)
                .totalEp(totalEp)
                .colorHsl(colorHsl)
                .providerId(providerId)
                .build();

        return webtoonRepository.save(webtoon);
    }

    // 장르 저장
    //TODO 여기 하고 있음
    public void saveGenre(Webtoon webtoon, WebtoonDataDTO webtoonDataDTO) {
        List<WebtoonGenre> webtoonGenres = new ArrayList<>();
        webtoonDataDTO.getGenre_arr().forEach(g -> {
            int genreId = GenreRepository.findByName(g).orElseThrow();
            Genre genre = GenreRepository.findById(genreId).orElseThrow();
            WebtoonGenre webtoonGenre = WebtoonGenre.builder()
                    .genre(g)
                    .webtoon(webtoon)
                    .build();
        });



    }

    // 요일 저장
    public void saveDay(DayDTO dayDTO) {

    }

    // 작가 저장
    public void saveAuthor(AuthorDTO authorDTO) {

    }







//
//
//
//    public void webtoonsData(WebtoonDataArrayDTO webtoonDataArrayDTO, int providerId) {
//
//    }
//
//    /**
//     * 웹툰 데이터 저장
//     */
//    public void saveWebtoon(WebtoonDataDTO webtoonDataDTO, int providerId) {
//
//        Webtoon webtoon = Webtoon.builder()
//                .name(webtoonDataDTO.getName())
//                .imagePath(webtoonDataDTO.getImage())
//                .plot(webtoonDataDTO.getPlot())
//                .gradeId(webtoonDataDTO.getGrade())
//                .serialId(webtoonDataDTO.getSerialId())
//                .webtoonUrl(webtoonDataDTO.getWebtoonUrl())
//                .webtoonId(webtoonDataDTO.getWebtoonId())
//                .startDate(LocalDate.now())
//                .totalEp(webtoonDataDTO.getTotalEp())
//                .colorHsl(webtoonDataDTO.getColorHsl())
//                .isDeleted(false)
//                .providerId(web)
//                .build();
//
//        validateDuplicateWebtoon(webtoonDataDTO); //중복 웹툰 검증
//
//        WebtoonProvider web = webtoonProviderRepository
//                .findById(1)
//                .orElseThrow();
//
//        Webtoon webtoon = Webtoon.builder()
//                .name(webtoonDataDTO.getName())
//                .imagePath(webtoonDataDTO.getImagePath())
//                .plot(webtoonDataDTO.getPlot())
//                .gradeId(webtoonDataDTO.getGradeId())
//                .serialId(webtoonDataDTO.getSerialId())
//                .webtoonUrl(webtoonDataDTO.getWebtoonUrl())
//                .webtoonId(webtoonDataDTO.getWebtoonId())
//                .startDate(LocalDate.now())
//                .totalEp(webtoonDataDTO.getTotalEp())
//                .colorHsl(webtoonDataDTO.getColorHsl())
//                .isDeleted(false)
//                .providerId(web)
//                .build();
//
////        Webtoon webtoon = createWebtoonDTO.createWebtoon();
//        System.out.println("webtoon" + webtoon.getProviderId());
//        webtoonRepository.save(webtoon);
//    }
//
//    //TODO webtoonId, webtoonProvder둘다 확인하기
//    private void validateDuplicateWebtoon(WebtoonDataDTO webtoonDataDTO) {
//        WebtoonProvider web = webtoonProviderRepository
//                .findById(webtoonDataDTO.getProviderId())
//                .orElseThrow();
//
//        Optional<Webtoon> webtoon = webtoonRepository
//                .findByWebtoonIdAndProviderId(webtoonDataDTO.getWebtoonId(), web);
//
//
//        if(!webtoon.isEmpty()) {
//
//        }
//    }
//
//    /**
//     * 웹툰 전체 조회
//     */
//    public List<Webtoon> findWebtoon() {
//        return webtoonRepository.findAll();
//    }
//
//    /**
//     * 웹툰 단건 조회(id)
//     */
//    public Webtoon findOne(Long id) {
//        return webtoonRepository
//                .findById(id)
//                .orElseThrow();
//    }
//
//    /**
//     * 웹툰 단건 조회(webtoonId)
//     */
//    public Webtoon findOneByWebtoonId(String webtoonId) {
//        Webtoon webtoon = webtoonRepository
//                .findByWebtoonId(webtoonId)
//                .orElseThrow();
//        return webtoon;
//    }
//
//    public void saveWebtoons(WebtoonDataArrayDTO webtoonDataArrayDTO) {
//
//        int providerId = webtoonDataArrayDTO.getProvider_id();
//
//        webtoonDataArrayDTO.getData().forEach(w -> {
//            saveWebtoon(w, providerId);
//        });
//    }
}
