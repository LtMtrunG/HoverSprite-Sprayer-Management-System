package com.group12.springboot.hoversprite.field;

public interface FieldAPI {
    public FieldDTO findFieldById(Long id);

    public void updateLastSprayingDate(Long id);
}
