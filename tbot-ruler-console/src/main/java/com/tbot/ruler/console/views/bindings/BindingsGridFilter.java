package com.tbot.ruler.console.views.bindings;

import lombok.Setter;

@Setter
public class BindingsGridFilter {

    private String senderUuidTerm;
    private String senderNameTerm;
    private String senderTypeTerm;
    private String receiverUuidTerm;
    private String receiverNameTerm;

    public boolean test(BindingModel binding) {
        return matches(binding.getSenderUuid(), senderUuidTerm)
                && matches(binding.getSenderName(), senderNameTerm)
                && matches(binding.getSenderType(), senderTypeTerm)
                && matches(binding.getReceiverUuid(), receiverUuidTerm)
                && matches(binding.getReceiverName(), receiverNameTerm);
    }

    private boolean matches(String value, String searchTerm) {
        return searchTerm == null || searchTerm.isEmpty()
                || value.toLowerCase().contains(searchTerm.toLowerCase());
    }
}
