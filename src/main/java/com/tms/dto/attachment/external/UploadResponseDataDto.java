package com.tms.dto.attachment.external;

public record UploadResponseDataDto(
        String name,
        String id,
        String clientModified,
        String serverModified,
        String rev,
        long size,
        String pathLower,
        String pathDisplay
) {
}
