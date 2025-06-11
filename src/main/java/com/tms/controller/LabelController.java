package com.tms.controller;

import com.tms.dto.label.CreateLabelRequestDto;
import com.tms.dto.label.LabelDto;
import com.tms.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_USER')")
public class LabelController implements UserContextHelper {
    private final LabelService labelService;

    @Operation(summary = "Create label",
            description = "Create label for task")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelDto createLabel(Authentication authentication,
                                @Valid @RequestBody CreateLabelRequestDto labelDto) {
        return labelService.save(getUserId(authentication), labelDto);
    }

    @Operation(summary = "Get all labels",
            description = "Return page of labels of certain project as page")
    @GetMapping("/{id}")
    public Page<LabelDto> getAll(Authentication authentication,
                                 @PathVariable Long id,
                                 @ParameterObject @PageableDefault Pageable pageable) {
        return labelService.getAll(getUserId(authentication), id, pageable);
    }

    @Operation(summary = "Update label information",
            description = "Update label information")
    @PutMapping("/{id}")
    public LabelDto updateLabel(Authentication authentication,
                                @PathVariable Long id,
                                @Valid @RequestBody CreateLabelRequestDto labelDto) {
        return labelService.update(getUserId(authentication), id, labelDto);
    }

    @Operation(summary = "Delete label by id",
            description = "Delete label by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLabel(Authentication authentication,
                            @PathVariable Long id) {
        labelService.deleteById(getUserId(authentication), id);
    }
}

