package com.group12.springboot.hoversprite.field;

import java.util.List;

public interface FieldAPI {
    public FieldDTO findFieldById(Long id);

    public void updateLastSprayingDate(Long id);

    public List<Object[]> findFieldCoordinatesByIds(List<Long> fieldIds);
}
