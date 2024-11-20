package groom.Buddy_BE.area;

import groom.Buddy_BE.member.Member;
import groom.Buddy_BE.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final MemberService memberService;

    //영역 설정
    public Area createArea(Area.AreaType areaType) {
        Area area = new Area();
        area.setAreaType(areaType);

        areaRepository.save(area);

        return area;
    }

    // ID로 Area 조회하는 메서드
    public Area findById(Long areaId) {
        Optional<Area> area = areaRepository.findById(areaId);
        return area.orElse(null);  // 존재하지 않으면 null 반환
    }

    //영역 완수 여부 - 완료로 변경
    public Area completeArea(Long areaId) {

        //영역에 매핑되어있는 미션들이 다 완수가 되었는지

        Area area = findById(areaId);

        area.setCompleted(true);
        areaRepository.save(area);

        return area;
    }


    //수행 완료한 영역 리스트 출력
    public List<String> completeAreaTypes(Long kakaoId) {
        Member member = memberService.findByKakaoId(kakaoId);

        if (member == null) {
            throw new IllegalArgumentException("회원이 존재하지 않습니다.");
        }

        // 해당 유저의 영역 중 isCompleted가 true인 영역의 AreaType 추출
        return member.getAreas().stream()
                .filter(Area::isCompleted) // isCompleted가 true인 영역만 필터링
                .map(area -> area.getAreaType().name()) // AreaType을 문자열로 변환
                .collect(Collectors.toList());
    }

}
