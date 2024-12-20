package groom.Buddy_BE.character;

import groom.Buddy_BE.member.Member;
import groom.Buddy_BE.member.MemberInfoDTO;
import groom.Buddy_BE.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/character")
@RequiredArgsConstructor
public class CharacterController {

    private final MemberService memberService;
    private final CharacterService characterService;

    //캐릭터 생성 - 온보딩 화면
    @PostMapping("/create")
    public ResponseEntity<?> createCharacter(
            @RequestHeader("Authorization") String token,
            @RequestBody CharacterRequestDTO requestDTO) {

        // 1. JWT 토큰을 통해 멤버 조회
        Member member;
        try {
            member = memberService.findMemberByToken(token.replace("Bearer ", ""));
        } catch (Exception e) {
            return new ResponseEntity<>("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED);
        }

        // 2. 캐릭터가 이미 존재하는지 확인
        if (member.getCharacter() != null) {
            return new ResponseEntity<>("이미 캐릭터가 존재합니다.", HttpStatus.BAD_REQUEST);
        }

        // 3. 캐릭터 타입 변환
        Character.CharacterType characterType;
        try {
            characterType = Character.CharacterType.valueOf(requestDTO.getCharacterType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("유효하지 않은 캐릭터 타입입니다.", HttpStatus.BAD_REQUEST);
        }

        // 4. 캐릭터 생성 및 설정
        Character character = characterService.createCharacter(characterType, requestDTO.getCharacterName());
        character.setMember(member);
        member.setCharacter(character);

        // 5. 멤버 정보 업데이트
        memberService.save(member);

        // 6. 응답 DTO 생성
        MemberInfoDTO memberInfoDTO = new MemberInfoDTO();
        memberInfoDTO.setId(member.getId());
        memberInfoDTO.setNickname(member.getNickname());
        memberInfoDTO.setKakaoId(member.getKakaoId());

        // 캐릭터 응답 DTO 생성 - 순환 참조 방지
        CharacterResponseDTO characterResponseDTO = new CharacterResponseDTO();
        characterResponseDTO.setId(character.getId());
        characterResponseDTO.setCharacterType(character.getCharacterType().name());
        characterResponseDTO.setCharacterName(character.getCharacterName());
        characterResponseDTO.setLevel(character.getLevel());
        characterResponseDTO.setMember(memberInfoDTO);

        return ResponseEntity.ok(characterResponseDTO);
    }
}
