package com.group12.springboot.hoversprite;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class DocumentationTests {

    ApplicationModules modules = ApplicationModules.of(HoverspriteApplication.class);

    @Test
    void writeDocumentationSnippets() {

        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }
}
