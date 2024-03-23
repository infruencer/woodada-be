package com.woodada.core.diary.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.woodada.common.auth.argument_resolver.MemberHelper;
import com.woodada.common.auth.argument_resolver.WddMember;
import com.woodada.common.auth.domain.UserRole;
import com.woodada.common.exception.WddException;
import com.woodada.core.diary.application.port.in.WriteDiaryCommand;
import com.woodada.core.diary.application.port.out.FindDiaryPort;
import com.woodada.core.diary.application.port.out.SaveDiaryPort;
import com.woodada.core.diary.domain.Diary;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("[unit test] WriteDiaryUseCase 단위테스트")
class WriteDiaryServiceTest {

    @Mock private FindDiaryPort findDiaryPort;
    @Mock private SaveDiaryPort saveDiaryPort;

    private WriteDiaryService writeDiaryService;

    @BeforeEach
    void setUp() {
        writeDiaryService = new WriteDiaryService(findDiaryPort, saveDiaryPort);
    }

    @DisplayName("유효한 글 작성 모델을 전달받으면 작성에 성공한다.")
    @Test
    void given_valid_command_then_diary_is_wrote() {
        //given
        WddMember wddMember = getWddMember(1L);
        WriteDiaryCommand writeDiaryCommand = new WriteDiaryCommand("100자 이하의 제목", "5000자 이하의 본문", LocalDate.now());
        given(saveDiaryPort.saveDiary(any(Diary.class)))
            .willReturn(Diary.withId(1L, "100자 이하의 제목", "5000자 이하의 본문"));

        //when
        Diary savedDiary = writeDiaryService.writeDiary(wddMember, writeDiaryCommand);

        //then
        assertThat(savedDiary).isNotNull();
        then(saveDiaryPort).should(times(1)).saveDiary(any(Diary.class));
    }

    @DisplayName("요청을 보낸 날짜에 이미 작성된 일기가 있는 경우 예외가 발생한다.")
    @Test
    void given_diary_already_written_on_the_day_then_throw_exception() {
        //given
        WddMember wddMember = getWddMember(1L);
        LocalDate dateAlreadyWroteDiary = LocalDate.of(2024, 3, 24);
        WriteDiaryCommand writeDiaryCommand = new WriteDiaryCommand("100자 이하의 제목", "5000자 이하의 본문", dateAlreadyWroteDiary);

        given(findDiaryPort.existsDiary(wddMember.getId(), dateAlreadyWroteDiary))
            .willThrow(WddException.class);

        //when then
        assertThatThrownBy(() -> writeDiaryService.writeDiary(wddMember, writeDiaryCommand))
            .isInstanceOf(WddException.class);
    }

    private WddMember getWddMember(Long memberId) {
        WddMember writer = MemberHelper.createWddMember(memberId, "test@email.com", "테스트유저", "test_protile_url", UserRole.NORMAL);
        return writer;
    }
}
