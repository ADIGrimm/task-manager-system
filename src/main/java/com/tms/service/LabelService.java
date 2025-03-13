package com.tms.service;

import com.tms.dto.label.CreateLabelRequestDto;
import com.tms.dto.label.LabelDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LabelService {
    LabelDto save(CreateLabelRequestDto labelDto);

    Page<LabelDto> getAll(Pageable pageable);

    LabelDto update(Long id, CreateLabelRequestDto labelDto);

    void deleteById(Long id);
}
