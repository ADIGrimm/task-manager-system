package com.tms.mapper;

import com.tms.config.MapperConfig;
import com.tms.dto.attachment.external.UploadResponseDataDto;
import com.tms.dto.attachment.internal.AttachmentDto;
import com.tms.model.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface AttachmentMapper {
    @Mapping(source = "id", target = "fileId")
    @Mapping(source = "name", target = "fileName")
    @Mapping(target = "id", ignore = true)
    Attachment toModel(UploadResponseDataDto response);

    AttachmentDto toDto(Attachment attachment);
}
