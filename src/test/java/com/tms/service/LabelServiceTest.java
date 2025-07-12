package com.tms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tms.dto.label.CreateLabelRequestDto;
import com.tms.dto.label.LabelDto;
import com.tms.mapper.LabelMapper;
import com.tms.model.Label;
import com.tms.model.Task;
import com.tms.repository.label.LabelRepository;
import com.tms.repository.task.TaskRepository;
import com.tms.service.impl.LabelServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class LabelServiceTest {
    @InjectMocks
    private LabelServiceImpl labelService;
    @Mock
    private LabelRepository labelRepository;
    @Mock
    private LabelMapper labelMapper;
    @Mock
    private TaskRepository taskRepository;

    @Test
    void save_withValidRequest_ShouldReturnDtoOfSavedLabel() {
        // Given
        Long taskId = 1L;
        Label label = createLabel();
        Label savedLabel = new Label();
        savedLabel.setId(label.getId());
        savedLabel.setName(label.getName());
        savedLabel.setColor(label.getColor());
        savedLabel.setTask(label.getTask());
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto(
                "Label",
                "#ffffff",
                taskId
        );
        LabelDto expected = new LabelDto(
                savedLabel.getId(),
                "Label",
                "#ffffff",
                savedLabel.getTask().getId()
        );
        when(labelMapper.toModel(requestDto)).thenReturn(label);
        when(taskRepository.findAccessibleTask(anyLong(), anyLong()))
                .thenReturn(Optional.of(label.getTask()));
        when(labelRepository.save(label)).thenReturn(savedLabel);
        when(labelMapper.toDto(savedLabel)).thenReturn(expected);
        // When
        LabelDto actual = labelService.save(anyLong(), requestDto);
        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(labelMapper).toModel(requestDto);
        verify(taskRepository).findAccessibleTask(anyLong(), anyLong());
        verify(labelRepository).save(label);
        verify(labelMapper).toDto(savedLabel);
    }

    @Test
    void getAll_withPageable_ShouldReturnAllLabelsOfProject() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Label> labels = List.of(new Label(), new Label());
        Page<Label> labelPage = new PageImpl<>(labels);
        when(
                labelRepository
                        .findAllLabelsByProjectIdAndAccessibleToUser(projectId, userId, pageable)
        )
                .thenReturn(labelPage);
        List<LabelDto> expected = List.of(
                new LabelDto(
                        1L,
                        "Label 1",
                        "#ffffff",
                        new Task().getId()),
                new LabelDto(
                        2L,
                        "Label 2",
                        "#ffffff",
                        new Task().getId())
        );
        when(labelMapper.toDto(any(Label.class)))
                .thenAnswer(invocation -> {
                    Label label = invocation.getArgument(0);
                    int index = labels.indexOf(label);
                    return expected.get(index);
                });
        // When
        Page<LabelDto> actual = labelService.getAll(projectId, userId, pageable);
        // Then
        verify(labelRepository)
                .findAllLabelsByProjectIdAndAccessibleToUser(projectId, userId, pageable);
        verify(labelMapper, times(labels.size())).toDto(any(Label.class));
        assertEquals(expected, actual.getContent());
    }

    @Test
    void update_withValidRequest_ShouldReturnDtoOfUpdatedLabel() {
        // Given
        Label label = createLabel();
        Long labelId = label.getId();
        Long taskId = label.getTask().getId();
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto(
                "Updated Label",
                "#000000",
                taskId
        );
        when(labelRepository.findAccessibleLabelById(anyLong(), anyLong()))
                .thenReturn(Optional.of(label));
        when(taskRepository.findAccessibleTask(any(), any()))
                .thenReturn(Optional.of(new Task()));
        // When
        labelService.update(1L, labelId, requestDto);
        // Then
        verify(labelRepository).findAccessibleLabelById(anyLong(), anyLong());
        verify(labelMapper).updateLabel(requestDto, label);
        verify(labelMapper).toDto(label);
    }

    @Test
    void deleteById_withValidLabelAndUserId_ShouldDeleteLabel() {
        // Given
        Label label = createLabel();
        Long labelId = label.getId();
        Long userId = 1L;
        when(labelRepository.findAccessibleLabelById(labelId, userId))
                .thenReturn(Optional.of(label));
        doNothing().when(labelRepository).deleteById(labelId);
        // When
        labelService.deleteById(userId, labelId);
        // Then
        verify(labelRepository).findAccessibleLabelById(labelId, userId);
        verify(labelRepository).deleteById(labelId);
    }

    private Label createLabel() {
        Long labelId = 1L;
        Label label = new Label();
        label.setId(labelId);
        label.setName("Label");
        label.setColor("#ffffff");
        label.setTask(new Task());
        return label;
    }
}
