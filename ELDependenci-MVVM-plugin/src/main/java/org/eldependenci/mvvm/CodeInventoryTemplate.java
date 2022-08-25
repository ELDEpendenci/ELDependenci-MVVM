package org.eldependenci.mvvm;

import org.eldependenci.mvvm.view.ViewDescriptor;

import java.util.Arrays;
import java.util.LinkedHashMap;

public final class CodeInventoryTemplate extends InventoryTemplate {

    public CodeInventoryTemplate(ViewDescriptor viewDescriptor) {
        this.title = viewDescriptor.title();
        this.patterns = Arrays.asList(viewDescriptor.patterns());
        this.items = new LinkedHashMap<>();
    }
}
