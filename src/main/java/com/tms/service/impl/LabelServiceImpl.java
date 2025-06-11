package com.tms.service.impl;

import com.tms.dto.label.CreateLabelRequestDto;
import com.tms.dto.label.LabelDto;
import com.tms.exception.EntityNotFoundException;
import com.tms.mapper.LabelMapper;
import com.tms.model.Label;
import com.tms.repository.label.LabelRepository;
import com.tms.repository.task.TaskRepository;
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
    private final TaskRepository taskRepository;

    @Override
    public LabelDto save(Long userId, CreateLabelRequestDto requestDto) {
        Label label = labelMapper.toModel(requestDto);
        label.setTask(taskRepository.findAccessibleTask(requestDto.taskId(), userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find task by id: " + requestDto.taskId())
        ));
        return labelMapper.toDto(labelRepository.save(label));
    }

    @Override
    public Page<LabelDto> getAll(Long projectId, Long userId, Pageable pageable) {
        return labelRepository
                .findAllLabelsByProjectIdAndAccessibleToUser(projectId, userId, pageable)
                .map(labelMapper::toDto);
    }

    @Override
    public LabelDto update(Long userId, Long labelId, CreateLabelRequestDto labelDto) {
        Label label = labelRepository.findAccessibleLabelById(labelId, userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find label by id: " + labelId)
        );
        labelMapper.updateLabel(labelDto, label);
        labelRepository.save(label);
        return labelMapper.toDto(label);
    }

    @Override
    public void deleteById(Long userId, Long labelId) {
        if (labelRepository.findAccessibleLabelById(labelId, userId).isPresent()) {
            labelRepository.deleteById(labelId);
        } else {
            throw new EntityNotFoundException("Can't find label by id: " + labelId);
        }

    }
}
