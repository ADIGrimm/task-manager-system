package com.tms.service;

import com.tms.dto.label.CreateLabelRequestDto;
import com.tms.dto.label.LabelDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    LabelDto save(Long userId, CreateLabelRequestDto labelDto);

    Page<LabelDto> getAll(Long userId, Long projectId, Pageable pageable);

    LabelDto update(Long userId, Long labelId, CreateLabelRequestDto labelDto);

    void deleteById(Long userId, Long labelId);
}
