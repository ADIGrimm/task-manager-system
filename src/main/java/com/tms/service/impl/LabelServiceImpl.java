package com.tms.service.impl;

import com.tms.dto.label.CreateLabelRequestDto;
import com.tms.dto.label.LabelDto;
import com.tms.exception.EntityNotFoundException;
import com.tms.mapper.LabelMapper;
import com.tms.model.Label;
import com.tms.repository.label.LabelRepository;
import com.tms.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public LabelDto save(CreateLabelRequestDto requestDto) {
        Label label = labelMapper.toModel(requestDto);
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public Page<LabelDto> getAll(Pageable pageable) {
        return labelRepository.findAll(pageable).map(labelMapper::toDto);
    }

    @Override
    public LabelDto update(Long id, CreateLabelRequestDto labelDto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find label by id: " + id));
        labelMapper.updateLabel(labelDto, label);
        labelRepository.save(label);
        return labelMapper.toDto(label);
    }

    @Override
    public void deleteById(Long id) {
        labelRepository.deleteById(id);
    }
}
