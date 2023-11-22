package com.tbot.ruler.console.views.bindings;

import com.tbot.ruler.console.views.GridFilter;
import lombok.Setter;

@Setter
public class BindingsGridFilter implements GridFilter<BindingModel> {

    private String senderUuidTerm;
    private String senderNameTerm;
    private String senderTypeTerm;
    private String receiverUuidTerm;
    private String receiverNameTerm;

    @Override
    public boolean test(BindingModel binding) {
        return matches(binding.getSenderUuid(), senderUuidTerm)
                && matches(binding.getSenderName(), senderNameTerm)
                && matches(binding.getSenderType(), senderTypeTerm)
                && matches(binding.getReceiverUuid(), receiverUuidTerm)
                && matches(binding.getReceiverName(), receiverNameTerm);
    }
}
