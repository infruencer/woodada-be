package com.woodada.core.diary.adapter.out.persistence;

import com.woodada.core.diary.application.port.out.DiaryFindPort;
import com.woodada.core.diary.application.port.out.DiarySavePort;
import com.woodada.core.diary.domain.Diary;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.stereotype.Component;

@Component
public class DiaryPersistenceAdapter implements DiaryFindPort, DiarySavePort {

    private final DiaryRepository diaryRepository;

    public DiaryPersistenceAdapter(final DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @Override
    public boolean existsDiary(final Long createdBy, final LocalDate writeDate) {
        return diaryRepository.existsByCreatedByAndCreatedAtBetween(createdBy, writeDate.atStartOfDay(), writeDate.atTime(LocalTime.MAX));
    }

    @Override
    public Diary saveDiary(final Diary diary) {
        final DiaryJpaEntity diaryJpaEntity = DiaryJpaEntity.from(diary);
        final DiaryJpaEntity savedDiary = diaryRepository.save(diaryJpaEntity);

        return savedDiary.toDomainEntity();
    }
}
