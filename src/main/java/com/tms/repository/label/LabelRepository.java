package com.tms.repository.label;

import com.tms.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LabelRepository
        extends JpaRepository<Label, Long>, JpaSpecificationExecutor<Label> {

}
